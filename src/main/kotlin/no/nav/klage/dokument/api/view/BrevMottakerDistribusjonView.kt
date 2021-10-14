package no.nav.klage.dokument.api.view

import java.time.LocalDateTime

data class BrevMottakerDistribusjonView(
    val brevMottakerId: String,
    val opplastetDokumentId: String,
    val journalpostId: String,
    val ferdigstiltIJoark: LocalDateTime?,
    val dokdistReferanse: String?
)
