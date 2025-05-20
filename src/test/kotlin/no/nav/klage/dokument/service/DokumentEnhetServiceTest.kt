package no.nav.klage.dokument.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.klage.dokument.api.mapper.DokumentEnhetInputMapper
import no.nav.klage.dokument.clients.joark.JournalpostResponse
import no.nav.klage.dokument.clients.joark.JournalpostType
import no.nav.klage.dokument.clients.joark.TilknyttVedleggResponse
import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.dokument.repositories.AvsenderMottakerDistribusjonRepository
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
    private val avsenderMottakerDistribusjonRepository = mockk<AvsenderMottakerDistribusjonRepository>()

    val JOURNALPOST_ID_1 = "JOURNALPOST_ID_1"
    val JOURNALPOST_ID_2 = "JOURNALPOST_ID_2"
    val JOURNALPOST_ID_3 = "JOURNALPOST_ID_3"

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

    val JOURNALPOST_RESPONSE_3 = JournalpostResponse(
        journalpostId = JOURNALPOST_ID_3,
        journalpostferdigstilt = false,
        dokumenter = listOf()
    )

    val avsenderMottaker1 = AvsenderMottaker(
        partId = PartId(
            type = PartIdType.PERSON,
            value = "01011012345"
        ),
        navn = "Test Person",
        adresse = null,
        tvingSentralPrint = false,
        localPrint = false,
        kanal = null,
    )

    val avsenderMottaker2 = AvsenderMottaker(
        partId = PartId(
            type = PartIdType.PERSON,
            value = "20022012345"
        ),
        navn = "Mottaker Person",
        adresse = null,
        tvingSentralPrint = false,
        localPrint = false,
        kanal = null,
    )

    val avsenderMottaker3 = AvsenderMottaker(
        partId = PartId(
            type = PartIdType.PERSON,
            value = "01011012345"
        ),
        navn = "Test Person",
        adresse = null,
        tvingSentralPrint = false,
        localPrint = true,
        kanal = null,
    )

    val hovedDokument = OpplastetHoveddokument(
        mellomlagerId = "123",
        name = "fil.pdf",
        sourceReference = UUID.randomUUID(),
    )

    val avsenderMottakerDistribusjon1 = AvsenderMottakerDistribusjon(
        avsenderMottaker = avsenderMottaker1,
        opplastetDokumentId = hovedDokument.id,
    )

    val avsenderMottakerDistribusjon2 = AvsenderMottakerDistribusjon(
        avsenderMottaker = avsenderMottaker2,
        opplastetDokumentId = hovedDokument.id,
    )

    val avsenderMottakerDistribusjon3 = AvsenderMottakerDistribusjon(
        avsenderMottaker = avsenderMottaker3,
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
            journalpostType = JournalpostType.UTGAAENDE,
            inngaaendeKanal = null,
            datoMottatt = null,
        ),
        avsenderMottakere = setOf(avsenderMottaker1, avsenderMottaker2),
        avsenderMottakerDistribusjoner = setOf(avsenderMottakerDistribusjon1, avsenderMottakerDistribusjon2),
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
        arkivmeldingTilTrygderetten = null,
    )

    val dokumentEnhetWithLocalPrint = DokumentEnhet(
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
            journalpostType = JournalpostType.UTGAAENDE,
            inngaaendeKanal = null,
            datoMottatt = null,
        ),
        avsenderMottakere = setOf(avsenderMottaker3),
        avsenderMottakerDistribusjoner = setOf(avsenderMottakerDistribusjon3),
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
        arkivmeldingTilTrygderetten = null,
    )

    private val dokumentEnhetService = DokumentEnhetService(
        dokumentEnhetRepository = dokumentEnhetRepository,
        dokumentEnhetInputMapper = dokumentEnhetInputMapper,
        journalfoeringService = journalfoeringService,
        dokumentDistribusjonService = dokumentDistribusjonService,
        mellomlagerService = mellomlagerService,
        avsenderMottakerDistribusjonRepository = avsenderMottakerDistribusjonRepository
    )

    @BeforeEach
    fun setup() {
        every { dokumentEnhetRepository.save(any()) } returns baseDokumentEnhet
        every { avsenderMottakerDistribusjonRepository.save(any()) } returns avsenderMottakerDistribusjon1
        every {
            journalfoeringService.createJournalpostAsSystemUser(
                avsenderMottaker = avsenderMottaker1,
                hoveddokument = any(),
                vedleggDokumentSet = any(),
                journalfoeringData = any(),
                journalfoerendeSaksbehandlerIdent = any(),
            )
        } returns JOURNALPOST_RESPONSE_1

        every {
            journalfoeringService.createJournalpostAsSystemUser(
                avsenderMottaker = avsenderMottaker2,
                hoveddokument = any(),
                vedleggDokumentSet = any(),
                journalfoeringData = any(),
                journalfoerendeSaksbehandlerIdent = any(),
            )
        } returns JOURNALPOST_RESPONSE_2

        every {
            journalfoeringService.createJournalpostAsSystemUser(
                avsenderMottaker = avsenderMottaker3,
                hoveddokument = any(),
                vedleggDokumentSet = any(),
                journalfoeringData = any(),
                journalfoerendeSaksbehandlerIdent = any(),
            )
        } returns JOURNALPOST_RESPONSE_3

        every { journalfoeringService.ferdigstillJournalpostForAvsenderMottakerDistribusjon(any()) } returns LocalDateTime.now()
        every { journalfoeringService.tilknyttVedleggAsSystemUser(any(), any()) } returns TilknyttVedleggResponse(
            feiledeDokumenter = emptyList()
        )
        every {
            dokumentDistribusjonService.distribuerJournalpostTilMottaker(
                journalpostId = any(),
                dokumentType = any(),
                tvingSentralPrint = any(),
                adresse = any(),
                arkivmeldingTilTrygderetten = any(),
            )
        } returns UUID.randomUUID()
    }

    @Test
    fun `ubehandlet dokumentEnhet skal journalfoeres og distribueres korrekt`() {
        val dokumentEnhetTilDist = ikkeDistribuertDokumentEnhetMedVedleggOgToBrevMottakere()

        every { dokumentEnhetRepository.getReferenceById(any()) } returns dokumentEnhetTilDist

        assertFerdigDistribuert(
            dokumentEnhetService.ferdigstillDokumentEnhet(dokumentEnhetTilDist.id)
        )

        verify(exactly = 1) {
            journalfoeringService.createJournalpostAsSystemUser(
                avsenderMottaker1,
                any(),
                any(),
                any(),
                any()
            )
        }
        verify(exactly = 1) {
            journalfoeringService.createJournalpostAsSystemUser(
                avsenderMottaker2,
                any(),
                any(),
                any(),
                any()
            )
        }

        verify(exactly = 1) {
            journalfoeringService.ferdigstillJournalpostForAvsenderMottakerDistribusjon(
                avsenderMottakerDistribusjon1
            )
        }
        verify(exactly = 1) {
            journalfoeringService.ferdigstillJournalpostForAvsenderMottakerDistribusjon(
                avsenderMottakerDistribusjon2
            )
        }

        verify(exactly = 1) {
            dokumentDistribusjonService.distribuerJournalpostTilMottaker(
                journalpostId = JOURNALPOST_ID_1,
                dokumentType = any(),
                tvingSentralPrint = any(),
                adresse = any(),
                arkivmeldingTilTrygderetten = any(),,
            )
        }
        verify(exactly = 1) {
            dokumentDistribusjonService.distribuerJournalpostTilMottaker(
                journalpostId = JOURNALPOST_ID_2,
                dokumentType = any(),
                tvingSentralPrint = any(),
                adresse = any(),
                arkivmeldingTilTrygderetten = any(),,
            )
        }
    }

    @Test
    fun `ubehandlet dokumentEnhet med lokal print skal journalfoeres, men ikke distribueres`() {
        val dokumentEnhetTilDist = ikkeDistribuertDokumentEnhetMedVedleggOgEnBrevMottakerLokalPrint()

        every { dokumentEnhetRepository.getReferenceById(any()) } returns dokumentEnhetTilDist

        assertFerdigJournalfoert(
            dokumentEnhetService.ferdigstillDokumentEnhet(dokumentEnhetTilDist.id)
        )

        verify(exactly = 1) {
            journalfoeringService.createJournalpostAsSystemUser(
                avsenderMottaker3,
                any(),
                any(),
                any(),
                any()
            )
        }

        verify(exactly = 1) {
            journalfoeringService.ferdigstillJournalpostForAvsenderMottakerDistribusjon(
                avsenderMottakerDistribusjon3
            )
        }

        verify(exactly = 0) {
            dokumentDistribusjonService.distribuerJournalpostTilMottaker(
                journalpostId = any(),
                dokumentType = any(),
                tvingSentralPrint = any(),
                adresse = any(),
                arkivmeldingTilTrygderetten = any(),
            )
        }
    }

    @Test
    fun `ubehandlet dokumentEnhet av typen Notat skal journalfoeres og ikke distribueres`() {
        val dokumentEnhetTilDist = ikkeDistribuertNotatDokumentEnhetMedVedleggOgToBrevMottakere()

        every { dokumentEnhetRepository.getReferenceById(any()) } returns dokumentEnhetTilDist

        assertFerdigJournalfoert(
            dokumentEnhetService.ferdigstillDokumentEnhet(dokumentEnhetTilDist.id)
        )

        verify(exactly = 1) {
            journalfoeringService.createJournalpostAsSystemUser(
                avsenderMottaker1,
                any(),
                any(),
                any(),
                any()
            )
        }
        verify(exactly = 1) {
            journalfoeringService.createJournalpostAsSystemUser(
                avsenderMottaker2,
                any(),
                any(),
                any(),
                any()
            )
        }

        verify(exactly = 1) {
            journalfoeringService.ferdigstillJournalpostForAvsenderMottakerDistribusjon(
                avsenderMottakerDistribusjon1
            )
        }
        verify(exactly = 1) {
            journalfoeringService.ferdigstillJournalpostForAvsenderMottakerDistribusjon(
                avsenderMottakerDistribusjon2
            )
        }

        verify(exactly = 0) {
            dokumentDistribusjonService.distribuerJournalpostTilMottaker(
                journalpostId = any(),
                dokumentType = any(),
                tvingSentralPrint = any(),
                adresse = any(),
                arkivmeldingTilTrygderetten = any(),
            )
        }
    }

    @Test
    fun `journalfoert men ikke distribuert dokumentEnhet skal distribueres korrekt`() {

        val dokumentEnhetTilDist = journalfoertMenIkkeDistribuertDokumentEnhetMedVedleggOgToBrevMottakere()

        every { dokumentEnhetRepository.getReferenceById(any()) } returns dokumentEnhetTilDist

        assertFerdigDistribuert(
            dokumentEnhetService.ferdigstillDokumentEnhet(dokumentEnhetTilDist.id)
        )

        verify(exactly = 0) { journalfoeringService.createJournalpostAsSystemUser(any(), any(), any(), any(), any()) }

        verify(exactly = 1) {
            journalfoeringService.ferdigstillJournalpostForAvsenderMottakerDistribusjon(
                avsenderMottakerDistribusjon1
            )
        }
        verify(exactly = 1) {
            journalfoeringService.ferdigstillJournalpostForAvsenderMottakerDistribusjon(
                avsenderMottakerDistribusjon2
            )
        }

        verify(exactly = 1) {
            dokumentDistribusjonService.distribuerJournalpostTilMottaker(
                journalpostId = JOURNALPOST_ID_1,
                dokumentType = any(),
                tvingSentralPrint = any(),
                adresse = any(),
                arkivmeldingTilTrygderetten = any(),,
            )
        }
        verify(exactly = 1) {
            dokumentDistribusjonService.distribuerJournalpostTilMottaker(
                journalpostId = JOURNALPOST_ID_2,
                dokumentType = any(),
                tvingSentralPrint = any(),
                adresse = any(),
                arkivmeldingTilTrygderetten = any(),,
            )
        }
    }

    @Test
    fun `journalfoert og distribuert dokumentEnhet skal ikke kalle på andre funksjoner`() {

        val dokumentEnhetTilDist = journalfoertOgDistribuertDokumentEnhetMedVedleggOgToBrevMottakere()

        every { dokumentEnhetRepository.getReferenceById(any()) } returns dokumentEnhetTilDist

        assertFerdigDistribuert(
            dokumentEnhetService.ferdigstillDokumentEnhet(dokumentEnhetTilDist.id)
        )

        verify(exactly = 0) { journalfoeringService.createJournalpostAsSystemUser(any(), any(), any(), any(), any()) }
        verify(exactly = 0) { journalfoeringService.ferdigstillJournalpostForAvsenderMottakerDistribusjon(any()) }
        verify(exactly = 0) {
            dokumentDistribusjonService.distribuerJournalpostTilMottaker(
                journalpostId = any(),
                dokumentType = any(),
                tvingSentralPrint = any(),
                adresse = any(),
                arkivmeldingTilTrygderetten = any(),
            )
        }
    }

    private fun assertFerdigDistribuert(dokumentEnhet: DokumentEnhet) {
        assertThat(dokumentEnhet.isAvsluttet())
        assertThat(dokumentEnhet.avsenderMottakerDistribusjoner.size).isEqualTo(dokumentEnhet.avsenderMottakere.size)
        dokumentEnhet.avsenderMottakerDistribusjoner.forEach {
            assertThat(it.journalpostId).isNotNull
            assertThat(it.ferdigstiltIJoark).isNotNull
            assertThat(it.dokdistReferanse).isNotNull
        }
        assertThat(dokumentEnhet.hovedDokument).isNotNull
    }

    private fun assertFerdigJournalfoert(dokumentEnhet: DokumentEnhet) {
        assertThat(dokumentEnhet.isAvsluttet())
        assertThat(dokumentEnhet.avsenderMottakerDistribusjoner.size).isEqualTo(dokumentEnhet.avsenderMottakere.size)
        dokumentEnhet.avsenderMottakerDistribusjoner.forEach {
            assertThat(it.journalpostId).isNotNull
            assertThat(it.ferdigstiltIJoark).isNotNull
        }
        assertThat(dokumentEnhet.hovedDokument).isNotNull
    }

    fun ikkeDistribuertDokumentEnhetMedVedleggOgToBrevMottakere(): DokumentEnhet {
        return baseDokumentEnhet
    }

    fun ikkeDistribuertDokumentEnhetMedVedleggOgEnBrevMottakerLokalPrint(): DokumentEnhet {
        return dokumentEnhetWithLocalPrint
    }

    fun ikkeDistribuertNotatDokumentEnhetMedVedleggOgToBrevMottakere(): DokumentEnhet {
        val dokumentEnhet = baseDokumentEnhet
        dokumentEnhet.journalfoeringData.journalpostType = JournalpostType.NOTAT
        return dokumentEnhet
    }

    fun journalfoertMenIkkeDistribuertDokumentEnhetMedVedleggOgToBrevMottakere(): DokumentEnhet {
        val dokumentEnhet = baseDokumentEnhet
        dokumentEnhet.avsenderMottakerDistribusjoner.first().journalpostId = JOURNALPOST_ID_1
        dokumentEnhet.avsenderMottakerDistribusjoner.last().journalpostId = JOURNALPOST_ID_2
        return dokumentEnhet
    }

    fun journalfoertOgDistribuertDokumentEnhetMedVedleggOgToBrevMottakere(): DokumentEnhet {
        val dokumentEnhet = baseDokumentEnhet
        dokumentEnhet.avsenderMottakerDistribusjoner.first().journalpostId = JOURNALPOST_ID_1
        dokumentEnhet.avsenderMottakerDistribusjoner.first().ferdigstiltIJoark = LocalDateTime.now()
        dokumentEnhet.avsenderMottakerDistribusjoner.first().dokdistReferanse = UUID.randomUUID()
        dokumentEnhet.avsenderMottakerDistribusjoner.last().journalpostId = JOURNALPOST_ID_2
        dokumentEnhet.avsenderMottakerDistribusjoner.last().ferdigstiltIJoark = LocalDateTime.now()
        dokumentEnhet.avsenderMottakerDistribusjoner.last().dokdistReferanse = UUID.randomUUID()
        return dokumentEnhet
    }
}