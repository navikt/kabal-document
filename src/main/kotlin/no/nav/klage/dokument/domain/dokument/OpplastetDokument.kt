package no.nav.klage.dokument.domain.dokument

import java.time.LocalDateTime
import java.util.*

data class OpplastetDokument(
    val id: UUID = UUID.randomUUID(),
    val mellomlagerId: String,
    val opplastet: LocalDateTime,
    val size: Long,
    val name: String,
    val smartEditorId: UUID? = null,
    val dokumentType: String?,
)