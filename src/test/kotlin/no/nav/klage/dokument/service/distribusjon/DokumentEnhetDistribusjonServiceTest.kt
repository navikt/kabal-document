package no.nav.klage.dokument.service.distribusjon

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import no.nav.klage.dokument.delvisDistribuertDokumentEnhetMedToBrevMottakere
import no.nav.klage.dokument.domain.dokument.BrevMottaker
import no.nav.klage.dokument.domain.dokument.BrevMottakerDistribusjon
import no.nav.klage.dokument.domain.dokument.DokumentEnhet
import no.nav.klage.dokument.domain.dokument.JournalpostId
import no.nav.klage.dokument.ikkeDistribuertDokumentEnhetMedVedleggOgToBrevMottakere
import no.nav.klage.dokument.journalfoertMenIkkeDistribuertDokumentEnhetMedEnBrevMottakere
import no.nav.klage.dokument.service.MellomlagerService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

//TODO: Teste feil-scenarier
internal class DokumentEnhetDistribusjonServiceTest {

    private val mellomlagerService = mockk<MellomlagerService>()
    private val brevMottakerDistribusjonService = mockk<BrevMottakerDistribusjonService>()

    val dokumentEnhetDistribusjonService = DokumentEnhetDistribusjonService(
        brevMottakerDistribusjonService = brevMottakerDistribusjonService,
        mellomlagerService = mellomlagerService
    )

    @Test
    fun `ikke-distribuert dokumentEnhet skal distribueres korrekt`() {

        val dokumentEnhetTilDist = ikkeDistribuertDokumentEnhetMedVedleggOgToBrevMottakere()
        val brevMottakerSlot = slot<BrevMottaker>()
        val dokumentEnhetSlot = slot<DokumentEnhet>()

        every { mellomlagerService.deleteDocumentAsSystemUser(dokumentEnhetTilDist.hovedDokument!!.mellomlagerId) } returns Unit
        every {
            brevMottakerDistribusjonService.journalfoerOgDistribuerDokumentEnhetTilBrevMottaker(
                capture(brevMottakerSlot),
                capture(dokumentEnhetSlot)
            )
        } answers {
            BrevMottakerDistribusjon(
                brevMottakerId = brevMottakerSlot.captured.id,
                opplastetDokumentId = dokumentEnhetSlot.captured.hovedDokument!!.id,
                journalpostId = JournalpostId("random"),
                ferdigstiltIJoark = LocalDateTime.now(),
                dokdistReferanse = UUID.randomUUID(),
                dokumentEnhetId = dokumentEnhetTilDist.id,
            )
        }

        assertFerdigDistribuert(dokumentEnhetDistribusjonService.journalfoerOgDistribuerDokumentEnhet(dokumentEnhetTilDist))
    }

    @Test
    fun `journalfoert men ikke-distribuert dokumentEnhet skal distribueres korrekt`() {

        val dokumentEnhetTilDist =
            journalfoertMenIkkeDistribuertDokumentEnhetMedEnBrevMottakere(UUID.randomUUID(), UUID.randomUUID())

        val brevMottakerSlot = slot<BrevMottaker>()
        val dokumentEnhetSlot = slot<DokumentEnhet>()

        every { mellomlagerService.deleteDocumentAsSystemUser(dokumentEnhetTilDist.hovedDokument!!.mellomlagerId) } returns Unit
        every {
            brevMottakerDistribusjonService.journalfoerOgDistribuerDokumentEnhetTilBrevMottaker(
                capture(brevMottakerSlot),
                capture(dokumentEnhetSlot)
            )
        } answers {
            BrevMottakerDistribusjon(
                brevMottakerId = brevMottakerSlot.captured.id,
                opplastetDokumentId = dokumentEnhetSlot.captured.hovedDokument!!.id,
                journalpostId = JournalpostId("random"),
                ferdigstiltIJoark = LocalDateTime.now(),
                dokdistReferanse = UUID.randomUUID(),
                dokumentEnhetId = dokumentEnhetTilDist.id,
            )
        }

        assertFerdigDistribuert(dokumentEnhetDistribusjonService.journalfoerOgDistribuerDokumentEnhet(dokumentEnhetTilDist))
    }

    @Test
    fun `delvis distribuert dokumentEnhet skal distribueres korrekt`() {

        val dokumentEnhetTilDist =
            delvisDistribuertDokumentEnhetMedToBrevMottakere(UUID.randomUUID(), UUID.randomUUID())

        val brevMottakerSlot = slot<BrevMottaker>()
        val dokumentEnhetSlot = slot<DokumentEnhet>()

        every { mellomlagerService.deleteDocumentAsSystemUser(dokumentEnhetTilDist.hovedDokument!!.mellomlagerId) } returns Unit
        every {
            brevMottakerDistribusjonService.journalfoerOgDistribuerDokumentEnhetTilBrevMottaker(
                capture(brevMottakerSlot),
                capture(dokumentEnhetSlot)
            )
        } answers {
            BrevMottakerDistribusjon(
                brevMottakerId = brevMottakerSlot.captured.id,
                opplastetDokumentId = dokumentEnhetSlot.captured.hovedDokument!!.id,
                journalpostId = JournalpostId("random"),
                ferdigstiltIJoark = LocalDateTime.now(),
                dokdistReferanse = UUID.randomUUID(),
                dokumentEnhetId = dokumentEnhetTilDist.id,
            )
        }

        assertFerdigDistribuert(dokumentEnhetDistribusjonService.journalfoerOgDistribuerDokumentEnhet(dokumentEnhetTilDist))
    }

    @Test
    fun `ikke-distribuert dokumentEnhet skal lagres korrekt når feil oppstår`() {

        val dokumentEnhetTilDist = ikkeDistribuertDokumentEnhetMedVedleggOgToBrevMottakere()
        val brevMottakerSlot = slot<BrevMottaker>()
        val dokumentEnhetSlot = slot<DokumentEnhet>()

        every { mellomlagerService.deleteDocumentAsSystemUser(dokumentEnhetTilDist.hovedDokument!!.mellomlagerId) } returns Unit
        every {
            brevMottakerDistribusjonService.journalfoerOgDistribuerDokumentEnhetTilBrevMottaker(
                capture(brevMottakerSlot),
                capture(dokumentEnhetSlot)
            )
        } answers {
            BrevMottakerDistribusjon(
                brevMottakerId = brevMottakerSlot.captured.id,
                opplastetDokumentId = dokumentEnhetSlot.captured.hovedDokument!!.id,
                journalpostId = JournalpostId("random"),
                ferdigstiltIJoark = LocalDateTime.now(),
                dokdistReferanse = null,
                dokumentEnhetId = dokumentEnhetTilDist.id,
            )
        }

        assertHarBrevmottakerDistribusjoner(
            dokumentEnhetDistribusjonService.journalfoerOgDistribuerDokumentEnhet(
                dokumentEnhetTilDist
            )
        )
    }

    private fun assertFerdigDistribuert(dokumentEnhet: DokumentEnhet) {
        assertThat(dokumentEnhet.erAvsluttet())
        assertThat(dokumentEnhet.brevMottakerDistribusjoner.size).isEqualTo(dokumentEnhet.brevMottakere.size)
        dokumentEnhet.brevMottakerDistribusjoner.forEach {
            assertThat(it.ferdigstiltIJoark).isNotNull
            assertThat(it.dokdistReferanse).isNotNull
        }
        assertThat(dokumentEnhet.hovedDokument).isNotNull
    }

    private fun assertHarBrevmottakerDistribusjoner(dokumentEnhet: DokumentEnhet) {
        assertThat(dokumentEnhet.brevMottakerDistribusjoner.size).isEqualTo(dokumentEnhet.brevMottakere.size)
        dokumentEnhet.brevMottakerDistribusjoner.forEach {
            assertThat(it.ferdigstiltIJoark).isNotNull
            assertThat(it.dokdistReferanse).isNull()
        }
        assertThat(dokumentEnhet.hovedDokument).isNotNull
    }
}