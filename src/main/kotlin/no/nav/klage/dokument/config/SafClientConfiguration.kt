package no.nav.klage.dokument.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import java.util.function.Consumer

@Configuration
class SafClientConfiguration(private val webClientBuilder: WebClient.Builder) {

    @Value("\${SAF_BASE_URL}")
    private lateinit var safUrl: String

    @Bean
    fun safWebClient(): WebClient {
        return webClientBuilder
            .baseUrl(safUrl)
            .exchangeStrategies(
                ExchangeStrategies
                    .builder()
                    .codecs(Consumer { codecs: ClientCodecConfigurer? ->
                        codecs!!
                            .defaultCodecs()
                            .maxInMemorySize(512 * 1024 * 1024)
                    })
                    .build()
            )
            .build()
    }
}
