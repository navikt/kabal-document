package no.nav.klage.dokument.api.controller

import io.swagger.annotations.Api
import no.nav.klage.dokument.api.mapper.DokumentEnhetMapper
import no.nav.klage.dokument.api.view.*
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
    private val dokumentEnhetService: DokumentEnhetService
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @PostMapping("/")
    fun createDokumentEnhet(
        @ModelAttribute input: DokumentEnhetInput
    ): DokumentEnhetView {
        dokumentEnhetService.opprettDokumentEnhet(
            innloggetSaksbehandlerService.getInnloggetIdent(),
            input.brevMottakere,
            input.journalfoeringData
        )

        return DokumentEnhetView()
    }

    @PostMapping("/{dokumentEnhetId}/innhold")
    fun uploadHovedDokument(
        @PathVariable("dokumentEnhetId") dokumentEnhetId: UUID,
        @ModelAttribute input: FilInput
    ): HovedDokumentEditedView? {

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

        return dokumentEnhetMapper.mapToByteArray(dokumentEnhetService.hentMellomlagretHovedDokument(dokumentEnhetId))
    }

    @DeleteMapping("/{dokumentEnhetId}/innhold")
    fun deleteHovedDokument(
        @PathVariable("dokumentEnhetId") dokumentEnhetId: UUID,
    ): HovedDokumentEditedView {

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

        //TODO: Kan enten motta JournalfoeringData her eller i create?

        return dokumentEnhetMapper.mapToDokumentEnhetFullfoertView(
            dokumentEnhetService.ferdigstillDokumentEnhet(
                dokumentEnhetId,
                innloggetSaksbehandlerService.getInnloggetIdent()
            )
        )
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
    fun deleteHovedDokument(
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
