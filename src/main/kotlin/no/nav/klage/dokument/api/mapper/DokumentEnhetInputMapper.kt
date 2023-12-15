package no.nav.klage.dokument.api.mapper


import no.nav.klage.dokument.api.input.DokumentEnhetWithDokumentreferanserInput
import no.nav.klage.dokument.clients.joark.JournalpostType
import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.dokument.exceptions.DokumentEnhetNotValidException
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
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
        private val secureLogger = getSecureLogger()
    }

    fun mapBrevMottakereInput(brevMottakereInput: List<DokumentEnhetWithDokumentreferanserInput.BrevMottakerInput>): Set<BrevMottaker> =
        brevMottakereInput.map { mapBrevMottakerInput(it) }.toSet()

    fun mapBrevMottakerInput(brevMottakerInput: DokumentEnhetWithDokumentreferanserInput.BrevMottakerInput): BrevMottaker =
        try {
            BrevMottaker(
                partId = mapPartIdInput(brevMottakerInput.partId),
                navn = brevMottakerInput.navn,
            )
        } catch (iae: IllegalArgumentException) {
            logger.warn("Data fra klient er ikke gyldig", iae)
            throw DokumentEnhetNotValidException("Ulovlig input: ${iae.message}")
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
                DokumentType.KJENNELSE_FRA_TRYGDERETTEN -> {
                    JournalpostType.INNGAAENDE
                }
                else -> JournalpostType.UTGAAENDE
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
                inngaaendeKanal = if (journalpostType == JournalpostType.INNGAAENDE) input.inngaaendeKanal else null
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

    fun mapDokumentInputToVedlegg(dokument: DokumentEnhetWithDokumentreferanserInput.DokumentInput.Dokument, index: Int): OpplastetVedlegg =
        OpplastetVedlegg(
            mellomlagerId = dokument.mellomlagerId,
            name = dokument.name,
            index = index,
            sourceReference = dokument.sourceReference
        )

    fun mapDokumentInputToJournalfoertVedlegg(dokument: DokumentEnhetWithDokumentreferanserInput.DokumentInput.JournalfoertDokument, index: Int): JournalfoertVedlegg =
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