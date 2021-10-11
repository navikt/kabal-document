package no.nav.klage.dokument.service

import no.nav.klage.dokument.clients.dokdistfordeling.DokDistFordelingClient
import no.nav.klage.dokument.domain.dokument.BrevMottakerDistribusjon
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class BrevMottakerDistribusjonService(
    private val dokDistFordelingClient: DokDistFordelingClient
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
        const val SYSTEMBRUKER = "SYSTEMBRUKER" //TODO ??
        const val SYSTEM_JOURNALFOERENDE_ENHET = "9999"
    }

    fun distribuerJournalpostTilMottaker(brevMottakerDistribusjon: BrevMottakerDistribusjon): BrevMottakerDistribusjon =
        try {
            val dokdistReferanse: UUID =
                dokDistFordelingClient.distribuerJournalpost(brevMottakerDistribusjon.journalpostId.value).bestillingsId
            brevMottakerDistribusjon.copy(dokdistReferanse = dokdistReferanse)
        } catch (e: Exception) {
            logger.warn("Kunne ikke distribuere journalpost ${brevMottakerDistribusjon.journalpostId.value}")
            throw e
        }
}
