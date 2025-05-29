package no.nav.klage.dokument.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.function.client.ClientResponse
import reactor.core.publisher.Mono

fun getLogger(forClass: Class<*>): Logger = LoggerFactory.getLogger(forClass)

fun fun getTeamLogger(): Logger = LoggerFactory.getLogger("team-logs")

fun logErrorResponse(response: ClientResponse, functionName: String, logger: Logger): Mono<RuntimeException> {
    return response.bodyToMono(String::class.java).map {
        val errorString =
            "Got ${response.statusCode()} when requesting $functionName - response body: '$it'"
        logger.error(errorString)
        RuntimeException(errorString)
    }
}