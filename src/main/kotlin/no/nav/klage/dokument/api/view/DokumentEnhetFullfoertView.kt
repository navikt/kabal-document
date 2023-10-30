package no.nav.klage.dokument.api.view

import java.util.*

data class DokumentEnhetFullfoertView(
    //Remove after kabal-api starts using journalpostIdList.
    val brevMottakerWithJoarkAndDokDistInfoList: List<BrevMottakerWithJoarkAndDokDistInfo>,
    val journalpostIdList: List<String>,
    val dokumentUnderArbeidWithJoarkReferencesList: List<DokumentUnderArbeidWithJoarkReferences>,
)

data class BrevMottakerWithJoarkAndDokDistInfo(
    val journalpostId: JournalpostId,
)

data class JournalpostId(val value: String)

data class DokumentUnderArbeidWithJoarkReferences(
    val dokumentUnderArbeidReferanse: UUID?,
    val joarkReferenceList: List<JoarkReference>
)

data class JoarkReference(
    val journalpostId: String,
    val dokumentInfoId: String,
)