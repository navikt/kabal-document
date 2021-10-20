package no.nav.klage.dokument.domain.dokument

import no.nav.klage.dokument.domain.kodeverk.Fagsystem
import no.nav.klage.dokument.domain.kodeverk.Tema
import java.util.*

data class JournalfoeringData(
    val id: UUID = UUID.randomUUID(),
    val sakenGjelder: PartId,
    val tema: Tema,
    val sakFagsakId: String?,
    val sakFagsystem: Fagsystem?,
    val kildeReferanse: String,
    val enhet: String,
    val behandlingstema: String,
    val tittel: String,
    val brevKode: String,
    val tilleggsopplysning: Tilleggsopplysning?
)

