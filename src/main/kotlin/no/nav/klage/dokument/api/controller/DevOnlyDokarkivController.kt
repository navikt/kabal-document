package no.nav.klage.dokument.api.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.dokument.config.SecurityConfiguration.Companion.ISSUER_AAD
import no.nav.klage.dokument.service.JournalfoeringService
import no.nav.klage.dokument.util.getLogger
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * Kun tilgjengelig i dev. Brukes til opprydding av testdata etter E2E-tester.
 */
@Profile("dev")
@RestController
@Tag(name = "dokarkiv-dev", description = "API for opprydding av testdata i Dokarkiv. Kun tilgjengelig i dev.")
@ProtectedWithClaims(issuer = ISSUER_AAD)
@RequestMapping("/dokarkiv/dev")
class DevOnlyDokarkivController(
    private val journalfoeringService: JournalfoeringService,
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @Operation(
        summary = "Feilregistrer sakstilknytning for journalpost",
        description =
            "Feilregistrerer journalpostens knytning til sak i Dokarkiv, slik at SAF filtrerer den bort fra " +
                "dokumentoversikten. Kun for opprydding av testdata i dev.",
    )
    @PatchMapping("/journalposter/{journalpostId}/feilregistrersakstilknytning")
    @ResponseStatus(HttpStatus.OK)
    fun feilregistrerSakstilknytning(
        @PathVariable("journalpostId") journalpostId: String,
    ) {
        logger.debug("Kall mottatt på feilregistrerSakstilknytning for journalpost {}", journalpostId)
        journalfoeringService.feilregistrerSakstilknytning(journalpostId = journalpostId)
    }
}
