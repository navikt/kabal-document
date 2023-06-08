package no.nav.klage.dokument.api.input

import java.time.LocalDateTime

data class DokumentEnhetWithDokumentreferanserInput(
    val brevMottakere: List<BrevMottakerInput>,
    val journalfoeringData: JournalfoeringDataInput,
    val dokumentreferanser: DokumentInput,
    val dokumentTypeId: String,
    val journalfoerendeSaksbehandlerIdent: String,
) {
    data class DokumentInput(
        val hoveddokument: Dokument,
        val vedlegg: List<Dokument>?,
        val journalfoerteVedlegg: List<JournalfoertDokument>?,
    ) {
        data class Dokument(
            val mellomlagerId: String,
            val opplastet: LocalDateTime,
            val size: Long,
            val name: String,
        )

        data class JournalfoertDokument(
            val kildeJournalpostId: String,
            val dokumentInfoId: String,
        )
    }

    data class BrevMottakerInput(
        val partId: PartIdInput,
        val navn: String?,
    )

    data class JournalfoeringDataInput(
        val sakenGjelder: PartIdInput,
        val temaId: String,
        val sakFagsakId: String,
        val sakFagsystemId: String,
        val kildeReferanse: String,
        val enhet: String,
        val behandlingstema: String,
        val tittel: String,
        val brevKode: String,
        val tilleggsopplysning: TilleggsopplysningInput?
    ) {
        data class TilleggsopplysningInput(val key: String, val value: String)
    }

    data class PartIdInput(val type: String?, val partIdTypeId: String?, val value: String)
}