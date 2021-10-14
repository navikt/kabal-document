package no.nav.klage.dokument.api.view

data class BrevMottakerInput(
    val partId: PartIdInput,
    val navn: String,
    val rolle: String,
)
