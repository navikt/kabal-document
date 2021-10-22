package no.nav.klage.dokument.api.controller

import no.nav.klage.dokument.api.view.DokumentEnhetFullfoertView
import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.dokument.domain.kodeverk.PartIdType
import no.nav.klage.dokument.domain.kodeverk.Rolle
import no.nav.klage.dokument.domain.kodeverk.Tema
import no.nav.klage.dokument.domain.saksbehandler.SaksbehandlerIdent
import no.nav.klage.dokument.service.DokumentEnhetService
import no.nav.klage.dokument.util.getLogger
import no.nav.security.token.support.core.api.Unprotected
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.commons.CommonsMultipartFile
import java.util.*


@Profile("dev-gcp")
@RestController
@RequestMapping("test")
class TestController(
    private val dokumentEnhetService: DokumentEnhetService
) {

    companion object {
        private val logger = getLogger(TestController::class.java)

        private const val BREV_TITTEL = "Brev fra Klageinstans"
        private const val BREVKODE = "BREV_FRA_KLAGEINSTANS"
        private const val BEHANDLINGSTEMA_KLAGE_KLAGEINSTANS = "ab0164"
        private const val KLAGEBEHANDLING_ID_KEY = "klagebehandling_id"

        private val testSaksbehandler = SaksbehandlerIdent("Z994488")
    }

    @Unprotected
    @PostMapping("/integration")
    fun lagreOgDistribuerDokumentEnhet(): DokumentEnhetFullfoertView {
        try {
            val dokumentEnhet: DokumentEnhet = dokumentEnhetService.opprettDokumentEnhet(
                testSaksbehandler,
                brevMottakere(),
                journalfoeringData()
            )

            val oppdatertDokumentEnhet: DokumentEnhet = dokumentEnhetService.mellomlagreNyttHovedDokument(
                dokumentEnhet.id,
                getMultipartFile(),
                testSaksbehandler,
                true
            )

            val ferdigstiltDokumentEnhet: DokumentEnhet = dokumentEnhetService.ferdigstillDokumentEnhet(
                oppdatertDokumentEnhet.id
            )
            logger.info("Integrasjontest ferdig, resultat ${ferdigstiltDokumentEnhet.erAvsluttet()}")
            return DokumentEnhetFullfoertView(ferdigstiltDokumentEnhet.erAvsluttet())
        } catch (t: Throwable) {
            logger.error("Integrasjontest feilet", t)
            return DokumentEnhetFullfoertView(false)
        }
    }

    private fun getMultipartFile(): MultipartFile {
        val inputStream = TestController::class.java.getResourceAsStream("/testdata/test.pdf")
        val fileItem = DiskFileItemFactory().createItem(
            "file", "application/pdf", false, "test.pdf"
        )
        inputStream.use { input -> fileItem.outputStream.use { out -> input.transferTo(out) } }
        return CommonsMultipartFile(fileItem)
    }

    fun journalfoeringData() = JournalfoeringData(
        sakenGjelder = PartId(
            type = PartIdType.PERSON,
            value = randomFnr()
        ),
        tema = Tema.OMS,
        sakFagsakId = null,
        sakFagsystem = null,
        kildeReferanse = UUID.randomUUID().toString(),
        enhet = "4291",
        behandlingstema = BEHANDLINGSTEMA_KLAGE_KLAGEINSTANS,
        tittel = BREV_TITTEL,
        brevKode = BREVKODE,
        tilleggsopplysning = Tilleggsopplysning(KLAGEBEHANDLING_ID_KEY, UUID.randomUUID().toString())
    )

    fun brevMottakere() = listOf(
        BrevMottaker(
            partId = PartId(
                type = PartIdType.PERSON,
                value = randomFnr()
            ),
            navn = "Klager Person",
            rolle = Rolle.KOPIADRESSAT
        ),
        BrevMottaker(
            partId = PartId(
                type = PartIdType.PERSON,
                value = randomFnr()
            ),
            navn = "Prosessfullmektig Person",
            rolle = Rolle.HOVEDADRESSAT
        )
    )

    fun randomFnr(): String = listOf(
        "02446701749",
        "29437117843",
        "25438301286",
        "18496900509",
        "28416904490",
        "17457337760",
        "16498818653",
        "20467938577",
        "14437830275",
        "18418507701",
        "12518603068"
    ).shuffled().first()

}