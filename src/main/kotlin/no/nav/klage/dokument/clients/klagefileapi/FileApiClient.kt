package no.nav.klage.dokument.clients.klagefileapi

import brave.Tracer
import no.nav.klage.dokument.util.TokenUtil
import no.nav.klage.dokument.util.getLogger
import org.springframework.http.HttpHeaders
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class FileApiClient(
    private val fileWebClient: WebClient,
    private val tokenUtil: TokenUtil,
    private val tracer: Tracer
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    fun getDocument(id: String, systemUser: Boolean = false): ByteArray {
        logger.debug("Fetching document with id {}", id)

        val token = if (systemUser) {
            tokenUtil.getAppAccessTokenWithKabalFileApiScope()
        } else {
            tokenUtil.getSaksbehandlerAccessTokenWithKabalFileApiScope()
        }

        return this.fileWebClient.get()
            .uri { it.path("/document/{id}").build(id) }
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${token}")
            .header("Nav-Call-Id", tracer.currentSpan().context().traceIdString())
            .retrieve()
            .bodyToMono<ByteArray>()
            .block() ?: throw RuntimeException("Document could not be fetched")
    }

    fun deleteDocument(id: String, systemUser: Boolean = false) {
        logger.debug("Deleting document with id {}", id)

        val token = if (systemUser) {
            tokenUtil.getAppAccessTokenWithKabalFileApiScope()
        } else {
            tokenUtil.getSaksbehandlerAccessTokenWithKabalFileApiScope()
        }

        val deletedInGCS = fileWebClient
            .delete()
            .uri("/document/$id")
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${token}")
            .header("Nav-Call-Id", tracer.currentSpan().context().traceIdString())
            .retrieve()
            .bodyToMono<Boolean>()
            .block()

        if (deletedInGCS == true) {
            logger.debug("Document successfully deleted in file store.")
        } else {
            logger.warn("Could not successfully delete document in file store.")
        }
    }

    fun uploadDocument(bytes: ByteArray, originalFilename: String): String {
        logger.debug("Uploading document to storage")

        val bodyBuilder = MultipartBodyBuilder()
        bodyBuilder.part("file", bytes).filename(originalFilename)
        val response = fileWebClient
            .post()
            .uri("/document")
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenUtil.getSaksbehandlerAccessTokenWithKabalFileApiScope()}")
            .header("Nav-Call-Id", tracer.currentSpan().context().traceIdString())
            .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
            .retrieve()
            .bodyToMono<DocumentUploadedResponse>()
            .block()

        requireNotNull(response)

        logger.debug("Document uploaded to file store with id: {}", response.id)
        return response.id
    }
}

data class DocumentUploadedResponse(val id: String)