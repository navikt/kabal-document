package no.nav.klage.dokument.api.controller

import io.swagger.annotations.Api
import no.nav.klage.dokument.config.SecurityConfiguration
import no.nav.klage.dokument.domain.dokument.BrevMottaker
import no.nav.klage.dokument.domain.dokument.JournalfoeringData
import no.nav.klage.dokument.service.DokumentEnhetService
import no.nav.klage.dokument.service.DokumentService
import no.nav.klage.dokument.service.saksbehandler.InnloggetSaksbehandlerService
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@Api(tags = ["kabal-document"])
@ProtectedWithClaims(issuer = SecurityConfiguration.ISSUER_AAD)
@RequestMapping("/dokumenter")
class DokumentController(
    private val dokumentEnhetService: DokumentEnhetService,
    private val dokumentService: DokumentService,
    private val innloggetSaksbehandlerService: InnloggetSaksbehandlerService
) {

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
        dokumentEnhetService.ferdigstillDokumentEnhet(dokumentEnhetId = dokumentEnhet.id)
    }
}