package no.nav.klage.dokument.clients.saf.rest

import org.springframework.http.MediaType

data class ArkivertDokument(
    val bytes: ByteArray,
    val contentType: MediaType
)
