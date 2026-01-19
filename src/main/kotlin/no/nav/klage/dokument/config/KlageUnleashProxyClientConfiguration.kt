package no.nav.klage.dokument.config

import no.nav.klage.dokument.util.getLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class KlageUnleashProxyClientConfiguration(
    @Qualifier("fastLookupWebClientBuilder") private val fastLookupWebClientBuilder : WebClient.Builder
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @Value($$"${KLAGE_UNLEASH_PROXY_URL}")
    private lateinit var klageUnleashProxyURL: String

    @Bean
    fun fileWebClient(): WebClient {
        return fastLookupWebClientBuilder
            .baseUrl(klageUnleashProxyURL)
            .build()
    }
}