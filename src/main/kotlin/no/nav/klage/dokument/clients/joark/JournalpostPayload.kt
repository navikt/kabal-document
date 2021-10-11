package no.nav.klage.dokument.clients.joark

data class FerdigstillJournalpostPayload(
    val journalfoerendeEnhet: String
)

data class AvbrytJournalpostPayload(
    val journalpostId: String
)