package no.nav.klage.dokument.clients.joark

import brave.Tracer
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
    private val tracer: Tracer
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    fun createJournalpostInJoarkAsSystemUser(
        journalpost: Journalpost
    ): JournalpostResponse {
        val journalpostResponse = joarkWebClient.post()
            .uri("?forsoekFerdigstill=true")
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenUtil.getStsSystembrukerToken()}")
            .header("Nav-Call-Id", tracer.currentSpan().context().traceIdString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(journalpost)
            .retrieve()
            .bodyToMono(JournalpostResponse::class.java)
            .block()
            ?: throw RuntimeException("Journalpost could not be created.")

        logger.debug("Journalpost successfully created in Joark with id {}.", journalpostResponse.journalpostId)

        return journalpostResponse
    }

    fun cancelJournalpost(journalpostId: String): String {
        val response = joarkWebClient.patch()
            .uri("/${journalpostId}/feilregistrer/settStatusAvbryt")
            .header("Nav-Consumer-Token", "Bearer ${tokenUtil.getStsSystembrukerToken()}")
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenUtil.getSaksbehandlerAccessTokenWithJoarkScope()}")
            .header("Nav-Call-Id", tracer.currentSpan().context().traceIdString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(AvbrytJournalpostPayload(journalpostId))
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
            ?: throw RuntimeException("Journalpost with id $journalpostId could not be cancelled.")

        logger.debug("Journalpost with id $journalpostId was succesfully cancelled.")

        return response
    }


    fun finalizeJournalpostAsSystemUser(journalpostId: String, journalfoerendeEnhet: String): String {
        val response = joarkWebClient.patch()
            .uri("/${journalpostId}/ferdigstill")
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenUtil.getStsSystembrukerToken()}")
            .header("Nav-Call-Id", tracer.currentSpan().context().traceIdString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(FerdigstillJournalpostPayload(journalfoerendeEnhet))
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
            ?: throw RuntimeException("Journalpost with id $journalpostId could not be finalized.")

        logger.debug("Journalpost with id $journalpostId was succesfully finalized.")

        return response
    }
}