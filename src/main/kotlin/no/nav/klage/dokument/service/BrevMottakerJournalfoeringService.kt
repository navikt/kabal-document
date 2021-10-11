package no.nav.klage.dokument.service

import no.nav.klage.dokument.clients.joark.DefaultJoarkGateway
import no.nav.klage.dokument.clients.saf.graphql.Journalstatus
import no.nav.klage.dokument.clients.saf.graphql.SafGraphQlClient
import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.dokument.exceptions.DokumentEnhetNotFoundException
import no.nav.klage.dokument.exceptions.JournalpostFinalizationException
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class BrevMottakerJournalfoeringService(
    private val mellomlagerService: MellomlagerService,
    private val joarkGateway: DefaultJoarkGateway,
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
    ): JournalpostId {
        val documentInStorage = mellomlagerService.getUploadedDocumentAsSystemUser(opplastetDokument.mellomlagerId)
        return joarkGateway.createJournalpostAsSystemUser(
            journalfoeringData,
            documentInStorage,
            brevMottaker
        )
    }

    fun ferdigstillJournalpostForBrevMottaker(brevMottakerDistribusjon: BrevMottakerDistribusjon): BrevMottakerDistribusjon =
        try {
            //TODO Innføre Gateway
            val journalpost = safClient.getJournalpostAsSystembruker(brevMottakerDistribusjon.journalpostId.value)
                ?: throw DokumentEnhetNotFoundException("Journalpost med id ${brevMottakerDistribusjon.journalpostId.value} finnes ikke")
            if (journalpost.journalstatus != Journalstatus.FERDIGSTILT) { //TODO: Kan vi istedet sjekke brevMottakerDistribusjon.ferdigstiltIJoark ?
                joarkGateway.finalizeJournalpostAsSystemUser(
                    brevMottakerDistribusjon.journalpostId,
                    SYSTEM_JOURNALFOERENDE_ENHET
                )
                brevMottakerDistribusjon.copy(ferdigstiltIJoark = LocalDateTime.now())
            } else {
                brevMottakerDistribusjon.copy(ferdigstiltIJoark = LocalDateTime.now()) //TODO Kan denne datoen hentes fra saf?
            }
        } catch (e: Exception) {
            logger.warn("Kunne ikke ferdigstille journalpost ${brevMottakerDistribusjon.journalpostId.value}", e)
            throw JournalpostFinalizationException("Klarte ikke å ferdigstille journalpost")
        }
}