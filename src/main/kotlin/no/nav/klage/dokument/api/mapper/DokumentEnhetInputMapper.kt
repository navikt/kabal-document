package no.nav.klage.dokument.api.mapper


import no.nav.klage.dokument.api.input.DokumentEnhetWithDokumentreferanserInput
import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.dokument.exceptions.DokumentEnhetNotValidException
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
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

    fun mapJournalfoeringDataInput(input: DokumentEnhetWithDokumentreferanserInput.JournalfoeringDataInput): JournalfoeringData =
        try {
            JournalfoeringData(
                sakenGjelder = mapPartIdInput(input.sakenGjelder),
                tema = if (input.temaId != null) Tema.of(input.temaId) else Tema.valueOf(input.tema!!),
                sakFagsakId = input.sakFagsakId,
                sakFagsystem = if (input.sakFagsystemId != null) Fagsystem.of(input.sakFagsystemId) else
                    input.sakFagsystem?.let { Fagsystem.valueOf(it) },
                kildeReferanse = input.kildeReferanse,
                enhet = input.enhet,
                behandlingstema = input.behandlingstema,
                tittel = input.tittel,
                brevKode = input.brevKode,
                tilleggsopplysning = input.tilleggsopplysning?.let { Tilleggsopplysning(it.key, it.value) }
            )
        } catch (iae: IllegalArgumentException) {
            logger.warn("Data fra klient er ikke gyldig", iae)
            throw DokumentEnhetNotValidException("Ulovlig input: ${iae.message}")
        }

    fun mapDokumentInputToHoveddokument(dokument: DokumentEnhetWithDokumentreferanserInput.DokumentInput.Dokument): OpplastetHoveddokument =
        OpplastetHoveddokument(
            mellomlagerId = dokument.mellomlagerId,
            opplastet = dokument.opplastet,
            size = dokument.size,
            name = dokument.name
        )

    fun mapDokumentInputToVedlegg(dokument: DokumentEnhetWithDokumentreferanserInput.DokumentInput.Dokument): OpplastetVedlegg =
        OpplastetVedlegg(
            mellomlagerId = dokument.mellomlagerId,
            opplastet = dokument.opplastet,
            size = dokument.size,
            name = dokument.name
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