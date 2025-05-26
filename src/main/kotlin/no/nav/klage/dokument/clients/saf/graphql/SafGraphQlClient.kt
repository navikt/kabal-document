package no.nav.klage.dokument.clients.saf.graphql


import no.nav.klage.dokument.util.TokenUtil
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import no.nav.klage.dokument.util.logErrorResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.time.LocalDateTime

@Component
class SafGraphQlClient(
    private val safWebClient: WebClient,
    private val tokenUtil: TokenUtil,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    @Retryable
    fun getJournalpostAsSystembruker(
        journalpostId: String,
    ): Journalpost {
        return runWithTimingAndLogging({
            getJournalpostWithToken(journalpostId = journalpostId, token = tokenUtil.getAppAccessTokenWithSafScope())
        }, this::getJournalpostAsSystembruker.name)
    }

    @Retryable
    fun getDokumentoversiktBrukerAsSystembruker(
        fnr: String,
    ): List<Journalpost> {
        return getDokumentoversiktBruker(
            fnr = fnr,
            token = tokenUtil.getAppAccessTokenWithSafScope(),
        ).journalposter
    }


    private fun getJournalpostWithToken(
        journalpostId: String,
        token: String,
    ): Journalpost {
        return safWebClient.post()
            .uri("graphql")
            .header(
                HttpHeaders.AUTHORIZATION,
                "Bearer $token"
            )
            .bodyValue(hentJournalpostQuery(journalpostId))
            .retrieve()
            .onStatus(HttpStatusCode::isError) { response ->
                logErrorResponse(response, "getJournalpost", secureLogger)
            }
            .bodyToMono<JournalpostResponse>()
            .block()?.data?.journalpost
            ?: throw RuntimeException("Got null from SAF for journalpost with id $journalpostId")
    }

    fun getDokumentoversiktBruker(
        fnr: String,
        tema: List<Tema> = emptyList(),
        pageSize: Int = 50000,
        previousPageRef: String? = null,
        token: String,
    ): DokumentoversiktBruker {
        val start = System.currentTimeMillis()
        return safWebClient.post()
            .uri("graphql")
            .header(
                HttpHeaders.AUTHORIZATION,
                "Bearer $token"
            )
            .bodyValue(hentDokumentoversiktBrukerQuery(fnr, tema, pageSize, previousPageRef))
            .retrieve()
            .onStatus(HttpStatusCode::isError) { response ->
                logErrorResponse(response, ::getDokumentoversiktBruker.name, secureLogger)
            }
            .bodyToMono<DokumentoversiktBrukerResponse>()
            .block()
            ?.let { logErrorsFromSaf(it, fnr, pageSize, previousPageRef); it }
            ?.let { failOnErrors(it); it }
            ?.data!!.dokumentoversiktBruker.also {
                logger.debug(
                    "DokumentoversiktBruker: antall: {}, ms: {}, dato/tid: {}",
                    it.sideInfo.totaltAntall,
                    System.currentTimeMillis() - start,
                    LocalDateTime.now()
                )
            }
    }


    fun <T> runWithTimingAndLogging(block: () -> T, method: String): T {
        val start = System.currentTimeMillis()
        try {
            return block.invoke()
        } finally {
            val end = System.currentTimeMillis()
            logger.debug("Time it took to call saf using $method: ${end - start} millis")
        }
    }

    private fun logErrorsFromSaf(
        response: DokumentoversiktBrukerResponse,
        fnr: String,
        pageSize: Int,
        previousPageRef: String?
    ) {
        if (response.errors != null) {
            logger.error("Error from SAF, see securelogs")
            secureLogger.error("Error from SAF when making call with following parameters: fnr=$fnr, pagesize=$pageSize, previousPageRef=$previousPageRef. Error is ${response.errors}")
        }
    }

    private fun failOnErrors(response: DokumentoversiktBrukerResponse) {
        if (response.data == null || response.errors != null) {
            throw RuntimeException("getDokumentoversiktBruker failed")
        }
    }

}