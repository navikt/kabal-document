package no.nav.klage.dokument.clients.norg2

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class EnhetResponse(
    val navn: String
) {
    fun asEnhet() = Enhet(navn)
}
