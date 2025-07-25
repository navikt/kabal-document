package no.nav.klage.dokument.clients.dokdistfordeling

import no.nav.klage.dokument.util.TokenUtil
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.kodeverk.DokumentType
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class DokDistFordelingClient(
    private val dokDistWebClient: WebClient,
    private val tokenUtil: TokenUtil
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        const val EKSPEDISJONSBREV_ENABLED = false
    }

    @Value("\${spring.application.name}")
    lateinit var applicationName: String

    fun distribuerJournalpost(
        journalpostId: String,
        dokumentType: DokumentType,
        adresse: Adresse?,
        tvingSentralPrint: Boolean,
        arkivmeldingTilTrygderetten: String?,
    ): DistribuerJournalpostResponse {
        logger.debug("Skal distribuere journalpost $journalpostId")
        val payload = mapToDistribuerJournalpostRequest(
            journalpostId = journalpostId,
            dokumentType = dokumentType,
            adresse = adresse,
            tvingSentralPrint = tvingSentralPrint,
            arkivmeldingTilTrygderetten = arkivmeldingTilTrygderetten,
        )
        val distribuerJournalpostResponse = dokDistWebClient.post()
            .header("Nav-Consumer-Id", applicationName)
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenUtil.getAppAccessTokenWithSafScope()}")
            .bodyValue(payload)
            .retrieve()
            .bodyToMono(DistribuerJournalpostResponse::class.java)
            .block()
            ?: throw RuntimeException("Journalpost with id $journalpostId could not be distributed.")

        logger.debug(
            "Journalpost with id {} successfully distributed, resulting in bestillingsId {}.",
            journalpostId,
            distribuerJournalpostResponse.bestillingsId
        )

        return distribuerJournalpostResponse
    }

    private fun mapToDistribuerJournalpostRequest(
        journalpostId: String,
        dokumentType: DokumentType,
        tvingSentralPrint: Boolean,
        adresse: Adresse?,
        arkivmeldingTilTrygderetten: String?,
    ): DistribuerJournalpostRequest {
        return DistribuerJournalpostRequest(
            journalpostId = journalpostId,
            bestillendeFagsystem = applicationName,
            dokumentProdApp = applicationName,
            distribusjonstype = dokumentType.toDistribusjonsType(),
            distribusjonstidspunkt = dokumentType.toDistribusjonstidspunkt(),
            adresse = adresse,
            tvingKanal = if (dokumentType == DokumentType.EKSPEDISJONSBREV_TIL_TRYGDERETTEN && EKSPEDISJONSBREV_ENABLED) {
                DistribuerJournalpostRequest.Kanal.TRYGDERETTEN
            } else if (tvingSentralPrint) {
                DistribuerJournalpostRequest.Kanal.PRINT
            } else null
        )
    }
}