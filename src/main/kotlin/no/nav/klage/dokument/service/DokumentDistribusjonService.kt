package no.nav.klage.dokument.service

import no.nav.klage.dokument.clients.dokdistfordeling.Adressetype
import no.nav.klage.dokument.clients.dokdistfordeling.DokDistFordelingClient
import no.nav.klage.dokument.domain.dokument.Adresse
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.kodeverk.DokumentType
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class DokumentDistribusjonService(
    private val dokDistFordelingClient: DokDistFordelingClient,
    private val arkivmeldingService: ArkivmeldingService,
    @Value("\${spring.profiles.active:}") private val activeSpringProfile: String,
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
        arkivmeldingTilTrygderetten: String?,
        avsenderMottakerDistribusjonId: UUID,
    ): UUID {
        val arkivmelding = if (dokumentType == DokumentType.EKSPEDISJONSBREV_TIL_TRYGDERETTEN) {
            arkivmeldingService.generateArkivmelding(
                journalpostId = journalpostId,
                avsenderMottakerDistribusjonId = avsenderMottakerDistribusjonId,
            )
        } else {
            null
        }

        if (activeSpringProfile == "dev-gcp") {
            logger.debug("Arkivmelding for journalpost $journalpostId: $arkivmelding")
        }

        return dokDistFordelingClient.distribuerJournalpost(
            journalpostId = journalpostId,
            dokumentType = dokumentType,
            tvingSentralPrint = tvingSentralPrint,
            adresse = adresse?.toDokDistAdresse(),
            arkivmeldingTilTrygderetten = arkivmeldingTilTrygderetten,
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
