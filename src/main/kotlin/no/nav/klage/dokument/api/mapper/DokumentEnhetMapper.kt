package no.nav.klage.dokument.api.mapper


import no.nav.klage.dokument.api.view.DokumentEnhetFullfoertView
import no.nav.klage.dokument.api.view.HovedDokumentEditedView
import no.nav.klage.dokument.api.view.OpplastetFilMetadataView
import no.nav.klage.dokument.domain.dokument.DokumentEnhet
import no.nav.klage.dokument.domain.dokument.MellomlagretDokument
import no.nav.klage.dokument.domain.dokument.OpplastetDokument
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class DokumentEnhetMapper {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    fun getOpplastetFilMetadataView(opplastetDokument: OpplastetDokument?): OpplastetFilMetadataView? {
        return opplastetDokument?.let {
            OpplastetFilMetadataView(
                name = it.name,
                size = it.size,
                opplastet = it.opplastet
            )
        }
    }

    fun mapToHovedDokumentEditedView(dokumentEnhet: DokumentEnhet): HovedDokumentEditedView {
        return HovedDokumentEditedView(
            dokumentEnhet.modified,
            fileMetadata = getOpplastetFilMetadataView(dokumentEnhet.hovedDokument)
        )
    }

    fun mapToDokumentEnhetFullfoertView(dokumentEnhet: DokumentEnhet): DokumentEnhetFullfoertView {
        return DokumentEnhetFullfoertView(
            dokumentEnhet.modified,
            dokumentEnhet.avsluttetAvSaksbehandler!!
        )
    }

    fun mapToByteArray(mellomlagretDokument: MellomlagretDokument): ResponseEntity<ByteArray> {
        val responseHeaders = HttpHeaders()
        responseHeaders.contentType = mellomlagretDokument.contentType
        responseHeaders.add("Content-Disposition", "inline; filename=${mellomlagretDokument.title}")
        return ResponseEntity(
            mellomlagretDokument.content,
            responseHeaders,
            HttpStatus.OK
        )
    }
}

