package no.nav.klage.dokument.domain.dokument

import no.nav.klage.dokument.domain.kodeverk.PartIdType

data class PartId(
    val type: PartIdType,
    val value: String,
)
