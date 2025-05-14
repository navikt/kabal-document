package no.nav.klage.dokument.clients.joark

import no.nav.klage.kodeverk.Tema
import java.time.LocalDate

data class Journalpost(
    val journalposttype: JournalpostType?,
    val tema: Tema,
    val behandlingstema: String,
    val tittel: String,
    val kanal: Kanal?,
    var avsenderMottaker: JournalpostAvsenderMottaker?,
    val journalfoerendeEnhet: String?,
    val eksternReferanseId: String?,
    val datoMottatt: LocalDate?,
    val bruker: Bruker?,
    val sak: Sak,
    val tilleggsopplysninger: List<Tilleggsopplysning> = mutableListOf(),
    val dokumenter: List<Dokument>? = mutableListOf()
)

/**
 * Without documents
 */
data class JournalpostPartial(
    val journalposttype: JournalpostType?,
    val tema: Tema,
    val behandlingstema: String,
    val tittel: String,
    val kanal: Kanal?,
    var avsenderMottaker: JournalpostAvsenderMottaker?,
    val journalfoerendeEnhet: String?,
    val eksternReferanseId: String?,
    val datoMottatt: LocalDate?,
    val bruker: Bruker?,
    val sak: Sak,
    val tilleggsopplysninger: List<Tilleggsopplysning> = mutableListOf(),
)

enum class Kanal {
    ALTINN,
    ALTINN_INNBOKS,
    EIA,
    E_POST,
    NAV_NO,
    NAV_NO_UINNLOGGET,
    NAV_NO_CHAT,
    SKAN_NETS,
    SKAN_PEN,
    SKAN_IM,
    INNSENDT_NAV_ANSATT,
    EESSI,
    EKST_OPPS,
    S,
    L,
    SDP,
    TRYGDERETTEN,
    HELSENETTET,
    INGEN_DISTRIBUSJON,
    DPVT,
    UKJENT,
}

enum class JournalpostType {
    INNGAAENDE,
    UTGAAENDE,
    NOTAT
}

data class JournalpostAvsenderMottaker(
    val id: String?,
    val idType: AvsenderMottakerIdType?,
    val navn: String?,
    val land: String?,
)

enum class AvsenderMottakerIdType {
    FNR,
    ORGNR,
    HPRNR,
    UTL_ORG
}

data class Bruker(
    val id: String,
    val idType: BrukerIdType
)

enum class BrukerIdType {
    FNR,
    ORGNR,
    AKTOERID
}

data class Sak(
    val sakstype: Sakstype,
    val fagsaksystem: FagsaksSystem,
    val fagsakid: String,
    val arkivsaksystem: ArkivsaksSystem? = null,
    val arkivsaksnummer: String? = null
)

enum class Sakstype {
    FAGSAK,
    GENERELL_SAK,
    ARKIVSAK
}

//Should match no.nav.dokarkiv.journalpost.v1.api.Fagsaksystem
enum class FagsaksSystem {
    FS38,
    FS36,
    UFM,
    OEBS,
    OB36,
    AO01,
    AO11,
    IT01,
    PP01,
    K9,
    BISYS,
    BA,
    EF,
    KONT,
    SUPSTONAD,
    OMSORGSPENGER,
    HJELPEMIDLER,
    BARNEBRILLER,
    EY,
    KELVIN,
    DAGPENGER,
    KOMPYS,
    ARGUS,
    NEESSI,
    TILLEGGSSTONADER,
    ARBEIDSOPPFOLGING,
    TILTAKTSPENGER,
    TILTAKSADMINISTRASJON,
    FIA,
    HELT,
}

enum class ArkivsaksSystem {
    GSAK,
    PSAK
}

data class Dokument(
    val tittel: String,
    val brevkode: String,
    val dokumentVarianter: List<DokumentVariant> = mutableListOf()
)

data class DokumentVariant(
    val filnavn: String,
    val filtype: String,
    val fysiskDokument: String,
    val variantformat: String
)

data class Tilleggsopplysning(
    val nokkel: String,
    val verdi: String
)

data class UpdateDocumentTitleJournalpostInput(
    val dokumenter: List<UpdateDocumentTitleDokumentInput>,
)

data class UpdateDocumentTitleDokumentInput(
    val dokumentInfoId: String,
    val tittel: String,
)

