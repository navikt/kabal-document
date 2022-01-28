package no.nav.klage.dokument.api.controller

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import io.mockk.every
import no.nav.klage.dokument.api.input.TilleggsopplysningInput
import no.nav.klage.dokument.api.mapper.DokumentEnhetInputMapper
import no.nav.klage.dokument.api.mapper.DokumentEnhetMapper
import no.nav.klage.dokument.api.view.DokumentEnhetView
import no.nav.klage.dokument.domain.saksbehandler.SaksbehandlerIdent
import no.nav.klage.dokument.ikkeDistribuertDokumentEnhetMedToBrevMottakere
import no.nav.klage.dokument.service.DokumentEnhetService
import no.nav.klage.dokument.service.saksbehandler.InnloggetSaksbehandlerService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@WebMvcTest(DokumentEnhetController::class)
@ActiveProfiles("local")
internal class DokumentEnhetControllerTest {

    @MockkBean
    private lateinit var innloggetSaksbehandlerService: InnloggetSaksbehandlerService

    @MockkBean
    private lateinit var dokumentEnhetService: DokumentEnhetService

    @SpykBean
    private lateinit var dokumentEnhetMapper: DokumentEnhetMapper

    @SpykBean
    private lateinit var dokumentEnhetInputMapper: DokumentEnhetInputMapper

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val objectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())

    @Test
    fun createDokumentEnhetAndUploadHoveddokument() {

        val ikkeDistribuertDokumentEnhetMedToBrevMottakere = ikkeDistribuertDokumentEnhetMedToBrevMottakere()
        every { innloggetSaksbehandlerService.getInnloggetIdent() } returns SaksbehandlerIdent("IDENT")
        every {
            dokumentEnhetService.opprettDokumentEnhetOgMellomlagreNyttHoveddokument(
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns ikkeDistribuertDokumentEnhetMedToBrevMottakere

        val dokumentEnhetInput = GammelDokumentEnhetInput(
            brevMottakere = listOf(
                GammelDokumentEnhetInput.GammelBrevMottakerInput(
                    partId = GammelDokumentEnhetInput.GammelPartIdInput(type = "PERSON", value = "20022012345"),
                    navn = "Mottaker Person",
                    rolle = "HOVEDADRESSAT"
                )
            ),
            journalfoeringData = GammelDokumentEnhetInput.GammelJournalfoeringDataInput(
                sakenGjelder = GammelDokumentEnhetInput.GammelPartIdInput(
                    type = "PERSON",
                    value = "01011012345"
                ),
                tema = "OMS",
                sakFagsakId = null,
                sakFagsystem = null,
                kildeReferanse = "whatever",
                enhet = "0203",
                behandlingstema = "behandlingstema",
                tittel = "tittel",
                brevKode = "brevkode",
                tilleggsopplysning = null
            )

        )
        val file =
            MockMultipartFile("file", "file-name.pdf", "application/pdf", "whatever".toByteArray())

        val json = mockMvc.perform(
            MockMvcRequestBuilders.multipart("/dokumentenheter/innhold")
                .file(file)
                .content(objectMapper.writeValueAsString(dokumentEnhetInput))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andReturn().response.contentAsString

        val dokumentEnhet = objectMapper.readValue(json, DokumentEnhetView::class.java)
        assertThat(dokumentEnhet).isNotNull
        assertThat(dokumentEnhet.id).isEqualTo(ikkeDistribuertDokumentEnhetMedToBrevMottakere.id.toString())
    }
}

data class GammelDokumentEnhetInput(
    val brevMottakere: List<GammelBrevMottakerInput>,
    val journalfoeringData: GammelJournalfoeringDataInput
) {
    data class GammelBrevmottakereInput(
        val brevMottakere: List<GammelBrevMottakerInput>
    )

    data class GammelBrevMottakerInput(
        val partId: GammelPartIdInput,
        val navn: String?,
        val rolle: String,
    )

    data class GammelJournalfoeringDataInput(
        val sakenGjelder: GammelPartIdInput,
        val tema: String,
        val sakFagsakId: String?,
        val sakFagsystem: String?,
        val kildeReferanse: String,
        val enhet: String,
        val behandlingstema: String,
        val tittel: String,
        val brevKode: String,
        val tilleggsopplysning: TilleggsopplysningInput?
    )


    data class GammelPartIdInput(val type: String, val value: String)

}