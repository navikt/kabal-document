package no.nav.klage.dokument.api.view

import java.time.LocalDateTime

data class OpplastetDokumentView(
    val id: String,
    val mellomlagerId: String,
    val opplastet: LocalDateTime,
    val size: Long,
    val name: String
)


