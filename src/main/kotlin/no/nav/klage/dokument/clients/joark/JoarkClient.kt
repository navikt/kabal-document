package no.nav.klage.dokument.clients.joark

import no.nav.klage.dokument.util.TokenUtil
import no.nav.klage.dokument.util.getLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.resilience.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.io.File

@Component
class JoarkClient(
    @Qualifier("joarkLargeFileWebClient") private val joarkLargeFileWebClient: WebClient,
    @Qualifier("joarkSmallFileWebClient") private val joarkSmallFileWebClient: WebClient,
    private val tokenUtil: TokenUtil,
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)

        // Based on observation: 5MB takes ~4 seconds. With 15s timeout, we can handle up to ~15MB.
        // Files larger than 15MB use the long timeout (220s), smaller files use short timeout (15s)
        const val LARGE_FILE_THRESHOLD_BYTES = 15 * 1024 * 1024L
    }

    @Retryable
    fun createJournalpostInJoarkAsSystemUser(
        journalpostRequestAsFile: File,
        journalfoerendeSaksbehandlerIdent: String,
    ): JournalpostResponse {
        val dataBufferFactory = DefaultDataBufferFactory()
        val dataBuffer = DataBufferUtils.read(journalpostRequestAsFile.toPath(), dataBufferFactory, 256 * 256)

        // Choose WebClient based on file size
        val fileSize = journalpostRequestAsFile.length()
        val webClient = if (fileSize > LARGE_FILE_THRESHOLD_BYTES) {
            logger.debug("Using large file WebClient (220s timeout) for file of size {} bytes", fileSize)
            joarkLargeFileWebClient
        } else {
            logger.debug("Using small file WebClient (15s timeout) for file of size {} bytes", fileSize)
            joarkSmallFileWebClient
        }

        val post = webClient.post()
            .uri("?forsoekFerdigstill=false")
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenUtil.getAppAccessTokenWithDokarkivScope()}")

        if (journalfoerendeSaksbehandlerIdent != "SYSTEMBRUKER") {
            post.header("Nav-User-Id", journalfoerendeSaksbehandlerIdent)
        }

        val journalpostResponse = post.contentType(MediaType.APPLICATION_JSON)
            .body(dataBuffer, DataBuffer::class.java)
            .retrieve()
            .bodyToMono(JournalpostResponse::class.java)
            .block()
            ?: throw RuntimeException("Journalpost could not be created.")

        logger.debug("Journalpost successfully created in Joark with id {}.", journalpostResponse.journalpostId)

        journalpostRequestAsFile.delete()

        return journalpostResponse
    }

    @Retryable
    fun finalizeJournalpostAsSystemUser(journalpostId: String, journalfoerendeEnhet: String) {
        joarkSmallFileWebClient.patch()
            .uri("/${journalpostId}/ferdigstill")
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenUtil.getAppAccessTokenWithDokarkivScope()}")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(FerdigstillJournalpostPayload(journalfoerendeEnhet))
            .retrieve()
            .bodyToMono<String>()
            .block()
            ?: throw RuntimeException("Journalpost could not be finalized.")

        logger.debug("Journalpost with id $journalpostId was succesfully finalized.")
    }

    @Retryable
    fun tilknyttVedleggAsSystemUser(journalpostId: String, input: TilknyttVedleggPayload): TilknyttVedleggResponse {
        val response = joarkSmallFileWebClient.put()
            .uri("/${journalpostId}/tilknyttVedlegg")
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenUtil.getAppAccessTokenWithDokarkivScope()}")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(input)
            .retrieve()
            .bodyToMono<TilknyttVedleggResponse>()
            .block()
            ?: throw RuntimeException("Could not tilknytt vedlegg.")

        logger.debug("tilknyttVedleggAsSystemUser to journalpost with id $journalpostId was successful.")

        return response
    }

    @Retryable
    fun updateDocumentTitleOnBehalfOf(journalpostId: String, input: UpdateDocumentTitleJournalpostInput) {
        try {
            joarkSmallFileWebClient.put()
                .uri("/${journalpostId}")
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer ${tokenUtil.getSaksbehandlerAccessTokenWithDokarkivScope()}"
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(input)
                .retrieve()
                .bodyToMono(UpdateJournalpostResponse::class.java)
                .block()
                ?: throw RuntimeException("Journalpost could not be updated.")
        } catch (e: Exception) {
            logger.error("Error updating journalpost $journalpostId:", e)
        }

        logger.debug("Document from journalpost $journalpostId with dokumentInfoId id ${input.dokumenter.first().dokumentInfoId} was succesfully updated.")
    }
}