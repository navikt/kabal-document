package no.nav.klage.dokument.config

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import no.nav.klage.auth.test.AzureSecurityContractTest
import no.nav.klage.dokument.api.controller.DokarkivController
import no.nav.klage.dokument.repositories.DokumentEnhetRepository
import no.nav.klage.dokument.service.JournalfoeringService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(DokarkivController::class)
@Import(SecurityConfiguration::class)
class SecurityConfigurationTest : AzureSecurityContractTest() {
    @MockkBean
    private lateinit var journalfoeringService: JournalfoeringService

    // Dependencies of SaksbehandlerFilter, which @WebMvcTest picks up. It skips /dokarkiv requests.
    @MockkBean(relaxed = true)
    private lateinit var currentSaksbehandlerHolder: CurrentSaksbehandlerHolder

    @MockkBean(relaxed = true)
    private lateinit var dokumentEnhetRepository: DokumentEnhetRepository

    @BeforeEach
    fun setUp() {
        every { journalfoeringService.updateDocumentTitle(journalpostId = any(), dokumentInfoId = any(), title = any()) } just runs
    }

    override fun protectedRequest() =
        put("/dokarkiv/journalposter/123/dokumenter/456/title")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""{"title":"Ny tittel"}""")

    override fun assertAccepted(result: ResultActions) {
        result.andExpect(status().isOk)
        verify { journalfoeringService.updateDocumentTitle(journalpostId = "123", dokumentInfoId = "456", title = "Ny tittel") }
    }

    override fun unauthenticatedPaths() = listOf("/internal/health", "/internal/info", "/internal/prometheus")

    @Test
    fun `other internal endpoints require token`() {
        mockMvc.perform(get("/internal/metrics")).andExpect(status().isUnauthorized)
    }
}
