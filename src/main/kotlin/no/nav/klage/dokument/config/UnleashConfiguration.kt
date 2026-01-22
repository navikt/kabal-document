package no.nav.klage.dokument.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.web.context.WebApplicationContext

@Configuration
class UnleashConfiguration(
    @Value($$"${NAIS_POD_NAME}")
    private val naisPodName: String,
    @Value($$"${NAIS_APP_NAME}")
    private val naisAppName: String
) {

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    fun klageUnleashProxyContextProvider(currentSaksbehandlerHolder: CurrentSaksbehandlerHolder): KlageUnleashProxyContext {
        return KlageUnleashProxyContext(
            navIdent = currentSaksbehandlerHolder.navIdent,
            appName = naisAppName,
            podName = naisPodName,
        )
    }
}

open class KlageUnleashProxyContext(
    open val navIdent: String?,
    open val appName: String,
    open val podName: String,
)