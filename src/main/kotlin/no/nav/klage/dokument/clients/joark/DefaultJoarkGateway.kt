package no.nav.klage.dokument.clients.joark

import no.nav.klage.dokument.domain.dokument.BrevMottaker
import no.nav.klage.dokument.domain.dokument.JournalfoeringData
import no.nav.klage.dokument.domain.dokument.JournalpostId
import no.nav.klage.dokument.domain.dokument.MellomlagretDokument
import no.nav.klage.dokument.gateway.JoarkGateway
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
        document: MellomlagretDokument,
        brevMottaker: BrevMottaker
    ): JournalpostId =
        JournalpostId(
            joarkClient.createJournalpostInJoarkAsSystemUser(
                joarkMapper.createJournalpost(
                    journalfoeringData,
                    document,
                    brevMottaker
                )
            ).journalpostId
        )


    override fun cancelJournalpost(journalpostId: JournalpostId): String {
        return joarkClient.cancelJournalpost(journalpostId.value)
    }

    override fun finalizeJournalpostAsSystemUser(journalpostId: JournalpostId, journalfoerendeEnhet: String): String {
        //TODO: Hva returnerer dette kallet? Hva er den String'en?
        return joarkClient.finalizeJournalpostAsSystemUser(journalpostId.value, journalfoerendeEnhet)
    }
}