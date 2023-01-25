package no.nav.klage.dokument.api.view

data class DokumentEnhetFullfoertView(
    //Remove after kabal-api starts using journalpostIdList.
    val brevMottakerWithJoarkAndDokDistInfoList: List<BrevMottakerWithJoarkAndDokDistInfo>,
    val journalpostIdList: List<String>
)

data class BrevMottakerWithJoarkAndDokDistInfo(
    val journalpostId: JournalpostId,
)

data class JournalpostId(val value: String)