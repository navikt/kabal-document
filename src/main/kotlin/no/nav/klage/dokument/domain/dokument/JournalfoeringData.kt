package no.nav.klage.dokument.domain.dokument

import no.nav.klage.dokument.domain.kodeverk.Fagsystem
import no.nav.klage.dokument.domain.kodeverk.Tema

data class JournalfoeringData(
    val sakenGjelder: PartId,
    val tema: Tema,
    val sakFagsakId: String?,
    val sakFagsystem: Fagsystem?,
    val kildeReferanse: String,
    val enhet: String,
)

