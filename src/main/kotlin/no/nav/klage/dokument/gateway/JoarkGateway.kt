package no.nav.klage.dokument.gateway

import no.nav.klage.dokument.domain.dokument.BrevMottaker
import no.nav.klage.dokument.domain.dokument.JournalfoeringData
import no.nav.klage.dokument.domain.dokument.OpplastetHoveddokument
import no.nav.klage.dokument.service.JournalfoeringService

interface JoarkGateway {

    fun finalizeJournalpostAsSystemUser(journalpostId: String, journalfoerendeEnhet: String)

    fun createJournalpostAsSystemUser(
        journalfoeringData: JournalfoeringData,
        opplastetHovedDokument: OpplastetHoveddokument,
        hoveddokument: JournalfoeringService.MellomlagretDokument,
        vedleggDokumentList: List<JournalfoeringService.MellomlagretDokument>,
        brevMottaker: BrevMottaker,
        journalfoerendeSaksbehandlerIdent: String,
    ): String

    fun updateDocumentTitleOnBehalfOf(journalpostId: String, dokumentInfoId: String, newTitle: String)
}