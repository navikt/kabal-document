package no.nav.klage.dokument.clients.joark

import no.nav.klage.dokument.domain.dokument.BrevMottaker
import no.nav.klage.dokument.domain.dokument.JournalfoeringData
import no.nav.klage.dokument.domain.dokument.JournalfoertVedlegg
import no.nav.klage.dokument.domain.dokument.OpplastetHoveddokument
import no.nav.klage.dokument.gateway.JoarkGateway
import no.nav.klage.dokument.service.JournalfoeringService
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import org.springframework.stereotype.Component

@Component
class DefaultJoarkGateway(
    private val joarkClient: JoarkClient,
    private val joarkMapper: JoarkMapper
) : JoarkGateway {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val securelogger = getSecureLogger()
    }

    override fun createJournalpostAsSystemUser(
        journalfoeringData: JournalfoeringData,
        opplastetHovedDokument: OpplastetHoveddokument,
        hoveddokument: JournalfoeringService.MellomlagretDokument,
        vedleggDokumentList: List<JournalfoeringService.MellomlagretDokument>,
        brevMottaker: BrevMottaker,
        journalfoerendeSaksbehandlerIdent: String,
    ): JournalpostResponse =
        joarkClient.createJournalpostInJoarkAsSystemUser(
            journalpost = joarkMapper.createJournalpost(
                journalfoeringData = journalfoeringData,
                opplastetHovedDokument = opplastetHovedDokument,
                hovedDokument = hoveddokument,
                vedleggDokumentList = vedleggDokumentList,
                brevMottaker = brevMottaker
            ),
            journalfoerendeSaksbehandlerIdent = journalfoerendeSaksbehandlerIdent
        )


    override fun finalizeJournalpostAsSystemUser(journalpostId: String, journalfoerendeEnhet: String) {
        joarkClient.finalizeJournalpostAsSystemUser(journalpostId, journalfoerendeEnhet)
    }

    override fun tilknyttVedleggAsSystemUser(journalpostId: String, journalfoerteVedlegg: List<JournalfoertVedlegg>): FeiledeDokumenter? {
        return joarkClient.tilknyttVedleggAsSystemUser(
            journalpostId = journalpostId,
            input = TilknyttVedleggPayload(
                dokument = journalfoerteVedlegg.map {
                    TilknyttVedleggPayload.VedleggReference(
                        kildeJournalpostId = it.kildeJournalpostId,
                        dokumentInfoId = it.dokumentInfoId
                    )
                }
            )
        )
    }

    override fun updateDocumentTitleOnBehalfOf(journalpostId: String, dokumentInfoId: String, newTitle: String) {
        joarkClient.updateDocumentTitleOnBehalfOf(
            journalpostId = journalpostId,
            input = joarkMapper.createUpdateDocumentTitleJournalpostInput(
                dokumentInfoId = dokumentInfoId, title = newTitle
            )
        )
    }
}