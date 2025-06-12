package no.nav.klage.dokument.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class PdlClientConfiguration(private val webClientBuilder: WebClient.Builder) {

    @Value("\${PDL_BASE_URL}")
    private lateinit var pdlUrl: String

    @Bean
    fun pdlWebClient(): WebClient {
        return webClientBuilder
            .baseUrl(pdlUrl)
            .defaultHeader("TEMA", "KLA")
            //Fra behandlingskatalogen
            .defaultHeader("behandlingsnummer", "B392")
            .build()
    }
}
