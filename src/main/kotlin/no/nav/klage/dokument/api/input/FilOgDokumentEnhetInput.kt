package no.nav.klage.dokument.api.input

import org.springframework.web.multipart.MultipartFile

data class FilOgDokumentEnhetInput(
    val file: MultipartFile,
    val dokumentEnhetInput: DokumentEnhetInput
)
