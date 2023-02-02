package no.nav.klage.dokument.service


import no.nav.klage.dokument.clients.joark.DefaultJoarkGateway
import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.dokument.exceptions.JournalpostNotFoundException
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class JournalfoeringService(
    private val joarkGateway: DefaultJoarkGateway,
    private val mellomlagerService: MellomlagerService,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
        const val SYSTEM_JOURNALFOERENDE_ENHET = "9999"
    }

    fun createJournalpostAsSystemUser(
        //Skal kanskje være noe annet, om vi skal støtte både utgående og inngående?
        brevMottaker: BrevMottaker,
        hoveddokument: OpplastetHoveddokument,
        vedleggDokumentList: List<OpplastetVedlegg> = emptyList(),
        journalfoeringData: JournalfoeringData,
        journalfoerendeSaksbehandlerIdent: String,
    ): String {
        logger.debug("Skal opprette journalpost som systembruker for brevMottaker ${brevMottaker.id} og dokument ${hoveddokument.id}")
        val mellomlagretHovedDokument = MellomlagretDokument(
            title = hoveddokument.name,
            content = mellomlagerService.getUploadedDocumentAsSystemUser(mellomlagerId = hoveddokument.mellomlagerId),
            contentType = MediaType.APPLICATION_PDF
        )
        val mellomlagredeVedleggDokument = vedleggDokumentList.map {
            MellomlagretDokument(
                title = it.name,
                content = mellomlagerService.getUploadedDocumentAsSystemUser(mellomlagerId = it.mellomlagerId),
                contentType = MediaType.APPLICATION_PDF
            )
        }

        return joarkGateway.createJournalpostAsSystemUser(
            journalfoeringData = journalfoeringData,
            opplastetHovedDokument = hoveddokument,
            hoveddokument = mellomlagretHovedDokument,
            vedleggDokumentList = mellomlagredeVedleggDokument,
            brevMottaker = brevMottaker,
            journalfoerendeSaksbehandlerIdent = journalfoerendeSaksbehandlerIdent,
        )
    }

    fun finalizeJournalpostAsSystemUser(
        journalpostId: String,
    ) {
        return joarkGateway.finalizeJournalpostAsSystemUser(
            journalpostId = journalpostId,
            journalfoerendeEnhet = SYSTEM_JOURNALFOERENDE_ENHET
        )
    }

    fun ferdigstillJournalpostForBrevMottaker(brevMottakerDistribusjon: BrevMottakerDistribusjon): LocalDateTime {
        if (brevMottakerDistribusjon.journalpostId == null) {
            throw JournalpostNotFoundException("Ingen journalpostId registrert i brevmottakerDistribusjon ${brevMottakerDistribusjon.id}")
        }
        return if (brevMottakerDistribusjon.ferdigstiltIJoark == null) {
            finalizeJournalpostAsSystemUser(
                journalpostId = brevMottakerDistribusjon.journalpostId!!
            )
            LocalDateTime.now()
        } else {
            brevMottakerDistribusjon.ferdigstiltIJoark!!
        }
    }

    fun updateDocumentTitle(journalpostId: String, dokumentInfoId: String, newTitle: String) {
        joarkGateway.updateDocumentTitleOnBehalfOf(
            journalpostId = journalpostId,
            dokumentInfoId = dokumentInfoId,
            newTitle = newTitle
        )
    }

    data class MellomlagretDokument(
        val title: String,
        val content: ByteArray,
        val contentType: MediaType
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as MellomlagretDokument

            if (title != other.title) return false
            if (!content.contentEquals(other.content)) return false
            if (contentType != other.contentType) return false

            return true
        }

        override fun hashCode(): Int {
            var result = title.hashCode()
            result = 31 * result + content.contentHashCode()
            result = 31 * result + contentType.hashCode()
            return result
        }
    }
}