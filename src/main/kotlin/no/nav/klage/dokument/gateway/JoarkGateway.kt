package no.nav.klage.dokument.gateway

import no.nav.klage.dokument.domain.dokument.*

interface JoarkGateway {
    fun createJournalpostAsSystemUser(
        journalfoeringData: JournalfoeringData,
        opplastetDokument: OpplastetDokument,
        hoveddokument: MellomlagretDokument,
        vedleggDokumentList: List<MellomlagretDokument> = emptyList(),
        brevMottaker: BrevMottaker
    ): JournalpostIdOgDokumentInfo

    fun cancelJournalpost(journalpostId: JournalpostId): String
    fun finalizeJournalpostAsSystemUser(journalpostId: JournalpostId, journalfoerendeEnhet: String): String
}