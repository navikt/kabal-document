package no.nav.klage.dokument.api.view

import java.util.*

data class DokumentEnhetFullfoertView(
    val sourceReferenceWithJoarkReferencesList: List<SourceReferenceWithJoarkReferences>,
)

data class SourceReferenceWithJoarkReferences(
    val sourceReference: UUID?,
    val joarkReferenceList: List<JoarkReference>
)

data class JoarkReference(
    val journalpostId: String,
    val dokumentInfoId: String,
)