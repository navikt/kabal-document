package no.nav.klage.dokument.clients.joark

import no.nav.klage.dokument.domain.dokument.*
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
        brevMottaker: BrevMottaker
    ): String =
        joarkClient.createJournalpostInJoarkAsSystemUser(
            joarkMapper.createJournalpost(
                journalfoeringData = journalfoeringData,
                opplastetHovedDokument = opplastetHovedDokument,
                hovedDokument = hoveddokument,
                vedleggDokumentList = vedleggDokumentList,
                brevMottaker = brevMottaker
            )
        ).journalpostId


    override fun finalizeJournalpostAsSystemUser(journalpostId: String, journalfoerendeEnhet: String) {
        joarkClient.finalizeJournalpostAsSystemUser(journalpostId, journalfoerendeEnhet)
    }
}