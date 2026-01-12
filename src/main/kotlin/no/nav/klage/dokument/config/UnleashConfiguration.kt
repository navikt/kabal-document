package no.nav.klage.dokument.config

import io.getunleash.DefaultUnleash
import io.getunleash.Unleash
import io.getunleash.util.UnleashConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UnleashConfiguration(
    @Value($$"${UNLEASH_SERVER_API_URL}")
    private val unleashApiUrl: String,
    @Value($$"${UNLEASH_SERVER_API_TOKEN}")
    private val unleashApiKey: String,
    @Value($$"${UNLEASH_SERVER_API_ENV}")
    private val unleashApiEnv: String,
    @Value($$"${NAIS_POD_NAME}")
    private val naisPodName: String,
    @Value($$"${NAIS_APP_NAME}")
    private val naisAppName: String
) {

    @Bean
    fun unleash(): Unleash {
        val config = UnleashConfig.builder()
            .appName(naisAppName)
            .instanceId(naisPodName)
            .unleashAPI(unleashApiUrl)
            .apiKey(unleashApiKey)
            .environment(unleashApiEnv)
            .build()
        return DefaultUnleash(config)
    }
}