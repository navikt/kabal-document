package no.nav.klage.dokument.domain.dokument

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

data class BrevMottakerDistribusjon(
    val id: UUID = UUID.randomUUID(),
    val brevMottakerId: UUID,
    val opplastetDokumentId: UUID,
    val journalpostId: JournalpostId,
    val ferdigstiltIJoark: LocalDateTime? = null,
    val dokdistReferanse: UUID? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BrevMottakerDistribusjon

        if (id != other.id) return false
        if (brevMottakerId != other.brevMottakerId) return false
        if (opplastetDokumentId != other.opplastetDokumentId) return false
        if (journalpostId != other.journalpostId) return false
        if (ferdigstiltIJoark?.truncatedTo(ChronoUnit.MILLIS) != other.ferdigstiltIJoark?.truncatedTo(ChronoUnit.MILLIS)) return false
        if (dokdistReferanse != other.dokdistReferanse) return false

        return true
    }

    override fun hashCode(): Int = id.hashCode()
}