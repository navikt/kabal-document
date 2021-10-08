package no.nav.klage.dokument.util

import no.nav.klage.dokument.clients.clamav.ClamAvClient
import no.nav.klage.dokument.exceptions.AttachmentEncryptedException
import no.nav.klage.dokument.exceptions.AttachmentHasVirusException
import no.nav.klage.dokument.exceptions.AttachmentIsEmptyException
import no.nav.klage.dokument.exceptions.AttachmentTooLargeException
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException
import org.apache.tika.Tika
import org.springframework.http.MediaType
import org.springframework.util.unit.DataSize
import org.springframework.web.multipart.MultipartFile


class AttachmentValidator(
    private val clamAvClient: ClamAvClient,
    private val maxAttachmentSize: DataSize
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    fun validateAttachment(vedlegg: MultipartFile) {
        logger.debug("Validating attachment.")
        if (vedlegg.isEmpty) {
            logger.warn("Attachment is empty")
            throw AttachmentIsEmptyException()
        }

        if (vedlegg.isTooLarge()) {
            logger.warn("Attachment too large")
            throw AttachmentTooLargeException()
        }

        if (vedlegg.hasVirus()) {
            logger.warn("Attachment has virus")
            throw AttachmentHasVirusException()
        }

        if (vedlegg.isPDF() && vedlegg.isEncrypted()) {
            logger.warn("Attachment is encrypted")
            throw AttachmentEncryptedException()
        }

        logger.debug("Validation successful.")
    }

    private fun MultipartFile.hasVirus() = !clamAvClient.scan(this.bytes)

    private fun MultipartFile.isEncrypted(): Boolean {
        return try {
            val temp: PDDocument = PDDocument.load(this.bytes)
            temp.close()
            false
        } catch (ipe: InvalidPasswordException) {
            true
        }
    }

    private fun MultipartFile.isTooLarge() = this.bytes.size > maxAttachmentSize.toBytes()

    private fun MultipartFile.isPDF() =
        MediaType.valueOf(Tika().detect(this.bytes)) == MediaType.APPLICATION_PDF
}