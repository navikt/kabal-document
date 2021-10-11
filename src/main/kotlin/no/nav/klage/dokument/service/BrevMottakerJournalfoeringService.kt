package no.nav.klage.dokument.service

import no.nav.klage.dokument.clients.saf.graphql.Journalstatus
import no.nav.klage.dokument.clients.saf.graphql.SafGraphQlClient
import no.nav.klage.dokument.domain.dokument.BrevMottaker
import no.nav.klage.dokument.domain.dokument.JournalfoeringData
import no.nav.klage.dokument.domain.dokument.OpplastetDokument
import no.nav.klage.dokument.exceptions.DokumentEnhetNotFoundException
import no.nav.klage.dokument.exceptions.JournalpostFinalizationException
import no.nav.klage.dokument.gateway.JoarkGateway
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class BrevMottakerJournalfoeringService(
    private val mellomlagerService: MellomlagerService,
    private val joarkGateway: JoarkGateway,
    private val safClient: SafGraphQlClient,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
        const val SYSTEMBRUKER = "SYSTEMBRUKER" //TODO ??
        const val SYSTEM_JOURNALFOERENDE_ENHET = "9999"
    }

    fun opprettJournalpostForBrevMottaker(
        brevMottaker: BrevMottaker,
        opplastetDokument: OpplastetDokument,
        journalfoeringData: JournalfoeringData
    ): BrevMottaker {
        val documentInStorage = mellomlagerService.getUploadedDocumentAsSystemUser(opplastetDokument.mellomlagerId)
        if (brevMottaker.journalpostId == null) {
            val journalpostId = joarkGateway.createJournalpostAsSystemUser(
                journalfoeringData,
                documentInStorage,
                brevMottaker
            )

            return brevMottaker.copy(journalpostId = journalpostId)
        }
        return brevMottaker
    }

    fun ferdigstillJournalpostForBrevMottaker(brevMottaker: BrevMottaker): BrevMottaker {

        try {
            val journalpost = safClient.getJournalpostAsSystembruker(brevMottaker.journalpostId!!)
                ?: throw DokumentEnhetNotFoundException("Journalpost med id ${brevMottaker.journalpostId} finnes ikke")
            if (journalpost.journalstatus != Journalstatus.FERDIGSTILT) {
                joarkGateway.finalizeJournalpostAsSystemUser(
                    brevMottaker.journalpostId,
                    SYSTEM_JOURNALFOERENDE_ENHET
                )
            }
        } catch (e: Exception) {
            logger.warn("Kunne ikke ferdigstille journalpost ${brevMottaker.journalpostId}", e)
            throw JournalpostFinalizationException("Klarte ikke å ferdigstille journalpost")
        }

        return brevMottaker.copy(ferdigstiltIJoark = LocalDateTime.now())
    }
}