package no.nav.klage.dokument.clients.smarteditorapi

import brave.Tracer
import no.nav.klage.dokument.util.TokenUtil
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.util.*

@Component
class SmartEditorClient(
    private val smartEditorWebClient: WebClient,
    private val tracer: Tracer,
    private val tokenUtil: TokenUtil
) {

    @Value("\${spring.application.name}")
    lateinit var applicationName: String


    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    @Retryable
    fun getDocumentAsPDF(smartEditorId: UUID): SmartEditorMultipartFile {

        val document = smartEditorWebClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/{documentId}/pdf")
                    .build(smartEditorId)
            }
            .header("Authorization", "Bearer ${tokenUtil.getSaksbehandlerAccessTokenWithSmartEditorScope()}")
            .header("Nav-Call-Id", tracer.currentSpan().context().traceIdString())
            .header("Nav-Consumer-Id", applicationName)
            .exchangeToMono { response ->
                if (response.statusCode() == HttpStatus.OK) {
                    val header: MutableList<String> = response.headers().header("Content-Disposition")
                    header.forEach { logger.debug("Content-Disposition header: $it") }
                    response.bodyToMono<ByteArray>().map {
                        SmartEditorMultipartFile(
                            content = it,
                            fileName = "vedtak.pdf" //TODO: Get from header
                        )
                    }
                } else {
                    response.createException().flatMap { Mono.error(it) }
                }
            }.block()!!
        return document
    }
}