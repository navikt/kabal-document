package no.nav.klage.dokument.service

import no.arkivverket.standarder.noark5.arkivmelding.v2.*
import no.nav.klage.dokument.clients.pdl.graphql.PdlClient
import no.nav.klage.dokument.clients.saf.graphql.DokumentInfo
import no.nav.klage.dokument.clients.saf.graphql.Journalpost
import no.nav.klage.dokument.clients.saf.graphql.SafGraphQlClient
import no.nav.klage.dokument.clients.saf.graphql.Variantformat
import no.nav.klage.dokument.util.*
import no.nav.klage.kodeverk.Tema
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.util.*
import javax.xml.datatype.XMLGregorianCalendar

@Service
class ArkivmeldingService(
    private val safGraphQlClient: SafGraphQlClient,
    @Value("\${spring.application.name}")
    private val applicationName: String,
    private val pdlClient: PdlClient,
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        const val NAV_KLAGEINSTANS = "NAV Klageinstans"
        const val TRYGDERETTEN = "TRYGDERETTEN"
        const val SAKSPART_ROLLE_DAP = "DAP"
        const val SAKSPART_ROLLE_AMP = "AMP"
        const val TRYGDERETTEN_ORGNR = "974761084"
        const val NAV_KLAGEINSTANS_ORGNR = "991078045"
        const val UNDER_BEHANDLING = "Under behandling"
        const val MOTTAKER = "Mottaker"
        const val AVSENDER = "Avsender"
        const val UTGAAENDE_DOKUMENT = "Utgående dokument"
        const val EKSPEDERT = "Ekspedert"
        const val DOKUMENTASJON = "Dokumentasjon"
        const val DOKUMENTET_ER_FERDIGSTILT = "Dokumentet er ferdigstilt"
        const val HOVEDDOKUMENT = "Hoveddokument"
        const val VEDLEGG = "Vedlegg"

    }

    fun generateArkivmelding(journalpostId: String, avsenderMottakerDistribusjonId: UUID): String? {
        val journalpost = getJournalpost(journalpostId = journalpostId)

        val personInfo = pdlClient.getPersonInfo(ident = journalpost.bruker.id)
        val datoArkivmeldingOpprettet = getNow()

        val arkivmelding = Arkivmelding()
        arkivmelding.system = applicationName
        arkivmelding.meldingId = avsenderMottakerDistribusjonId.toString()
        arkivmelding.tidspunkt = datoArkivmeldingOpprettet
        arkivmelding.antallFiler = journalpost.dokumenter?.size ?: throw RuntimeException("No files in journalpost")

        val fnr = personInfo.data?.hentPerson?.folkeregisteridentifikator?.identifikasjonsnummer
            ?: throw RuntimeException("Foedselsnummer not found")

        val dokumentBeskrivelser = getDokumentbeskrivelser(
            dokumenter = journalpost.dokumenter,
            datoArkivmeldingOpprettet = datoArkivmeldingOpprettet,
            newJournalpost = journalpost,
            fnr = fnr,
        )

        val sakOpprettetDato = if (journalpost.sak?.datoOpprettet != null) {
            convertLocalDateTimeToXmlGregorianCalendar(journalpost.sak.datoOpprettet)
        } else {
            getOldestDateFromDokumentbeskrivelser(dokumentBeskrivelser)
        }

        arkivmelding.mappe.add(Saksmappe().apply {
            tittel = Tema.valueOf(journalpost.tema!!.name).beskrivelse
            opprettetDato = sakOpprettetDato
            opprettetAv = journalpost.opprettetAvNavn
            virksomhetsspesifikkeMetadata = getNavMappe(journalpost.sak?.fagsakId)
            part.add(Part().apply {
                partNavn = NAV_KLAGEINSTANS
                partRolle = SAKSPART_ROLLE_AMP
                organisasjonsnummer = EnhetsidentifikatorType().apply { organisasjonsnummer = NAV_KLAGEINSTANS_ORGNR }
                kontaktperson = journalpost.opprettetAvNavn
            }
            )
            part.add(Part().apply {
                partNavn = getSammensattNavn(personInfo.data.hentPerson.navn.firstOrNull())
                partRolle = SAKSPART_ROLLE_DAP
                foedselsnummer = FoedselsnummerType().apply {
                    foedselsnummer = fnr
                }
            }
            )

            saksdato = sakOpprettetDato
            administrativEnhet = NAV_KLAGEINSTANS
            saksansvarlig = journalpost.opprettetAvNavn
            journalenhet = journalpost.journalforendeEnhet
            saksstatus = UNDER_BEHANDLING
            registrering.add(Journalpost().apply {
                opprettetDato = convertLocalDateTimeToXmlGregorianCalendar(journalpost.datoOpprettet)
                opprettetAv = journalpost.opprettetAvNavn
                tittel = journalpost.tittel
                //mottaker
                korrespondansepart.add(Korrespondansepart().apply {
                    korrespondanseparttype = MOTTAKER
                    korrespondansepartNavn = TRYGDERETTEN
                    organisasjonsnummer = EnhetsidentifikatorType().apply {
                        organisasjonsnummer = TRYGDERETTEN_ORGNR
                    }
                }
                )
                //avsender
                korrespondansepart.add(Korrespondansepart().apply {
                    korrespondanseparttype = AVSENDER
                    korrespondansepartNavn = NAV_KLAGEINSTANS
                    organisasjonsnummer = EnhetsidentifikatorType().apply {
                        organisasjonsnummer = NAV_KLAGEINSTANS_ORGNR
                    }
                }
                )
                journalposttype = UTGAAENDE_DOKUMENT
                journalstatus = EKSPEDERT
                journaldato = convertLocalDateTimeToXmlGregorianCalendar(
                    journalpost.getDatoJournalfoert() ?: throw RuntimeException("No journalfoeringData in journalpost")
                )

                dokumentbeskrivelse.addAll(dokumentBeskrivelser)
            }
            )
        }
        )

        return marshalArkivmelding(arkivmelding)
    }

    private fun getDokumentbeskrivelser(
        dokumenter: List<DokumentInfo>,
        datoArkivmeldingOpprettet: XMLGregorianCalendar?,
        newJournalpost: Journalpost,
        fnr: String
    ): Collection<Dokumentbeskrivelse> {
        var index = 1
        val existingJournalpostList = getJournalpostListForFnr(fnr = fnr)

        val output = dokumenter.mapNotNull { dokument ->
            if (dokument.isFerdigstilt()) {
                val dokumentIsFromOldJournalpost = dokument.originalJournalpostId != null

                val originalJournalpost = if (dokumentIsFromOldJournalpost) {
                    existingJournalpostList.find { it.journalpostId == dokument.originalJournalpostId }
                } else null

                val dokumentbeskrivelse = Dokumentbeskrivelse().apply {
                    dokumenttype = DOKUMENTASJON
                    dokumentstatus = DOKUMENTET_ER_FERDIGSTILT
                    tittel = getDokumentbeskrivelseTittel(
                        dokumentInfo = dokument,
                        originalJournalpost = originalJournalpost,
                        dokumentIsFromOldJournalpost
                    )
                    opprettetDato = getDokumentbeskrivelseOpprettetDato(
                        originalJournalpost = originalJournalpost,
                        newJournalpost = newJournalpost,
                        dokumentIsFromOldJournalpost
                    )
                    opprettetAv = getDokumentbeskrivelseOpprettetAv(
                        originalJournalpost = originalJournalpost,
                        newJournalpost = newJournalpost,
                    )
                    tilknyttetRegistreringSom = if (index == 1) {
                        HOVEDDOKUMENT
                    } else {
                        VEDLEGG
                    }
                    dokumentnummer = BigInteger.valueOf(index.toLong())
                    tilknyttetDato = datoArkivmeldingOpprettet
                    tilknyttetAv = newJournalpost.journalfortAvNavn
                    dokumentobjekt.add(Dokumentobjekt().apply {
                        val gjeldendeDokumentVariant = dokument.dokumentvarianter.firstOrNull {
                            it.variantformat == Variantformat.SLADDET
                        } ?: dokument.dokumentvarianter.firstOrNull {
                            it.variantformat == Variantformat.ARKIV
                        } ?: throw RuntimeException("No dokumentvariant found for dokument ${dokument.dokumentInfoId}")

                        versjonsnummer = BigInteger.valueOf(1.toLong())
                        variantformat = getDokumentbeskrivelseVariantFormat(gjeldendeDokumentVariant)
                        format = gjeldendeDokumentVariant.filtype.toString().lowercase()
                        opprettetDato = getDokumentbeskrivelseOpprettetDato(
                            originalJournalpost = originalJournalpost,
                            newJournalpost = newJournalpost,
                            dokumentIsFromOldJournalpost = dokumentIsFromOldJournalpost
                        )
                        opprettetAv = getDokumentbeskrivelseOpprettetAv(
                            originalJournalpost = originalJournalpost,
                            newJournalpost = newJournalpost,
                        )
                        referanseDokumentfil = getDokumentbeskrivelseReferanseDokumentFil(
                            dokument,
                            newJournalpost,
                            gjeldendeDokumentVariant
                        )
                    }
                    )
                }

                index++
                dokumentbeskrivelse
            } else null
        }
        return output
    }

    fun getJournalpost(
        journalpostId: String,
    ): Journalpost {
        return safGraphQlClient.getJournalpostAsSystembruker(journalpostId = journalpostId)
    }

    fun getJournalpostListForFnr(
        fnr: String,
    ): List<Journalpost> {
        return safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(fnr = fnr)
    }
}