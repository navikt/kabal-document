package no.nav.klage.dokument.clients.clamav

import com.fasterxml.jackson.annotation.JsonAlias

data class ScanResult(
    @JsonAlias("Filename")
    val filename: String,
    @JsonAlias("Result")
    val result: ClamAvResult
)

enum class ClamAvResult {
    FOUND, OK, ERROR
}
