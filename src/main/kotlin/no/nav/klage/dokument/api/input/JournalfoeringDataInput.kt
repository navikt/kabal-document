package no.nav.klage.dokument.api.input

data class JournalfoeringDataInput(
    val sakenGjelder: PartIdInput,
    val tema: String,
    val sakFagsakId: String?,
    val sakFagsystem: String?,
    val kildeReferanse: String,
    val enhet: String,
)
