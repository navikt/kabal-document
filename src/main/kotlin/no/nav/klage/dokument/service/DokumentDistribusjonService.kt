package no.nav.klage.dokument.service

import no.nav.klage.dokument.clients.dokdistfordeling.DokDistFordelingClient
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import no.nav.klage.kodeverk.DokumentType
import org.springframework.stereotype.Service
import java.util.*

@Service
class DokumentDistribusjonService(
    private val dokDistFordelingClient: DokDistFordelingClient,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    fun distribuerJournalpostTilMottaker(journalpostId: String, dokumentType: DokumentType): UUID {
        return dokDistFordelingClient.distribuerJournalpost(
            journalpostId = journalpostId,
            dokumentType = dokumentType,
        ).bestillingsId
    }
}
