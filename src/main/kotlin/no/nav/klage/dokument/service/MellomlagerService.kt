package no.nav.klage.dokument.service

import no.nav.klage.dokument.clients.klagefileapi.FileApiClient
import no.nav.klage.dokument.domain.dokument.MellomlagretDokument
import no.nav.klage.dokument.util.getLogger
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class MellomlagerService(
    private val fileApiClient: FileApiClient
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val standardMediaTypeInGCS = MediaType.valueOf("application/pdf")
    }

    fun uploadDocument(file: MultipartFile): String =
        fileApiClient.uploadDocument(file.bytes, file.name)

    fun getUploadedDocument(mellomlagerId: String): MellomlagretDokument =
        MellomlagretDokument(
            getFileNameFromMellomlagerId(mellomlagerId),
            fileApiClient.getDocument(mellomlagerId),
            standardMediaTypeInGCS
        )

    fun deleteDocument(mellomlagerId: String): Unit =
        fileApiClient.deleteDocument(mellomlagerId)

    fun getUploadedDocumentAsSystemUser(mellomlagerId: String): MellomlagretDokument =
        MellomlagretDokument(
            getFileNameFromMellomlagerId(mellomlagerId),
            fileApiClient.getDocument(mellomlagerId, true),
            standardMediaTypeInGCS
        )

    fun deleteDocumentAsSystemUser(mellomlagerId: String): Unit =
        fileApiClient.deleteDocument(mellomlagerId, true)

    private fun getFileNameFromMellomlagerId(mellomlagerId: String): String = mellomlagerId.substring(36)
}