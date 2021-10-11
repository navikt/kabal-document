package no.nav.klage.dokument.gateway

import no.nav.klage.dokument.domain.dokument.BrevMottaker
import no.nav.klage.dokument.domain.dokument.JournalfoeringData
import no.nav.klage.dokument.domain.dokument.JournalpostId
import no.nav.klage.dokument.domain.dokument.MellomlagretDokument

interface JoarkGateway {
    fun createJournalpostAsSystemUser(
        journalfoeringData: JournalfoeringData,
        document: MellomlagretDokument,
        brevMottaker: BrevMottaker
    ): JournalpostId

    fun cancelJournalpost(journalpostId: JournalpostId): String
    fun finalizeJournalpostAsSystemUser(journalpostId: JournalpostId, journalfoerendeEnhet: String): String
}