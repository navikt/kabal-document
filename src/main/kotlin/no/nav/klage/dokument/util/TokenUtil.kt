package no.nav.klage.dokument.util

import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService
import no.nav.security.token.support.client.spring.ClientConfigurationProperties
import org.springframework.stereotype.Service

@Service
class TokenUtil(
    private val clientConfigurationProperties: ClientConfigurationProperties,
    private val oAuth2AccessTokenService: OAuth2AccessTokenService,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val securelogger = getSecureLogger()
    }

    fun getAppAccessTokenWithKabalFileApiScope(): String {
        val clientProperties = clientConfigurationProperties.registration["kabal-file-api-maskintilmaskin"]
        val response = oAuth2AccessTokenService.getAccessToken(clientProperties)
        return response.accessToken
    }

    fun getAppAccessTokenWithSafScope(): String {
        val clientProperties = clientConfigurationProperties.registration["saf-maskintilmaskin"]
        val response = oAuth2AccessTokenService.getAccessToken(clientProperties)
        return response.accessToken
    }

    fun getSaksbehandlerAccessTokenWithDokarkivkScope(): String {
        val clientProperties = clientConfigurationProperties.registration["dokarkiv-onbehalfof"]
        val response = oAuth2AccessTokenService.getAccessToken(clientProperties)
        return response.accessToken
    }

    fun getAppAccessTokenWithDokarkivScope(): String {
        val clientProperties = clientConfigurationProperties.registration["dokarkiv-maskintilmaskin"]
        val response = oAuth2AccessTokenService.getAccessToken(clientProperties)
        return response.accessToken
    }
}
