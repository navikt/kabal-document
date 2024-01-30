package no.nav.klage.dokument.util

import no.nav.klage.kodeverk.DokumentType

fun DokumentType.isInngaaende(): Boolean {
    return this in listOf(
        DokumentType.KJENNELSE_FRA_TRYGDERETTEN,
        DokumentType.ANNEN_INNGAAENDE_POST
    )
}

fun DokumentType.isUtgaaende(): Boolean {
    return this in listOf(
        DokumentType.BESLUTNING,
        DokumentType.BREV,
        DokumentType.VEDTAK,
    )
}