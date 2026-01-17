package no.nav.klage.dokument.config

import no.nav.klage.dokument.util.getLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class EregClientConfiguration(
    @Qualifier("fastLookupWebClientBuilder") private val fastLookupWebClientBuilder: WebClient.Builder
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @Value("\${EREG_URL}")
    private lateinit var eregServiceURL: String

    @Bean
    fun eregWebClient(): WebClient {
        return fastLookupWebClientBuilder
            .baseUrl(eregServiceURL)
            .build()
    }
}