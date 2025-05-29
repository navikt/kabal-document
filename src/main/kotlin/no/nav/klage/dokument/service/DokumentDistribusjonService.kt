package no.nav.klage.dokument.service

import no.nav.klage.dokument.clients.dokdistfordeling.Adressetype
import no.nav.klage.dokument.clients.dokdistfordeling.DokDistFordelingClient
import no.nav.klage.dokument.domain.dokument.Adresse
import no.nav.klage.dokument.util.getLogger
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
    }

    fun distribuerJournalpostTilMottaker(
        journalpostId: String,
        dokumentType: DokumentType,
        tvingSentralPrint: Boolean,
        adresse: Adresse?,
    ): UUID {
        return dokDistFordelingClient.distribuerJournalpost(
            journalpostId = journalpostId,
            dokumentType = dokumentType,
            tvingSentralPrint = tvingSentralPrint,
            adresse = adresse?.toDokDistAdresse()
        ).bestillingsId
    }

    private fun Adresse.toDokDistAdresse(): no.nav.klage.dokument.clients.dokdistfordeling.Adresse {
        return no.nav.klage.dokument.clients.dokdistfordeling.Adresse(
            adressetype = Adressetype.valueOf(adressetype),
            adresselinje1 = adresselinje1,
            adresselinje2 = adresselinje2,
            adresselinje3 = adresselinje3,
            postnummer = postnummer,
            poststed = poststed,
            land = land,
        )
    }
}
