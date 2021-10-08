package no.nav.klage.dokument.domain.dokument

import java.time.LocalDateTime

data class OpplastetDokument(
    val mellomlagerId: String,
    val opplastet: LocalDateTime,
    val size: Long,
    val name: String
)