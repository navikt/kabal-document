package no.nav.klage.dokument.clients.joark

data class FerdigstillJournalpostPayload(
    val journalfoerendeEnhet: String
)

data class TilknyttVedleggPayload(
    val dokument: List<VedleggReference>
) {
    data class VedleggReference(
        val kildeJournalpostId: String,
        val dokumentInfoId: String,
        val rekkefoelge: Int,
    )
}