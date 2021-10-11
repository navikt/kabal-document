package no.nav.klage.dokument.domain.dokument

import java.time.LocalDateTime
import java.util.*

data class BrevMottakerDistribusjon(
    val id: UUID = UUID.randomUUID(),
    val brevMottakerId: UUID,
    //val opplastetDokumentId: UUID,
    val journalpostId: JournalpostId,
    val ferdigstiltIJoark: LocalDateTime? = null,
    val dokdistReferanse: UUID? = null
)