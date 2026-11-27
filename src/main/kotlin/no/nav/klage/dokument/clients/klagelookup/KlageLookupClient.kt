package no.nav.klage.dokument.clients.klagelookup

import no.nav.klage.dokument.util.TokenUtil
import no.nav.klage.dokument.util.getLogger
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.resilience.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Component
class KlageLookupClient(
    private val klageLookupWebClient: WebClient,
    private val tokenUtil: TokenUtil,
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @Retryable
    fun getPerson(fnr: String): PersonResponse {
        val start = System.currentTimeMillis()
        try {
            return klageLookupWebClient
                .post()
                .uri("/person")
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenUtil.getAppAccessTokenWithKlageLookupScope()}")
                .bodyValue(GetPersonRequest(fnr = fnr))
                .retrieve()
                .onStatus(HttpStatusCode::isError) { response ->
                    logger.warn("klage-lookup person lookup failed with HTTP {}", response.statusCode().value())
                    response.releaseBody().then(Mono.error(RuntimeException("Person lookup failed")))
                }.bodyToMono<PersonResponse>()
                .block() ?: throw RuntimeException("Person lookup returned an empty response")
        } catch (_: Exception) {
            // Decode errors and upstream error bodies can contain personal data.
            throw RuntimeException("Could not get person from klage-lookup.")
        } finally {
            logger.debug("Time it took to call klage-lookup: {} millis", System.currentTimeMillis() - start)
        }
    }
}
