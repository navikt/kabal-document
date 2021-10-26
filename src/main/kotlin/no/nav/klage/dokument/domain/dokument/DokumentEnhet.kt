package no.nav.klage.dokument.domain.dokument

import no.nav.klage.dokument.domain.kodeverk.Rolle
import no.nav.klage.dokument.domain.saksbehandler.SaksbehandlerIdent
import no.nav.klage.dokument.exceptions.DokumentEnhetNotValidException
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
    val avsluttet: LocalDateTime? = null,
    val modified: LocalDateTime = LocalDateTime.now()
) {

    fun erAvsluttet() = avsluttet != null

    fun erDistribuertTil(brevMottaker: BrevMottaker): Boolean =
        findBrevMottakerDistribusjon(brevMottaker)?.dokdistReferanse != null

    fun erDistribuertTilAlle(): Boolean =
        brevMottakere.all { erDistribuertTil(it) }

    fun findBrevMottakerDistribusjon(brevMottaker: BrevMottaker): BrevMottakerDistribusjon? =
        brevMottakerDistribusjoner.find { it.brevMottakerId == brevMottaker.id }

    fun validateDistribuertTilAlle(): DokumentEnhet =
        if (erDistribuertTilAlle()) {
            this
        } else throw DokumentEnhetNotValidException("DokumentEnhet ikke distribuert til alle brevmottakere")

    fun harHovedDokument(): Boolean = hovedDokument != null

    fun getJournalpostIdHovedadressat(): String =
        findBrevMottakerDistribusjon(brevMottakere.find { it.rolle == Rolle.HOVEDADRESSAT }!!)?.journalpostId?.value
            ?: throw RuntimeException("Could not find journalpostId for hovedadressat")
}