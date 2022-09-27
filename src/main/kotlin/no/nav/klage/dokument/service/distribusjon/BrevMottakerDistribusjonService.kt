package no.nav.klage.dokument.service.distribusjon

import no.nav.klage.dokument.clients.dokdistfordeling.DokDistFordelingClient
import no.nav.klage.dokument.domain.dokument.BrevMottaker
import no.nav.klage.dokument.domain.dokument.BrevMottakerDistribusjon
import no.nav.klage.dokument.domain.dokument.DokumentEnhet
import no.nav.klage.dokument.exceptions.DokumentEnhetNotFoundException
import no.nav.klage.dokument.repositories.DokumentEnhetRepository
import no.nav.klage.dokument.service.JournalfoeringService
import no.nav.klage.dokument.util.ChainableOperation
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import no.nav.klage.kodeverk.DokumentType
import org.springframework.stereotype.Service
import java.util.*

@Service
class BrevMottakerDistribusjonService(
    private val brevMottakerJournalfoeringService: BrevMottakerJournalfoeringService,
    private val dokDistFordelingClient: DokDistFordelingClient,
    private val dokumentEnhetRepository: DokumentEnhetRepository,
    private val journalfoeringService: JournalfoeringService,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    fun journalfoerOgDistribuerDokumentEnhetTilBrevMottaker(
        brevMottaker: BrevMottaker,
        dokumentEnhet: DokumentEnhet
    ): BrevMottakerDistribusjon? =
        if (dokumentEnhet.erDistribuertTil(brevMottaker)) {
            logger.info("Dokumentenhet ${dokumentEnhet.id} er allerede distribuert til brevmottaker ${brevMottaker.id}")
            dokumentEnhet.findBrevMottakerDistribusjon(brevMottaker)!!
        } else {
            try {
                logger.info("Skal distribuere dokumentenhet ${dokumentEnhet.id} til brevmottaker ${brevMottaker.id}")
                findOrCreateBrevMottakerDistribusjonWithJournalpost(brevMottaker, dokumentEnhet).chainable()
                    .chain(brevMottakerJournalfoeringService::ferdigstillJournalpostForBrevMottaker)
                    .chain(this::distribuerJournalpostTilMottaker)
                    .value
            } catch (t: Throwable) {
                logger.error("Failed to create journalpost for brevmottaker ${brevMottaker.id}", t)
                null
            }
        }

    private fun distribuerJournalpostTilMottaker(brevMottakerDistribusjon: BrevMottakerDistribusjon): BrevMottakerDistribusjon =
        brevMottakerDistribusjon.copy(
            dokdistReferanse = dokDistFordelingClient.distribuerJournalpost(
                brevMottakerDistribusjon.journalpostId.value,
                getDocumentType(brevMottakerDistribusjon),
            ).bestillingsId
        )

    private fun getDocumentType(brevMottakerDistribusjon: BrevMottakerDistribusjon): DokumentType {
        return dokumentEnhetRepository.findById(brevMottakerDistribusjon.dokumentEnhetId)?.dokumentType
            ?: throw DokumentEnhetNotFoundException("DokumentEnhet not found based on brevMottakerDistribusjon with id ${brevMottakerDistribusjon.id}")
    }


    //This first call is the only one allowed to throw exception
    private fun findOrCreateBrevMottakerDistribusjonWithJournalpost(
        brevMottaker: BrevMottaker,
        dokumentEnhet: DokumentEnhet
    ): BrevMottakerDistribusjon =
        dokumentEnhet.findBrevMottakerDistribusjon(brevMottaker)
            ?: createBrevMottakerDistribusjonWithJournalpost(brevMottaker, dokumentEnhet)

    private fun createBrevMottakerDistribusjonWithJournalpost(
        brevMottaker: BrevMottaker,
        dokumentEnhet: DokumentEnhet
    ): BrevMottakerDistribusjon =
        BrevMottakerDistribusjon(
            brevMottakerId = brevMottaker.id,
            opplastetDokumentId = dokumentEnhet.hovedDokument!!.id,
            journalpostId = journalfoeringService.createJournalpostAsSystemUser(
                brevMottaker = brevMottaker,
                hoveddokument = dokumentEnhet.hovedDokument,
                vedleggDokumentList = dokumentEnhet.vedlegg,
                journalfoeringData = dokumentEnhet.journalfoeringData
            ),
            dokumentEnhetId = dokumentEnhet.id,
        )

    private fun BrevMottakerDistribusjon.chainable() = ChainableOperation(this, true)
}
