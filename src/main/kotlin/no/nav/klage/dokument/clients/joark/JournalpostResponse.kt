package no.nav.klage.dokument.clients.joark

data class JournalpostResponse(
    val journalpostId: String,
    val melding: String,
    val journalpostferdigstilt: Boolean,
    val dokumenter: List<DokumentInfoId>,
)

data class DokumentInfoId(
    val dokumentInfoId: String,
)