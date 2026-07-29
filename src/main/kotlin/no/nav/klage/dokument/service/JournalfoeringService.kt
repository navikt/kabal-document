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
            fileName = hoveddokument.name,
            file = mellomlagerService.getUploadedDocumentAsSystemUser(mellomlagerId = hoveddokument.mellomlagerId),
            contentType = MediaType.APPLICATION_PDF,
            rekkefoelge = null,
        )

        val partialJournalpostWithoutDocuments = joarkMapper.createPartialJournalpostWithoutDocuments(
            journalfoeringData = journalfoeringData,
            opplastetHovedDokument = hoveddokument,
            avsenderMottaker = avsenderMottaker
        )

        val partialJournalpostAsJson = ourJacksonObjectMapper.writeValueAsString(partialJournalpostWithoutDocuments)
        val partialJournalpostAppendable = partialJournalpostAsJson.substring(0, partialJournalpostAsJson.length - 1)
        val journalpostRequestAsFile = Files.createTempFile(null, null).toFile()

        FileOutputStream(journalpostRequestAsFile).use { journalpostRequestAsFileOutputStream ->
            journalpostRequestAsFileOutputStream.write(partialJournalpostAppendable.toByteArray())

            //add the hoveddokument (base64 encoded) to the request. Vedlegg are added separately via lastOppVedlegg,
            //because the total request size of a createJournalpost call is limited to ~500 MB.
            journalpostRequestAsFileOutputStream.write(",\"dokumenter\":[".toByteArray())

            writeDocumentToOutputStream(
                mellomlagretDokument = mellomlagretHovedDokument,
                outputStream = journalpostRequestAsFileOutputStream,
                brevkode = journalfoeringData.brevKode,
            )

            journalpostRequestAsFileOutputStream.write("]}".toByteArray())
            journalpostRequestAsFileOutputStream.flush()
        }

        return joarkClient.createJournalpostInJoarkAsSystemUser(
            journalpostRequestAsFile = journalpostRequestAsFile,
            journalfoerendeSaksbehandlerIdent = journalfoerendeSaksbehandlerIdent
        )
    }

    fun lastOppVedleggAsSystemUser(
        journalpostId: String,
        vedlegg: OpplastetVedlegg,
        journalfoeringData: JournalfoeringData,
        journalfoerendeSaksbehandlerIdent: String,
    ): LastOppVedleggResponse {
        logger.debug(
            "Skal laste opp vedlegg {} til journalpost {}",
            vedlegg.id,
            journalpostId
        )

        val mellomlagretVedlegg = MellomlagretDokument(
            title = vedlegg.name,
            //Dokarkiv rejects a vedlegg if a document with the same filnavn already exists on the journalpost,
            //so we make it unique. The title, which is what the user sees, is left untouched.
            fileName = "${vedlegg.index}_${vedlegg.name}",
            file = mellomlagerService.getUploadedDocumentAsSystemUser(mellomlagerId = vedlegg.mellomlagerId),
            contentType = MediaType.APPLICATION_PDF,
            rekkefoelge = vedlegg.index,
        )

        val vedleggRequestAsFile = Files.createTempFile(null, null).toFile()

        FileOutputStream(vedleggRequestAsFile).use { vedleggRequestAsFileOutputStream ->
            vedleggRequestAsFileOutputStream.write("{\"dokument\":".toByteArray())

            writeDocumentToOutputStream(
                mellomlagretDokument = mellomlagretVedlegg,
                outputStream = vedleggRequestAsFileOutputStream,
                brevkode = journalfoeringData.brevKode,
            )

            vedleggRequestAsFileOutputStream.write("}".toByteArray())
            vedleggRequestAsFileOutputStream.flush()
        }

        return joarkClient.lastOppVedleggAsSystemUser(
            journalpostId = journalpostId,
            vedleggRequestAsFile = vedleggRequestAsFile,
            journalfoerendeSaksbehandlerIdent = journalfoerendeSaksbehandlerIdent,
        )
    }

    private fun writeDocumentToOutputStream(
        mellomlagretDokument: MellomlagretDokument,
        outputStream: FileOutputStream,
        brevkode: String,
    ) {
        val base64File = Files.createTempFile(null, null).toFile()
        encodeFileToBase64(mellomlagretDokument.file, base64File)

        val base64FileInputStream = FileInputStream(base64File)

        outputStream.write("{\"tittel\":${ourJacksonObjectMapper.writeValueAsString(mellomlagretDokument.title)},\"brevkode\":\"$brevkode\",\"rekkefoelge\":${mellomlagretDokument.rekkefoelge},\"dokumentvarianter\":[{\"filnavn\":${ourJacksonObjectMapper.writeValueAsString(mellomlagretDokument.fileName)},\"filtype\":\"PDF\",\"variantformat\":\"ARKIV\",\"fysiskDokument\":\"".toByteArray())

        base64FileInputStream.use { input ->
            val buffer = ByteArray(1024) // Use a buffer size of 1K for example
            var length: Int
            while (input.read(buffer).also { length = it } != -1) {
                outputStream.write(buffer, 0, length)
            }
        }
        outputStream.write("\"}]}".toByteArray())

        base64File.delete()
        mellomlagretDokument.file.delete()
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
                    logger.debug("Adding vedlegg with dokumentInfoId ${it.dokumentInfoId} to journalpost $journalpostId with rekkefoelge ${it.index}")
                    TilknyttVedleggPayload.VedleggReference(
                        kildeJournalpostId = it.kildeJournalpostId,
                        dokumentInfoId = it.dokumentInfoId,
                        rekkefoelge = it.index,
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
        val fileName: String,
        val file: File,
        val contentType: MediaType,
        val rekkefoelge: Int?,
    )

}