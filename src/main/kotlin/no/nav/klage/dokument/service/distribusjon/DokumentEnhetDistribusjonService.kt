package no.nav.klage.dokument.service.distribusjon

import no.nav.klage.dokument.domain.dokument.DokumentEnhet
import no.nav.klage.dokument.service.MellomlagerService
import no.nav.klage.dokument.util.ChainableOperation
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class DokumentEnhetDistribusjonService(
    private val brevMottakerDistribusjonService: BrevMottakerDistribusjonService,
    private val mellomlagerService: MellomlagerService
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    fun distribuerDokumentEnhet(dokumentEnhet: DokumentEnhet): DokumentEnhet =
        if (!dokumentEnhet.erAvsluttet()) {
            logger.debug("dokumentEnhet ${dokumentEnhet.id} er ikke distribuert")
            val oppdatertDokumentEnhet = distribuerDokumentEnhetTilBrevMottakere(dokumentEnhet)
            if (oppdatertDokumentEnhet.erDistribuertTilAlle()) {
                logger.debug("dokumentEnhet ${dokumentEnhet.id} er distribuert til alle, markerer som ferdig")
                val ferdigDistribuertDokumentEnhet = markerDokumentEnhetSomFerdigDistribuert(oppdatertDokumentEnhet)
                slettMellomlagretDokumentHvisDistribuert(ferdigDistribuertDokumentEnhet)
                ferdigDistribuertDokumentEnhet
            } else {
                logger.debug("dokumentEnhet ${dokumentEnhet.id} er ikke distribuert til alle, markerer ikke som ferdig")
                oppdatertDokumentEnhet
            }
        } else {
            logger.debug("dokumentEnhet ${dokumentEnhet.id} er allerede distribuert")
            dokumentEnhet
        }

    private fun distribuerDokumentEnhetTilBrevMottakere(dokumentEnhet: DokumentEnhet): DokumentEnhet =
        dokumentEnhet.copy(
            brevMottakerDistribusjoner = dokumentEnhet
                .brevMottakere
                .mapNotNull { brevMottaker ->
                    brevMottakerDistribusjonService.distribuerDokumentEnhetTilBrevMottaker(
                        brevMottaker,
                        dokumentEnhet
                    )
                })

    private fun markerDokumentEnhetSomFerdigDistribuert(dokumentEnhet: DokumentEnhet): DokumentEnhet {
        logger.debug("Markerer dokumentEnhet ${dokumentEnhet.id} som ferdig distribuert")
        return dokumentEnhet.copy(avsluttet = LocalDateTime.now())
    }

    private fun slettMellomlagretDokumentHvisDistribuert(dokumentEnhet: DokumentEnhet): DokumentEnhet {
        try {
            logger.debug("Sletter mellomlagret fil i dokumentEnhet ${dokumentEnhet.id}")
            dokumentEnhet.hovedDokument?.let { mellomlagerService.deleteDocumentAsSystemUser(it.mellomlagerId) }
        } catch (t: Throwable) {
            logger.warn("Klarte ikke å slette mellomlagret dokument ${dokumentEnhet.hovedDokument?.mellomlagerId}")
        }
        return dokumentEnhet
    }

    private fun DokumentEnhet.chainable() = ChainableOperation(this, true)
}




