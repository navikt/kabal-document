package no.nav.klage.dokument.clients.saf.graphql


import no.nav.klage.dokument.util.TokenUtil
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getTeamLogger
import no.nav.klage.dokument.util.logErrorResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.resilience.annotation.Retryable
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
        private val teamLogger = getTeamLogger()
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
        brukerId: String,
    ): List<Journalpost> {
        return getDokumentoversiktBruker(
            brukerId = brukerId,
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
                logErrorResponse(
                    response = response,
                    functionName = ::getJournalpostWithToken.name,
                    classLogger = logger,
                )
            }
            .bodyToMono<JournalpostResponse>()
            .block()?.data?.journalpost
            ?: throw RuntimeException("Got null from SAF for journalpost with id $journalpostId")
    }

    fun getDokumentoversiktBruker(
        brukerId: String,
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
            .bodyValue(hentDokumentoversiktBrukerQuery(brukerId, tema, pageSize, previousPageRef))
            .retrieve()
            .onStatus(HttpStatusCode::isError) { response ->
                logErrorResponse(
                    response = response,
                    functionName = ::getDokumentoversiktBruker.name,
                    classLogger = logger,
                )
            }
            .bodyToMono<DokumentoversiktBrukerResponse>()
            .block()
            .also { logger.debug("DokumentoversiktBruker response size: ${it?.toString()?.toByteArray()?.size}") }
            ?.let { logErrorsFromSaf(it, brukerId, pageSize, previousPageRef); it }
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
            logger.error("Error from SAF, see more details in team-logs")
            teamLogger.error("Error from SAF when making call with following parameters: fnr=$fnr, pagesize=$pageSize, previousPageRef=$previousPageRef. Error is ${response.errors}")
        }
    }

    private fun failOnErrors(response: DokumentoversiktBrukerResponse) {
        if (response.data == null || response.errors != null) {
            throw RuntimeException("getDokumentoversiktBruker failed")
        }
    }

}