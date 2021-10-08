package no.nav.klage.dokument.config

import no.nav.klage.dokument.util.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient


@Configuration
class DokDistFordelingClientConfiguration(
    private val webClientBuilder: WebClient.Builder
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @Value("\${DOKDIST_SERVICE_URL}")
    private lateinit var dokDistServiceURL: String

    @Value("\${DOKDIST_APIKEY}")
    private lateinit var apiKey: String

    @Bean
    fun dokDistWebClient(): WebClient {
        return webClientBuilder
            .defaultHeader("x-nav-apiKey", apiKey)
            .baseUrl(dokDistServiceURL)
            .build()
    }
}