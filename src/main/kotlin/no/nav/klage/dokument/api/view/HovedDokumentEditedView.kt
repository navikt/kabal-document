package no.nav.klage.dokument.api.view

import java.time.LocalDateTime

data class HovedDokumentEditedView(
    val modified: LocalDateTime,
    val fileMetadata: OpplastetFilMetadataView?
)
