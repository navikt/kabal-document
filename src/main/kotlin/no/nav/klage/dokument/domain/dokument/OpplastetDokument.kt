package no.nav.klage.dokument.domain.dokument

import java.time.LocalDateTime
import java.util.*

data class OpplastetDokument(
    val opplastetDokumentId: UUID = UUID.randomUUID(),
    val mellomlagerId: String,
    val opplastet: LocalDateTime,
    val size: Long,
    val name: String
)