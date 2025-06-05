package no.nav.klage.dokument.service

import no.arkivverket.standarder.noark5.arkivmelding.v2.*
import no.nav.klage.dokument.clients.ereg.EregClient
import no.nav.klage.dokument.clients.pdl.graphql.PdlClient
import no.nav.klage.dokument.clients.saf.graphql.*
import no.nav.klage.dokument.clients.saf.graphql.Journalpost
import no.nav.klage.dokument.util.*
import no.nav.klage.kodeverk.Tema
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.math.BigInteger
import javax.xml.datatype.XMLGregorianCalendar
import kotlin.collections.Collection
import kotlin.collections.List
import kotlin.collections.find
import kotlin.collections.firstOrNull
import kotlin.collections.mapNotNull
import no.arkivverket.standarder.noark5.arkivmelding.v2.Journalpost as ArkivmeldingJournalpost

@Service
class ArkivmeldingService(
    private val safGraphQlClient: SafGraphQlClient,
    @Value("\${spring.application.name}")
    private val applicationName: String,
    private val pdlClient: PdlClient,
    private val eregClient: EregClient,
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)

    }

    fun generateMarshalledArkivmelding(
        journalpostId: String,
        bestillingsId: String
    ): String {
        return try {
            marshalArkivmelding(
                generateArkivmelding(
                    journalpostId = journalpostId,
                    bestillingsId = bestillingsId,
                )
            )
        } catch (e: Exception) {
            logger.error("Failed to generate arkivmelding for journalpost $journalpostId", e)
            throw e
        }
    }

    fun generateArkivmelding(journalpostId: String, bestillingsId: String): Arkivmelding {
        val journalpost = getJournalpost(journalpostId = journalpostId)
        val datoArkivmeldingOpprettet = getNow()

        val arkivmelding = Arkivmelding()
        arkivmelding.system = applicationName
        arkivmelding.meldingId = bestillingsId
        arkivmelding.tidspunkt = datoArkivmeldingOpprettet
        arkivmelding.antallFiler = journalpost.dokumenter?.filter { it.isFerdigstiltForArkivmelding() }?.size ?: throw RuntimeException("No files in journalpost")

        val dokumentBeskrivelser = getDokumentbeskrivelser(
            dokumenter = journalpost.dokumenter,
            datoArkivmeldingOpprettet = datoArkivmeldingOpprettet,
            newJournalpost = journalpost,
        )

        arkivmelding.mappe.add(getSaksmappe(journalpost = journalpost, dokumentBeskrivelser = dokumentBeskrivelser))

        return arkivmelding
    }

    private fun getSaksmappe(journalpost: Journalpost, dokumentBeskrivelser: Collection<Dokumentbeskrivelse>): Saksmappe {
        val sakOpprettetDato = if (journalpost.sak?.datoOpprettet != null) {
            convertLocalDateTimeToXmlGregorianCalendar(journalpost.sak.datoOpprettet)
        } else {
            getOldestDateFromDokumentbeskrivelser(dokumentBeskrivelser)
        }
        return Saksmappe().apply {
            tittel = Tema.valueOf(journalpost.tema!!.name).beskrivelse
            opprettetDato = sakOpprettetDato
            opprettetAv = journalpost.opprettetAvNavn
            virksomhetsspesifikkeMetadata = getNavMappe(journalpost.sak?.fagsakId)
            part.add(getAMPPart(opprettetAvNavn = journalpost.opprettetAvNavn))
            part.add(getDAPPart(bruker = journalpost.bruker))
            saksdato = sakOpprettetDato
            administrativEnhet = NAV_KLAGEINSTANS_NAVN
            saksansvarlig = journalpost.opprettetAvNavn
            journalenhet = journalpost.journalforendeEnhet
            saksstatus = UNDER_BEHANDLING
            registrering.add(
                getArkivmeldingJournalpost(
                    journalpost = journalpost,
                    dokumentBeskrivelser = dokumentBeskrivelser
                )
            )
        }
    }

    private fun getArkivmeldingJournalpost(
        journalpost: Journalpost,
        dokumentBeskrivelser: Collection<Dokumentbeskrivelse>
    ): ArkivmeldingJournalpost {
        return ArkivmeldingJournalpost().apply {
            opprettetDato = convertLocalDateTimeToXmlGregorianCalendar(journalpost.datoOpprettet)
            opprettetAv = journalpost.opprettetAvNavn
            tittel = journalpost.tittel
            korrespondansepart.addAll(getKorrespondansepartList())
            journalposttype = UTGAAENDE_DOKUMENT
            journalstatus = EKSPEDERT
            journaldato = convertLocalDateTimeToXmlGregorianCalendar(
                journalpost.getDatoJournalfoert() ?: throw RuntimeException("No journalfoeringData in journalpost")
            )
            dokumentbeskrivelse.addAll(dokumentBeskrivelser)
        }
    }

    private fun getKorrespondansepartList(): Collection<Korrespondansepart> {
        return listOf(
            Korrespondansepart().apply {
                korrespondanseparttype = MOTTAKER
                korrespondansepartNavn = TRYGDERETTEN_NAVN
                organisasjonsnummer = EnhetsidentifikatorType().apply {
                    organisasjonsnummer = TRYGDERETTEN_ORGNR
                }
            },
            Korrespondansepart().apply {
                korrespondanseparttype = AVSENDER
                korrespondansepartNavn = NAV_KLAGEINSTANS_NAVN
                organisasjonsnummer = EnhetsidentifikatorType().apply {
                    organisasjonsnummer = NAV_KLAGEINSTANS_ORGNR
                }
            }
        )
    }


    private fun getDokumentbeskrivelser(
        dokumenter: List<DokumentInfo>,
        datoArkivmeldingOpprettet: XMLGregorianCalendar?,
        newJournalpost: Journalpost,
    ): Collection<Dokumentbeskrivelse> {
        val bruker = newJournalpost.bruker
        val brukerId = when (bruker.type) {
            BrukerType.FNR, BrukerType.ORGNR -> bruker.id
            BrukerType.AKTOERID -> pdlClient.getPersonInfo(ident = bruker.id).data?.hentPerson?.folkeregisteridentifikator?.firstOrNull()?.identifikasjonsnummer
                ?: throw RuntimeException("Foedselsnummer not found")
        }

        val existingJournalpostList = if (dokumenter.any { it.originalJournalpostId != null }) {
            getJournalpostListForBrukerId(brukerId = brukerId)
        } else {
            listOf(newJournalpost)
        }

        var index = 1

        val output = dokumenter.mapNotNull { dokumentInfo ->
            if (dokumentInfo.isFerdigstiltForArkivmelding()) {
                val dokumentIsFromOldJournalpost = dokumentInfo.originalJournalpostId != null

                val originalJournalpost = if (dokumentIsFromOldJournalpost) {
                    existingJournalpostList.find { it.journalpostId == dokumentInfo.originalJournalpostId }
                } else null

                val dokumentbeskrivelse = Dokumentbeskrivelse().apply {
                    dokumenttype = DOKUMENTASJON
                    dokumentstatus = DOKUMENTET_ER_FERDIGSTILT
                    tittel = getDokumentbeskrivelseTittel(
                        dokumentInfo = dokumentInfo,
                        originalJournalpost = originalJournalpost,
                        dokumentIsFromOldJournalpost = dokumentIsFromOldJournalpost
                    )
                    opprettetDato = getDokumentbeskrivelseOpprettetDato(
                        originalJournalpost = originalJournalpost,
                        newJournalpost = newJournalpost,
                        dokumentIsFromOldJournalpost = dokumentIsFromOldJournalpost
                    )
                    opprettetAv = getDokumentbeskrivelseOpprettetAv(
                        originalJournalpost = originalJournalpost,
                        newJournalpost = newJournalpost,
                        isHoveddokument = index == 1,
                    )
                    tilknyttetRegistreringSom = if (index == 1) {
                        HOVEDDOKUMENT
                    } else {
                        VEDLEGG
                    }
                    dokumentnummer = BigInteger.valueOf(index.toLong())
                    tilknyttetDato = datoArkivmeldingOpprettet
                    tilknyttetAv = newJournalpost.journalfortAvNavn
                    dokumentobjekt.add(
                        getDokumentobjekt(
                            dokumentInfo = dokumentInfo,
                            originalJournalpost = originalJournalpost,
                            newJournalpost = newJournalpost,
                            dokumentIsFromOldJournalpost = dokumentIsFromOldJournalpost,
                            isHoveddokument = index == 1,
                        )
                    )
                }

                index++
                dokumentbeskrivelse
            } else null
        }
        return output
    }

    private fun getDokumentobjekt(
        dokumentInfo: DokumentInfo,
        originalJournalpost: Journalpost?,
        newJournalpost: Journalpost,
        dokumentIsFromOldJournalpost: Boolean,
        isHoveddokument: Boolean,
    ): Dokumentobjekt {
        return Dokumentobjekt().apply {
            val gjeldendeDokumentVariant = dokumentInfo.dokumentvarianter.firstOrNull {
                it.variantformat == Variantformat.SLADDET
            } ?: dokumentInfo.dokumentvarianter.firstOrNull {
                it.variantformat == Variantformat.ARKIV
            } ?: throw RuntimeException("No dokumentvariant found for dokument ${dokumentInfo.dokumentInfoId}")

            versjonsnummer = BigInteger.ONE
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
                isHoveddokument = isHoveddokument
            )
            referanseDokumentfil = getDokumentbeskrivelseReferanseDokumentFil(
                dokumentInfo,
                newJournalpost,
                gjeldendeDokumentVariant
            )
        }
    }

    fun getJournalpost(
        journalpostId: String,
    ): Journalpost {
        return safGraphQlClient.getJournalpostAsSystembruker(journalpostId = journalpostId)
    }

    fun getJournalpostListForBrukerId(
        brukerId: String,
    ): List<Journalpost> {
        return safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(brukerId = brukerId)
    }

    private fun getDAPPart(bruker: Bruker): Part {
        return when (bruker.type) {
            BrukerType.FNR, BrukerType.AKTOERID -> {
                val personInfo = pdlClient.getPersonInfo(ident = bruker.id)
                val fnr = personInfo.data?.hentPerson?.folkeregisteridentifikator?.firstOrNull()?.identifikasjonsnummer
                    ?: throw RuntimeException("Foedselsnummer not found for bruker ${bruker.id}")
                Part().apply {
                    partNavn = getSammensattNavn(personInfo.data.hentPerson.navn.firstOrNull())
                    partRolle = SAKSPART_ROLLE_DAP
                    foedselsnummer = FoedselsnummerType().apply { foedselsnummer = fnr }
                }
            }

            BrukerType.ORGNR -> {
                val orgnummer = bruker.id
                val noekkelInfoOmOrganisasjon = eregClient.hentNoekkelInformasjonOmOrganisasjon(orgnummer)
                Part().apply {
                    partNavn = noekkelInfoOmOrganisasjon.navn.sammensattnavn
                    partRolle = SAKSPART_ROLLE_DAP
                    organisasjonsnummer = EnhetsidentifikatorType().apply { organisasjonsnummer = orgnummer }
                }
            }
        }
    }
}