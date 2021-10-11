package no.nav.klage.dokument.service.distribusjon

import no.nav.klage.dokument.clients.dokdistfordeling.DokDistFordelingClient
import no.nav.klage.dokument.domain.ChainableOperation
import no.nav.klage.dokument.domain.dokument.BrevMottaker
import no.nav.klage.dokument.domain.dokument.BrevMottakerDistribusjon
import no.nav.klage.dokument.domain.dokument.DokumentEnhet
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import org.springframework.stereotype.Service

@Service
class BrevMottakerDistribusjonService(
    private val brevMottakerJournalfoeringService: BrevMottakerJournalfoeringService,
    private val dokDistFordelingClient: DokDistFordelingClient
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
        const val SYSTEMBRUKER = "SYSTEMBRUKER" //TODO ??
        const val SYSTEM_JOURNALFOERENDE_ENHET = "9999"
    }

    fun distribuerDokumentEnhetTilBrevMottaker(
        brevMottaker: BrevMottaker,
        dokumentEnhet: DokumentEnhet
    ): BrevMottakerDistribusjon? =
        kotlin.runCatching {
            if (dokumentEnhet.erDistribuertTil(brevMottaker)) {
                dokumentEnhet.findBrevMottakerDistribusjon(brevMottaker)!!
            } else {
                logger.debug("dokumentEnhet ${dokumentEnhet.id} er ikke distribuert til brevmottaker ${brevMottaker.id}")
                findOrCreateBrevMottakerDistribusjon(brevMottaker, dokumentEnhet).chainable()
                    .chain(brevMottakerJournalfoeringService::ferdigstillJournalpostForBrevMottaker)
                    .chain(this::distribuerJournalpostTilMottaker)
                    .value
            }
        }.onFailure {
            logger.error("Failed to create journalpost for brevmottaker ${brevMottaker.id}", it)
        }.getOrNull()

    fun distribuerJournalpostTilMottaker(brevMottakerDistribusjon: BrevMottakerDistribusjon): BrevMottakerDistribusjon =
        brevMottakerDistribusjon.copy(
            dokdistReferanse = dokDistFordelingClient.distribuerJournalpost(
                brevMottakerDistribusjon.journalpostId.value
            ).bestillingsId
        )

    //This first call is the only one allowed to throw exception
    private fun findOrCreateBrevMottakerDistribusjon(
        brevMottaker: BrevMottaker,
        dokumentEnhet: DokumentEnhet
    ) = dokumentEnhet.findBrevMottakerDistribusjon(brevMottaker)
        ?: createBrevMottakerDistribusjon(brevMottaker, dokumentEnhet)

    private fun createBrevMottakerDistribusjon(
        brevMottaker: BrevMottaker,
        dokumentEnhet: DokumentEnhet
    ) = BrevMottakerDistribusjon(
        brevMottakerId = brevMottaker.id,
        journalpostId = brevMottakerJournalfoeringService.opprettJournalpostForBrevMottaker(
            brevMottaker,
            dokumentEnhet.hovedDokument!!, //TODO vedlegg?
            dokumentEnhet.journalfoeringData
        )
    )

    private fun BrevMottakerDistribusjon.chainable() = ChainableOperation(this, true)
}
