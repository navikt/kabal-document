package no.nav.klage.dokument.clients.klagelookup

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class PersonResponse(
    val sammensattNavn: String,
)
