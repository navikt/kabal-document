package no.nav.klage.dokument.api.input

import java.util.*

data class SmartEditorDokumentInput(
    val smartEditorId: UUID, //TODO: Skal denne være i inputen, eller skal kabal-document ordne det og returnere den i outputen??
    val dokumentType: String,
    val eksternReferanse: String,
)