package no.nav.klage.dokument.clients.joark

import no.nav.klage.dokument.util.TokenUtil
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

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
            .uri("?forsoekFerdigstill=false")
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
            .bodyToMono<String>()
            .block()
            ?: throw RuntimeException("Journalpost could not be finalized.")

        logger.debug("Journalpost with id $journalpostId was succesfully finalized.")
    }

    fun tilknyttVedleggAsSystemUser(journalpostId: String, input: TilknyttVedleggPayload): FeiledeDokumenter? {
        joarkWebClient.put()
            .uri("/${journalpostId}/tilknyttVedlegg")
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenUtil.getAppAccessTokenWithDokarkivScope()}")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(input)
            .retrieve()
            .bodyToMono<FeiledeDokumenter>()
            .block()
            ?: throw RuntimeException("Could not tilknytt vedlegg.")

        logger.debug("tilknyttVedleggAsSystemUser to journalpost with id $journalpostId was successful.")
    }

    fun updateDocumentTitleOnBehalfOf(journalpostId: String, input: UpdateDocumentTitleJournalpostInput) {
        try {
            joarkWebClient.put()
                .uri("/${journalpostId}")
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer ${tokenUtil.getSaksbehandlerAccessTokenWithDokarkivScope()}"
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(input)
                .retrieve()
                .bodyToMono(JournalpostResponse::class.java)
                .block()
                ?: throw RuntimeException("Journalpost could not be updated.")
        } catch (e: Exception) {
            logger.error("Error updating journalpost $journalpostId:", e)
        }

        logger.debug("Document from journalpost $journalpostId with dokumentInfoId id ${input.dokumenter.first().dokumentInfoId} was succesfully updated.")
    }
}