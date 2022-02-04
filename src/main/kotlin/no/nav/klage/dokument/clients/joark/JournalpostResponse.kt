package no.nav.klage.dokument.clients.joark

data class JournalpostResponse(
    val journalpostId: String,
    val journalstatus: String,
    val melding: String?,
    val journalpostferdigstilt: Boolean,
    val dokumenter: List<DokumentInfo>
) {
    data class DokumentInfo(val dokumentInfoId: String)
}