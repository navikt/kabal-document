package no.nav.klage.dokument.service.unleash

import no.finn.unleash.strategy.Strategy
import no.nav.klage.dokument.service.saksbehandler.InnloggetSaksbehandlerService
import org.springframework.stereotype.Component

@Component
class ByUserStrategy(private val innloggetSaksbehandlerService: InnloggetSaksbehandlerService) : Strategy {

    companion object {
        const val PARAM = "user"
    }

    override fun getName(): String = "byUserId"

    override fun isEnabled(parameters: Map<String, String>?): Boolean =
        getEnabledUsers(parameters)?.any { isCurrentUserEnabled(it) } ?: false

    private fun getEnabledUsers(parameters: Map<String, String>?) =
        parameters?.get(PARAM)?.split(',')

    private fun isCurrentUserEnabled(ident: String): Boolean {
        return ident == innloggetSaksbehandlerService.getInnloggetIdent().navIdent
    }

}