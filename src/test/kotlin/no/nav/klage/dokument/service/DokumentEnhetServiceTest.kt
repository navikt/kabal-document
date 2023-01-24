package no.nav.klage.dokument.service

import io.mockk.every
import io.mockk.mockk
import no.nav.klage.dokument.api.mapper.DokumentEnhetInputMapper
import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.dokument.repositories.DokumentEnhetRepository
import no.nav.klage.kodeverk.DokumentType
import no.nav.klage.kodeverk.Fagsystem
import no.nav.klage.kodeverk.PartIdType
import no.nav.klage.kodeverk.Tema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

internal class DokumentEnhetServiceTest {
    private val dokumentEnhetRepository = mockk<DokumentEnhetRepository>()
    private val dokumentEnhetInputMapper = mockk<DokumentEnhetInputMapper>()
    private val journalfoeringService = mockk<JournalfoeringService>()
    private val dokumentDistribusjonService = mockk<DokumentDistribusjonService>()
    private val mellomlagerService = mockk<MellomlagerService>()

    val JOURNALPOST_ID_1 = "JOURNALPOST_ID_1"
    val JOURNALPOST_ID_2 = "JOURNALPOST_ID_2"

    private val dokumentEnhetService = DokumentEnhetService(
        dokumentEnhetRepository = dokumentEnhetRepository,
        dokumentEnhetInputMapper = dokumentEnhetInputMapper,
        journalfoeringService = journalfoeringService,
        dokumentDistribusjonService = dokumentDistribusjonService,
        mellomlagerService = mellomlagerService,
    )

    @Test
    fun `ubehandlet dokumentEnhet skal journalfoeres og distribueres korrekt`() {
        val dokumentEnhetTilDist = ikkeDistribuertDokumentEnhetMedVedleggOgToBrevMottakere()

        every { dokumentEnhetRepository.getReferenceById(any()) } returns dokumentEnhetTilDist

        every {
            journalfoeringService.createJournalpostAsSystemUser(
                brevMottaker = dokumentEnhetTilDist.brevMottakere.first(),
                hoveddokument = any(),
                vedleggDokumentList = any(),
                journalfoeringData = any(),
            )
        } returns JOURNALPOST_ID_1

        every {
            journalfoeringService.createJournalpostAsSystemUser(
                brevMottaker = dokumentEnhetTilDist.brevMottakere.last(),
                hoveddokument = any(),
                vedleggDokumentList = any(),
                journalfoeringData = any(),
            )
        } returns JOURNALPOST_ID_2

        every { dokumentEnhetRepository.save(any()) } returns dokumentEnhetTilDist
        every { journalfoeringService.ferdigstillJournalpostForBrevMottaker(any()) } returns LocalDateTime.now()
        every { dokumentDistribusjonService.distribuerJournalpostTilMottaker(any(), any()) } returns UUID.randomUUID()

        assertFerdigDistribuert(
            dokumentEnhetService.ferdigstillDokumentEnhet(dokumentEnhetTilDist.id)
        )
    }

    @Test
    fun `ubehandlet dokumentEnhet uten distribusjon skal journalfoeres`() {
        val dokumentEnhetTilDist = ikkeDistribuertDokumentEnhetMedVedleggOgToBrevMottakereNotForDistribution()

        every { dokumentEnhetRepository.getReferenceById(any()) } returns dokumentEnhetTilDist

        every {
            journalfoeringService.createJournalpostAsSystemUser(
                brevMottaker = dokumentEnhetTilDist.brevMottakere.first(),
                hoveddokument = any(),
                vedleggDokumentList = any(),
                journalfoeringData = any(),
            )
        } returns JOURNALPOST_ID_1

        every {
            journalfoeringService.createJournalpostAsSystemUser(
                brevMottaker = dokumentEnhetTilDist.brevMottakere.last(),
                hoveddokument = any(),
                vedleggDokumentList = any(),
                journalfoeringData = any(),
            )
        } returns JOURNALPOST_ID_2

        every { dokumentEnhetRepository.save(any()) } returns dokumentEnhetTilDist
        every { journalfoeringService.ferdigstillJournalpostForBrevMottaker(any()) } returns LocalDateTime.now()

        assertFerdigJournalfoert(
            dokumentEnhetService.ferdigstillDokumentEnhet(dokumentEnhetTilDist.id)
        )
    }

    @Test
    fun `journalfoert men ikke distribuert dokumentEnhet skal distribueres korrekt`() {

        val dokumentEnhetTilDist = journalfoertMenIkkeDistribuertDokumentEnhetMedVedleggOgToBrevMottakere()

        every { dokumentEnhetRepository.getReferenceById(any()) } returns dokumentEnhetTilDist

        every { dokumentEnhetRepository.save(any()) } returns dokumentEnhetTilDist
        every { journalfoeringService.ferdigstillJournalpostForBrevMottaker(any()) } returns LocalDateTime.now()
        every { dokumentDistribusjonService.distribuerJournalpostTilMottaker(any(), any()) } returns UUID.randomUUID()

        assertFerdigDistribuert(
            dokumentEnhetService.ferdigstillDokumentEnhet(dokumentEnhetTilDist.id)
        )
    }

    @Test
    fun `journalfoert og distribuert dokumentEnhet skal ikke kalle på andre funksjoner`() {

        val dokumentEnhetTilDist = journalfoertOgDistribuertDokumentEnhetMedVedleggOgToBrevMottakere()

        every { dokumentEnhetRepository.getReferenceById(any()) } returns dokumentEnhetTilDist
        every { dokumentEnhetRepository.save(any()) } returns dokumentEnhetTilDist

        assertFerdigDistribuert(
            dokumentEnhetService.ferdigstillDokumentEnhet(dokumentEnhetTilDist.id)
        )
    }

    private fun assertFerdigDistribuert(dokumentEnhet: DokumentEnhet) {
        assertThat(dokumentEnhet.isAvsluttet())
        assertThat(dokumentEnhet.brevMottakerDistribusjoner.size).isEqualTo(dokumentEnhet.brevMottakere.size)
        dokumentEnhet.brevMottakerDistribusjoner.forEach {
            assertThat(it.journalpostId).isNotNull
            assertThat(it.ferdigstiltIJoark).isNotNull
            assertThat(it.dokdistReferanse).isNotNull
        }
        assertThat(dokumentEnhet.hovedDokument).isNotNull
    }

    private fun assertFerdigJournalfoert(dokumentEnhet: DokumentEnhet) {
        assertThat(dokumentEnhet.isAvsluttet())
        assertThat(dokumentEnhet.brevMottakerDistribusjoner.size).isEqualTo(dokumentEnhet.brevMottakere.size)
        dokumentEnhet.brevMottakerDistribusjoner.forEach {
            assertThat(it.journalpostId).isNotNull
            assertThat(it.ferdigstiltIJoark).isNotNull
        }
        assertThat(dokumentEnhet.hovedDokument).isNotNull
    }

    fun ikkeDistribuertDokumentEnhetMedVedleggOgToBrevMottakere(): DokumentEnhet {
        val brevMottaker1 = BrevMottaker(
            partId = PartId(
                type = PartIdType.PERSON,
                value = "01011012345"
            ),
            navn = "Test Person",
        )

        val brevMottaker2 = BrevMottaker(
            partId = PartId(
                type = PartIdType.PERSON,
                value = "20022012345"
            ),
            navn = "Mottaker Person",
        )

        val hovedDokument = OpplastetHoveddokument(
            mellomlagerId = "123",
            opplastet = LocalDateTime.now(),
            size = 1000L,
            name = "fil.pdf"
        )

        val brevMottakerDistribusjon1 = BrevMottakerDistribusjon(
            brevMottaker = brevMottaker1,
            opplastetDokumentId = hovedDokument.id,
        )

        val brevMottakerDistribusjon2 = BrevMottakerDistribusjon(
            brevMottaker = brevMottaker2,
            opplastetDokumentId = hovedDokument.id,
        )

        return DokumentEnhet(
            journalfoeringData = JournalfoeringData(
                sakenGjelder = PartId(
                    type = PartIdType.PERSON,
                    value = "20022012345"
                ),
                tema = Tema.OMS,
                sakFagsakId = "sakFagsakId",
                sakFagsystem = Fagsystem.FS36,
                kildeReferanse = "kildeReferanse",
                enhet = "Enhet",
                behandlingstema = "behandlingstema",
                tittel = "Tittel",
                brevKode = "brevKode",
                tilleggsopplysning = Tilleggsopplysning("key", "value")
            ),
            brevMottakere = setOf(brevMottaker1, brevMottaker2),
            brevMottakerDistribusjoner = setOf(brevMottakerDistribusjon1, brevMottakerDistribusjon2),
            hovedDokument = hovedDokument,
            vedlegg = listOf(
                OpplastetVedlegg(
                    mellomlagerId = "456",
                    opplastet = LocalDateTime.now(),
                    size = 1001L,
                    name = "fil2.pdf"
                )
            ),
            avsluttet = null,
            dokumentType = DokumentType.VEDTAK,
        )
    }

    fun ikkeDistribuertDokumentEnhetMedVedleggOgToBrevMottakereNotForDistribution(): DokumentEnhet {
        val brevMottaker1 = BrevMottaker(
            partId = PartId(
                type = PartIdType.PERSON,
                value = "01011012345"
            ),
            navn = "Test Person",
        )

        val brevMottaker2 = BrevMottaker(
            partId = PartId(
                type = PartIdType.PERSON,
                value = "20022012345"
            ),
            navn = "Mottaker Person",
        )

        val hovedDokument = OpplastetHoveddokument(
            mellomlagerId = "123",
            opplastet = LocalDateTime.now(),
            size = 1000L,
            name = "fil.pdf"
        )

        val brevMottakerDistribusjon1 = BrevMottakerDistribusjon(
            brevMottaker = brevMottaker1,
            opplastetDokumentId = hovedDokument.id,
        )

        val brevMottakerDistribusjon2 = BrevMottakerDistribusjon(
            brevMottaker = brevMottaker2,
            opplastetDokumentId = hovedDokument.id,
        )

        return DokumentEnhet(
            journalfoeringData = JournalfoeringData(
                sakenGjelder = PartId(
                    type = PartIdType.PERSON,
                    value = "20022012345"
                ),
                tema = Tema.OMS,
                sakFagsakId = "sakFagsakId",
                sakFagsystem = Fagsystem.FS36,
                kildeReferanse = "kildeReferanse",
                enhet = "Enhet",
                behandlingstema = "behandlingstema",
                tittel = "Tittel",
                brevKode = "brevKode",
                tilleggsopplysning = Tilleggsopplysning("key", "value")
            ),
            brevMottakere = setOf(brevMottaker1, brevMottaker2),
            brevMottakerDistribusjoner = setOf(brevMottakerDistribusjon1, brevMottakerDistribusjon2),
            hovedDokument = hovedDokument,
            vedlegg = listOf(
                OpplastetVedlegg(
                    mellomlagerId = "456",
                    opplastet = LocalDateTime.now(),
                    size = 1001L,
                    name = "fil2.pdf"
                )
            ),
            avsluttet = null,
            dokumentType = DokumentType.VEDTAK,
            shouldBeDistributed = false,
        )
    }

    fun journalfoertMenIkkeDistribuertDokumentEnhetMedVedleggOgToBrevMottakere(): DokumentEnhet {
        val brevMottaker1 = BrevMottaker(
            partId = PartId(
                type = PartIdType.PERSON,
                value = "01011012345"
            ),
            navn = "Test Person",
        )

        val brevMottaker2 = BrevMottaker(
            partId = PartId(
                type = PartIdType.PERSON,
                value = "20022012345"
            ),
            navn = "Mottaker Person",
        )

        val hovedDokument = OpplastetHoveddokument(
            mellomlagerId = "123",
            opplastet = LocalDateTime.now(),
            size = 1000L,
            name = "fil.pdf"
        )

        val brevMottakerDistribusjon1 = BrevMottakerDistribusjon(
            brevMottaker = brevMottaker1,
            opplastetDokumentId = hovedDokument.id,
            journalpostId = JOURNALPOST_ID_1,
        )

        val brevMottakerDistribusjon2 = BrevMottakerDistribusjon(
            brevMottaker = brevMottaker2,
            opplastetDokumentId = hovedDokument.id,
            journalpostId = JOURNALPOST_ID_2,
        )

        return DokumentEnhet(
            journalfoeringData = JournalfoeringData(
                sakenGjelder = PartId(
                    type = PartIdType.PERSON,
                    value = "20022012345"
                ),
                tema = Tema.OMS,
                sakFagsakId = "sakFagsakId",
                sakFagsystem = Fagsystem.FS36,
                kildeReferanse = "kildeReferanse",
                enhet = "Enhet",
                behandlingstema = "behandlingstema",
                tittel = "Tittel",
                brevKode = "brevKode",
                tilleggsopplysning = Tilleggsopplysning("key", "value")
            ),
            brevMottakere = setOf(brevMottaker1, brevMottaker2),
            brevMottakerDistribusjoner = setOf(brevMottakerDistribusjon1, brevMottakerDistribusjon2),
            hovedDokument = hovedDokument,
            vedlegg = listOf(
                OpplastetVedlegg(
                    mellomlagerId = "456",
                    opplastet = LocalDateTime.now(),
                    size = 1001L,
                    name = "fil2.pdf"
                )
            ),
            avsluttet = null,
            dokumentType = DokumentType.VEDTAK,
        )
    }

    fun journalfoertOgDistribuertDokumentEnhetMedVedleggOgToBrevMottakere(): DokumentEnhet {
        val brevMottaker1 = BrevMottaker(
            partId = PartId(
                type = PartIdType.PERSON,
                value = "01011012345"
            ),
            navn = "Test Person",
        )

        val brevMottaker2 = BrevMottaker(
            partId = PartId(
                type = PartIdType.PERSON,
                value = "20022012345"
            ),
            navn = "Mottaker Person",
        )

        val hovedDokument = OpplastetHoveddokument(
            mellomlagerId = "123",
            opplastet = LocalDateTime.now(),
            size = 1000L,
            name = "fil.pdf"
        )

        val brevMottakerDistribusjon1 = BrevMottakerDistribusjon(
            brevMottaker = brevMottaker1,
            opplastetDokumentId = hovedDokument.id,
            journalpostId = JOURNALPOST_ID_1,
            ferdigstiltIJoark = LocalDateTime.now(),
            dokdistReferanse = UUID.randomUUID(),
        )

        val brevMottakerDistribusjon2 = BrevMottakerDistribusjon(
            brevMottaker = brevMottaker2,
            opplastetDokumentId = hovedDokument.id,
            journalpostId = JOURNALPOST_ID_2,
            ferdigstiltIJoark = LocalDateTime.now(),
            dokdistReferanse = UUID.randomUUID(),
        )

        return DokumentEnhet(
            journalfoeringData = JournalfoeringData(
                sakenGjelder = PartId(
                    type = PartIdType.PERSON,
                    value = "20022012345"
                ),
                tema = Tema.OMS,
                sakFagsakId = "sakFagsakId",
                sakFagsystem = Fagsystem.FS36,
                kildeReferanse = "kildeReferanse",
                enhet = "Enhet",
                behandlingstema = "behandlingstema",
                tittel = "Tittel",
                brevKode = "brevKode",
                tilleggsopplysning = Tilleggsopplysning("key", "value")
            ),
            brevMottakere = setOf(brevMottaker1, brevMottaker2),
            brevMottakerDistribusjoner = setOf(brevMottakerDistribusjon1, brevMottakerDistribusjon2),
            hovedDokument = hovedDokument,
            vedlegg = listOf(
                OpplastetVedlegg(
                    mellomlagerId = "456",
                    opplastet = LocalDateTime.now(),
                    size = 1001L,
                    name = "fil2.pdf"
                )
            ),
            avsluttet = null,
            dokumentType = DokumentType.VEDTAK,
        )
    }
}