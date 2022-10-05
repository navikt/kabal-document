package no.nav.klage.dokument.api.input

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class BrevMottakerInput(
    val partId: PartIdInput,
    val navn: String?,
)
