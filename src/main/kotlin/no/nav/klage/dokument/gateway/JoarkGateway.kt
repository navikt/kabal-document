package no.nav.klage.dokument.gateway

import no.nav.klage.dokument.clients.joark.JournalpostResponse
import no.nav.klage.dokument.clients.joark.TilknyttVedleggResponse
import no.nav.klage.dokument.domain.dokument.AvsenderMottaker
import no.nav.klage.dokument.domain.dokument.JournalfoeringData
import no.nav.klage.dokument.domain.dokument.JournalfoertVedlegg
import no.nav.klage.dokument.domain.dokument.OpplastetHoveddokument
import no.nav.klage.dokument.service.JournalfoeringService

interface JoarkGateway {

    fun finalizeJournalpostAsSystemUser(
        journalpostId: String,
        journalfoerendeEnhet: String
    )

    fun createJournalpostAsSystemUser(
        journalfoeringData: JournalfoeringData,
        opplastetHovedDokument: OpplastetHoveddokument,
        hoveddokument: JournalfoeringService.MellomlagretDokument,
        vedleggDokumentList: List<JournalfoeringService.MellomlagretDokument>,
        avsenderMottaker: AvsenderMottaker,
        journalfoerendeSaksbehandlerIdent: String,
    ): JournalpostResponse

    fun updateDocumentTitleOnBehalfOf(
        journalpostId: String,
        dokumentInfoId: String,
        newTitle: String
    )

    fun tilknyttVedleggAsSystemUser(journalpostId: String, journalfoerteVedlegg: List<JournalfoertVedlegg>): TilknyttVedleggResponse
}