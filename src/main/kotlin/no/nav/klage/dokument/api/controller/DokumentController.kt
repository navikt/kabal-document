package no.nav.klage.dokument.api.controller

import io.swagger.annotations.Api
import no.nav.klage.dokument.api.input.*
import no.nav.klage.dokument.api.input.dokument.HovedDokumentInput
import no.nav.klage.dokument.api.input.dokument.HovedDokumentView
import no.nav.klage.dokument.api.view.DokumentEnhetView
import no.nav.klage.dokument.config.SecurityConfiguration
import no.nav.klage.dokument.domain.dokument.BrevMottaker
import no.nav.klage.dokument.domain.dokument.JournalfoeringData
import no.nav.klage.dokument.service.DokumentEnhetService
import no.nav.klage.dokument.service.DokumentService
import no.nav.klage.dokument.service.saksbehandler.InnloggetSaksbehandlerService
import no.nav.klage.dokument.util.getLogger
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@Api(tags = ["kabal-document"])
@ProtectedWithClaims(issuer = SecurityConfiguration.ISSUER_AAD)
@RequestMapping("/dokumenter")
class DokumentController(
    private val dokumentEnhetService: DokumentEnhetService,
    private val dokumentService: DokumentService,
    private val innloggetSaksbehandlerService: InnloggetSaksbehandlerService,
    private val dokumentMapper: DokumentMapper,
    private val dokumenInputMapper: DokumentInputMapper,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @PostMapping("/hoveddokumenter")
    fun createAndUploadHoveddokument(
        @RequestBody body: HovedDokumentInput,
        @ModelAttribute input: FilInput? //TODO: Kan denne være nullable? Må lage test!
    ): HovedDokumentView {
        logger.debug("Kall mottatt på createAndUploadHoveddokument")
        return dokumentMapper.mapToDokumentEnhetView(
            dokumentService.opprettOgMellomlagreNyttHoveddokument(
                innloggetIdent = innloggetSaksbehandlerService.getInnloggetIdent(),
                dokumentType = body.dokumentType,
                eksternReferanse = body.eksternReferanse,
                smartEditorId = body.smartEditorId,
                opplastetFil = input?.file
            )
        )
    }

    /*
    @PostMapping("/smart")
    fun createDokumentEnhetAndSmartEditorDokument(
        @RequestBody input: SmartEditorDokumentInput
    ): DokumentEnhetView {
        logger.debug("Kall mottatt på createDokumentEnhetAndSmartEditorDokument")
        return dokumentEnhetMapper.mapToDokumentEnhetView(
            dokumentEnhetService.opprettDokumentEnhetOgMellomlagreNyttSmartEditorHoveddokument(
                innloggetIdent = innloggetSaksbehandlerService.getInnloggetIdent(),
                brevMottakere = emptyList(),
                journalfoeringData = null,
                smartEditorId = input.smartEditorId,
                dokumentType = input.dokumentType,
                eksternReferanse = input.eksternReferanse,
            )
        )
    }

     */

    //TODO: Har hoppet over endepunkter for å oppdatere dokumentet

    @ResponseBody
    @GetMapping("/{dokumentId}/pdf")
    fun getPdf(
        @PathVariable("dokumentId") dokumentId: UUID,
    ): ResponseEntity<ByteArray> {
        logger.debug("Kall mottatt på getPdf for $dokumentId")
        return dokumentMapper.mapToByteArray(
            dokumentService.hentMellomlagretDokument(
                dokumentId = dokumentId,
                innloggetIdent = innloggetSaksbehandlerService.getInnloggetIdent()
            )
        )
    }

    @DeleteMapping("/{dokumentId}")
    fun deleteDokument(
        @PathVariable("dokumentId") dokumentId: UUID,
    ) {
        logger.debug("Kall mottatt på deleteDokumentEnhet for $dokumentId")
        dokumentService.slettDokument(
            dokumentId = dokumentId,
            innloggetIdent = innloggetSaksbehandlerService.getInnloggetIdent()
        )
    }

    @PostMapping("/{dokumentEnhetId}/vedlegg")
    fun kobleVedlegg(
        @PathVariable("dokumentEnhetId") dokumentEnhetId: UUID,
        @RequestBody input: DokumentEnhetIdInput
    ): DokumentEnhetView {
        logger.debug("Kall mottatt på kobleVedlegg for $dokumentEnhetId")
        return dokumentEnhetMapper.mapToDokumentEnhetView(
            dokumentEnhetService.kobleVedlegg(
                dokumentEnhetId = dokumentEnhetId,
                dokumentEnhetSomSkalBliVedleggId = input.id,
                innloggetIdent = innloggetSaksbehandlerService.getInnloggetIdent()
            )
        )
    }

    @DeleteMapping("/{dokumentEnhetId}/vedlegg/{vedleggId}")
    fun fristillVedlegg(
        @PathVariable("dokumentEnhetId") dokumentEnhetId: UUID,
        @PathVariable("vedleggId") vedleggId: UUID,
    ): DokumentEnhetView {
        logger.debug("Kall mottatt på fristillVedlegg for $dokumentEnhetId og $vedleggId")
        return dokumentEnhetMapper.mapToDokumentEnhetView(
            dokumentEnhetService.fristillVedlegg(
                dokumentEnhetId = dokumentEnhetId,
                vedleggId = vedleggId,
                innloggetIdent = innloggetSaksbehandlerService.getInnloggetIdent()
            )
        )
    }

    @ResponseBody
    @GetMapping("/{dokumentEnhetId}/vedlegg/{vedleggId}")
    fun getVedlegg(
        @PathVariable("dokumentEnhetId") dokumentEnhetId: UUID,
        @PathVariable("vedleggId") vedleggId: UUID,
    ): ResponseEntity<ByteArray> {
        logger.debug("Kall mottatt på getVedlegg for $dokumentEnhetId og $vedleggId")
        return dokumentEnhetMapper.mapToByteArray(
            dokumentEnhetService.hentMellomlagretVedlegg(
                dokumentEnhetId = dokumentEnhetId,
                vedleggId = vedleggId,
                innloggetIdent = innloggetSaksbehandlerService.getInnloggetIdent()
            )
        )
    }

    @GetMapping
    fun findHovedDokumenter(
        @RequestParam("eksternReferanse") eksternReferanse: String,
    ): List<HovedDokumentView> {
        return dokumentService.findHovedDokumenter(eksternReferanse)
            .map { dokumentMapper.mapToDokumentView(it) }
    }

    @PostMapping("/{hoveddokumentid}/ferdigstill")
    //TODO: Må mappes fra input-klasser
    fun idempotentOpprettOgFerdigstillDokumentEnhetFraHovedDokument(
        hovedDokumentId: UUID,
        brevMottakere: List<BrevMottaker>,
        journalfoeringData: JournalfoeringData
    ) {
        val ident = innloggetSaksbehandlerService.getInnloggetIdent()

        //Alle de tre påfølgende stegene kjøres som egne transaksjoner og de to første skal være idempotente
        val hovedDokument = dokumentService.finnOgFerdigstillHovedDokument(hovedDokumentId)
        val dokumentEnhet = dokumentEnhetService.finnEllerOpprettDokumentEnhetFraHovedDokument(
            eier = ident,
            brevMottakere = brevMottakere,
            journalfoeringData = journalfoeringData,
            hovedDokument = hovedDokument,
        )
        val ferdigstiltDokumentEnhet = dokumentEnhetService.ferdigstillDokumentEnhet(dokumentEnhetId = dokumentEnhet.id)
        if (!ferdigstiltDokumentEnhet.erAvsluttet()) {
            throw RuntimeException("DokumentEnhet (id: ${ferdigstiltDokumentEnhet.id}) feilet under fullføring. Se logger.")
        }
    }
}