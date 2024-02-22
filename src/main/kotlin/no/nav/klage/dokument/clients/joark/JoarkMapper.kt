package no.nav.klage.dokument.clients.joark

//import no.nav.klage.dokument.domain.dokument.AvsenderMottaker
import no.nav.klage.dokument.domain.dokument.AvsenderMottaker
import no.nav.klage.dokument.domain.dokument.JournalfoeringData
import no.nav.klage.dokument.domain.dokument.OpplastetHoveddokument
import no.nav.klage.dokument.service.JournalfoeringService
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import no.nav.klage.kodeverk.PartIdType
import org.springframework.stereotype.Service
import java.util.*

@Service
class JoarkMapper {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val securelogger = getSecureLogger()
    }

    fun createJournalpost(
        journalfoeringData: JournalfoeringData,
        opplastetHovedDokument: OpplastetHoveddokument,
        hovedDokument: JournalfoeringService.MellomlagretDokument,
        vedleggDokumentList: List<JournalfoeringService.MellomlagretDokument> = emptyList(),
        avsenderMottaker: AvsenderMottaker
    ): Journalpost {

        val kanal =  if (journalfoeringData.journalpostType == JournalpostType.INNGAAENDE) {
            avsenderMottaker.kanal ?: journalfoeringData.inngaaendeKanal
        } else if(avsenderMottaker.localPrint) {
            Kanal.L
        } else if (journalfoeringData.journalpostType == JournalpostType.UTGAAENDE) {
            avsenderMottaker.kanal
        } else null

        val journalpost = Journalpost(
            avsenderMottaker = null,
            journalposttype = journalfoeringData.journalpostType,
            tema = journalfoeringData.tema,
            behandlingstema = journalfoeringData.behandlingstema,
            sak = createSak(journalfoeringData),
            kanal = kanal,
            tittel = journalfoeringData.tittel,
            journalfoerendeEnhet = journalfoeringData.enhet,
            eksternReferanseId = "${opplastetHovedDokument.id}_${avsenderMottaker.id}",
            datoMottatt = journalfoeringData.datoMottatt,
            bruker = createBruker(journalfoeringData),
            dokumenter = createDokumentListFromHoveddokumentAndVedleggList(
                hoveddokument = hovedDokument,
                vedleggList = vedleggDokumentList,
                journalfoeringData = journalfoeringData
            ),
            tilleggsopplysninger = journalfoeringData.tilleggsopplysning?.let {
                listOf(
                    Tilleggsopplysning(
                        nokkel = it.key, verdi = it.value
                    )
                )
            } ?: emptyList()
        )

        if (journalfoeringData.journalpostType in listOf(JournalpostType.UTGAAENDE, JournalpostType.INNGAAENDE)) {
            journalpost.avsenderMottaker = createJournalpostAvsenderMottager(avsenderMottaker)
        }

        return journalpost
    }

    private fun createJournalpostAvsenderMottager(avsenderMottaker: AvsenderMottaker): JournalpostAvsenderMottaker =
        JournalpostAvsenderMottaker(
            id = avsenderMottaker.partId.value,
            idType = if (avsenderMottaker.partId.type == PartIdType.PERSON) {
                AvsenderMottakerIdType.FNR
            } else {
                AvsenderMottakerIdType.ORGNR
            },
            navn = avsenderMottaker.navn
        )

    private fun createSak(journalfoeringData: JournalfoeringData): Sak =
        Sak(
            sakstype = Sakstype.FAGSAK,
            fagsaksystem = FagsaksSystem.valueOf(journalfoeringData.sakFagsystem.name),
            fagsakid = journalfoeringData.sakFagsakId
        )

    private fun createBruker(journalfoeringData: JournalfoeringData): Bruker =
        Bruker(
            journalfoeringData.sakenGjelder.value,
            if (journalfoeringData.sakenGjelder.type == PartIdType.VIRKSOMHET) BrukerIdType.ORGNR else BrukerIdType.FNR
        )

    private fun createDokument(
        mellomlagretDokument: JournalfoeringService.MellomlagretDokument, journalfoeringData: JournalfoeringData
    ): Dokument =
        Dokument(
            tittel = mellomlagretDokument.title,
            brevkode = journalfoeringData.brevKode, //TODO: Har alle dokumentene samme brevkode?
            dokumentVarianter = listOf(
                DokumentVariant(
                    filnavn = mellomlagretDokument.title,
                    //Hardcode to 'PDF' for now. Had some issues with Vera (old) and Spring Boot (new).
                    //Might work in the future if we need it.
                    filtype = "PDF",
                    variantformat = "ARKIV",
                    fysiskDokument = Base64.getEncoder().encodeToString(mellomlagretDokument.content)
                )
            )
        )

    private fun createDokumentListFromHoveddokumentAndVedleggList(
        hoveddokument: JournalfoeringService.MellomlagretDokument,
        vedleggList: List<JournalfoeringService.MellomlagretDokument> = emptyList(),
        journalfoeringData: JournalfoeringData
    ): List<Dokument> {
        val documents = mutableListOf(createDokument(hoveddokument, journalfoeringData))
        documents.addAll(vedleggList.map { createDokument(it, journalfoeringData) })
        return documents
    }

    fun createUpdateDocumentTitleJournalpostInput(
        dokumentInfoId: String,
        title: String
    ): UpdateDocumentTitleJournalpostInput {
        return UpdateDocumentTitleJournalpostInput(
            dokumenter = listOf(
                UpdateDocumentTitleDokumentInput(
                    dokumentInfoId = dokumentInfoId,
                    tittel = title
                )
            )
        )
    }
}