package no.nav.klage.dokument.clients.joark

import no.nav.klage.dokument.domain.dokument.BrevMottaker
import no.nav.klage.dokument.domain.dokument.JournalfoeringData
import no.nav.klage.dokument.domain.dokument.MellomlagretDokument
import no.nav.klage.dokument.domain.dokument.OpplastetDokument
import no.nav.klage.dokument.util.PdfUtils
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import no.nav.klage.kodeverk.PartIdType
import org.springframework.stereotype.Service
import java.util.*

@Service
class JoarkMapper(
    private val pdfUtils: PdfUtils
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val securelogger = getSecureLogger()
    }

    fun createJournalpost(
        journalfoeringData: JournalfoeringData,
        opplastetDokument: OpplastetDokument,
        hovedDokument: MellomlagretDokument,
        vedleggDokumentList: List<MellomlagretDokument> = emptyList(),
        brevMottaker: BrevMottaker
    ): Journalpost =
        Journalpost(
            journalposttype = JournalpostType.UTGAAENDE,
            tema = journalfoeringData.tema,
            behandlingstema = journalfoeringData.behandlingstema,
            avsenderMottaker = createAvsenderMottager(brevMottaker),
            sak = createSak(journalfoeringData),
            tittel = journalfoeringData.tittel,
            journalfoerendeEnhet = journalfoeringData.enhet,
            eksternReferanseId = "${opplastetDokument.id}_${brevMottaker.id}",
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

    private fun createSak(journalfoeringData: JournalfoeringData): Sak =
        if (journalfoeringData.sakFagsakId == null || journalfoeringData.sakFagsystem == null) {
            Sak(Sakstype.GENERELL_SAK)
        } else {
            Sak(
                sakstype = Sakstype.FAGSAK,
                fagsaksystem = FagsaksSystem.valueOf(journalfoeringData.sakFagsystem.name),
                fagsakid = journalfoeringData.sakFagsakId
            )
        }


    private fun createBruker(journalfoeringData: JournalfoeringData): Bruker =
        Bruker(
            journalfoeringData.sakenGjelder.value,
            if (journalfoeringData.sakenGjelder.type == PartIdType.VIRKSOMHET) BrukerIdType.ORGNR else BrukerIdType.FNR
        )

    private fun createDokument(
        mellomlagretDokument: MellomlagretDokument, journalfoeringData: JournalfoeringData
    ): Dokument =
            Dokument(
                tittel = mellomlagretDokument.title, //TODO: Bruke navnet på dokumentet?
                brevkode = journalfoeringData.brevKode, //TODO: Har alle dokumentene samme brevkode?
                dokumentVarianter = listOf(
                    DokumentVariant(
                        filnavn = mellomlagretDokument.title,
                        filtype = if (pdfUtils.pdfByteArrayIsPdfa(mellomlagretDokument.content)) "PDFA" else "PDF",
                        variantformat = "ARKIV",
                        fysiskDokument = Base64.getEncoder().encodeToString(mellomlagretDokument.content)
                    )
                )
            )

    private fun createDokumentListFromHoveddokumentAndVedleggList(
        hoveddokument: MellomlagretDokument,
        vedleggList: List<MellomlagretDokument> = emptyList(),
        journalfoeringData: JournalfoeringData
    ): List<Dokument> {
        val documents = mutableListOf(createDokument(hoveddokument, journalfoeringData))
        documents.addAll(vedleggList.map { createDokument(it, journalfoeringData) })
        return documents
    }
}