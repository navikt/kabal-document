package no.nav.klage.dokument.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.klage.dokument.api.mapper.DokumentEnhetInputMapper
import no.nav.klage.dokument.clients.joark.JournalpostResponse
import no.nav.klage.dokument.clients.joark.JournalpostType
import no.nav.klage.dokument.clients.joark.TilknyttVedleggResponse
import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.dokument.repositories.BrevMottakerDistribusjonRepository
import no.nav.klage.dokument.repositories.DokumentEnhetRepository
import no.nav.klage.kodeverk.DokumentType
import no.nav.klage.kodeverk.Fagsystem
import no.nav.klage.kodeverk.PartIdType
import no.nav.klage.kodeverk.Tema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

internal class DokumentEnhetServiceTest {
    private val dokumentEnhetRepository = mockk<DokumentEnhetRepository>()
    private val dokumentEnhetInputMapper = mockk<DokumentEnhetInputMapper>()
    private val journalfoeringService = mockk<JournalfoeringService>()
    private val dokumentDistribusjonService = mockk<DokumentDistribusjonService>()
    private val mellomlagerService = mockk<MellomlagerService>()
    private val brevMottakerDistribusjonRepository = mockk<BrevMottakerDistribusjonRepository>()

    val JOURNALPOST_ID_1 = "JOURNALPOST_ID_1"
    val JOURNALPOST_ID_2 = "JOURNALPOST_ID_2"

    val JOURNALPOST_RESPONSE_1 = JournalpostResponse(
        journalpostId = JOURNALPOST_ID_1,
        journalpostferdigstilt = false,
        dokumenter = listOf()
    )

    val JOURNALPOST_RESPONSE_2 = JournalpostResponse(
        journalpostId = JOURNALPOST_ID_2,
        journalpostferdigstilt = false,
        dokumenter = listOf()
    )

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
        name = "fil.pdf",
        sourceReference = UUID.randomUUID(),
    )

    val brevMottakerDistribusjon1 = BrevMottakerDistribusjon(
        brevMottaker = brevMottaker1,
        opplastetDokumentId = hovedDokument.id,
    )

    val brevMottakerDistribusjon2 = BrevMottakerDistribusjon(
        brevMottaker = brevMottaker2,
        opplastetDokumentId = hovedDokument.id,
    )

    val baseDokumentEnhet = DokumentEnhet(
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
            tilleggsopplysning = Tilleggsopplysning("key", "value"),
            journalpostType = JournalpostType.UTGAAENDE
        ),
        brevMottakere = setOf(brevMottaker1, brevMottaker2),
        brevMottakerDistribusjoner = setOf(brevMottakerDistribusjon1, brevMottakerDistribusjon2),
        hovedDokument = hovedDokument,
        vedlegg = setOf(
            OpplastetVedlegg(
                mellomlagerId = "456",
                name = "fil2.pdf",
                index = 0,
                sourceReference = UUID.randomUUID(),
            )
        ),
        avsluttet = null,
        journalfoerendeSaksbehandlerIdent = "S123456",
        dokumentType = DokumentType.VEDTAK,
    )

    private val dokumentEnhetService = DokumentEnhetService(
        dokumentEnhetRepository = dokumentEnhetRepository,
        dokumentEnhetInputMapper = dokumentEnhetInputMapper,
        journalfoeringService = journalfoeringService,
        dokumentDistribusjonService = dokumentDistribusjonService,
        mellomlagerService = mellomlagerService,
        brevMottakerDistribusjonRepository = brevMottakerDistribusjonRepository
    )

    @BeforeEach
    fun setup() {
        every { dokumentEnhetRepository.save(any()) } returns baseDokumentEnhet
        every { brevMottakerDistribusjonRepository.save(any()) } returns brevMottakerDistribusjon1
        every {
            journalfoeringService.createJournalpostAsSystemUser(
                brevMottaker = brevMottaker1,
                hoveddokument = any(),
                vedleggDokumentSet = any(),
                journalfoeringData = any(),
                journalfoerendeSaksbehandlerIdent = any(),
            )
        } returns JOURNALPOST_RESPONSE_1

        every {
            journalfoeringService.createJournalpostAsSystemUser(
                brevMottaker = brevMottaker2,
                hoveddokument = any(),
                vedleggDokumentSet = any(),
                journalfoeringData = any(),
                journalfoerendeSaksbehandlerIdent = any(),
            )
        } returns JOURNALPOST_RESPONSE_2

        every { journalfoeringService.ferdigstillJournalpostForBrevMottaker(any()) } returns LocalDateTime.now()
        every { journalfoeringService.tilknyttVedleggAsSystemUser(any(), any()) } returns TilknyttVedleggResponse(feiledeDokumenter = emptyList())
        every { dokumentDistribusjonService.distribuerJournalpostTilMottaker(any(), any()) } returns UUID.randomUUID()
    }

    @Test
    fun `ubehandlet dokumentEnhet skal journalfoeres og distribueres korrekt`() {
        val dokumentEnhetTilDist = ikkeDistribuertDokumentEnhetMedVedleggOgToBrevMottakere()

        every { dokumentEnhetRepository.getReferenceById(any()) } returns dokumentEnhetTilDist

        assertFerdigDistribuert(
            dokumentEnhetService.ferdigstillDokumentEnhet(dokumentEnhetTilDist.id)
        )

        verify(exactly = 1) { journalfoeringService.createJournalpostAsSystemUser(brevMottaker1, any(), any(), any(), any()) }
        verify(exactly = 1) { journalfoeringService.createJournalpostAsSystemUser(brevMottaker2, any(), any(), any(), any()) }

        verify(exactly = 1) { journalfoeringService.ferdigstillJournalpostForBrevMottaker(brevMottakerDistribusjon1) }
        verify(exactly = 1) { journalfoeringService.ferdigstillJournalpostForBrevMottaker(brevMottakerDistribusjon2) }

        verify(exactly = 1) { dokumentDistribusjonService.distribuerJournalpostTilMottaker(JOURNALPOST_ID_1, any()) }
        verify(exactly = 1) { dokumentDistribusjonService.distribuerJournalpostTilMottaker(JOURNALPOST_ID_2, any()) }
    }

    @Test
    fun `ubehandlet dokumentEnhet uten distribusjon skal journalfoeres`() {
        val dokumentEnhetTilDist = ikkeDistribuertDokumentEnhetMedVedleggOgToBrevMottakereNotForDistribution()

        every { dokumentEnhetRepository.getReferenceById(any()) } returns dokumentEnhetTilDist

        assertFerdigJournalfoert(
            dokumentEnhetService.ferdigstillDokumentEnhet(dokumentEnhetTilDist.id)
        )

        verify(exactly = 1) { journalfoeringService.createJournalpostAsSystemUser(brevMottaker1, any(), any(), any(), any()) }
        verify(exactly = 1) { journalfoeringService.createJournalpostAsSystemUser(brevMottaker2, any(), any(), any(), any()) }

        verify(exactly = 1) { journalfoeringService.ferdigstillJournalpostForBrevMottaker(brevMottakerDistribusjon1) }
        verify(exactly = 1) { journalfoeringService.ferdigstillJournalpostForBrevMottaker(brevMottakerDistribusjon2) }

        verify(exactly = 0) { dokumentDistribusjonService.distribuerJournalpostTilMottaker(any(), any()) }
    }

    @Test
    fun `journalfoert men ikke distribuert dokumentEnhet skal distribueres korrekt`() {

        val dokumentEnhetTilDist = journalfoertMenIkkeDistribuertDokumentEnhetMedVedleggOgToBrevMottakere()

        every { dokumentEnhetRepository.getReferenceById(any()) } returns dokumentEnhetTilDist

        assertFerdigDistribuert(
            dokumentEnhetService.ferdigstillDokumentEnhet(dokumentEnhetTilDist.id)
        )

        verify(exactly = 0) { journalfoeringService.createJournalpostAsSystemUser(any(), any(), any(), any(), any()) }

        verify(exactly = 1) { journalfoeringService.ferdigstillJournalpostForBrevMottaker(brevMottakerDistribusjon1) }
        verify(exactly = 1) { journalfoeringService.ferdigstillJournalpostForBrevMottaker(brevMottakerDistribusjon2) }

        verify(exactly = 1) { dokumentDistribusjonService.distribuerJournalpostTilMottaker(JOURNALPOST_ID_1, any()) }
        verify(exactly = 1) { dokumentDistribusjonService.distribuerJournalpostTilMottaker(JOURNALPOST_ID_2, any()) }
    }

    @Test
    fun `journalfoert og distribuert dokumentEnhet skal ikke kalle på andre funksjoner`() {

        val dokumentEnhetTilDist = journalfoertOgDistribuertDokumentEnhetMedVedleggOgToBrevMottakere()

        every { dokumentEnhetRepository.getReferenceById(any()) } returns dokumentEnhetTilDist

        assertFerdigDistribuert(
            dokumentEnhetService.ferdigstillDokumentEnhet(dokumentEnhetTilDist.id)
        )

        verify(exactly = 0) { journalfoeringService.createJournalpostAsSystemUser(any(), any(), any(), any(), any()) }
        verify(exactly = 0) { journalfoeringService.ferdigstillJournalpostForBrevMottaker(any()) }
        verify(exactly = 0) { dokumentDistribusjonService.distribuerJournalpostTilMottaker(any(), any()) }
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
        return baseDokumentEnhet
    }

    fun ikkeDistribuertDokumentEnhetMedVedleggOgToBrevMottakereNotForDistribution(): DokumentEnhet {
        val dokumentEnhet = baseDokumentEnhet
        dokumentEnhet.journalfoeringData.journalpostType = JournalpostType.NOTAT
        return dokumentEnhet
    }

    fun journalfoertMenIkkeDistribuertDokumentEnhetMedVedleggOgToBrevMottakere(): DokumentEnhet {
        val dokumentEnhet = baseDokumentEnhet
        dokumentEnhet.brevMottakerDistribusjoner.first().journalpostId = JOURNALPOST_ID_1
        dokumentEnhet.brevMottakerDistribusjoner.last().journalpostId = JOURNALPOST_ID_2
        return dokumentEnhet
    }

    fun journalfoertOgDistribuertDokumentEnhetMedVedleggOgToBrevMottakere(): DokumentEnhet {
        val dokumentEnhet = baseDokumentEnhet
        dokumentEnhet.brevMottakerDistribusjoner.first().journalpostId = JOURNALPOST_ID_1
        dokumentEnhet.brevMottakerDistribusjoner.first().ferdigstiltIJoark = LocalDateTime.now()
        dokumentEnhet.brevMottakerDistribusjoner.first().dokdistReferanse = UUID.randomUUID()
        dokumentEnhet.brevMottakerDistribusjoner.last().journalpostId = JOURNALPOST_ID_2
        dokumentEnhet.brevMottakerDistribusjoner.last().ferdigstiltIJoark = LocalDateTime.now()
        dokumentEnhet.brevMottakerDistribusjoner.last().dokdistReferanse = UUID.randomUUID()
        return dokumentEnhet
    }
}