package no.nav.klage.dokument.api.input

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class TrygderettenMetadataInput(
    val kravfremsettelsesdato: LocalDate?,
    val paaanketVedtaksdato: LocalDate,
    val tidligereITROgOpphevetHenvist: Boolean?,
    val gjenopptak: Boolean?,
    val forsterketRett: Boolean,
    val ettersendelse: Boolean,
    val lovhenvisning: String,
)
