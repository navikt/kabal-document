package no.nav.klage.dokument.clients.saf.graphql

import brave.Tracer
import no.nav.klage.dokument.util.TokenUtil
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import org.springframework.http.HttpHeaders
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class SafGraphQlClient(
    private val safWebClient: WebClient,
    private val tokenUtil: TokenUtil,
    private val tracer: Tracer,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    @Retryable
    fun getJournalpostAsSystembruker(journalpostId: String): Journalpost? {
        return runWithTimingAndLogging {
            val token = tokenUtil.getAppAccessTokenWithSafScope()
            getJournalpostWithToken(journalpostId, token)
        }
    }

    private fun getJournalpostWithToken(journalpostId: String, token: String) = safWebClient.post()
        .uri("graphql")
        .header(
            HttpHeaders.AUTHORIZATION,
            "Bearer ${token}"
        )
        .bodyValue(hentJournalpostQuery(journalpostId))
        .retrieve()
        .bodyToMono<JournalpostResponse>()
        .block()
        ?.let { logErrorsFromSaf(it, journalpostId); it }
        ?.let { failOnErrors(it); it }
        ?.data?.journalpost

    private fun failOnErrors(response: JournalpostResponse) {
        if (response.data == null || response.errors != null && response.errors.map { it.extensions.classification }
                .contains("ValidationError")) {
            throw RuntimeException("getJournalpost failed")
        }
    }

    private fun logErrorsFromSaf(
        response: JournalpostResponse,
        journalpostId: String
    ) {
        if (response.errors != null) {
            logger.error("Error from SAF, see securelogs")
            secureLogger.error("Error from SAF when making call with following parameters: journalpostId=$journalpostId. Error is ${response.errors}")
        }
    }

    fun <T> runWithTimingAndLogging(block: () -> T): T {
        val start = System.currentTimeMillis()
        try {
            return block.invoke().let { secureLogger.debug("Received response: $it"); it }
        } finally {
            val end = System.currentTimeMillis()
            logger.debug("Time it took to call saf: ${end - start} millis")
        }
    }
}