package no.nav.klage.dokument.api.mapper


import no.nav.klage.dokument.api.input.DokumentEnhetWithDokumentreferanserInput
import no.nav.klage.dokument.clients.joark.JournalpostType
import no.nav.klage.dokument.clients.joark.Kanal
import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.dokument.exceptions.DokumentEnhetNotValidException
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.kodeverk.DokumentType
import no.nav.klage.kodeverk.Fagsystem
import no.nav.klage.kodeverk.PartIdType
import no.nav.klage.kodeverk.Tema
import org.springframework.stereotype.Service

@Service
class DokumentEnhetInputMapper {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    fun mapAvsenderMottakerInputList(avsenderMottakerInput: List<DokumentEnhetWithDokumentreferanserInput.AvsenderMottakerInput>): Set<AvsenderMottaker> =
        avsenderMottakerInput.map { mapAvsenderMottakerInput(it) }.toSet()

    fun mapAvsenderMottakerInput(avsenderMottakerInput: DokumentEnhetWithDokumentreferanserInput.AvsenderMottakerInput): AvsenderMottaker =
        try {
            if (avsenderMottakerInput.kanal != null) {
                if (avsenderMottakerInput.kanal !in listOf(
                        Kanal.EESSI,
                        Kanal.ALTINN,
                        Kanal.ALTINN_INNBOKS,
                        Kanal.E_POST,
                        Kanal.NAV_NO,
                        Kanal.S,
                        Kanal.L,
                        Kanal.SDP,
                        Kanal.EIA,
                        Kanal.HELSENETTET,
                        Kanal.TRYGDERETTEN,
                        Kanal.INGEN_DISTRIBUSJON,
                        Kanal.NAV_NO_CHAT,
                        Kanal.DPVT
                    )
                ) {
                    throw Exception("Invalid kanal in avsenderMottakerInput: ${avsenderMottakerInput.kanal.name}")
                }
            }

            if (avsenderMottakerInput.partId == null) {
                if (avsenderMottakerInput.navn == null) {
                    throw IllegalArgumentException("Avsender/Mottaker må ha enten partId eller navn")
                }
                if (avsenderMottakerInput.adresse == null) {
                    throw IllegalArgumentException("Avsender/Mottaker må ha adresse")
                }
            }

            AvsenderMottaker(
                partId = avsenderMottakerInput.partId?.let { mapPartIdInput(avsenderMottakerInput.partId) },
                navn = avsenderMottakerInput.navn,
                tvingSentralPrint = avsenderMottakerInput.tvingSentralPrint,
                adresse = validateAndMapAdresseInput(avsenderMottakerInput.adresse),
                localPrint = avsenderMottakerInput.localPrint,
                kanal = avsenderMottakerInput.kanal,
            )
        } catch (iae: IllegalArgumentException) {
            logger.warn("Data fra klient er ikke gyldig", iae)
            throw DokumentEnhetNotValidException("Ulovlig input: ${iae.message}")
        }

    private fun validateAndMapAdresseInput(input: DokumentEnhetWithDokumentreferanserInput.AdresseInput?): Adresse? {
        if (input == null) return null
        when (input.adressetype) {
            DokumentEnhetWithDokumentreferanserInput.Adressetype.UTENLANDSK_POSTADRESSE -> {
                if (input.adresselinje1 == null) {
                    throw IllegalArgumentException("Adressetype utenlandskPostadresse krever adresselinje1.")
                }
            }

            DokumentEnhetWithDokumentreferanserInput.Adressetype.NORSK_POSTADRESSE -> {
                if (input.poststed == null || input.postnummer == null) {
                    throw IllegalArgumentException("Adressetype norskPostadresse krever postnummer og poststed.")
                }
            }
        }

        return Adresse(
            adressetype = input.adressetype.navn,
            adresselinje1 = input.adresselinje1,
            adresselinje2 = input.adresselinje2,
            adresselinje3 = input.adresselinje3,
            postnummer = input.postnummer,
            poststed = input.poststed,
            land = input.land,
        )
    }

    fun mapJournalfoeringDataInput(
        input: DokumentEnhetWithDokumentreferanserInput.JournalfoeringDataInput,
        dokumentType: DokumentType,
    ): JournalfoeringData =
        try {
            val journalpostType = when (dokumentType) {
                DokumentType.NOTAT -> {
                    JournalpostType.NOTAT
                }

                DokumentType.KJENNELSE_FRA_TRYGDERETTEN, DokumentType.ANNEN_INNGAAENDE_POST -> {
                    JournalpostType.INNGAAENDE
                }

                DokumentType.VEDTAK, DokumentType.BREV, DokumentType.BESLUTNING, DokumentType.SVARBREV, DokumentType.FORLENGET_BEHANDLINGSTIDSBREV, DokumentType.EKSPEDISJONSBREV_TIL_TRYGDERETTEN -> {
                    JournalpostType.UTGAAENDE
                }
            }

            if (journalpostType != JournalpostType.INNGAAENDE && input.datoMottatt != null) {
                logger.error("Data fra klient er ikke gyldig, datoMottatt kan kun settes for inngående journalpost.")
                throw IllegalArgumentException("datoMottatt kan kun settes for inngående journalpost")
            }

            JournalfoeringData(
                sakenGjelder = mapPartIdInput(input.sakenGjelder),
                tema = Tema.of(input.temaId),
                sakFagsakId = input.sakFagsakId,
                sakFagsystem = Fagsystem.of(input.sakFagsystemId),
                kildeReferanse = input.kildeReferanse,
                enhet = input.enhet,
                behandlingstema = input.behandlingstema,
                tittel = input.tittel,
                brevKode = input.brevKode,
                tilleggsopplysning = input.tilleggsopplysning?.let { Tilleggsopplysning(it.key, it.value) },
                journalpostType = journalpostType,
                inngaaendeKanal = if (journalpostType == JournalpostType.INNGAAENDE) input.inngaaendeKanal else null,
                datoMottatt = if (journalpostType == JournalpostType.INNGAAENDE && input.datoMottatt != null) input.datoMottatt else null,
            )
        } catch (iae: IllegalArgumentException) {
            logger.warn("Data fra klient er ikke gyldig", iae)
            throw DokumentEnhetNotValidException("Ulovlig input: ${iae.message}")
        }

    fun mapDokumentInputToHoveddokument(dokument: DokumentEnhetWithDokumentreferanserInput.DokumentInput.Dokument): OpplastetHoveddokument =
        OpplastetHoveddokument(
            mellomlagerId = dokument.mellomlagerId,
            name = dokument.name,
            sourceReference = dokument.sourceReference,
        )

    fun mapDokumentInputToVedlegg(
        dokument: DokumentEnhetWithDokumentreferanserInput.DokumentInput.Dokument,
        index: Int
    ): OpplastetVedlegg =
        OpplastetVedlegg(
            mellomlagerId = dokument.mellomlagerId,
            name = dokument.name,
            index = index,
            sourceReference = dokument.sourceReference
        )

    fun mapDokumentInputToJournalfoertVedlegg(
        dokument: DokumentEnhetWithDokumentreferanserInput.DokumentInput.JournalfoertDokument,
        index: Int
    ): JournalfoertVedlegg =
        JournalfoertVedlegg(
            kildeJournalpostId = dokument.kildeJournalpostId,
            dokumentInfoId = dokument.dokumentInfoId,
            index = index,
        )

    private fun mapPartIdInput(partIdInput: DokumentEnhetWithDokumentreferanserInput.PartIdInput): PartId {
        return try {
            PartId(
                type = if (partIdInput.partIdTypeId != null) PartIdType.of(partIdInput.partIdTypeId)
                else PartIdType.valueOf(partIdInput.type!!),
                value = partIdInput.value
            )
        } catch (iae: IllegalArgumentException) {
            logger.warn("Data fra klient er ikke gyldig", iae)
            throw DokumentEnhetNotValidException("Ulovlig input: ${iae.message}")
        }
    }
}