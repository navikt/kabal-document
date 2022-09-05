package no.nav.klage.dokument.config

import no.nav.klage.dokument.api.controller.DokumentEnhetController
import org.springdoc.core.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun apiInternal(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("internal")
            .packagesToScan(DokumentEnhetController::class.java.packageName)
            .build()
    }
}
