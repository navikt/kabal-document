package no.nav.klage.dokument.clients.klageunleashproxy

data class UnleashProxyRequest(
    val navIdent: String,
    val appName: String,
    val podName: String,
)