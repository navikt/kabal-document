package no.nav.klage.dokument.api.controller

import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.dokument.api.input.DokumentEnhetWithDokumentreferanserInput
import no.nav.klage.dokument.api.mapper.DokumentEnhetMapper
import no.nav.klage.dokument.api.view.DokumentEnhetFullfoertView
import no.nav.klage.dokument.api.view.DokumentEnhetView
import no.nav.klage.dokument.config.SecurityConfiguration.Companion.ISSUER_AAD
import no.nav.klage.dokument.service.DokumentEnhetService
import no.nav.klage.dokument.service.JournalfoeringService
import no.nav.klage.dokument.util.getLogger
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@Tag(name = "kabal-document", description = "API for håndtering av dokumentenheter.")
@ProtectedWithClaims(issuer = ISSUER_AAD)
@RequestMapping("/dokumentenheter")
class DokumentEnhetController(
    private val dokumentEnhetMapper: DokumentEnhetMapper,
    private val dokumentEnhetService: DokumentEnhetService,
    private val journalfoeringService: JournalfoeringService
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @PostMapping("/meddokumentreferanser")
    fun createDokumentEnhetWithDokumentreferanser(
        @RequestBody input: DokumentEnhetWithDokumentreferanserInput,
    ): DokumentEnhetView {
        logger.debug("Kall mottatt på createDokumentEnhetWithDokumentreferanser")
        return dokumentEnhetMapper.mapToDokumentEnhetView(
            dokumentEnhetService.opprettDokumentEnhetMedDokumentreferanser(
                input
            )
        )
    }

    @PostMapping("/{dokumentEnhetId}/fullfoer")
    fun fullfoerDokumentEnhet(
        @PathVariable("dokumentEnhetId") dokumentEnhetId: UUID
    ): DokumentEnhetFullfoertView {
        logger.debug("Kall mottatt på fullfoerDokumentEnhet for $dokumentEnhetId")
        val dokumentEnhet = dokumentEnhetService.ferdigstillDokumentEnhet(dokumentEnhetId)
        if (!dokumentEnhet.isAvsluttet()) {
            throw RuntimeException("DokumentEnhet (id: $dokumentEnhetId) feilet under fullføring. Se logger.")
        }
        return dokumentEnhetMapper.mapToDokumentEnhetFullfoertView(dokumentEnhet)
    }

    @GetMapping("/updatetitle/{newTitle}")
    fun updateDocumentTitleInDokarkiv(
        @PathVariable("newTitle") newTitle: String = "ny tittel"
    ) {
        logger.debug("Kall mottatt på updateDocumentTitleInDokarkiv")
        journalfoeringService.updateDocumentTitle(
            journalpostId = "598114725", dokumentInfoId = "598114725", newTitle = newTitle
        )
    }
}