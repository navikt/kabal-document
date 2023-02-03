package no.nav.klage.dokument.api.input

data class UpdateTitleInput(
    val dokumentInfoId: String,
    val newTitle: String
)