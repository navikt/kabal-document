package no.nav.klage.dokument.gateway

import brave.Tracer
import no.nav.klage.dokument.clients.joark.JoarkClient
import no.nav.klage.dokument.domain.dokument.BrevMottaker
import no.nav.klage.dokument.domain.dokument.JournalfoeringData
import no.nav.klage.dokument.domain.dokument.MellomlagretDokument
import no.nav.klage.dokument.domain.joark.*
import no.nav.klage.dokument.domain.kodeverk.PartIdType
import no.nav.klage.dokument.util.PdfUtils
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import org.springframework.stereotype.Component
import java.util.*

@Component
class JoarkGateway(
    private val joarkClient: JoarkClient,
    private val tracer: Tracer,
    private val pdfUtils: PdfUtils
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val securelogger = getSecureLogger()

        private const val BREV_TITTEL = "Brev fra Klageinstans"
        private const val BREVKODE = "BREV_FRA_KLAGEINSTANS"
        private const val BEHANDLINGSTEMA_KLAGE_KLAGEINSTANS = "ab0164"
        private const val KLAGEBEHANDLING_ID_KEY = "klagebehandling_id"
    }

    fun createJournalpostAsSystemUser(
        journalfoeringData: JournalfoeringData,
        document: MellomlagretDokument,
        brevMottaker: BrevMottaker
    ): String {
        val journalpost = createJournalpostObject(
            journalfoeringData,
            document,
            brevMottaker
        )
        return joarkClient.createJournalpostInJoarkAsSystemUser(journalpost).journalpostId
    }

    fun cancelJournalpost(journalpostId: String): String {
        return joarkClient.cancelJournalpost(journalpostId)
    }

    fun finalizeJournalpostAsSystemUser(journalpostId: String, journalfoerendeEnhet: String): String {
        return joarkClient.finalizeJournalpostAsSystemUser(journalpostId, journalfoerendeEnhet)
    }

    private fun createJournalpostObject(
        journalfoeringData: JournalfoeringData,
        document: MellomlagretDokument,
        brevMottaker: BrevMottaker
    ): Journalpost =
        Journalpost(
            journalposttype = JournalpostType.UTGAAENDE,
            tema = journalfoeringData.tema,
            behandlingstema = BEHANDLINGSTEMA_KLAGE_KLAGEINSTANS,
            avsenderMottaker = createAvsenderMottager(brevMottaker),
            sak = createSak(journalfoeringData),
            tittel = BREV_TITTEL,
            journalfoerendeEnhet = journalfoeringData.enhet,
            eksternReferanseId = tracer.currentSpan().context().traceIdString(),
            bruker = createBruker(journalfoeringData),
            dokumenter = createDokument(document),
            tilleggsopplysninger = listOf(
                Tilleggsopplysning(
                    nokkel = KLAGEBEHANDLING_ID_KEY, verdi = journalfoeringData.kildeReferanse
                )
            )
        )


    private fun createAvsenderMottager(brevMottaker: BrevMottaker): AvsenderMottaker =
        AvsenderMottaker(
            id = brevMottaker.partId.value,
            idType = if (brevMottaker.partId.type == PartIdType.PERSON) {
                AvsenderMottakerIdType.FNR
            } else {
                AvsenderMottakerIdType.ORGNR
            },
            navn = brevMottaker.navn
        )

    private fun createSak(journalfoeringData: JournalfoeringData): Sak {
        return if (journalfoeringData.sakFagsakId == null || journalfoeringData.sakFagsystem == null) {
            Sak(Sakstype.GENERELL_SAK)
        } else {
            Sak(
                sakstype = Sakstype.FAGSAK,
                fagsaksystem = journalfoeringData.sakFagsystem.navn.let { FagsaksSystem.valueOf(it) },
                fagsakid = journalfoeringData.sakFagsakId
            )
        }
    }

    //TODO: Kan SakenGjelder være org?? Datatypen åpner for det!
    private fun createBruker(journalfoeringData: JournalfoeringData): Bruker {
        return Bruker(
            journalfoeringData.sakenGjelder.value,
            BrukerIdType.FNR
        )
    }

    private fun createDokument(
        document: MellomlagretDokument
    ): List<Dokument> {
        val hovedDokument = Dokument(
            tittel = BREV_TITTEL,
            brevkode = BREVKODE,
            dokumentVarianter = listOf(
                DokumentVariant(
                    filnavn = document.title,
                    filtype = if (pdfUtils.pdfByteArrayIsPdfa(document.content)) "PDFA" else "PDF",
                    variantformat = "ARKIV",
                    fysiskDokument = Base64.getEncoder().encodeToString(document.content)
                )
            ),

            )
        return listOf(hovedDokument)
    }
}