package no.nav.klage.dokument.clients.klageunleashproxy

import no.nav.klage.dokument.config.KlageUnleashProxyContext
import no.nav.klage.dokument.util.TokenUtil
import no.nav.klage.dokument.util.getLogger
import org.springframework.resilience.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class KlageUnleashProxyClient(
    private val tokenUtil: TokenUtil,
    private val klageUnleashProxyContext: KlageUnleashProxyContext,
    private val klageUnleashProxyWebClient: WebClient,
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @Retryable
    fun isEnabled(feature: String): Boolean {
        if (klageUnleashProxyContext.navIdent == null) {
            logger.error("Cannot check feature toggle '$feature' without navIdent in KlageUnleashProxyContext. Returning false.")
            return false
        }

        val requestBody = UnleashProxyRequest(
            navIdent = klageUnleashProxyContext.navIdent!!,
            appName = klageUnleashProxyContext.appName,
            podName = klageUnleashProxyContext.podName,
        )

        return klageUnleashProxyWebClient.post()
            .uri("/features/${feature}")
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono<FeatureToggleResponse>()
            .block()?.enabled ?: false
    }
}

data class UnleashProxyRequest(
    val navIdent: String,
    val appName: String,
    val podName: String,
)