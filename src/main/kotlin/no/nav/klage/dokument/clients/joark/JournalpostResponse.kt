package no.nav.klage.dokument.clients.joark

data class JournalpostResponse(
    val journalpostId: String,
    val journalpostferdigstilt: Boolean,
    val dokumenter: List<DokumentInfoId>,
)

data class DokumentInfoId(
    val dokumentInfoId: String,
)

data class TilknyttVedleggResponse(
    val feiledeDokumenter: List<FeiletDokument>
) {
    data class FeiletDokument(
        val arsakKode: ArsakKode,
        val dokumentInfoId: String,
        val kildeJournalpostId: String
    ) {
        enum class ArsakKode {
            UGYLDIG_STATUS,
            IKKE_FUNNET,
            DOKUMENT_TILLATES_IKKE_GJENBRUKT,
            SIKKERHETSBEGRENSNING,
            TILKNYTNING_FEILET;
        }
    }
}
