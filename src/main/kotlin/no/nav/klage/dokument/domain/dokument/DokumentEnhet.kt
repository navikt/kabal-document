package no.nav.klage.dokument.domain.dokument

import no.nav.klage.dokument.domain.kodeverk.Rolle
import no.nav.klage.dokument.domain.saksbehandler.SaksbehandlerIdent
import no.nav.klage.dokument.exceptions.DokumentEnhetNotValidException
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
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

    fun getJournalpostIdHovedadressat(): String? =
        brevMottakere.find { it.rolle == Rolle.HOVEDADRESSAT }
            ?.let { findBrevMottakerDistribusjon(it)?.journalpostId?.value }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DokumentEnhet

        if (id != other.id) return false
        if (eier != other.eier) return false
        if (journalfoeringData != other.journalfoeringData) return false
        if (brevMottakere != other.brevMottakere) return false
        if (hovedDokument != other.hovedDokument) return false
        if (vedlegg != other.vedlegg) return false
        if (brevMottakerDistribusjoner != other.brevMottakerDistribusjoner) return false
        if (avsluttet?.truncatedTo(ChronoUnit.MILLIS) != other.avsluttet?.truncatedTo(ChronoUnit.MILLIS)) return false
        if (modified.truncatedTo(ChronoUnit.MILLIS) != other.modified.truncatedTo(ChronoUnit.MILLIS)) return false

        return true
    }

    override fun hashCode(): Int = id.hashCode()
}