package no.nav.klage.dokument.service

import no.nav.klage.dokument.domain.dokument.BrevMottaker
import no.nav.klage.dokument.domain.dokument.DokumentEnhet
import no.nav.klage.dokument.domain.dokument.OpplastetDokument
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class DokumentEnhetDistribusjonService(
    private val brevMottakerDistribusjonService: BrevMottakerDistribusjonService,
    private val vedtakJournalfoeringService: BrevMottakerJournalfoeringService,
    private val mellomlagerService: MellomlagerService
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
        const val SYSTEMBRUKER = "SYSTEMBRUKER" //TODO ??
        const val SYSTEM_JOURNALFOERENDE_ENHET = "9999"
    }

    @Transactional(propagation = Propagation.NEVER)
    fun distribuerKlagebehandling(dokumentEnhet: DokumentEnhet): DokumentEnhet {
        try {
            return if (dokumentEnhet.avsluttet != null) {
                logger.debug("dokumentEnhet ${dokumentEnhet.id} er ikke distribuert")
                dokumentEnhet
                    .let { distribuerDokumentEnhet(it) }
                    .let { slettMellomlagretDokument(it) }
                    .let { markerDokumentEnhetSomFerdigDistribuert(it) }
            } else {
                dokumentEnhet
            }
        } catch (e: Exception) {
            logger.error("Feilet under distribuering av dokumentEnhet ${dokumentEnhet.id}", e)
            throw e
        }
    }

    private fun distribuerDokumentEnhet(dokumentEnhet: DokumentEnhet): DokumentEnhet {
        val brevmottakere = dokumentEnhet
            .brevMottakere
            .map {
                distribuerDokumentEnhetTilBrevMottaker(it, dokumentEnhet)
            }
        return dokumentEnhet.copy(brevMottakere = brevmottakere)
    }

    private fun distribuerDokumentEnhetTilBrevMottaker(
        brevMottaker: BrevMottaker,
        dokumentEnhet: DokumentEnhet
    ): BrevMottaker = if (brevMottaker.erDistribuertTil()) {
        brevMottaker
    } else {
        logger.debug("dokumentEnhet ${dokumentEnhet.id} er ikke distribuert til brevmottaker ${brevMottaker.id}")
        brevMottaker
            .let { opprettJournalpostForBrevMottaker(it, dokumentEnhet) }
            .let { ferdigstillJournalpostForBrevMottaker(brevMottaker) }
            .let { distribuerVedtakTilBrevmottaker(brevMottaker) }
    }

    private fun opprettJournalpostForBrevMottaker(
        brevMottaker: BrevMottaker, dokumentEnhet: DokumentEnhet
    ): BrevMottaker {
        //TODO: Save brevmottaker
        return vedtakJournalfoeringService.opprettJournalpostForBrevMottaker(
            brevMottaker,
            dokumentEnhet.hovedDokument!!, //TODO
            dokumentEnhet.journalfoeringData
        )
    }

    private fun ferdigstillJournalpostForBrevMottaker(brevMottaker: BrevMottaker): BrevMottaker {
        //TODO: Save brevmottaker
        return vedtakJournalfoeringService.ferdigstillJournalpostForBrevMottaker(brevMottaker)
    }

    private fun distribuerVedtakTilBrevmottaker(brevMottaker: BrevMottaker): BrevMottaker {
        logger.debug("Distribuerer til brevmottaker ${brevMottaker.id}")
        //TODO: Save brevmottaker
        return brevMottakerDistribusjonService.distribuerJournalpostTilMottaker(brevMottaker)
    }

    //TODO: Flytt til dokumentEnhetService?
    fun slettMellomlagretDokument(dokumentEnhet: DokumentEnhet): DokumentEnhet {
        logger.debug("Sletter mellomlagret fil i dokumentEnhet ${dokumentEnhet.id}")
        dokumentEnhet.hovedDokument?.let { slettMellomlagretDokument(it) }
        //TODO: Save dokumentEnhet
        return dokumentEnhet.copy(hovedDokument = null)
    }

    //TODO: Flytt til dokumentEnhetService?
    private fun markerDokumentEnhetSomFerdigDistribuert(dokumentEnhet: DokumentEnhet): DokumentEnhet {
        logger.debug("Markerer dokumentEnhet ${dokumentEnhet.id} som ferdig distribuert")
        //TODO: Save dokumentEnhet
        return dokumentEnhet.copy(avsluttet = LocalDateTime.now())
    }

    private fun slettMellomlagretDokument(opplastetDokument: OpplastetDokument) {
        mellomlagerService.deleteDocumentAsSystemUser(opplastetDokument.mellomlagerId)
    }
}




