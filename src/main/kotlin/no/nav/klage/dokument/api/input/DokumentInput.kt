package no.nav.klage.dokument.api.input

import java.time.LocalDateTime

data class DokumentInput(
    val hoveddokument: Dokument,
    val vedlegg: List<Dokument>?,
) {
    data class Dokument(
        val mellomlagerId: String,
        val opplastet: LocalDateTime,
        val size: Long,
        val name: String
    )
}