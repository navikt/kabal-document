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
        const val SYSTEMBRUKER = "SYSTEMBRUKER" //TODO ??
        const val SYSTEM_JOURNALFOERENDE_ENHET = "9999"
    }

    fun distribuerDokumentEnhet(dokumentEnhet: DokumentEnhet): DokumentEnhet =
        if (dokumentEnhet.avsluttet != null) {
            logger.debug("dokumentEnhet ${dokumentEnhet.id} er ikke distribuert")
            dokumentEnhet.chainable()
                .chain(this::distribuerDokumentEnhetTilBrevMottakere) //May not succeed 100%
                .chain(this::markerDokumentEnhetSomFerdigDistribuert)
                .chain(this::slettMellomlagretDokumentHvisDistribuert)
                .value
        } else {
            dokumentEnhet
        }

    fun distribuerDokumentEnhetTilBrevMottakere(dokumentEnhet: DokumentEnhet): DokumentEnhet =
        dokumentEnhet.copy(
            brevMottakerDistribusjoner = dokumentEnhet
                .brevMottakere
                .mapNotNull { brevMottaker ->
                    brevMottakerDistribusjonService.distribuerDokumentEnhetTilBrevMottaker(
                        brevMottaker,
                        dokumentEnhet
                    )
                })
            .validateDistribuertTilAlle()

    fun markerDokumentEnhetSomFerdigDistribuert(dokumentEnhet: DokumentEnhet): DokumentEnhet {
        logger.debug("Markerer dokumentEnhet ${dokumentEnhet.id} som ferdig distribuert")
        return dokumentEnhet.copy(avsluttet = LocalDateTime.now())
    }

    fun slettMellomlagretDokumentHvisDistribuert(dokumentEnhet: DokumentEnhet): DokumentEnhet {
        return try {
            logger.debug("Sletter mellomlagret fil i dokumentEnhet ${dokumentEnhet.id}")
            dokumentEnhet.hovedDokument?.let { mellomlagerService.deleteDocumentAsSystemUser(it.mellomlagerId) }
            dokumentEnhet.copy(hovedDokument = null)
        } catch (t: Throwable) {
            "Klarte ikke å slette mellomlagret dokument ${dokumentEnhet.hovedDokument?.mellomlagerId}"
            dokumentEnhet
        }
    }

    private fun DokumentEnhet.chainable() = ChainableOperation(this, true)
}




