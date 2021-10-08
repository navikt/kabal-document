package no.nav.klage.dokument.service

import io.mockk.mockk
import no.nav.klage.dokument.util.AttachmentValidator
import java.util.*

class VedtakServiceTest {

    val attachmentValidator = mockk<AttachmentValidator>()

    val mellomlagerService = mockk<MellomlagerService>()

    val vedtakService = VedtakService(
        attachmentValidator,
        mellomlagerService,
    )
    private val SAKSBEHANDLER_IDENT = "SAKSBEHANDLER_IDENT"
    private val MEDUNDERSKRIVER_IDENT = "MEDUNDERSKRIVER_IDENT"
    private val JOURNALFOERENDE_ENHET = "1234"
    private val MELLOMLAGER_ID = "MELLOMLAGER_ID"
    private val KLAGEBEHANDLING_ID = UUID.randomUUID()


    /*
    @Test
    fun `Forsøk på avslutting av vedtak fra andre enn medunderskriver skal ikke lykkes`() {
        every { klagebehandlingService.getKlagebehandlingForUpdate(any(), any()) } returns getKlageBehandling()
        assertThrows<MissingTilgangException> {
            vedtakService.ferdigstillVedtak(
                KLAGEBEHANDLING_ID,
                VedtakFullfoerInput(
                    JOURNALFOERENDE_ENHET
                ),
                SAKSBEHANDLER_IDENT
            )
        }
    }

    @Test
    fun `Forsøk på avslutting av vedtak som allerede er ferdigstilt i Joark skal ikke lykkes`() {
        every {
            klagebehandlingService.getKlagebehandlingForUpdate(
                any(),
                any()
            )
        } returns getFerdigstiltKlagebehandling()
        assertThrows<VedtakFinalizedException> {
            vedtakService.ferdigstillVedtak(
                KLAGEBEHANDLING_ID,
                VedtakFullfoerInput(
                    JOURNALFOERENDE_ENHET
                ),
                MEDUNDERSKRIVER_IDENT
            )
        }
    }

    @Test
    fun `Forsøk på avslutting av vedtak som ikke har mellomlagret dokument skal ikke lykkes`() {
        every { klagebehandlingService.getKlagebehandlingForUpdate(any(), any()) } returns getKlageBehandling()
        assertThrows<VedtakNotFoundException> {
            vedtakService.ferdigstillVedtak(
                KLAGEBEHANDLING_ID,
                VedtakFullfoerInput(
                    JOURNALFOERENDE_ENHET
                ),
                MEDUNDERSKRIVER_IDENT
            )
        }
    }

    @Test
    fun `Forsøk på avslutting av vedtak som ikke har utfall skal ikke lykkes`() {
        every { klagebehandlingService.getKlagebehandlingForUpdate(any(), any()) } returns getKlageBehandling()
        assertThrows<VedtakNotFoundException> {
            vedtakService.ferdigstillVedtak(
                KLAGEBEHANDLING_ID,
                VedtakFullfoerInput(
                    JOURNALFOERENDE_ENHET
                ),
                MEDUNDERSKRIVER_IDENT
            )
        }
    }

    @Test
    fun `Forsøk på avslutting av vedtak som er riktig utfylt skal lykkes`() {
        every {
            klagebehandlingService.getKlagebehandlingForUpdate(
                any(),
                any()
            )
        } returns getKlagebehandlingMedUtfall()
        every {
            klagebehandlingService.markerKlagebehandlingSomAvsluttetAvSaksbehandler(
                any(),
                any()
            )
        } returns getFerdigstiltKlagebehandling()

        val result = vedtakService.ferdigstillVedtak(
            KLAGEBEHANDLING_ID,
            VedtakFullfoerInput(
                JOURNALFOERENDE_ENHET
            ),
            MEDUNDERSKRIVER_IDENT
        )
        assert(result.avsluttetAvSaksbehandler != null)
    }

    private fun getKlageBehandling(): Klagebehandling {
        return Klagebehandling(
            innsender = Innsender(partId = PartId(type = PartIdType.PERSON, value = "23452354")),
            sakenGjelder = SakenGjelder(
                partId = PartId(type = PartIdType.PERSON, value = "23452354"),
                skalMottaKopi = false
            ),
            tema = Tema.OMS,
            type = Type.KLAGE,
            frist = LocalDate.now(),
            hjemler = mutableSetOf(
                Hjemmel.FTL_8_7
            ),
            created = LocalDateTime.now(),
            modified = LocalDateTime.now(),
            mottattKlageinstans = LocalDateTime.now(),
            kildesystem = Fagsystem.K9,
            mottakId = UUID.randomUUID(),
            vedtak = Vedtak(
                hjemler = mutableSetOf(
                    Hjemmel.FTL
                )
            ),
            medunderskriver = MedunderskriverTildeling(
                MEDUNDERSKRIVER_IDENT,
                LocalDateTime.now()
            )
        )
    }

    private fun getFerdigstiltKlagebehandling(): Klagebehandling {
        return getKlageBehandling().apply { avsluttetAvSaksbehandler = LocalDateTime.now() }
    }

    private fun getKlagebehandlingMedUtfall(): Klagebehandling {
        return getKlageBehandling().apply {
            getVedtakOrException().utfall = Utfall.AVVIST
            getVedtakOrException().mellomlagerId = MELLOMLAGER_ID
        }
    }

     */
}