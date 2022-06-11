package no.nav.klage.dokument.service.distribusjon

import no.nav.klage.dokument.clients.dokdistfordeling.DokDistFordelingClient
import no.nav.klage.dokument.domain.dokument.BrevMottaker
import no.nav.klage.dokument.domain.dokument.BrevMottakerDistribusjon
import no.nav.klage.dokument.domain.dokument.DokumentEnhet
import no.nav.klage.dokument.exceptions.DokumentEnhetNotValidException
import no.nav.klage.dokument.util.ChainableOperation
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import no.nav.klage.kodeverk.DokumentType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BrevMottakerDistribusjonService(
    private val brevMottakerJournalfoeringService: BrevMottakerJournalfoeringService,
    private val dokDistFordelingClient: DokDistFordelingClient,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    fun distribuerDokumentEnhetTilBrevMottaker(
        brevMottaker: BrevMottaker,
        dokumentEnhet: DokumentEnhet
    ): BrevMottakerDistribusjon? =
        if (dokumentEnhet.erDistribuertTil(brevMottaker)) {
            logger.info("Dokumentenhet ${dokumentEnhet.id} er allerede distribuert til brevmottaker ${brevMottaker.id}")
            dokumentEnhet.findBrevMottakerDistribusjon(brevMottaker)!!
        } else {
            try {
                logger.info("Skal distribuere dokumentenhet ${dokumentEnhet.id} til brevmottaker ${brevMottaker.id}")
                findOrCreateBrevMottakerDistribusjon(brevMottaker, dokumentEnhet).chainable()
                    .chain(brevMottakerJournalfoeringService::ferdigstillJournalpostForBrevMottaker)
                    .chain(this::distribuerJournalpostTilMottaker)
                    .value
            } catch (t: Throwable) {
                logger.error("Failed to create journalpost for brevmottaker ${brevMottaker.id}", t)
                null
            }
        }

    private fun distribuerJournalpostTilMottaker(brevMottakerDistribusjon: BrevMottakerDistribusjon): BrevMottakerDistribusjon {
        val bestillingsId = dokDistFordelingClient.distribuerJournalpost(
            brevMottakerDistribusjon.journalpostId.value,
            getDocumentType(brevMottakerDistribusjon),
        ).bestillingsId
        brevMottakerDistribusjon.dokdistReferanse = bestillingsId
        return brevMottakerDistribusjon
    }

    private fun getDocumentType(brevMottakerDistribusjon: BrevMottakerDistribusjon): DokumentType {
        return brevMottakerDistribusjon.dokumentEnhet.dokumentType
    }


    //This first call is the only one allowed to throw exception
    private fun findOrCreateBrevMottakerDistribusjon(
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
            opplastetDokumentId = dokumentEnhet.getHovedDokument()?.id
                ?: throw DokumentEnhetNotValidException("Hoveddokument ikke funnet"),
            journalpostId = brevMottakerJournalfoeringService.opprettJournalpostForBrevMottaker(
                brevMottaker = brevMottaker,
                hoveddokument = dokumentEnhet.getHovedDokument()
                    ?: throw DokumentEnhetNotValidException("Hoveddokument ikke funnet"),
                vedleggDokumentList = dokumentEnhet.getVedlegg(),
                journalfoeringData = dokumentEnhet.journalfoeringData
            ),
            dokumentEnhet = dokumentEnhet,
        )

    private fun BrevMottakerDistribusjon.chainable() = ChainableOperation(this, true)
}
