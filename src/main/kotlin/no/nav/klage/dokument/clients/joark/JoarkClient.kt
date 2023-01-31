package no.nav.klage.dokument.clients.joark

import no.nav.klage.dokument.util.TokenUtil
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class JoarkClient(
    private val joarkWebClient: WebClient,
    private val tokenUtil: TokenUtil,
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    fun createJournalpostInJoarkAsSystemUser(
        journalpost: Journalpost,
        journalfoerendeSaksbehandlerIdent: String,
    ): JournalpostResponse {
        val journalpostResponse = joarkWebClient.post()
            .uri("?forsoekFerdigstill=true")
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenUtil.getAppAccessTokenWithDokarkivScope()}")
            .header("Nav-User-Id", journalfoerendeSaksbehandlerIdent)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(journalpost)
            .retrieve()
            .bodyToMono(JournalpostResponse::class.java)
            .block()
            ?: throw RuntimeException("Journalpost could not be created.")

        logger.debug("Journalpost successfully created in Joark with id {}.", journalpostResponse.journalpostId)

        return journalpostResponse
    }

    fun finalizeJournalpostAsSystemUser(journalpostId: String, journalfoerendeEnhet: String) {
        joarkWebClient.patch()
            .uri("/${journalpostId}/ferdigstill")
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenUtil.getAppAccessTokenWithDokarkivScope()}")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(FerdigstillJournalpostPayload(journalfoerendeEnhet))
            .retrieve()

        logger.debug("Journalpost with id $journalpostId was succesfully finalized.")

    }

    fun updateDocumentTitleOnBehalfOf(journalpostId: String, input: UpdateDocumentTitleJournalpostInput) {
        joarkWebClient.put()
            .uri("/${journalpostId}")
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenUtil.getSaksbehandlerAccessTokenWithDokarkivkScope()}")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(input)
            .retrieve()

        logger.debug("Document from journalpost $journalpostId with dokumentInfoId id ${input.dokumenter.first().dokumentInfoId} was succesfully updated.")
    }
}