package no.nav.klage.dokument.domain.joark

data class FerdigstillJournalpostPayload(
    val journalfoerendeEnhet: String
)

data class AvbrytJournalpostPayload(
    val journalpostId: String
)