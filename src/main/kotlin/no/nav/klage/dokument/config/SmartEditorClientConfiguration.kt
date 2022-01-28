package no.nav.klage.dokument.config

import no.nav.klage.dokument.util.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class SmartEditorClientConfiguration(
    private val webClientBuilder: WebClient.Builder
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @Value("\${KABAL_SMART_EDITOR_API_BASE_URL}")
    private lateinit var smartEditorApiURL: String

    @Bean
    fun smartEditorWebClient(): WebClient {
        return webClientBuilder
            .baseUrl(smartEditorApiURL)
            .build()
    }
}