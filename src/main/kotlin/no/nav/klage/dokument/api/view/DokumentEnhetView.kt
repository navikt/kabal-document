package no.nav.klage.dokument.api.view

import java.time.LocalDateTime

data class DokumentEnhetView(
    val id: String,
    val eier: String,
    val journalfoeringData: JournalfoeringDataView,
    val brevMottakere: List<BrevMottakerView>,
    val hovedDokument: OpplastetDokumentView?,
    val vedlegg: List<OpplastetDokumentView>,
    val brevMottakerDistribusjoner: List<BrevMottakerDistribusjonView>,
    val avsluttet: LocalDateTime?,
    val modified: LocalDateTime,
    val journalpostIdHovedadressat: String? = null
)
