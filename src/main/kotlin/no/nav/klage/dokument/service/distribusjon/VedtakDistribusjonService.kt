package no.nav.klage.dokument.service.distribusjon

import no.nav.klage.dokument.clients.dokdistfordeling.DokDistFordelingClient
import no.nav.klage.dokument.domain.dokument.BrevMottaker
import no.nav.klage.dokument.domain.dokument.OpplastetDokument
import no.nav.klage.dokument.service.MellomlagerService
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class VedtakDistribusjonService(
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val dokDistFordelingClient: DokDistFordelingClient,
    private val mellomlagerService: MellomlagerService,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
        const val SYSTEMBRUKER = "SYSTEMBRUKER" //TODO ??
        const val SYSTEM_JOURNALFOERENDE_ENHET = "9999"
    }

    fun distribuerJournalpostTilMottaker(
        brevMottaker: BrevMottaker
    ): BrevMottaker {
        try {
            val dokdistReferanse: UUID =
                dokDistFordelingClient.distribuerJournalpost(brevMottaker.journalpostId!!).bestillingsId
            return brevMottaker.copy(dokdistReferanse = dokdistReferanse)
        } catch (e: Exception) {
            logger.warn("Kunne ikke distribuere journalpost ${brevMottaker.journalpostId}")
            throw e
        }
    }

    fun slettMellomlagretDokument(
        opplastetDokument: OpplastetDokument
    ) {
        mellomlagerService.deleteDocumentAsSystemUser(opplastetDokument.mellomlagerId)
    }
}
