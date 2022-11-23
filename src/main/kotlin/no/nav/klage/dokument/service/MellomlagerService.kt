package no.nav.klage.dokument.service

import no.nav.klage.dokument.clients.klagefileapi.FileApiClient
import no.nav.klage.dokument.domain.dokument.MellomlagretDokument
import no.nav.klage.dokument.util.getLogger
import org.springframework.http.MediaType
import org.springframework.stereotype.Service

@Service
class MellomlagerService(
    private val fileApiClient: FileApiClient
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    fun getUploadedDocumentAsSystemUser(mellomlagerId: String): ByteArray =
        fileApiClient.getDocument(mellomlagerId)

    fun deleteDocumentAsSystemUser(mellomlagerId: String): Unit =
        fileApiClient.deleteDocument(mellomlagerId)
}