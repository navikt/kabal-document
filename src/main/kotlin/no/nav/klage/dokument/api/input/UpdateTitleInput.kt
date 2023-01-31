package no.nav.klage.dokument.api.input

data class UpdateTitleInput(
    val journalpostId: String,
    val dokumentInfoId: String,
    val newTitle: String
)