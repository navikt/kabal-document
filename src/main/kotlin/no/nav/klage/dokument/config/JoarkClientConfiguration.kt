package no.nav.klage.dokument.config

import no.nav.klage.dokument.util.getLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient


@Configuration
class JoarkClientConfiguration(
    @Qualifier("dokarkivLargeFileWebClientBuilder") private val dokarkivLargeFileWebClientBuilder: WebClient.Builder,
    @Qualifier("dokarkivSmallFileWebClientBuilder") private val dokarkivSmallFileWebClientBuilder: WebClient.Builder,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @Value("\${JOARK_SERVICE_URL}")
    private lateinit var joarkServiceURL: String

    /**
     * WebClient for large file uploads (220s timeout).
     * Use when file size exceeds LARGE_FILE_THRESHOLD_BYTES.
     */
    @Bean
    fun joarkLargeFileWebClient(): WebClient {
        return dokarkivLargeFileWebClientBuilder
            .baseUrl(joarkServiceURL)
            .build()
    }

    /**
     * WebClient for small file uploads (15s timeout).
     * Provides faster failure detection for normal-sized files.
     */
    @Bean
    fun joarkSmallFileWebClient(): WebClient {
        return dokarkivSmallFileWebClientBuilder
            .baseUrl(joarkServiceURL)
            .build()
    }
}