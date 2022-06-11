package no.nav.klage.dokument.domain.saksbehandler

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
open class SaksbehandlerIdent(
    @Column(name = "eier")
    val navIdent: String
)