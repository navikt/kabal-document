package no.nav.klage.dokument.service.distribusjon

import no.nav.klage.dokument.domain.dokument.DokumentEnhet
import no.nav.klage.dokument.service.MellomlagerService
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
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
            logger.debug("dokumentEnhet ${dokumentEnhet.id} er ikke avsluttet")
            val oppdatertDokumentEnhet = distribuerDokumentEnhetTilBrevMottakere(dokumentEnhet)
            if (oppdatertDokumentEnhet.erDistribuertTilAlle()) {
                logger.debug("dokumentEnhet ${dokumentEnhet.id} er distribuert til alle, markerer som ferdig")
                val ferdigDistribuertDokumentEnhet = markerDokumentEnhetSomFerdigDistribuert(oppdatertDokumentEnhet)
                slettMellomlagretDokument(ferdigDistribuertDokumentEnhet)
                ferdigDistribuertDokumentEnhet
            } else {
                logger.debug("dokumentEnhet ${dokumentEnhet.id} er ikke distribuert til alle, markerer ikke som ferdig")
                oppdatertDokumentEnhet
            }
        } else {
            logger.debug("dokumentEnhet ${dokumentEnhet.id} er allerede distribuert")
            dokumentEnhet
        }

    private fun distribuerDokumentEnhetTilBrevMottakere(dokumentEnhet: DokumentEnhet): DokumentEnhet {
        val brevMottakerDistribusjoner =
            dokumentEnhet
                .brevMottakere
                .mapNotNull { brevMottaker ->
                    brevMottakerDistribusjonService.distribuerDokumentEnhetTilBrevMottaker(
                        brevMottaker,
                        dokumentEnhet
                    )
                }

        dokumentEnhet.brevMottakerDistribusjoner = brevMottakerDistribusjoner.toMutableList()

        return dokumentEnhet
    }

    private fun markerDokumentEnhetSomFerdigDistribuert(dokumentEnhet: DokumentEnhet): DokumentEnhet {
        logger.debug("Markerer dokumentEnhet ${dokumentEnhet.id} som ferdig distribuert")
        dokumentEnhet.avsluttet = LocalDateTime.now()
        return dokumentEnhet
    }

    private fun slettMellomlagretDokument(dokumentEnhet: DokumentEnhet) {
        try {
            logger.debug("Sletter mellomlagret fil i dokumentEnhet ${dokumentEnhet.id}")
            dokumentEnhet.getHovedDokument()?.let { mellomlagerService.deleteDocumentAsSystemUser(it.mellomlagerId) }
        } catch (t: Throwable) {
            logger.warn("Klarte ikke å slette mellomlagret dokument ${dokumentEnhet.getHovedDokument()?.mellomlagerId}")
        }
    }
}




