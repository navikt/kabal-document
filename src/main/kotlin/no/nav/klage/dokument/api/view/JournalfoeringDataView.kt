package no.nav.klage.dokument.api.view

data class JournalfoeringDataView(
    val sakenGjelder: PartIdView,
    val tema: String,
    val sakFagsakId: String?,
    val sakFagsystem: String?,
    val kildeReferanse: String,
    val enhet: String,
)
