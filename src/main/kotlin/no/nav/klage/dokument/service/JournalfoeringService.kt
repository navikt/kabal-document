package no.nav.klage.dokument.service

import no.nav.klage.dokument.clients.joark.DefaultJoarkGateway
import no.nav.klage.dokument.clients.joark.Journalpost
import org.springframework.stereotype.Service


import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import org.springframework.http.MediaType


@Service
class JournalfoeringService(
    private val joarkGateway: DefaultJoarkGateway,
    private val mellomlagerService: MellomlagerService,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
        const val SYSTEMBRUKER = "SYSTEMBRUKER" //TODO ??
        const val SYSTEM_JOURNALFOERENDE_ENHET = "9999"
    }

    fun createJournalpostAsSystemUser(
        //Skal kanskje være noe annet, om vi skal støtte både utgående og inngående?
        brevMottaker: BrevMottaker,
        hoveddokument: OpplastetDokument,
        vedleggDokumentList: List<OpplastetDokument> = emptyList(),
        journalfoeringData: JournalfoeringData
    ): JournalpostId {
        logger.debug("Skal opprette journalpost som systembruker for brevMottaker ${brevMottaker.id} og dokument ${hoveddokument.id}")
        val mellomlagretHovedDokument = MellomlagretDokument(
            title = hoveddokument.name,
            content = mellomlagerService.getUploadedDocumentAsSystemUser(hoveddokument.mellomlagerId),
            contentType = MediaType.APPLICATION_PDF
        )
        val mellomlagredeVedleggDokument =
            vedleggDokumentList.map {
                MellomlagretDokument(
                    title = it.name,
                    content = mellomlagerService.getUploadedDocumentAsSystemUser(it.mellomlagerId),
                    contentType = MediaType.APPLICATION_PDF
                )
            }

        return joarkGateway.createJournalpostAsSystemUser(
            journalfoeringData = journalfoeringData,
            opplastetDokument = hoveddokument,
            hoveddokument = mellomlagretHovedDokument,
            vedleggDokumentList = mellomlagredeVedleggDokument,
            brevMottaker = brevMottaker
        )
    }

    fun finalizeJournalpostAsSystemUser(
        journalpostId: JournalpostId,
    ): String {
        return joarkGateway.finalizeJournalpostAsSystemUser(
            journalpostId = journalpostId,
            journalfoerendeEnhet = SYSTEM_JOURNALFOERENDE_ENHET
        )
    }
}