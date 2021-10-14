package no.nav.klage.dokument.api.mapper


import no.nav.klage.dokument.api.view.DokumentEnhetFullfoertView
import no.nav.klage.dokument.api.view.HovedDokumentEditedView
import no.nav.klage.dokument.api.view.OpplastetFilMetadataView
import no.nav.klage.dokument.domain.dokument.DokumentEnhet
import no.nav.klage.dokument.domain.dokument.MellomlagretDokument
import no.nav.klage.dokument.domain.dokument.OpplastetDokument
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class DokumentEnhetMapper {

    fun getOpplastetFilMetadataView(opplastetDokument: OpplastetDokument?): OpplastetFilMetadataView? =
        opplastetDokument?.let {
            OpplastetFilMetadataView(
                name = it.name,
                size = it.size,
                opplastet = it.opplastet
            )
        }

    fun mapToHovedDokumentEditedView(dokumentEnhet: DokumentEnhet): HovedDokumentEditedView =
        HovedDokumentEditedView(
            dokumentEnhet.modified,
            fileMetadata = getOpplastetFilMetadataView(dokumentEnhet.hovedDokument)
        )

    fun mapToDokumentEnhetFullfoertView(dokumentEnhet: DokumentEnhet): DokumentEnhetFullfoertView =
        DokumentEnhetFullfoertView(dokumentEnhet.erAvsluttet())

    fun mapToByteArray(mellomlagretDokument: MellomlagretDokument): ResponseEntity<ByteArray> =
        ResponseEntity(
            mellomlagretDokument.content,
            HttpHeaders().apply {
                contentType = mellomlagretDokument.contentType
                add("Content-Disposition", "inline; filename=${mellomlagretDokument.title}")
            },
            HttpStatus.OK
        )

}

