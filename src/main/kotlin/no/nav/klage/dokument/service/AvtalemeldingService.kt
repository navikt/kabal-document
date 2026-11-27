package no.nav.klage.dokument.service

import no.arkivverket.standarder.noark5.arkivmelding.v2.Arkivmelding
import no.arkivverket.standarder.noark5.arkivmelding.v2.Dokumentbeskrivelse
import no.arkivverket.standarder.noark5.arkivmelding.v2.Dokumentobjekt
import no.arkivverket.standarder.noark5.arkivmelding.v2.EnhetsidentifikatorType
import no.arkivverket.standarder.noark5.arkivmelding.v2.FoedselsnummerType
import no.arkivverket.standarder.noark5.arkivmelding.v2.Korrespondansepart
import no.arkivverket.standarder.noark5.arkivmelding.v2.Part
import no.arkivverket.standarder.noark5.arkivmelding.v2.Saksmappe
import no.nav.klage.dokument.api.input.TrygderettenMetadataInput
import no.nav.klage.dokument.clients.klagelookup.KlageLookupClient
import no.nav.klage.dokument.clients.klageunleashproxy.KlageUnleashProxyClient
import no.nav.klage.dokument.clients.saf.graphql.DokumentInfo
import no.nav.klage.dokument.clients.saf.graphql.Journalpost
import no.nav.klage.dokument.clients.saf.graphql.SafGraphQlClient
import no.nav.klage.dokument.clients.saf.graphql.Variantformat
import no.nav.klage.dokument.util.AVSENDER
import no.nav.klage.dokument.util.DOKUMENTASJON
import no.nav.klage.dokument.util.DOKUMENTET_ER_FERDIGSTILT
import no.nav.klage.dokument.util.EKSPEDERT
import no.nav.klage.dokument.util.HOVEDDOKUMENT
import no.nav.klage.dokument.util.MOTTAKER
import no.nav.klage.dokument.util.NAV_KLAGEINSTANS_NAVN
import no.nav.klage.dokument.util.NAV_KLAGEINSTANS_ORGNR
import no.nav.klage.dokument.util.SAKSPART_ROLLE_DAP
import no.nav.klage.dokument.util.TRYGDERETTEN_NAVN
import no.nav.klage.dokument.util.TRYGDERETTEN_ORGNR
import no.nav.klage.dokument.util.UNDER_BEHANDLING
import no.nav.klage.dokument.util.UTGAAENDE_DOKUMENT
import no.nav.klage.dokument.util.VEDLEGG
import no.nav.klage.dokument.util.convertLocalDateTimeToXmlGregorianCalendar
import no.nav.klage.dokument.util.getAMPPart
import no.nav.klage.dokument.util.getDokumentbeskrivelseOpprettetAv
import no.nav.klage.dokument.util.getDokumentbeskrivelseOpprettetDato
import no.nav.klage.dokument.util.getDokumentbeskrivelseReferanseDokumentFil
import no.nav.klage.dokument.util.getDokumentbeskrivelseTittel
import no.nav.klage.dokument.util.getDokumentbeskrivelseVariantFormat
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getNavMappe
import no.nav.klage.dokument.util.getNow
import no.nav.klage.dokument.util.getOldestDateFromDokumentbeskrivelser
import no.nav.klage.dokument.util.getREPPart
import no.nav.klage.dokument.util.marshalAvtalemelding
import no.nav.klage.dokument.util.splitTrygderettenSaksnummer
import no.nav.klage.kodeverk.Tema
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.math.BigInteger
import javax.xml.datatype.XMLGregorianCalendar
import no.arkivverket.standarder.noark5.arkivmelding.v2.Journalpost as AvtalemeldingJournalpost

@Service
class AvtalemeldingService(
    private val safGraphQlClient: SafGraphQlClient,
    @Value($$"${spring.application.name}")
    private val applicationName: String,
    private val klageLookupClient: KlageLookupClient,
    private val klageUnleashProxyClient: KlageUnleashProxyClient,
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)

        private const val NAV_TR_V2_TOGGLE = "nav-tr-v2"
    }

    fun generateMarshalledAvtalemelding(
        journalpostId: String,
        bestillingsId: String,
        trygderettenMetadata: TrygderettenMetadataInput?,
        sakenGjelderFnr: String,
    ): Pair<String, String> =
        try {
            val useV2 = klageUnleashProxyClient.isEnabled(NAV_TR_V2_TOGGLE)
            val (arkivsaksnummer, avtalemelding) =
                generateAvtalemelding(
                    journalpostId = journalpostId,
                    bestillingsId = bestillingsId,
                    trygderettenMetadata = trygderettenMetadata,
                    useV2 = useV2,
                    sakenGjelderFnr = sakenGjelderFnr,
                )
            val avtalemeldingAsString =
                marshalAvtalemelding(
                    avtalemelding = avtalemelding,
                    useV2 = useV2,
                )
            arkivsaksnummer to avtalemeldingAsString
        } catch (e: Exception) {
            logger.error("Failed to generate avtalemelding for journalpost $journalpostId", e)
            throw e
        }

    // Avtalemelding er en utvidet versjon av arkivmelding
    fun generateAvtalemelding(
        journalpostId: String,
        bestillingsId: String,
        trygderettenMetadata: TrygderettenMetadataInput?,
        useV2: Boolean = false,
        sakenGjelderFnr: String,
    ): Pair<String, Arkivmelding> {
        val journalpost = getJournalpost(journalpostId = journalpostId)
        val datoAvtalemeldingOpprettet = getNow()

        val avtalemelding = Arkivmelding()
        avtalemelding.system = applicationName
        avtalemelding.meldingId = bestillingsId
        avtalemelding.tidspunkt = datoAvtalemeldingOpprettet
        avtalemelding.antallFiler = journalpost.dokumenter?.filter { it.isFerdigstiltForAvtalemelding() }?.size
            ?: throw RuntimeException("No files in journalpost")

        val dokumentBeskrivelser =
            getDokumentbeskrivelser(
                datoAvtalemeldingOpprettet = datoAvtalemeldingOpprettet,
                newJournalpost = journalpost,
                sakenGjelderFnr = sakenGjelderFnr,
            )

        val arkivsaksnummer =
            if (journalpost.sak?.arkivsaksnummer != null) {
                journalpost.sak.arkivsaksnummer
            } else {
                throw RuntimeException("No arkivsaksnummer in journalpost ${journalpost.journalpostId}")
            }

        avtalemelding.mappe.add(
            getSaksmappe(
                journalpost = journalpost,
                dokumentBeskrivelser = dokumentBeskrivelser,
                arkivsaksnummer = arkivsaksnummer,
                trygderettenMetadata = trygderettenMetadata,
                useV2 = useV2,
                sakenGjelderFnr = sakenGjelderFnr,
            ),
        )

        return arkivsaksnummer to avtalemelding
    }

    private fun getSaksmappe(
        journalpost: Journalpost,
        dokumentBeskrivelser: Collection<Dokumentbeskrivelse>,
        arkivsaksnummer: String,
        trygderettenMetadata: TrygderettenMetadataInput?,
        useV2: Boolean,
        sakenGjelderFnr: String,
    ): Saksmappe {
        val sakOpprettetDato =
            if (journalpost.sak?.datoOpprettet != null) {
                convertLocalDateTimeToXmlGregorianCalendar(journalpost.sak.datoOpprettet)
            } else {
                getOldestDateFromDokumentbeskrivelser(dokumentBeskrivelser)
            }
        return Saksmappe().apply {
            tittel = Tema.valueOf(journalpost.tema!!.name).beskrivelse
            opprettetDato = sakOpprettetDato
            opprettetAv = journalpost.opprettetAvNavn
            trygderettenMetadata?.trygderettenSaksnummer?.let { trygderettenSaksnummer ->
                val (aar, sekvensnummer) = splitTrygderettenSaksnummer(trygderettenSaksnummer)
                saksaar = aar.toBigInteger()
                sakssekvensnummer = sekvensnummer.toBigInteger()
            }
            virksomhetsspesifikkeMetadata =
                getNavMappe(
                    arkivsaknummer = arkivsaksnummer,
                    useV2 = useV2,
                    trygderettenMetadata = trygderettenMetadata,
                )
            part.add(getAMPPart(opprettetAvNavn = journalpost.opprettetAvNavn))
            part.add(getDAPPart(sakenGjelderFnr = sakenGjelderFnr))
            if (useV2) {
                trygderettenMetadata?.representant?.let { part.add(getREPPart(representant = it)) }
            }
            saksdato = sakOpprettetDato
            administrativEnhet = NAV_KLAGEINSTANS_NAVN
            saksansvarlig = journalpost.opprettetAvNavn
            journalenhet = journalpost.journalforendeEnhet
            saksstatus = UNDER_BEHANDLING
            registrering.add(
                getAvtalemeldingJournalpost(
                    journalpost = journalpost,
                    dokumentBeskrivelser = dokumentBeskrivelser,
                ),
            )
        }
    }

    private fun getAvtalemeldingJournalpost(
        journalpost: Journalpost,
        dokumentBeskrivelser: Collection<Dokumentbeskrivelse>,
    ): AvtalemeldingJournalpost =
        AvtalemeldingJournalpost().apply {
            opprettetDato = convertLocalDateTimeToXmlGregorianCalendar(journalpost.datoOpprettet)
            opprettetAv = journalpost.opprettetAvNavn
            tittel = journalpost.tittel ?: throw RuntimeException("No tittel in journalpost")
            korrespondansepart.addAll(getKorrespondansepartList())
            journalposttype = UTGAAENDE_DOKUMENT
            journalstatus = EKSPEDERT
            journaldato =
                convertLocalDateTimeToXmlGregorianCalendar(
                    journalpost.getDatoJournalfoert() ?: throw RuntimeException("No journalfoeringData in journalpost"),
                )
            dokumentbeskrivelse.addAll(dokumentBeskrivelser)
        }

    private fun getKorrespondansepartList(): Collection<Korrespondansepart> =
        listOf(
            Korrespondansepart().apply {
                korrespondanseparttype = MOTTAKER
                korrespondansepartNavn = TRYGDERETTEN_NAVN
                organisasjonsnummer =
                    EnhetsidentifikatorType().apply {
                        organisasjonsnummer = TRYGDERETTEN_ORGNR
                    }
            },
            Korrespondansepart().apply {
                korrespondanseparttype = AVSENDER
                korrespondansepartNavn = NAV_KLAGEINSTANS_NAVN
                organisasjonsnummer =
                    EnhetsidentifikatorType().apply {
                        organisasjonsnummer = NAV_KLAGEINSTANS_ORGNR
                    }
            },
        )

    private fun getDokumentbeskrivelser(
        datoAvtalemeldingOpprettet: XMLGregorianCalendar?,
        newJournalpost: Journalpost,
        sakenGjelderFnr: String,
    ): Collection<Dokumentbeskrivelse> {
        val dokumenter = newJournalpost.dokumenter ?: throw RuntimeException("No files in journalpost")
        val existingJournalpostList =
            if (dokumenter.any { it.originalJournalpostId != null }) {
                getJournalpostListForBrukerId(brukerId = sakenGjelderFnr)
            } else {
                listOf(newJournalpost)
            }

        var index = 1

        val output =
            dokumenter.mapNotNull { dokumentInfo ->
                if (dokumentInfo.isFerdigstiltForAvtalemelding()) {
                    val originalJournalpost: Journalpost? =
                        if (dokumentInfo.originalJournalpostId != null) {
                            val foundJournalpost =
                                existingJournalpostList.find { it.journalpostId == dokumentInfo.originalJournalpostId }
                            if (foundJournalpost != null) {
                                foundJournalpost
                            } else {
                                logger.warn(
                                    "Could not find original journalpost with id ${dokumentInfo.originalJournalpostId} for dokumentInfoId ${dokumentInfo.dokumentInfoId}. Trying to fetch it directly.",
                                )

                                val foundJournalpostFromSaf =
                                    try {
                                        getJournalpost(journalpostId = dokumentInfo.originalJournalpostId)
                                    } catch (e: Exception) {
                                        logger.error(
                                            "Failed to fetch original journalpost with id ${dokumentInfo.originalJournalpostId} for dokumentInfoId ${dokumentInfo.dokumentInfoId}",
                                            e,
                                        )
                                        throw e
                                    }
                                foundJournalpostFromSaf
                            }
                        } else {
                            null
                        }

                    val dokumentbeskrivelse =
                        Dokumentbeskrivelse().apply {
                            dokumenttype = DOKUMENTASJON
                            dokumentstatus = DOKUMENTET_ER_FERDIGSTILT
                            tittel =
                                getDokumentbeskrivelseTittel(
                                    dokumentInfo = dokumentInfo,
                                    originalJournalpost = originalJournalpost,
                                )
                            opprettetDato =
                                getDokumentbeskrivelseOpprettetDato(
                                    originalJournalpost = originalJournalpost,
                                    newJournalpost = newJournalpost,
                                )
                            opprettetAv =
                                getDokumentbeskrivelseOpprettetAv(
                                    originalJournalpost = originalJournalpost,
                                    newJournalpost = newJournalpost,
                                    isHoveddokument = index == 1,
                                )
                            tilknyttetRegistreringSom =
                                if (index == 1) {
                                    HOVEDDOKUMENT
                                } else {
                                    VEDLEGG
                                }
                            dokumentnummer = BigInteger.valueOf(index.toLong())
                            tilknyttetDato = datoAvtalemeldingOpprettet
                            tilknyttetAv = newJournalpost.journalfortAvNavn
                            dokumentobjekt.add(
                                getDokumentobjekt(
                                    dokumentInfo = dokumentInfo,
                                    originalJournalpost = originalJournalpost,
                                    newJournalpost = newJournalpost,
                                    isHoveddokument = index == 1,
                                ),
                            )
                        }

                    index++
                    dokumentbeskrivelse
                } else {
                    null
                }
            }
        return output
    }

    private fun getDokumentobjekt(
        dokumentInfo: DokumentInfo,
        originalJournalpost: Journalpost?,
        newJournalpost: Journalpost,
        isHoveddokument: Boolean,
    ): Dokumentobjekt =
        Dokumentobjekt().apply {
            val gjeldendeDokumentVariant =
                dokumentInfo.dokumentvarianter.firstOrNull {
                    it.variantformat == Variantformat.SLADDET
                } ?: dokumentInfo.dokumentvarianter.firstOrNull {
                    it.variantformat == Variantformat.ARKIV
                } ?: throw RuntimeException("No dokumentvariant found for dokument ${dokumentInfo.dokumentInfoId}")

            versjonsnummer = BigInteger.ONE
            variantformat = getDokumentbeskrivelseVariantFormat(gjeldendeDokumentVariant)
            format = gjeldendeDokumentVariant.filtype.toString().lowercase()
            opprettetDato =
                getDokumentbeskrivelseOpprettetDato(
                    originalJournalpost = originalJournalpost,
                    newJournalpost = newJournalpost,
                )
            opprettetAv =
                getDokumentbeskrivelseOpprettetAv(
                    originalJournalpost = originalJournalpost,
                    newJournalpost = newJournalpost,
                    isHoveddokument = isHoveddokument,
                )
            referanseDokumentfil =
                getDokumentbeskrivelseReferanseDokumentFil(
                    dokument = dokumentInfo,
                    newJournalpost = newJournalpost,
                    gjeldendeDokumentVariant = gjeldendeDokumentVariant,
                )
        }

    fun getJournalpost(journalpostId: String): Journalpost = safGraphQlClient.getJournalpostAsSystembruker(journalpostId = journalpostId)

    fun getJournalpostListForBrukerId(brukerId: String): List<Journalpost> =
        safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(brukerId = brukerId)

    private fun getDAPPart(sakenGjelderFnr: String): Part {
        val personInfo = klageLookupClient.getPerson(fnr = sakenGjelderFnr)
        return Part().apply {
            partNavn = personInfo.sammensattNavn
            partRolle = SAKSPART_ROLLE_DAP
            foedselsnummer = FoedselsnummerType().apply { foedselsnummer = sakenGjelderFnr }
        }
    }
}
