package no.nav.klage.dokument.clients.pdl.graphql

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDate


@JsonIgnoreProperties(ignoreUnknown = true)
data class HentPersonResponse(val data: PdlPersonDataWrapper?, val errors: List<PdlPerson.PdlError>? = null)

data class PdlPersonDataWrapper(val hentPerson: PdlPerson?)

data class PdlPerson(
    val folkeregisteridentifikator: Folkeregisteridentifikator,
    val adressebeskyttelse: List<Adressebeskyttelse>,
    val navn: List<Navn>,
    val kjoenn: List<Kjoenn>,
    val sivilstand: List<Sivilstand>,
    val vergemaalEllerFremtidsfullmakt: List<VergemaalEllerFremtidsfullmakt>,
    val doedsfall: List<Doedsfall>,
    val sikkerhetstiltak: List<Sikkerhetstiltak>
) {
    data class Folkeregisteridentifikator(
        val identifikasjonsnummer: String
    )

    data class Adressebeskyttelse(val gradering: GraderingType) {
        enum class GraderingType { STRENGT_FORTROLIG_UTLAND, STRENGT_FORTROLIG, FORTROLIG, UGRADERT }
    }

    data class Sivilstand(
        val type: SivilstandType,
        val gyldigFraOgMed: LocalDate?,
        val relatertVedSivilstand: String?,
        val bekreftelsesdato: LocalDate?
    ) {

        fun dato(): LocalDate? = gyldigFraOgMed ?: bekreftelsesdato

        enum class SivilstandType {
            UOPPGITT,
            UGIFT,
            GIFT,
            ENKE_ELLER_ENKEMANN,
            SKILT,
            SEPARERT,
            REGISTRERT_PARTNER,
            SEPARERT_PARTNER,
            SKILT_PARTNER,
            GJENLEVENDE_PARTNER
        }
    }

    data class Navn(
        val fornavn: String,
        val mellomnavn: String?,
        val etternavn: String
    )

    data class Kjoenn(val kjoenn: KjoennType?) {
        enum class KjoennType { MANN, KVINNE, UKJENT }
    }

    data class VergemaalEllerFremtidsfullmakt(
        val type: String,
        val embete: String,
        val vergeEllerFullmektig: VergeEllerFullmektig
    ) {
        data class VergeEllerFullmektig(
            val motpartsPersonident: String,
            val omfang: String?,
            val omfangetErInnenPersonligOmraad: Boolean?
        )
    }

    data class Doedsfall(
        val doedsdato: LocalDate,
    )

    data class Sikkerhetstiltak(
        val tiltakstype: Tiltakstype,
        val beskrivelse: String,
        val gyldigFraOgMed: LocalDate,
        val gyldigTilOgMed: LocalDate,
    )

    enum class Tiltakstype {
        FYUS,
        TFUS,
        FTUS,
        DIUS,
        TOAN,
    }

    data class PdlError(
        val message: String,
        val locations: List<PdlErrorLocation>,
        val path: List<String>?,
        val extensions: PdlErrorExtension
    )

    data class PdlErrorLocation(
        val line: Int?,
        val column: Int?
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class PdlErrorExtension(
        val code: String?,
        val classification: String?,
        val warnings: List<String>?,
    )
}
