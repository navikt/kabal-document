package no.nav.klage.dokument.clients.joark

import org.springframework.http.HttpStatus
import org.springframework.resilience.retry.MethodRetryPredicate
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.lang.reflect.Method

class SkipRetryOnContentTooLargePredicate : MethodRetryPredicate {
    override fun shouldRetry(
        method: Method,
        throwable: Throwable,
    ): Boolean = !(throwable is WebClientResponseException && throwable.statusCode == HttpStatus.CONTENT_TOO_LARGE)
}
