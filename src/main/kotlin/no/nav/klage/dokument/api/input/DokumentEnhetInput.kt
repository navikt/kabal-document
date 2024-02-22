package no.nav.klage.dokument.api.input

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.klage.dokument.clients.joark.Kanal
import java.time.LocalDate
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class DokumentEnhetWithDokumentreferanserInput(
    val brevMottakere: List<AvsenderMottakerInput>?,
    val avsenderMottakerList: List<AvsenderMottakerInput>?,
    val journalfoeringData: JournalfoeringDataInput,
    val dokumentreferanser: DokumentInput,
    val dokumentTypeId: String,
    val journalfoerendeSaksbehandlerIdent: String,
) {
    data class DokumentInput(
        val hoveddokument: Dokument,
        val vedlegg: List<Dokument>?,
        val journalfoerteVedlegg: List<JournalfoertDokument>?,
    ) {
        data class Dokument(
            val mellomlagerId: String,
            val name: String,
            val sourceReference: UUID?,
        )

        data class JournalfoertDokument(
            val kildeJournalpostId: String,
            val dokumentInfoId: String,
        )
    }

    data class AvsenderMottakerInput(
        val partId: PartIdInput,
        val navn: String?,
        val localPrint: Boolean,
        val tvingSentralPrint: Boolean,
        val adresse: AdresseInput?,
        val kanal: Kanal?,
    )

    data class AdresseInput(
        val adressetype: Adressetype,
        val adresselinje1: String?,
        val adresselinje2: String?,
        val adresselinje3: String?,
        val postnummer: String?,
        val poststed: String?,
        val land: String,
    )

    enum class Adressetype(val navn: String) {
        NORSK_POSTADRESSE("norskPostadresse"),
        UTENLANDSK_POSTADRESSE("utenlandskPostadresse"),
    }

    data class JournalfoeringDataInput(
        val sakenGjelder: PartIdInput,
        val temaId: String,
        val sakFagsakId: String,
        val sakFagsystemId: String,
        val kildeReferanse: String,
        val enhet: String,
        val behandlingstema: String,
        val tittel: String,
        val brevKode: String,
        val tilleggsopplysning: TilleggsopplysningInput?,
        val inngaaendeKanal: Kanal?,
        val datoMottatt: LocalDate?,
    ) {
        data class TilleggsopplysningInput(
            val key: String,
            val value: String
        )
    }

    data class PartIdInput(
        val type: String?,
        val partIdTypeId: String?,
        val value: String
    )
}