package no.nav.klage.dokument.service

import no.nav.klage.dokument.clients.smarteditorapi.SmartEditorClient
import no.nav.klage.dokument.domain.dokument.MellomlagretDokument
import no.nav.klage.dokument.domain.dokument2.HovedDokument
import no.nav.klage.dokument.domain.saksbehandler.SaksbehandlerIdent
import no.nav.klage.dokument.exceptions.ValidationException
import no.nav.klage.dokument.repositories.DokumentRepository
import no.nav.klage.dokument.repositories.HovedDokumentRepository
import no.nav.klage.dokument.util.AttachmentValidator
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.util.*
import javax.transaction.Transactional

@Service
@Transactional
class DokumentService(
    private val hovedDokumentRepository: HovedDokumentRepository,
    private val dokumentRepository: DokumentRepository,
    private val attachmentValidator: AttachmentValidator,
    private val mellomlagerService: MellomlagerService,
    private val smartEditorClient: SmartEditorClient,
) {

    fun finnOgFerdigstillHovedDokument(hovedDokumentId: UUID): HovedDokument {
        return hovedDokumentRepository.getById(hovedDokumentId).apply { ferdigstillHvisIkkeAlleredeFerdigstilt() }
    }

    fun opprettOgMellomlagreNyttHoveddokument(
        innloggetIdent: SaksbehandlerIdent,
        dokumentType: String,
        eksternReferanse: String,
        smartEditorId: UUID?,
        opplastetFil: MultipartFile?
    ): HovedDokument {

        val fil = smartEditorId?.let {
            //TODO: Brukerkontekst eller systemkontekst?
            smartEditorClient.getDocumentAsPDF(smartEditorId)
        } ?: opplastetFil ?: throw ValidationException("Ingen fil angitt")

        attachmentValidator.validateAttachment(fil)
        val mellomlagerId = mellomlagerService.uploadDocument(fil)

        return hovedDokumentRepository.save(
            HovedDokument(
                mellomlagerId = mellomlagerId,
                opplastet = LocalDateTime.now(),
                size = fil.size,
                name = fil.originalFilename ?: throw RuntimeException("missing original filename"),
                dokumentType = dokumentType,
                eksternReferanse = eksternReferanse,
                vedlegg = emptyList(),
            )
        )
    }

    fun hentMellomlagretDokument(dokumentId: UUID, innloggetIdent: SaksbehandlerIdent): MellomlagretDokument {
        val dokument = dokumentRepository.findOne(dokumentId) ?: throw ValidationException("Dokument ikke funnet")
        return mellomlagerService.getUploadedDocument(dokument.mellomlagerId)
    }

    fun slettDokument(dokumentId: UUID, innloggetIdent: SaksbehandlerIdent) {
        TODO("Not yet implemented")
    }

    fun findHovedDokumenter(eksternReferanse: String): List<HovedDokument> {
        return hovedDokumentRepository.findByEksternReferanse(eksternReferanse)
    }
}