package no.nav.klage.dokument.service


import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.klage.dokument.clients.joark.*
import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.dokument.exceptions.JournalpostNotFoundException
import no.nav.klage.dokument.util.getLogger
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

@Service
class JournalfoeringService(
    private val joarkClient: JoarkClient,
    private val joarkMapper: JoarkMapper,
    private val mellomlagerService: MellomlagerService,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        const val SYSTEM_JOURNALFOERENDE_ENHET = "9999"

        val ourJacksonObjectMapper = jacksonObjectMapper()
            .registerModule(JavaTimeModule())
            .setDateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"))
    }

    fun createJournalpostAsSystemUser(
        //Skal kanskje være noe annet, om vi skal støtte både utgående og inngående?
        avsenderMottaker: AvsenderMottaker,
        hoveddokument: OpplastetHoveddokument,
        vedleggDokumentSet: Set<OpplastetVedlegg> = emptySet(),
        journalfoeringData: JournalfoeringData,
        journalfoerendeSaksbehandlerIdent: String,
    ): JournalpostResponse {
        logger.debug(
            "Skal opprette journalpost som systembruker for avsenderMottaker {} og dokument {}",
            avsenderMottaker.id,
            hoveddokument.id
        )
        val mellomlagretHovedDokument = MellomlagretDokument(
            title = hoveddokument.name,
            file = mellomlagerService.getUploadedDocumentAsSystemUser(mellomlagerId = hoveddokument.mellomlagerId),
            contentType = MediaType.APPLICATION_PDF
        )
        val mellomlagredeVedleggDokument = vedleggDokumentSet.map {
            MellomlagretDokument(
                title = it.name,
                file = mellomlagerService.getUploadedDocumentAsSystemUser(mellomlagerId = it.mellomlagerId),
                contentType = MediaType.APPLICATION_PDF
            )
        }

        val partialJournalpostWithoutDocuments = joarkMapper.createPartialJournalpostWithoutDocuments(
            journalfoeringData = journalfoeringData,
            opplastetHovedDokument = hoveddokument,
            avsenderMottaker = avsenderMottaker
        )

        val partialJournalpostAsJson = ourJacksonObjectMapper.writeValueAsString(partialJournalpostWithoutDocuments)
        val partialJournalpostAppendable = partialJournalpostAsJson.substring(0, partialJournalpostAsJson.length - 1)
        val journalpostRequestAsFile = Files.createTempFile(null, null)
        val journalpostRequestAsFileOutputStream = FileOutputStream(journalpostRequestAsFile.toFile())
        journalpostRequestAsFileOutputStream.write(partialJournalpostAppendable.toByteArray())

        //add documents (base64 encoded) to the request
        journalpostRequestAsFileOutputStream.write(",\"dokumenter\":[".toByteArray())

        writeDocumentsToJournalpostRequestAsFile(
            mellomlagretDokumenter = listOf(mellomlagretHovedDokument) + mellomlagredeVedleggDokument,
            journalpostRequestAsFileOutputStream = journalpostRequestAsFileOutputStream,
            brevkode = journalfoeringData.brevKode
        )

        journalpostRequestAsFileOutputStream.write("]}".toByteArray())
        journalpostRequestAsFileOutputStream.flush()

        return joarkClient.createJournalpostInJoarkAsSystemUser(
            journalpostRequestAsFile = journalpostRequestAsFile.toFile(),
            journalfoerendeSaksbehandlerIdent = journalfoerendeSaksbehandlerIdent
        )
    }

    private fun writeDocumentsToJournalpostRequestAsFile(
        mellomlagretDokumenter: List<MellomlagretDokument>,
        journalpostRequestAsFileOutputStream: FileOutputStream,
        brevkode: String,
    ) {
        mellomlagretDokumenter.forEachIndexed { index, dokument ->
            val base64File = Files.createTempFile(null, null).toFile()
            encodeFileToBase64(dokument.file, base64File)

            val base64FileInputStream = FileInputStream(base64File)

            journalpostRequestAsFileOutputStream.write("{\"tittel\":${ourJacksonObjectMapper.writeValueAsString(dokument.title)},\"brevkode\":\"$brevkode\",\"dokumentvarianter\":[{\"filnavn\":${ourJacksonObjectMapper.writeValueAsString(dokument.title)},\"filtype\":\"PDF\",\"variantformat\":\"ARKIV\",\"fysiskDokument\":\"".toByteArray())

            base64FileInputStream.use { input ->
                val buffer = ByteArray(1024) // Use a buffer size of 1K for example
                var length: Int
                while (input.read(buffer).also { length = it } != -1) {
                    journalpostRequestAsFileOutputStream.write(buffer, 0, length)
                }
            }
            journalpostRequestAsFileOutputStream.write("\"}]}".toByteArray())
            if (index < mellomlagretDokumenter.size - 1) {
                journalpostRequestAsFileOutputStream.write(",".toByteArray())
            }

            base64File.delete()
            dokument.file.delete()
        }

    }

    private fun encodeFileToBase64(sourceFile: File, destinationFile: File) {
        val sourceFileInputStream = FileInputStream(sourceFile)
        val destinationFileOutputStream = FileOutputStream(destinationFile)
        val encoder = Base64.getEncoder().wrap(destinationFileOutputStream)

        BufferedInputStream(sourceFileInputStream).use { input ->
            val buffer = ByteArray(3 * 1024) // Use a buffer size of 3K for example
            var length: Int
            while (input.read(buffer).also { length = it } != -1) {
                encoder.write(buffer, 0, length)
            }
        }

        encoder.close()

        destinationFileOutputStream.close()
    }

    fun finalizeJournalpostAsSystemUser(
        journalpostId: String,
    ) {
        return joarkClient.finalizeJournalpostAsSystemUser(
            journalpostId = journalpostId,
            journalfoerendeEnhet = SYSTEM_JOURNALFOERENDE_ENHET
        )
    }

    fun tilknyttVedleggAsSystemUser(
        journalpostId: String,
        journalfoerteVedlegg: List<JournalfoertVedlegg>
    ): TilknyttVedleggResponse {
        return joarkClient.tilknyttVedleggAsSystemUser(
            journalpostId = journalpostId,
            input = TilknyttVedleggPayload(
                dokument = journalfoerteVedlegg.map {
                    TilknyttVedleggPayload.VedleggReference(
                        kildeJournalpostId = it.kildeJournalpostId,
                        dokumentInfoId = it.dokumentInfoId
                    )
                }
            )
        )
    }

    fun ferdigstillJournalpostForAvsenderMottakerDistribusjon(avsenderMottakerDistribusjon: AvsenderMottakerDistribusjon): LocalDateTime {
        if (avsenderMottakerDistribusjon.journalpostId == null) {
            throw JournalpostNotFoundException("Ingen journalpostId registrert i avsenderMottakerDistribusjon ${avsenderMottakerDistribusjon.id}")
        }

        finalizeJournalpostAsSystemUser(
            journalpostId = avsenderMottakerDistribusjon.journalpostId!!
        )

        return LocalDateTime.now()
    }

    fun updateDocumentTitle(journalpostId: String, dokumentInfoId: String, title: String) {
        joarkClient.updateDocumentTitleOnBehalfOf(
            journalpostId = journalpostId,
            input = joarkMapper.createUpdateDocumentTitleJournalpostInput(
                dokumentInfoId = dokumentInfoId, title = title
            )
        )
    }

    data class MellomlagretDokument(
        val title: String,
        val file: File,
        val contentType: MediaType,
    )

}