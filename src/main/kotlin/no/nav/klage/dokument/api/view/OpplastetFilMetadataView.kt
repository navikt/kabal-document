package no.nav.klage.dokument.api.view

import java.time.LocalDateTime

data class OpplastetFilMetadataView(
    val name: String,
    val size: Long,
    val opplastet: LocalDateTime
)