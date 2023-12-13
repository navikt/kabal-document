package no.nav.klage.dokument.clients.dokdistfordeling

import no.nav.klage.kodeverk.DokumentType
import no.nav.klage.kodeverk.DokumentType.*
import java.util.*

data class DistribuerJournalpostRequestTo(
    val batchId: String? = null,
    val bestillendeFagSystem: String? = null,
    val dokumentProdApp: String? = null,
    val journalpostId: String,
    val distribusjonstype: Distribusjonstype,
    val distribusjonstidspunkt: Distribusjonstidspunkt,

)

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
        BREV -> Distribusjonstidspunkt.KJERNETID
        VEDTAK -> Distribusjonstidspunkt.KJERNETID
        BESLUTNING -> Distribusjonstidspunkt.KJERNETID
        //Disse blir ikke distribuert, tas med for fullstendighet.
        NOTAT, KJENNELSE_FRA_TRYGDERETTEN -> Distribusjonstidspunkt.KJERNETID
    }


fun DokumentType.toDistribusjonsType(): Distribusjonstype =
    when (this) {
        BREV -> Distribusjonstype.VIKTIG
        VEDTAK -> Distribusjonstype.VEDTAK
        BESLUTNING -> Distribusjonstype.VIKTIG
        //Disse blir ikke distribuert, tas med for fullstendighet.
        NOTAT, KJENNELSE_FRA_TRYGDERETTEN -> Distribusjonstype.ANNET
    }



