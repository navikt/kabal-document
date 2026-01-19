package no.nav.klage.dokument.clients.klageunleashproxy

import no.nav.klage.dokument.config.KlageUnleashProxyContext
import no.nav.klage.dokument.util.TokenUtil
import no.nav.klage.dokument.util.getLogger
import org.springframework.http.HttpHeaders
import org.springframework.resilience.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class KlageUnleashProxyClient(
    private val fileWebClient: WebClient,
    private val tokenUtil: TokenUtil,
    private val klageUnleashProxyContext: KlageUnleashProxyContext,
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

        return fileWebClient.post()
            .uri("/features/${feature}")
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenUtil.getAppAccessTokenWithKlageUnleashProxyScope()}")
            .bodyValue(klageUnleashProxyContext)
            .retrieve()
            .bodyToMono<FeatureToggleResponse>()
            .block()?.enabled ?: false
    }
}