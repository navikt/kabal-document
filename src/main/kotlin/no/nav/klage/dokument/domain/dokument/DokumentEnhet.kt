package no.nav.klage.dokument.domain.dokument

import no.nav.klage.dokument.domain.saksbehandler.SaksbehandlerIdent
import java.time.LocalDateTime
import java.util.*

data class DokumentEnhet(
    val id: UUID = UUID.randomUUID(),
    val eier: SaksbehandlerIdent,
    val journalfoeringData: JournalfoeringData,
    val brevMottakere: List<BrevMottaker>,
    val hovedDokument: OpplastetDokument? = null,
    val vedlegg: List<OpplastetDokument> = emptyList(),

    val brevMottakerDistribusjoner: List<BrevMottakerDistribusjon> = emptyList(),
    val avsluttetAvSaksbehandler: LocalDateTime? = null,
    val avsluttet: LocalDateTime? = null,
    val modified: LocalDateTime = LocalDateTime.now()
) {
    fun erDistribuertTil(brevMottaker: BrevMottaker): Boolean =
        distribusjonAvBrevMottaker(brevMottaker)?.dokdistReferanse != null

    fun distribusjonAvBrevMottaker(brevMottaker: BrevMottaker): BrevMottakerDistribusjon? =
        brevMottakerDistribusjoner.find { it.brevMottakerId == brevMottaker.id }

    fun erDistribuertTilAlle(): Boolean {
        return true
    }
}