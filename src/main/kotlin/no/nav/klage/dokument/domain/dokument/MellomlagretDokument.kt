package no.nav.klage.dokument.domain.dokument

import org.springframework.http.MediaType

data class MellomlagretDokument(
    val title: String,
    val content: ByteArray,
    val contentType: MediaType
)
