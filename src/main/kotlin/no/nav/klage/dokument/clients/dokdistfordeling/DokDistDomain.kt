package no.nav.klage.dokument.clients.dokdistfordeling

import no.nav.klage.kodeverk.DokumentType
import no.nav.klage.kodeverk.DokumentType.*
import java.util.*

data class DistribuerJournalpostRequest(
    val batchId: String? = null,
    val bestillendeFagsystem: String? = null,
    val dokumentProdApp: String? = null,
    val journalpostId: String,
    val distribusjonstype: Distribusjonstype,
    val distribusjonstidspunkt: Distribusjonstidspunkt,
    val adresse: Adresse?,
    val tvingKanal: Kanal?,
    val forsendelseMetadata: String?,
    val forsendelseMetadataType: ForsendelseMetadataType?,
) {
    enum class Kanal {
        PRINT,
        TRYGDERETTEN
    }

    enum class ForsendelseMetadataType {
        DPO_ARKIVMELDING,
        DPO_AVTALEMELDING
    }
}



data class Adresse(
    val adressetype: Adressetype,
    val adresselinje1: String?,
    val adresselinje2: String?,
    val adresselinje3: String?,
    val postnummer: String?,
    val poststed: String?,
    val land: String,
)

enum class Adressetype {
    norskPostadresse,
    utenlandskPostadresse,
}


data class DistribuerJournalpostResponse(
    val bestillingsId: UUID
)

enum class Distribusjonstype {
    VEDTAK,
    VIKTIG,
    ANNET,
}

enum class Distribusjonstidspunkt {
    UMIDDELBART,
    KJERNETID
}

fun DokumentType.toDistribusjonstidspunkt(): Distribusjonstidspunkt =
    when (this) {
        BREV, SVARBREV, FORLENGET_BEHANDLINGSTIDSBREV -> Distribusjonstidspunkt.KJERNETID
        VEDTAK -> Distribusjonstidspunkt.KJERNETID
        BESLUTNING -> Distribusjonstidspunkt.KJERNETID
        EKSPEDISJONSBREV_TIL_TRYGDERETTEN -> Distribusjonstidspunkt.UMIDDELBART
        //Disse blir ikke distribuert, tas med for fullstendighet.
        NOTAT, KJENNELSE_FRA_TRYGDERETTEN, ANNEN_INNGAAENDE_POST -> Distribusjonstidspunkt.KJERNETID
    }


fun DokumentType.toDistribusjonsType(): Distribusjonstype =
    when (this) {
        BREV, SVARBREV, FORLENGET_BEHANDLINGSTIDSBREV -> Distribusjonstype.VIKTIG
        VEDTAK -> Distribusjonstype.VEDTAK
        BESLUTNING -> Distribusjonstype.VIKTIG
        //Disse blir ikke distribuert, tas med for fullstendighet.
        NOTAT, KJENNELSE_FRA_TRYGDERETTEN, ANNEN_INNGAAENDE_POST, EKSPEDISJONSBREV_TIL_TRYGDERETTEN -> Distribusjonstype.ANNET
    }



