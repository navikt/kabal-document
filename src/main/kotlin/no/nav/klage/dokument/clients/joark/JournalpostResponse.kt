package no.nav.klage.dokument.clients.joark

data class JournalpostResponse(
    val journalpostId: String,
    val journalpostferdigstilt: Boolean,
    val dokumenter: List<DokumentInfoId>,
)

data class DokumentInfoId(
    val dokumentInfoId: String,
)

data class FeiledeDokumenter(
    val arsakKode: ArsakKode,
    val dokumentInfoId: String,
    val kildeJournalpostId: String
) {

    /**
     * Årsak til at dokumentet ikke lot seg knytte til journalpostId
     */
    enum class ArsakKode {
        UGYLDIG_STATUS,
        IKKE_FUNNET,
        DOKUMENT_TILLATES_IKKE_GJENBRUKT,
        SIKKERHETSBEGRENSNING,
        TILKNYTNING_FEILET;
    }

}