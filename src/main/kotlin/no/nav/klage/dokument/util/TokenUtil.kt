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
    }

    fun getAppAccessTokenWithKabalFileApiScope(): String {
        val clientProperties = clientConfigurationProperties.registration["kabal-file-api-maskintilmaskin"]!!
        val response = oAuth2AccessTokenService.getAccessToken(clientProperties)
        return response.access_token!!
    }

    fun getAppAccessTokenWithSafScope(): String {
        val clientProperties = clientConfigurationProperties.registration["saf-maskintilmaskin"]!!
        val response = oAuth2AccessTokenService.getAccessToken(clientProperties)
        return response.access_token!!
    }

    fun getAppAccessTokenWithPdlScope(): String {
        val clientProperties = clientConfigurationProperties.registration["pdl-maskintilmaskin"]!!
        val response = oAuth2AccessTokenService.getAccessToken(clientProperties)
        return response.access_token!!
    }

    fun getSaksbehandlerAccessTokenWithDokarkivScope(): String {
        val clientProperties = clientConfigurationProperties.registration["dokarkiv-onbehalfof"]!!
        val response = oAuth2AccessTokenService.getAccessToken(clientProperties)
        return response.access_token!!
    }

    fun getAppAccessTokenWithDokarkivScope(): String {
        val clientProperties = clientConfigurationProperties.registration["dokarkiv-maskintilmaskin"]!!
        val response = oAuth2AccessTokenService.getAccessToken(clientProperties)
        return response.access_token!!
    }

    fun getAppAccessTokenWithDokdistScope(): String {
        val clientProperties = clientConfigurationProperties.registration["dokdist-maskintilmaskin"]!!
        val response = oAuth2AccessTokenService.getAccessToken(clientProperties)
        return response.access_token!!
    }

}
