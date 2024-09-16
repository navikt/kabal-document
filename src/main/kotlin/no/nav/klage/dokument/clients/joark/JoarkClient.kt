package no.nav.klage.dokument.clients.joark

import no.nav.klage.dokument.util.TokenUtil
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.io.File

@Component
class JoarkClient(
    private val joarkWebClient: WebClient,
    private val tokenUtil: TokenUtil,
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    fun createJournalpostInJoarkAsSystemUser(
        journalpostRequestAsFile: File,
        journalfoerendeSaksbehandlerIdent: String,
    ): JournalpostResponse {
        val dataBufferFactory = DefaultDataBufferFactory()
        val dataBuffer = DataBufferUtils.read(journalpostRequestAsFile.toPath(), dataBufferFactory, 256 * 256)

        val post = joarkWebClient.post()
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

    fun finalizeJournalpostAsSystemUser(journalpostId: String, journalfoerendeEnhet: String) {
        joarkWebClient.patch()
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

    fun tilknyttVedleggAsSystemUser(journalpostId: String, input: TilknyttVedleggPayload): TilknyttVedleggResponse {
        val response = joarkWebClient.put()
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

    fun updateDocumentTitleOnBehalfOf(journalpostId: String, input: UpdateDocumentTitleJournalpostInput) {
        try {
            joarkWebClient.put()
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