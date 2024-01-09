package no.nav.klage.dokument.clients.joark

import no.nav.klage.kodeverk.Tema
import java.time.LocalDate

data class Journalpost(
    val journalposttype: JournalpostType? = null,
    val tema: Tema,
    val behandlingstema: String,
    val tittel: String,
    val kanal: Kanal? = null,
    var avsenderMottaker: AvsenderMottaker? = null,
    val journalfoerendeEnhet: String? = null,
    val eksternReferanseId: String? = null,
    val datoMottatt: LocalDate?,
    val bruker: Bruker? = null,
    val sak: Sak,
    val dokumenter: List<Dokument>? = mutableListOf(),
    val tilleggsopplysninger: List<Tilleggsopplysning> = mutableListOf()
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
    SENTRAL_UTSKRIFT,
    LOKAL_UTSKRIFT,
    SDP,
    TRYGDERETTEN,
    HELSENETTET,
    INGEN_DISTRIBUSJON,
    DPV,
    DPVS,
    UKJENT,
}

enum class JournalpostType {
    INNGAAENDE,
    UTGAAENDE,
    NOTAT
}

data class AvsenderMottaker(
    val id: String,
    val idType: AvsenderMottakerIdType,
    val navn: String? = null,
    val land: String? = null
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

enum class FagsaksSystem {
    AO01,
    AO11,
    BISYS,
    FS36,
    FS38,
    IT01,
    K9,
    OB36,
    OEBS,
    PP01,
    UFM,
    BA,
    EF,
    KONT,
    SUPSTONAD,
    OMSORGSPENGER,
    FS22,
    HJELPEMIDLER,
    BARNEBRILLER,
    EY,
    KABAL,
}

enum class ArkivsaksSystem {
    GSAK,
    PSAK
}

data class Dokument(
    val tittel: String,
    val brevkode: String? = null,
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

