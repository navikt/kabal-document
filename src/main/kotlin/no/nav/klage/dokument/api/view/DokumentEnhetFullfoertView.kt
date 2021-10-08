package no.nav.klage.dokument.api.view

import java.time.LocalDateTime

data class DokumentEnhetFullfoertView(
    val modified: LocalDateTime,
    val avsluttetAvSaksbehandler: LocalDateTime,
)
