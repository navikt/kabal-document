package no.nav.klage.dokument.service.distribusjon

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import no.nav.klage.dokument.clients.dokdistfordeling.DistribuerJournalpostResponse
import no.nav.klage.dokument.clients.dokdistfordeling.DokDistFordelingClient
import no.nav.klage.dokument.domain.dokument.BrevMottakerDistribusjon
import no.nav.klage.dokument.domain.dokument.JournalpostId
import no.nav.klage.dokument.ikkeDistribuertDokumentEnhetMedVedleggOgToBrevMottakere
import no.nav.klage.dokument.ikkeDistribuertDokumentEnhetUtenVedleggMedToBrevMottakere
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

//TODO: Teste feil-scenarier
internal class BrevMottakerDistribusjonServiceTest {

    private val brevMottakerJournalfoeringService = mockk<BrevMottakerJournalfoeringService>()
    private val dokDistFordelingClient = mockk<DokDistFordelingClient>()

    private val brevMottakerDistribusjonService = BrevMottakerDistribusjonService(
        brevMottakerJournalfoeringService = brevMottakerJournalfoeringService,
        dokDistFordelingClient = dokDistFordelingClient
    )

    @Test
    fun distribuerDokumentEnhetMedVedleggTilBrevMottaker() {

        val dokumentEnhet = ikkeDistribuertDokumentEnhetMedVedleggOgToBrevMottakere()
        val brevMottaker = dokumentEnhet.brevMottakere.first()

        val brevMottakerDistribusjonSlot = slot<BrevMottakerDistribusjon>()
        every {
            brevMottakerJournalfoeringService.opprettJournalpostForBrevMottaker(
                brevMottaker = brevMottaker,
                hoveddokument = dokumentEnhet.hovedDokument!!,
                vedleggDokumentList = dokumentEnhet.vedlegg,
                journalfoeringData = dokumentEnhet.journalfoeringData
            )
        } returns JournalpostId("journalpostId")

        every {
            brevMottakerJournalfoeringService.ferdigstillJournalpostForBrevMottaker(capture(brevMottakerDistribusjonSlot))
        } answers { brevMottakerDistribusjonSlot.captured.copy(ferdigstiltIJoark = LocalDateTime.now()) }

        every { dokDistFordelingClient.distribuerJournalpost(any()) } returns DistribuerJournalpostResponse(UUID.randomUUID())

        val brevMottakerDistribusjon =
            brevMottakerDistribusjonService.distribuerDokumentEnhetTilBrevMottaker(brevMottaker, dokumentEnhet)
        assertFerdigDistribuert(brevMottakerDistribusjon)
    }

    @Test
    fun distribuerDokumentEnhetUtenVedleggTilBrevMottaker() {

        val dokumentEnhet = ikkeDistribuertDokumentEnhetUtenVedleggMedToBrevMottakere()
        val brevMottaker = dokumentEnhet.brevMottakere.first()

        val brevMottakerDistribusjonSlot = slot<BrevMottakerDistribusjon>()
        every {
            brevMottakerJournalfoeringService.opprettJournalpostForBrevMottaker(
                brevMottaker = brevMottaker,
                hoveddokument = dokumentEnhet.hovedDokument!!,
                journalfoeringData = dokumentEnhet.journalfoeringData
            )
        } returns JournalpostId("journalpostId")

        every {
            brevMottakerJournalfoeringService.ferdigstillJournalpostForBrevMottaker(capture(brevMottakerDistribusjonSlot))
        } answers { brevMottakerDistribusjonSlot.captured.copy(ferdigstiltIJoark = LocalDateTime.now()) }

        every { dokDistFordelingClient.distribuerJournalpost(any()) } returns DistribuerJournalpostResponse(UUID.randomUUID())

        val brevMottakerDistribusjon =
            brevMottakerDistribusjonService.distribuerDokumentEnhetTilBrevMottaker(brevMottaker, dokumentEnhet)
        assertFerdigDistribuert(brevMottakerDistribusjon)
    }

    private fun assertFerdigDistribuert(brevMottakerDistribusjon: BrevMottakerDistribusjon?) {
        assertThat(brevMottakerDistribusjon).isNotNull
        assertThat(brevMottakerDistribusjon!!.ferdigstiltIJoark).isNotNull
        assertThat(brevMottakerDistribusjon.dokdistReferanse).isNotNull
    }

}