package no.nav.klage.dokument.api.controller

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.klage.dokument.api.mapper.DokumentEnhetMapper
import no.nav.klage.dokument.api.view.DokumentEnhetFullfoertView
import no.nav.klage.dokument.clients.klageunleashproxy.KlageUnleashProxyClient
import no.nav.klage.dokument.domain.dokument.DokumentEnhet
import no.nav.klage.dokument.service.DokumentEnhetService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.util.UUID

internal class DokumentEnhetControllerTest {
    private val dokumentEnhetMapper = mockk<DokumentEnhetMapper>()
    private val dokumentEnhetService = mockk<DokumentEnhetService>()
    private val klageUnleashProxyClient = mockk<KlageUnleashProxyClient>()
    private val controller =
        DokumentEnhetController(
            dokumentEnhetMapper = dokumentEnhetMapper,
            dokumentEnhetService = dokumentEnhetService,
            klageUnleashProxyClient = klageUnleashProxyClient,
        )

    @Test
    fun `should throw before completing document unit when failure toggle is enabled`() {
        val dokumentEnhetId = UUID.randomUUID()
        every { klageUnleashProxyClient.isEnabled(FAIL_FERDIGSTILLING_KABAL_DOCUMENT_TOGGLE) } returns true

        assertThatThrownBy { controller.fullfoerDokumentEnhet(dokumentEnhetId) }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessageContaining(FAIL_FERDIGSTILLING_KABAL_DOCUMENT_TOGGLE)

        verify(exactly = 1) { klageUnleashProxyClient.isEnabled(FAIL_FERDIGSTILLING_KABAL_DOCUMENT_TOGGLE) }
        verify(exactly = 0) { dokumentEnhetService.ferdigstillDokumentEnhet(any()) }
        verify(exactly = 0) { dokumentEnhetMapper.mapToDokumentEnhetFullfoertView(any()) }
    }

    @Test
    fun `should complete and map document unit when failure toggle is disabled`() {
        val dokumentEnhetId = UUID.randomUUID()
        val dokumentEnhet = mockk<DokumentEnhet>()
        val expectedView = DokumentEnhetFullfoertView(sourceReferenceWithJoarkReferencesList = emptyList())
        every { klageUnleashProxyClient.isEnabled(FAIL_FERDIGSTILLING_KABAL_DOCUMENT_TOGGLE) } returns false
        every { dokumentEnhetService.ferdigstillDokumentEnhet(dokumentEnhetId) } returns dokumentEnhet
        every { dokumentEnhet.isAvsluttet() } returns true
        every { dokumentEnhetMapper.mapToDokumentEnhetFullfoertView(dokumentEnhet) } returns expectedView

        assertThat(controller.fullfoerDokumentEnhet(dokumentEnhetId)).isEqualTo(expectedView)

        verify(exactly = 1) { dokumentEnhetService.ferdigstillDokumentEnhet(dokumentEnhetId) }
        verify(exactly = 1) { dokumentEnhetMapper.mapToDokumentEnhetFullfoertView(dokumentEnhet) }
    }

    @Test
    fun `should throw when document unit is not closed and failure toggle is disabled`() {
        val dokumentEnhetId = UUID.randomUUID()
        val dokumentEnhet = mockk<DokumentEnhet>()
        every { klageUnleashProxyClient.isEnabled(FAIL_FERDIGSTILLING_KABAL_DOCUMENT_TOGGLE) } returns false
        every { dokumentEnhetService.ferdigstillDokumentEnhet(dokumentEnhetId) } returns dokumentEnhet
        every { dokumentEnhet.isAvsluttet() } returns false

        assertThatThrownBy { controller.fullfoerDokumentEnhet(dokumentEnhetId) }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessageContaining("feilet under fullføring")

        verify(exactly = 0) { dokumentEnhetMapper.mapToDokumentEnhetFullfoertView(any()) }
    }

    private companion object {
        const val FAIL_FERDIGSTILLING_KABAL_DOCUMENT_TOGGLE = "fail-ferdigstilling-kabal-document"
    }
}
