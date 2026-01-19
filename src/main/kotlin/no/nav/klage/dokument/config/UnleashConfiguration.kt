package no.nav.klage.dokument.config

import io.getunleash.DefaultUnleash
import io.getunleash.Unleash
import io.getunleash.UnleashContext
import io.getunleash.UnleashContextProvider
import io.getunleash.util.UnleashConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.web.context.WebApplicationContext

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
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    fun unleashContextProvider(currentSaksbehandlerHolder: CurrentSaksbehandlerHolder): UnleashContextProvider {
        return UnleashContextProvider {
            val builder = UnleashContext.builder()
            currentSaksbehandlerHolder.navIdent?.let { builder.userId(it) }
            builder.build()
        }
    }

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    fun klageUnleashProxyContextProvider(currentSaksbehandlerHolder: CurrentSaksbehandlerHolder): KlageUnleashProxyContext {
        return KlageUnleashProxyContext(
            navIdent = currentSaksbehandlerHolder.navIdent,
            appName = naisAppName,
            podName = naisPodName,
        )
    }

    @Bean
    fun unleash(unleashContextProvider: UnleashContextProvider): Unleash {
        val config = UnleashConfig.builder()
            .appName(naisAppName)
            .instanceId(naisPodName)
            .unleashAPI("$unleashApiUrl/api")
            .apiKey(unleashApiKey)
            .environment(unleashApiEnv)
            .unleashContextProvider(unleashContextProvider)
            .build()
        return DefaultUnleash(config)
    }
}

open class KlageUnleashProxyContext(
    open val navIdent: String?,
    open val appName: String,
    open val podName: String,
)