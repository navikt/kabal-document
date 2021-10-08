package no.nav.klage.dokument.api.controller

import io.swagger.annotations.Api
import no.nav.klage.dokument.api.mapper.DokumentEnhetMapper
import no.nav.klage.dokument.api.view.*
import no.nav.klage.dokument.config.SecurityConfiguration.Companion.ISSUER_AAD
import no.nav.klage.dokument.exceptions.JournalpostNotFoundException
import no.nav.klage.dokument.repositories.InnloggetSaksbehandlerRepository
import no.nav.klage.dokument.service.DokumentEnhetService
import no.nav.klage.dokument.service.MellomlagerService
import no.nav.klage.dokument.service.VedtakService
import no.nav.klage.dokument.util.getLogger
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@Api(tags = ["kabal-api"])
@ProtectedWithClaims(issuer = ISSUER_AAD)
@RequestMapping("/dokumentenheter")
class DokumentEnhetController(
    private val innloggetSaksbehandlerRepository: InnloggetSaksbehandlerRepository,
    private val dokumentEnhetMapper: DokumentEnhetMapper,
    private val vedtakService: VedtakService,
    private val mellomlagerService: MellomlagerService,
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
        //TODO:
        return DokumentEnhetView()
    }

    @PostMapping("/{dokumentEnhetId}/innhold")
    fun uploadHovedDokument(
        @PathVariable("dokumentEnhetId") dokumentEnhetId: UUID,
        @ModelAttribute input: FilInput
    ): HovedDokumentEditedView? {

        //TODO: Gjenstår å lagre endringer
        val dokumentEnhet = dokumentEnhetService.findById(dokumentEnhetId)
            ?: throw JournalpostNotFoundException("Dokument er ikke lastet opp")
        return dokumentEnhetMapper.mapToHovedDokumentEditedView(
            vedtakService.knyttVedtaksFilTilVedtak(
                dokumentEnhet,
                input.file,
                innloggetSaksbehandlerRepository.getInnloggetIdent()
            )
        )
    }

    @DeleteMapping("/{dokumentEnhetId}/innhold")
    fun deleteHovedDokument(
        @PathVariable("dokumentEnhetId") dokumentEnhetId: UUID,
    ): HovedDokumentEditedView {

        //TODO: Gjenstår å lagre endringer
        val dokumentEnhet = dokumentEnhetService.findById(dokumentEnhetId)
            ?: throw JournalpostNotFoundException("Dokument er ikke lastet opp")
        return dokumentEnhetMapper.mapToHovedDokumentEditedView(
            vedtakService.slettFilTilknyttetVedtak(
                dokumentEnhet,
                innloggetSaksbehandlerRepository.getInnloggetIdent()
            )
        )
    }

    @PostMapping("/{dokumentEnhetId}/fullfoer")
    fun fullfoerDokumentEnhet(
        @PathVariable("dokumentEnhetId") dokumentEnhetId: UUID
    ): DokumentEnhetFullfoertView {

        //TODO: Gjenstår å lagre endringer
        val dokumentEnhet = dokumentEnhetService.findById(dokumentEnhetId)
            ?: throw JournalpostNotFoundException("Dokument er ikke lastet opp")

        val klagebehandling = vedtakService.ferdigstillVedtak(
            dokumentEnhet,
            innloggetSaksbehandlerRepository.getInnloggetIdent()
        )
        return dokumentEnhetMapper.mapToDokumentEnhetFullfoertView(klagebehandling)
    }

    @ResponseBody
    @GetMapping("/{dokumentEnhetId}/innhold")
    fun getHovedDokument(
        @PathVariable("dokumentEnhetId") dokumentEnhetId: UUID,
    ): ResponseEntity<ByteArray> {
        val mellomlagerId = dokumentEnhetService.findById(dokumentEnhetId)?.hovedDokument?.mellomlagerId
            ?: throw JournalpostNotFoundException("Dokument er ikke lastet opp")

        val mellomlagretDokument = mellomlagerService.getUploadedDocument(mellomlagerId)
        val responseHeaders = HttpHeaders()
        responseHeaders.contentType = mellomlagretDokument.contentType
        responseHeaders.add("Content-Disposition", "inline; filename=${mellomlagretDokument.title}")
        return ResponseEntity(
            mellomlagretDokument.content,
            responseHeaders,
            HttpStatus.OK
        )
    }

}
