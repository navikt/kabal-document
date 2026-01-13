package no.nav.klage.dokument.config

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import no.nav.klage.dokument.api.input.DokumentEnhetWithDokumentreferanserInput
import no.nav.klage.dokument.repositories.DokumentEnhetRepository
import no.nav.klage.dokument.util.getTeamLogger
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.util.*

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class SaksbehandlerFilter(
    private val currentSaksbehandlerHolder: CurrentSaksbehandlerHolder,
    private val dokumentEnhetRepository: DokumentEnhetRepository,
) : OncePerRequestFilter() {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val teamLogger = getTeamLogger()
        private val objectMapper = jacksonObjectMapper()
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Extract dokumentEnhetId from path if present
        val dokumentEnhetId = extractDokumentEnhetId(request.requestURI)

        if (dokumentEnhetId != null) {
            // Lookup from database
            val dokumentEnhet = dokumentEnhetRepository.findById(dokumentEnhetId).orElse(null)
            if (dokumentEnhet != null) {
                teamLogger.debug("Populating CurrentSaksbehandlerHolder from database lookup: ${dokumentEnhet.journalfoerendeSaksbehandlerIdent}")
                currentSaksbehandlerHolder.navIdent = dokumentEnhet.journalfoerendeSaksbehandlerIdent
            }
            filterChain.doFilter(request, response)
        } else if (request.method == "POST" && request.contentType?.contains("application/json") == true) {
            // Read and cache the request body
            val cachedRequest = CachedBodyHttpServletRequest(request)
            try {
                val body = String(cachedRequest.cachedBody, Charsets.UTF_8)
                val input = objectMapper.readValue(body, DokumentEnhetWithDokumentreferanserInput::class.java)
                teamLogger.debug("Populating CurrentSaksbehandlerHolder from request body: ${input.journalfoerendeSaksbehandlerIdent}")
                currentSaksbehandlerHolder.navIdent = input.journalfoerendeSaksbehandlerIdent
            } catch (e: Exception) {
                teamLogger.warn("Failed to parse request body for saksbehandler ident", e)
            }
            filterChain.doFilter(cachedRequest, response)
        } else {
            filterChain.doFilter(request, response)
        }
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return !request.requestURI.startsWith("/dokumentenheter")
    }

    private fun extractDokumentEnhetId(uri: String): UUID? {
        val regex = """/dokumentenheter/([a-f0-9-]{36})""".toRegex()
        return regex.find(uri)?.groupValues?.get(1)?.let {
            try {
                UUID.fromString(it)
            } catch (_: IllegalArgumentException) {
                null
            }
        }
    }
}

class CachedBodyHttpServletRequest(request: HttpServletRequest) : HttpServletRequestWrapper(request) {

    val cachedBody: ByteArray = request.inputStream.readAllBytes()

    override fun getInputStream(): ServletInputStream {
        return CachedBodyServletInputStream(cachedBody)
    }

    override fun getReader(): BufferedReader {
        return BufferedReader(InputStreamReader(ByteArrayInputStream(cachedBody)))
    }
}

class CachedBodyServletInputStream(cachedBody: ByteArray) : ServletInputStream() {

    private val inputStream = ByteArrayInputStream(cachedBody)

    override fun read(): Int = inputStream.read()

    override fun isFinished(): Boolean = inputStream.available() == 0

    override fun isReady(): Boolean = true

    override fun setReadListener(listener: ReadListener?) {
        throw UnsupportedOperationException("setReadListener is not supported")
    }
}