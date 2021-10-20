package no.nav.klage.dokument.domain.dokument

import no.nav.klage.dokument.domain.kodeverk.Rolle
import java.util.*

data class BrevMottaker(
    val id: UUID = UUID.randomUUID(),
    val partId: PartId,
    val navn: String?,
    val rolle: Rolle,
)