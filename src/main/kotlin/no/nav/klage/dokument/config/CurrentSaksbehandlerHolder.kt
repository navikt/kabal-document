package no.nav.klage.dokument.config

import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope

@Component
@RequestScope
class CurrentSaksbehandlerHolder(
    var navIdent: String? = null,
)