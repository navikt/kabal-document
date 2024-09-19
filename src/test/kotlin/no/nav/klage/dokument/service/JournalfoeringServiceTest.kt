package no.nav.klage.dokument.service

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import no.nav.klage.dokument.clients.joark.JoarkMapper
import no.nav.klage.dokument.clients.joark.JournalpostType
import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.kodeverk.DokumentType
import no.nav.klage.kodeverk.Fagsystem
import no.nav.klage.kodeverk.PartIdType
import no.nav.klage.kodeverk.Tema
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

internal class JournalfoeringServiceTest {
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

    val hovedDokument = OpplastetHoveddokument(
        mellomlagerId = "123",
        name = "Title with \"quotes\"",
        sourceReference = UUID.randomUUID(),
    )

    val vedlegg = OpplastetVedlegg(
        mellomlagerId = "456",
        name = "vedlegg.pdf",
        sourceReference = UUID.randomUUID(),
        index = 0,
    )

    val avsenderMottakerDistribusjon1 = AvsenderMottakerDistribusjon(
        avsenderMottaker = avsenderMottaker1,
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
            datoMottatt = LocalDate.now(),
        ),
        avsenderMottakere = setOf(avsenderMottaker1),
        avsenderMottakerDistribusjoner = setOf(avsenderMottakerDistribusjon1),
        hovedDokument = hovedDokument,
        vedlegg = setOf(vedlegg),
        avsluttet = null,
        journalfoerendeSaksbehandlerIdent = "S123456",
        dokumentType = DokumentType.VEDTAK,
    )

    @Test
    @Disabled
    fun `test new file regime`() {
        val jacksonObjectMapper = jacksonObjectMapper()
        jacksonObjectMapper.registerModule(JavaTimeModule())
        jacksonObjectMapper.dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")


        val mellomlagerService = mockk<MellomlagerService>()
        val journalfoeringService = JournalfoeringService(
            joarkClient = mockk(),
            joarkMapper = JoarkMapper(),
            mellomlagerService = mellomlagerService,
        )

        //NB: Files do get deleted after test.
        every { mellomlagerService.getUploadedDocumentAsSystemUser("123") } returns File("/home/andreas/Documents/mini.pdf")
        every { mellomlagerService.getUploadedDocumentAsSystemUser("456") } returns File("/home/andreas/Documents/mini_2.pdf")

        journalfoeringService.createJournalpostAsSystemUser(
            avsenderMottaker = avsenderMottaker1,
            hoveddokument = hovedDokument,
            vedleggDokumentSet = baseDokumentEnhet.vedlegg,
            journalfoeringData = baseDokumentEnhet.journalfoeringData,
            journalfoerendeSaksbehandlerIdent = baseDokumentEnhet.journalfoerendeSaksbehandlerIdent
        )

    }

    @Test
    @Disabled
    fun `test escaping invalid characters in json`() {
        val jacksonObjectMapper = jacksonObjectMapper()

        val invalidTitle = "Title with \"quotes\""
        val invalidContent = "Content with \n newline"

        println(invalidTitle)
        println(invalidContent)

        println(jacksonObjectMapper.writeValueAsString(invalidTitle))
        println(jacksonObjectMapper.writeValueAsString(invalidContent))
    }

}