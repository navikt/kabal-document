package no.nav.klage.dokument.api.view

import no.nav.klage.dokument.domain.dokument.JournalpostId
import no.nav.klage.dokument.domain.dokument.PartId
import no.nav.klage.dokument.domain.kodeverk.Rolle
import java.util.*

data class DokumentEnhetFullfoertView(
    val brevMottakerWithJoarkAndDokDistInfoList: List<BrevMottakerWithJoarkAndDokDistInfo>
)

data class BrevMottakerWithJoarkAndDokDistInfo(
    val partId: PartId,
    val navn: String?,
    val rolle: Rolle,
    val journalpostId: JournalpostId,
    val dokdistReferanse: UUID?
)
