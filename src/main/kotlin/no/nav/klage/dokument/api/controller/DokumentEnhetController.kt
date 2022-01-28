package no.nav.klage.dokument.api.controller

import io.swagger.annotations.Api
import no.nav.klage.dokument.api.input.BrevmottakereInput
import no.nav.klage.dokument.api.input.DokumentEnhetInput
import no.nav.klage.dokument.api.input.FilInput
import no.nav.klage.dokument.api.input.JournalfoeringDataInput
import no.nav.klage.dokument.api.mapper.DokumentEnhetInputMapper
import no.nav.klage.dokument.api.mapper.DokumentEnhetMapper
import no.nav.klage.dokument.api.view.DokumentEnhetFullfoertView
import no.nav.klage.dokument.api.view.DokumentEnhetView
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
@RequestMapping("/dokumentenheter")
class DokumentEnhetController(
    private val innloggetSaksbehandlerService: InnloggetSaksbehandlerService,
    private val dokumentEnhetMapper: DokumentEnhetMapper,
    private val dokumentEnhetInputMapper: DokumentEnhetInputMapper,
    private val dokumentEnhetService: DokumentEnhetService
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @PostMapping
    fun createDokumentEnhet(
        @RequestBody input: DokumentEnhetInput
    ): DokumentEnhetView {
        logger.debug("Kall mottatt på createDokumentEnhet")
        return dokumentEnhetMapper.mapToDokumentEnhetView(
            dokumentEnhetService.opprettDokumentEnhet(
                innloggetSaksbehandlerService.getInnloggetIdent(),
                input.dokumentType,
                input.eksternReferanse,
                dokumentEnhetInputMapper.mapBrevMottakereInput(input.brevMottakere),
                dokumentEnhetInputMapper.mapJournalfoeringDataInput(input.journalfoeringData)
            )
        )
    }

    @PostMapping("/innhold")
    fun createDokumentEnhetAndUploadHoveddokument(
        @RequestBody body: DokumentEnhetInput,
        @ModelAttribute input: FilInput
    ): DokumentEnhetView {
        logger.debug("Kall mottatt på createDokumentEnhetAndUploadHoveddokument")
        return dokumentEnhetMapper.mapToDokumentEnhetView(
            dokumentEnhetService.opprettDokumentEnhetOgMellomlagreNyttHoveddokument(
                innloggetIdent = innloggetSaksbehandlerService.getInnloggetIdent(),
                dokumentType = body.dokumentType,
                eksternReferanse = body.eksternReferanse,
                brevMottakere = dokumentEnhetInputMapper.mapBrevMottakereInput(body.brevMottakere),
                journalfoeringData = dokumentEnhetInputMapper.mapJournalfoeringDataInput(body.journalfoeringData),
                fil = input.file
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
                dokumentEnhetId,
                ident
            )
        )
    }

    @PostMapping("/{dokumentEnhetId}/innhold")
    fun uploadHovedDokument(
        @PathVariable("dokumentEnhetId") dokumentEnhetId: UUID,
        @ModelAttribute input: FilInput
    ): HovedDokumentEditedView? {
        logger.debug("Kall mottatt på uploadHovedDokument for $dokumentEnhetId")
        return dokumentEnhetMapper.mapToHovedDokumentEditedView(
            dokumentEnhetService.mellomlagreNyttHovedDokument(
                dokumentEnhetId,
                input.file,
                innloggetSaksbehandlerService.getInnloggetIdent()
            )
        )
    }

    @ResponseBody
    @GetMapping("/{dokumentEnhetId}/innhold")
    fun getHovedDokument(
        @PathVariable("dokumentEnhetId") dokumentEnhetId: UUID,
    ): ResponseEntity<ByteArray> {
        logger.debug("Kall mottatt på getHovedDokument for $dokumentEnhetId")
        return dokumentEnhetMapper.mapToByteArray(
            dokumentEnhetService.hentMellomlagretHovedDokument(
                dokumentEnhetId,
                innloggetSaksbehandlerService.getInnloggetIdent()
            )
        )
    }

    @DeleteMapping("/{dokumentEnhetId}/innhold")
    fun deleteHovedDokument(
        @PathVariable("dokumentEnhetId") dokumentEnhetId: UUID,
    ): HovedDokumentEditedView {
        logger.debug("Kall mottatt på deleteHovedDokument for $dokumentEnhetId")
        return dokumentEnhetMapper.mapToHovedDokumentEditedView(
            dokumentEnhetService.slettMellomlagretHovedDokument(
                dokumentEnhetId,
                innloggetSaksbehandlerService.getInnloggetIdent()
            )
        )
    }

    @PostMapping("/{dokumentEnhetId}/fullfoer")
    fun fullfoerDokumentEnhet(
        @PathVariable("dokumentEnhetId") dokumentEnhetId: UUID
    ): DokumentEnhetFullfoertView {
        logger.debug("Kall mottatt på fullfoerDokumentEnhet for $dokumentEnhetId")
        val dokumentEnhet = dokumentEnhetService.ferdigstillDokumentEnhet(dokumentEnhetId)
        if (!dokumentEnhet.erAvsluttet()) {
            throw RuntimeException("DokumentEnhet (id: $dokumentEnhetId) feilet under fullføring. Se logger.")
        }
        return dokumentEnhetMapper.mapToDokumentEnhetFullfoertView(dokumentEnhet)
    }

    @PutMapping("/{dokumentEnhetId}/brevmottakere")
    fun updateBrevMottakere(
        @PathVariable("dokumentEnhetId") dokumentEnhetId: UUID,
        @ModelAttribute input: BrevmottakereInput
    ) {
    }

    @PutMapping("/{dokumentEnhetId}/journalfoeringdata")
    fun updateJournalfoeringData(
        @PathVariable("dokumentEnhetId") dokumentEnhetId: UUID,
        @ModelAttribute input: JournalfoeringDataInput
    ) {
    }

    @PostMapping("/{dokumentEnhetId}/vedlegg")
    fun uploadVedlegg(
        @PathVariable("dokumentEnhetId") dokumentEnhetId: UUID,
        @ModelAttribute input: FilInput
    ) {
    }

    @DeleteMapping("/{dokumentEnhetId}/vedlegg/{vedleggId}")
    fun deleteVedlegg(
        @PathVariable("dokumentEnhetId") dokumentEnhetId: UUID,
        @PathVariable("vedleggId") vedleggId: UUID,
    ) {
    }

    @ResponseBody
    @GetMapping("/{dokumentEnhetId}/vedlegg/{vedleggId}")
    fun getVedlegg(
        @PathVariable("dokumentEnhetId") dokumentEnhetId: UUID,
        @PathVariable("vedleggId") vedleggId: UUID,
    ) {
    }

}
