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
        //TODO: Denne er ikke i bruk i frontend enda. Skal antageligvis ha et annet regelsett, der det ikke skjer distribusjon.
        NOTAT -> Distribusjonstidspunkt.KJERNETID
        VEDTAK -> Distribusjonstidspunkt.KJERNETID
        BESLUTNING -> Distribusjonstidspunkt.KJERNETID
    }


fun DokumentType.toDistribusjonsType(): Distribusjonstype =
    when (this) {
        BREV -> Distribusjonstype.VIKTIG
        //TODO: Denne er ikke i bruk i frontend enda. Skal antageligvis ha et annet regelsett, der det ikke skjer distribusjon.
        NOTAT -> Distribusjonstype.ANNET
        VEDTAK -> Distribusjonstype.VEDTAK
        BESLUTNING -> Distribusjonstype.VIKTIG
    }



