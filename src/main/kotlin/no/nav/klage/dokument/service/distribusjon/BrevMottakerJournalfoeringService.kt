package no.nav.klage.dokument.service.distribusjon

import no.nav.klage.dokument.clients.saf.graphql.Journalstatus
import no.nav.klage.dokument.clients.saf.graphql.SafGraphQlClient
import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.dokument.exceptions.JournalpostNotFoundException
import no.nav.klage.dokument.service.JournalfoeringService
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BrevMottakerJournalfoeringService(
    private val safClient: SafGraphQlClient,
    private val journalfoeringService: JournalfoeringService,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    fun ferdigstillJournalpostForBrevMottaker(brevMottakerDistribusjon: BrevMottakerDistribusjon): BrevMottakerDistribusjon {
        //TODO Innføre Gateway
        val journalpost = safClient.getJournalpostAsSystembruker(brevMottakerDistribusjon.journalpostId.value)
            ?: throw JournalpostNotFoundException("Journalpost med id ${brevMottakerDistribusjon.journalpostId.value} finnes ikke")
        return if (journalpost.journalstatus != Journalstatus.FERDIGSTILT) { //TODO: Kan vi istedet sjekke brevMottakerDistribusjon.ferdigstiltIJoark ?
            journalfoeringService.finalizeJournalpostAsSystemUser(
                brevMottakerDistribusjon.journalpostId,
            )
            brevMottakerDistribusjon.copy(ferdigstiltIJoark = LocalDateTime.now())
        } else {
            brevMottakerDistribusjon.copy(ferdigstiltIJoark = LocalDateTime.now())//TODO Kan denne datoen hentes fra saf?
        }
    }
}

