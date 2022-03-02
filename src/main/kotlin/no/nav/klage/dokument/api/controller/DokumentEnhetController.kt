package no.nav.klage.dokument.api.controller

import io.swagger.annotations.Api
import no.nav.klage.dokument.api.input.DokumentEnhetWithDokumentreferanserInput
import no.nav.klage.dokument.api.mapper.DokumentEnhetInputMapper
import no.nav.klage.dokument.api.mapper.DokumentEnhetMapper
import no.nav.klage.dokument.api.view.DokumentEnhetFullfoertView
import no.nav.klage.dokument.api.view.DokumentEnhetView
import no.nav.klage.dokument.config.SecurityConfiguration.Companion.ISSUER_AAD
import no.nav.klage.dokument.domain.saksbehandler.SaksbehandlerIdent
import no.nav.klage.dokument.service.DokumentEnhetService
import no.nav.klage.dokument.util.getLogger
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@Api(tags = ["kabal-document"])
@ProtectedWithClaims(issuer = ISSUER_AAD)
@RequestMapping("/dokumentenheter")
class DokumentEnhetController(
    private val dokumentEnhetMapper: DokumentEnhetMapper,
    private val dokumentEnhetInputMapper: DokumentEnhetInputMapper,
    private val dokumentEnhetService: DokumentEnhetService
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        const val SYSTEMBRUKER = "SYSTEMBRUKER" //TODO ??
    }

    @PostMapping("/meddokumentreferanser")
    fun createDokumentEnhetWithDokumentreferanser(
        @RequestBody body: DokumentEnhetWithDokumentreferanserInput,
    ): DokumentEnhetView {
        logger.debug("Kall mottatt på createDokumentEnhetWithDokumentreferanser")
        return dokumentEnhetMapper.mapToDokumentEnhetView(
            dokumentEnhetService.opprettDokumentEnhetMedDokumentreferanser(
                innloggetIdent = SaksbehandlerIdent(navIdent = SYSTEMBRUKER),
                brevMottakere = dokumentEnhetInputMapper.mapBrevMottakereInput(body.brevMottakere),
                journalfoeringData = dokumentEnhetInputMapper.mapJournalfoeringDataInput(body.journalfoeringData),
                hovedokument = dokumentEnhetInputMapper.mapDokumentInput(body.dokumentreferanser.hoveddokument),
                vedlegg = body.dokumentreferanser.vedlegg?.map {
                    dokumentEnhetInputMapper.mapDokumentInput(it)
                } ?: emptyList()
            )
        )
    }

    @ResponseBody
    @GetMapping("/{dokumentEnhetId}")
    fun getDokumentEnhet(
        @PathVariable("dokumentEnhetId") dokumentEnhetId: UUID,
    ): DokumentEnhetView {
        logger.debug("Kall mottatt på getDokumentEnhet for $dokumentEnhetId")
        return dokumentEnhetMapper.mapToDokumentEnhetView(
            dokumentEnhetService.getDokumentEnhet(
                dokumentEnhetId
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
}