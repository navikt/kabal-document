package no.nav.klage.dokument.service

import no.nav.klage.dokument.domain.dokument.BrevMottaker
import no.nav.klage.dokument.domain.dokument.BrevMottakerDistribusjon
import no.nav.klage.dokument.domain.dokument.DokumentEnhet
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DokumentEnhetDistribusjonService(
    private val brevMottakerDistribusjonService: BrevMottakerDistribusjonService,
    private val brevMottakerJournalfoeringService: BrevMottakerJournalfoeringService,
    private val dokumentEnhetService: DokumentEnhetService
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
        const val SYSTEMBRUKER = "SYSTEMBRUKER" //TODO ??
        const val SYSTEM_JOURNALFOERENDE_ENHET = "9999"
    }

    @Transactional(propagation = Propagation.NEVER)
    fun distribuerDokumentEnhet(dokumentEnhet: DokumentEnhet): DokumentEnhet =
        try {
            if (dokumentEnhet.avsluttet != null) {
                logger.debug("dokumentEnhet ${dokumentEnhet.id} er ikke distribuert")
                dokumentEnhet
                    .let { distribuerDokumentEnhetTilBrevMottakere(it) }
                    .let { dokumentEnhetService.slettMellomlagretDokumentHvisDistribuert(it) }
                    .let { dokumentEnhetService.markerDokumentEnhetSomFerdigDistribuert(it) }
            } else {
                dokumentEnhet
            }
        } catch (e: Exception) {
            logger.error("Feilet under distribuering av dokumentEnhet ${dokumentEnhet.id}", e)
            throw e
        }

    private fun distribuerDokumentEnhetTilBrevMottakere(dokumentEnhet: DokumentEnhet): DokumentEnhet =
        dokumentEnhet.copy(brevMottakerDistribusjoner = dokumentEnhet
            .brevMottakere
            .map {
                //TODO: Feilhåndtering. I hvert ledd? Ikke kaste exceptions kanskje?
                distribuerDokumentEnhetTilBrevMottaker(it, dokumentEnhet)
            })

    private fun distribuerDokumentEnhetTilBrevMottaker(
        brevMottaker: BrevMottaker,
        dokumentEnhet: DokumentEnhet
    ): BrevMottakerDistribusjon =
        if (dokumentEnhet.erDistribuertTil(brevMottaker)) {
            dokumentEnhet.distribusjonAvBrevMottaker(brevMottaker)!!
        } else {
            logger.debug("dokumentEnhet ${dokumentEnhet.id} er ikke distribuert til brevmottaker ${brevMottaker.id}")
            brevMottaker
                .let { idempotentOpprettJournalpostForBrevMottaker(it, dokumentEnhet) }
                .let { idempotentFerdigstillJournalpostForBrevMottaker(it) }
                .let { distribuerVedtakTilBrevmottaker(it) }
        }

    private fun idempotentOpprettJournalpostForBrevMottaker(
        brevMottaker: BrevMottaker, dokumentEnhet: DokumentEnhet
    ): BrevMottakerDistribusjon =
        dokumentEnhet.distribusjonAvBrevMottaker(brevMottaker)
            ?: BrevMottakerDistribusjon(
                brevMottakerId = brevMottaker.id,
                journalpostId = brevMottakerJournalfoeringService.opprettJournalpostForBrevMottaker(
                    brevMottaker,
                    dokumentEnhet.hovedDokument!!, //TODO vedlegg?
                    dokumentEnhet.journalfoeringData
                )
            )


    private fun idempotentFerdigstillJournalpostForBrevMottaker(
        brevMottakerDistribusjon: BrevMottakerDistribusjon
    ): BrevMottakerDistribusjon =
        brevMottakerJournalfoeringService.ferdigstillJournalpostForBrevMottaker(brevMottakerDistribusjon)

    private fun distribuerVedtakTilBrevmottaker(
        brevMottakerDistribusjon: BrevMottakerDistribusjon
    ): BrevMottakerDistribusjon {
        logger.debug("Distribuerer brevmottakerDistribusjon ${brevMottakerDistribusjon.id}")
        return brevMottakerDistribusjonService.distribuerJournalpostTilMottaker(brevMottakerDistribusjon)
    }
}




