package no.nav.klage.dokument.api.input

import java.util.*

data class SmartEditorDokumentInput(
    val smartEditorId: UUID,
    val dokumentType: String,
    val eksternReferanse: String,
)