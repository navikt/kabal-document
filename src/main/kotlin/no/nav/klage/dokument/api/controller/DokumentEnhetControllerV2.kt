package no.nav.klage.dokument.api.controller

import io.swagger.annotations.Api
import no.nav.klage.dokument.api.input.*
import no.nav.klage.dokument.api.mapper.DokumentEnhetInputMapper
import no.nav.klage.dokument.api.mapper.DokumentEnhetMapper
import no.nav.klage.dokument.api.view.DokumentEnhetView
import no.nav.klage.dokument.api.view.DokumentEnhetViewV2
import no.nav.klage.dokument.api.view.HovedDokumentEditedView
import no.nav.klage.dokument.config.SecurityConfiguration.Companion.ISSUER_AAD
import no.nav.klage.dokument.service.DokumentEnhetService
import no.nav.klage.dokument.service.saksbehandler.InnloggetSaksbehandlerService
import no.nav.klage.dokument.util.getLogger
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@Api(tags = ["kabal-document"])
@ProtectedWithClaims(issuer = ISSUER_AAD)
@RequestMapping("/v2/dokumentenheter")
class DokumentEnhetControllerV2(
    private val innloggetSaksbehandlerService: InnloggetSaksbehandlerService,
    private val dokumentEnhetMapper: DokumentEnhetMapper,
    private val dokumentEnhetInputMapper: DokumentEnhetInputMapper,
    private val dokumentEnhetService: DokumentEnhetService
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @PostMapping("/regular")
    fun createDokumentEnhetAndUploadHoveddokument(
        @RequestBody body: DokumentEnhetInputV2,
        @ModelAttribute input: FilInput
    ): DokumentEnhetView {
        logger.debug("Kall mottatt på createDokumentEnhetAndUploadHoveddokument")
        return dokumentEnhetMapper.mapToDokumentEnhetView(
            dokumentEnhetService.opprettDokumentEnhetOgMellomlagreNyttHoveddokument(
                innloggetIdent = innloggetSaksbehandlerService.getInnloggetIdent(),
                brevMottakere = emptyList(),
                journalfoeringData = null,
                dokumentType = body.dokumentType,
                eksternReferanse = body.eksternReferanse,
                fil = input.file
            )
        )
    }

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

    @ResponseBody
    @GetMapping("/{dokumentEnhetId}")
    fun getDokumentEnhet(
        @PathVariable("dokumentEnhetId") dokumentEnhetId: UUID,
    ): DokumentEnhetView {
        logger.debug("Kall mottatt på getDokumentEnhet for $dokumentEnhetId")
        val ident = runCatching { innloggetSaksbehandlerService.getInnloggetIdent() }.getOrNull()
        return dokumentEnhetMapper.mapToDokumentEnhetView(
            dokumentEnhetService.getDokumentEnhet(
                dokumentEnhetId = dokumentEnhetId,
                innloggetIdent = ident
            )
        )
    }

    //TODO: Skal dette være mulig, eller må man slette og lage nytt?
    @PostMapping("/{dokumentEnhetId}/innhold")
    fun uploadHovedDokument(
        @PathVariable("dokumentEnhetId") dokumentEnhetId: UUID,
        @ModelAttribute input: FilInput
    ): HovedDokumentEditedView? {
        logger.debug("Kall mottatt på uploadHovedDokument for $dokumentEnhetId")
        return dokumentEnhetMapper.mapToHovedDokumentEditedView(
            dokumentEnhetService.mellomlagreNyttHovedDokument(
                dokumentEnhetId = dokumentEnhetId,
                fil = input.file,
                innloggetIdent = innloggetSaksbehandlerService.getInnloggetIdent()
            )
        )
    }

    //TODO: Litt avhengig av svaret på sp over, så kanskje dette heller ikke skal være lovlig, men man trenger evt en operasjon for å lage nytt snapshot
    @PostMapping("/{dokumentEnhetId}/smarteditorid")
    fun uploadSmartEditorHovedDokument(
        @PathVariable("dokumentEnhetId") dokumentEnhetId: UUID,
        @RequestBody input: SmartEditorDokumentInput //TODO: Denne inneholder nå for mange felter, må få svar på noen spørsmål før jeg gjør noe med det
    ): HovedDokumentEditedView? {
        logger.debug("Kall mottatt på uploadHovedDokument for $dokumentEnhetId")
        return dokumentEnhetMapper.mapToHovedDokumentEditedView(
            dokumentEnhetService.mellomlagreNyttHovedDokument(
                dokumentEnhetId = dokumentEnhetId,
                smartEditorId = input.smartEditorId,
                innloggetIdent = innloggetSaksbehandlerService.getInnloggetIdent()
            )
        )
    }

    @ResponseBody
    @GetMapping("/{dokumentEnhetId}/hoveddokument")
    fun getHovedDokument(
        @PathVariable("dokumentEnhetId") dokumentEnhetId: UUID,
    ): ResponseEntity<ByteArray> {
        logger.debug("Kall mottatt på getHovedDokument for $dokumentEnhetId")
        return dokumentEnhetMapper.mapToByteArray(
            dokumentEnhetService.hentMellomlagretHovedDokument(
                dokumentEnhetId = dokumentEnhetId,
                innloggetIdent = innloggetSaksbehandlerService.getInnloggetIdent()
            )
        )
    }

    @DeleteMapping("/{dokumentEnhetId}")
    fun deleteDokumentEnhet(
        @PathVariable("dokumentEnhetId") dokumentEnhetId: UUID,
    ) {
        logger.debug("Kall mottatt på deleteDokumentEnhet for $dokumentEnhetId")
        dokumentEnhetService.slettDokumentEnhet(
            dokumentEnhetId = dokumentEnhetId,
            innloggetIdent = innloggetSaksbehandlerService.getInnloggetIdent()
        )
    }

    @PostMapping("/{dokumentEnhetId}/fullfoer")
    fun fullfoerDokumentEnhet(
        @PathVariable("dokumentEnhetId") dokumentEnhetId: UUID,
        @RequestBody input: DokumentEnhetInput
    ) {
        logger.debug("Kall mottatt på fullfoerDokumentEnhet for $dokumentEnhetId")
        val dokumentEnhet = dokumentEnhetService.ferdigstillDokumentEnhet(
            dokumentEnhetId = dokumentEnhetId,
            brevMottakere = dokumentEnhetInputMapper.mapBrevMottakereInput(input.brevMottakere),
            journalfoeringData = dokumentEnhetInputMapper.mapJournalfoeringDataInput(input.journalfoeringData)
        )

        if (!dokumentEnhet.erAvsluttet()) {
            throw RuntimeException("DokumentEnhet (id: $dokumentEnhetId) feilet under fullføring. Se logger.")
        }
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
    fun findDokumentEnheter(
        @RequestParam("eksternReferanse") eksternReferanse: String,
    ): List<DokumentEnhetViewV2> {
        return dokumentEnhetService.findDokumentEnhet(eksternReferanse)
            .map { dokumentEnhetMapper.mapToDokumentEnhetViewV2(it) }
    }

}
