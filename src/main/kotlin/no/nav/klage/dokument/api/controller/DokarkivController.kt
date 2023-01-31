package no.nav.klage.dokument.api.controller

import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.dokument.config.SecurityConfiguration.Companion.ISSUER_AAD
import no.nav.klage.dokument.service.JournalfoeringService
import no.nav.klage.dokument.util.getLogger
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@Tag(name = "dokarkiv", description = "API for direkte interaksjon med Dokarkiv.")
@ProtectedWithClaims(issuer = ISSUER_AAD)
@RequestMapping("/dokarkiv")
class DokarkivController(
    private val journalfoeringService: JournalfoeringService
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @GetMapping("/updatetitle/{newTitle}")
    fun updateDocumentTitleInDokarkiv(
        @PathVariable("newTitle") newTitle: String = "ny tittel"
    ): String {
        logger.debug("Kall mottatt på updateDocumentTitleInDokarkiv")
        journalfoeringService.updateDocumentTitle(
            journalpostId = "598114725", dokumentInfoId = "624861697", newTitle = newTitle
        )
        return "Funka"
    }
}