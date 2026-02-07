package no.nav.klage.dokument.config

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration
import java.util.concurrent.TimeUnit

@Configuration
class WebClientConfig {

    companion object {
        // Timeouts for different service types
        const val LARGE_FILE_UPLOAD_TIMEOUT_SECONDS = 220L  // dokarkiv - large file uploads (supports 200s+ uploads)
        const val SMALL_FILE_UPLOAD_TIMEOUT_SECONDS = 25L   // dokarkiv - small file uploads (faster failure detection)
        const val FILE_API_TIMEOUT_SECONDS = 60L            // file-api - file operations
        const val STANDARD_TIMEOUT_SECONDS = 120L            // saf, dokdist
        const val FAST_LOOKUP_TIMEOUT_SECONDS = 10L         // pdl, ereg - quick lookups
        const val CONNECT_TIMEOUT_MILLIS = 5_000
    }

    /**
     * HttpClient for dokarkiv - supports large file uploads up to 200 seconds.
     * This is needed because document uploads with base64-encoded PDFs can be large.
     */
    @Bean
    fun dokarkivLargeFileHttpClient(): HttpClient {
        return createHttpClient(LARGE_FILE_UPLOAD_TIMEOUT_SECONDS)
    }

    /**
     * HttpClient for dokarkiv - small file uploads with 25 second timeout.
     * Used for faster failure detection when files are small.
     */
    @Bean
    fun dokarkivSmallFileHttpClient(): HttpClient {
        return createHttpClient(SMALL_FILE_UPLOAD_TIMEOUT_SECONDS)
    }

    /**
     * HttpClient for standard operations (saf, dokdist) - 30 second timeout.
     */
    @Bean
    fun standardHttpClient(): HttpClient {
        return createHttpClient(STANDARD_TIMEOUT_SECONDS)
    }

    /**
     * HttpClient for file-api operations - 60 second timeout.
     */
    @Bean
    fun fileApiHttpClient(): HttpClient {
        return createHttpClient(FILE_API_TIMEOUT_SECONDS)
    }

    /**
     * HttpClient for fast lookups (pdl, ereg) - 10 second timeout.
     */
    @Bean
    fun fastLookupHttpClient(): HttpClient {
        return createHttpClient(FAST_LOOKUP_TIMEOUT_SECONDS)
    }

    private fun createHttpClient(timeoutInSeconds: Long): HttpClient {
        return HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_MILLIS)
            .responseTimeout(Duration.ofSeconds(timeoutInSeconds))
            .doOnConnected { conn ->
                conn.addHandlerLast(ReadTimeoutHandler(timeoutInSeconds, TimeUnit.SECONDS))
                conn.addHandlerLast(WriteTimeoutHandler(timeoutInSeconds, TimeUnit.SECONDS))
            }
    }

    @Bean
    fun dokarkivLargeFileWebClientBuilder(dokarkivLargeFileHttpClient: HttpClient): WebClient.Builder {
        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(dokarkivLargeFileHttpClient))
    }

    @Bean
    fun dokarkivSmallFileWebClientBuilder(dokarkivSmallFileHttpClient: HttpClient): WebClient.Builder {
        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(dokarkivSmallFileHttpClient))
    }

    @Bean
    fun standardWebClientBuilder(standardHttpClient: HttpClient): WebClient.Builder {
        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(standardHttpClient))
    }

    @Bean
    fun fileApiWebClientBuilder(fileApiHttpClient: HttpClient): WebClient.Builder {
        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(fileApiHttpClient))
    }

    @Bean
    fun fastLookupWebClientBuilder(fastLookupHttpClient: HttpClient): WebClient.Builder {
        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(fastLookupHttpClient))
    }
}