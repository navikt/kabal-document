package no.nav.klage.dokument.domain.dokument

import java.util.*

data class BrevMottaker(
    val id: UUID = UUID.randomUUID(),
    val partId: PartId,
    val navn: String?,
)