package no.nav.klage.dokument.service.distribusjon

import no.nav.klage.dokument.clients.joark.DefaultJoarkGateway
import no.nav.klage.dokument.clients.saf.graphql.Journalstatus
import no.nav.klage.dokument.clients.saf.graphql.SafGraphQlClient
import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.dokument.exceptions.JournalpostNotFoundException
import no.nav.klage.dokument.service.MellomlagerService
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BrevMottakerJournalfoeringService(
    private val mellomlagerService: MellomlagerService,
    private val joarkGateway: DefaultJoarkGateway,
    private val safClient: SafGraphQlClient,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
        const val SYSTEMBRUKER = "SYSTEMBRUKER" //TODO ??
        const val SYSTEM_JOURNALFOERENDE_ENHET = "9999"
    }

    fun opprettJournalpostForBrevMottaker(
        brevMottaker: BrevMottaker,
        hoveddokument: OpplastetDokument,
        vedleggDokumentList: List<OpplastetDokument> = emptyList(),
        journalfoeringData: JournalfoeringData
    ): JournalpostId {
        logger.debug("Skal opprette journalpost for brevMottaker ${brevMottaker.id} og dokument ${hoveddokument.id}")
        val mellomlagretDokument = mellomlagerService.getUploadedDocumentAsSystemUser(hoveddokument.mellomlagerId)
        val mellomlagredeVedleggDokument = vedleggDokumentList.map { mellomlagerService.getUploadedDocumentAsSystemUser(it.mellomlagerId) }
        return joarkGateway.createJournalpostAsSystemUser(
            journalfoeringData = journalfoeringData,
            opplastetDokument = hoveddokument,
            hoveddokument = mellomlagretDokument,
            vedleggDokumentList = mellomlagredeVedleggDokument,
            brevMottaker = brevMottaker
        )
    }

    fun ferdigstillJournalpostForBrevMottaker(brevMottakerDistribusjon: BrevMottakerDistribusjon): BrevMottakerDistribusjon {
        //TODO Innføre Gateway
        val journalpost = safClient.getJournalpostAsSystembruker(brevMottakerDistribusjon.journalpostId.value)
            ?: throw JournalpostNotFoundException("Journalpost med id ${brevMottakerDistribusjon.journalpostId.value} finnes ikke")
        return if (journalpost.journalstatus != Journalstatus.FERDIGSTILT) { //TODO: Kan vi istedet sjekke brevMottakerDistribusjon.ferdigstiltIJoark ?
            joarkGateway.finalizeJournalpostAsSystemUser(
                brevMottakerDistribusjon.journalpostId,
                SYSTEM_JOURNALFOERENDE_ENHET
            )
            brevMottakerDistribusjon.copy(ferdigstiltIJoark = LocalDateTime.now())
        } else {
            brevMottakerDistribusjon.copy(ferdigstiltIJoark = LocalDateTime.now())//TODO Kan denne datoen hentes fra saf?
        }
    }
}

