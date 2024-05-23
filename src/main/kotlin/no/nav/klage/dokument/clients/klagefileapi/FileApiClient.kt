package no.nav.klage.dokument.clients.klagefileapi

import no.nav.klage.dokument.util.TokenUtil
import no.nav.klage.dokument.util.getLogger
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class FileApiClient(
    private val fileWebClient: WebClient,
    private val tokenUtil: TokenUtil,
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    fun getDocument(id: String): ByteArray {
        logger.debug("Fetching document with id {}", id)

        val token = tokenUtil.getAppAccessTokenWithKabalFileApiScope()

        return this.fileWebClient.get()
            .uri { it.path("/document/{id}").build(id) }
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .bodyToMono<ByteArray>()
            .block() ?: throw RuntimeException("Document could not be fetched")
    }

    fun deleteDocument(id: String) {
        logger.debug("Deleting document with id {}", id)

        val token = tokenUtil.getAppAccessTokenWithKabalFileApiScope()

        val deletedInGCS = fileWebClient
            .delete()
            .uri("/document/$id")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .bodyToMono<Boolean>()
            .block()

        if (deletedInGCS == true) {
            logger.debug("Document successfully deleted in file store.")
        } else {
            logger.warn("Could not successfully delete document in file store.")
        }
    }
}