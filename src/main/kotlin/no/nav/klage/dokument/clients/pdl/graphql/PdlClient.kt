package no.nav.klage.dokument.clients.pdl.graphql

import no.nav.klage.dokument.util.TokenUtil
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.logErrorResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.resilience.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.lang.System.currentTimeMillis


@Component
class PdlClient(
    private val pdlWebClient: WebClient,
    private val tokenUtil: TokenUtil
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @Retryable
    fun getPersonInfo(ident: String): HentPersonResponse {
        return runWithTiming {
            val pdlSystembrukerToken = tokenUtil.getAppAccessTokenWithPdlScope()
            pdlWebClient.post()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $pdlSystembrukerToken")
                .bodyValue(hentPersonQuery(ident))
                .retrieve()
                .onStatus(HttpStatusCode::isError) { response ->
                    logErrorResponse(
                        response = response,
                        functionName = ::getPersonInfo.name,
                        classLogger = logger,
                    )
                }
                .bodyToMono<HentPersonResponse>()
                .block() ?: throw RuntimeException("Person not found")
        }
    }

    fun <T> runWithTiming(block: () -> T): T {
        val start = currentTimeMillis()
        try {
            return block.invoke()
        } finally {
            val end = currentTimeMillis()
            logger.debug("Time it took to call pdl: ${end - start} millis")
        }
    }
}