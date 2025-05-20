package no.nav.klage.dokument.clients.saf.graphql


import no.nav.klage.dokument.util.TokenUtil
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import no.nav.klage.dokument.util.logErrorResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class SafGraphQlClient(
    private val safWebClient: WebClient,
    private val tokenUtil: TokenUtil,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    @Retryable
    fun getJournalpostAsSystembruker(
        journalpostId: String,
    ): Journalpost {
        return getJournalpostWithToken(
            journalpostId = journalpostId,
            token = tokenUtil.getAppAccessTokenWithSafScope()
        )
    }

    private fun getJournalpostWithToken(
        journalpostId: String,
        token: String,
    ): Journalpost {
        return safWebClient.post()
            .uri("graphql")
            .header(
                HttpHeaders.AUTHORIZATION,
                "Bearer $token"
            )
            .bodyValue(hentJournalpostQuery(journalpostId))
            .retrieve()
            .onStatus(HttpStatusCode::isError) { response ->
                logErrorResponse(response, "getJournalpost", secureLogger)
            }
            .bodyToMono<JournalpostResponse>()
            .block()?.data?.journalpost ?: throw RuntimeException("Got null from SAF for journalpost with id $journalpostId")
    }
}