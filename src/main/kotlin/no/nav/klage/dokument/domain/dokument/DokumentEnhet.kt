package no.nav.klage.dokument.domain.dokument

import no.nav.klage.dokument.domain.saksbehandler.SaksbehandlerIdent
import java.time.LocalDateTime
import java.util.*

data class DokumentEnhet(
    val id: UUID = UUID.randomUUID(),
    val eier: SaksbehandlerIdent,
    val journalfoeringData: JournalfoeringData,
    val brevMottakere: List<BrevMottaker> = emptyList(),
    val hovedDokument: OpplastetDokument? = null,
    val vedlegg: List<OpplastetDokument> = emptyList(),
    val avsluttetAvSaksbehandler: LocalDateTime? = null,
    val avsluttet: LocalDateTime? = null,
    val modified: LocalDateTime = LocalDateTime.now()
)