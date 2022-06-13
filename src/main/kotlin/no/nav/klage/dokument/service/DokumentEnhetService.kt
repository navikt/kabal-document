package no.nav.klage.dokument.service

import no.nav.klage.dokument.domain.dokument.BrevMottaker
import no.nav.klage.dokument.domain.dokument.DokumentEnhet
import no.nav.klage.dokument.domain.dokument.JournalfoeringData
import no.nav.klage.dokument.domain.dokument.OpplastetDokument
import no.nav.klage.dokument.domain.saksbehandler.SaksbehandlerIdent
import no.nav.klage.dokument.exceptions.DokumentEnhetNotFoundException
import no.nav.klage.dokument.exceptions.DokumentEnhetNotValidException
import no.nav.klage.dokument.repositories.DokumentEnhetRepository
import no.nav.klage.dokument.service.distribusjon.DokumentEnhetDistribusjonService
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import no.nav.klage.kodeverk.DokumentType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class DokumentEnhetService(
    private val dokumentEnhetRepository: DokumentEnhetRepository,
    private val dokumentEnhetDistribusjonService: DokumentEnhetDistribusjonService,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    //TODO: Logge hvem som gjør hva. Via logger eller lagre i db eller begge deler?

    fun ferdigstillDokumentEnhet(
        dokumentEnhetId: UUID
    ): DokumentEnhet {

        val dokumentEnhet = dokumentEnhetRepository.getById(dokumentEnhetId)

        //verifyTilgangTilDokumentEnhet(dokumentEnhet, innloggetIdent)
        if (dokumentEnhet.erAvsluttet()) return dokumentEnhet //Vi går for idempotens og returnerer ingen feil her

        //Sjekker om fil er lastet opp til mellomlager
        if (!dokumentEnhet.harHovedDokument()) {
            throw DokumentEnhetNotValidException("Hoveddokument er ikke lastet opp")
        }

        return dokumentEnhetDistribusjonService.distribuerDokumentEnhet(
            dokumentEnhet
        )
    }

    fun getDokumentEnhet(dokumentEnhetId: UUID): DokumentEnhet =
        dokumentEnhetRepository.getById(dokumentEnhetId)

    fun opprettDokumentEnhetMedDokumentreferanser(
        innloggetIdent: SaksbehandlerIdent,
        brevMottakere: List<BrevMottaker>,
        journalfoeringData: JournalfoeringData,
        hovedokument: OpplastetDokument,
        vedlegg: List<OpplastetDokument>,
        dokumentType: DokumentType,
    ): DokumentEnhet {
        val dokumentEnhet = dokumentEnhetRepository.save(
            DokumentEnhet(
                eier = innloggetIdent,
                brevMottakere = brevMottakere,
                journalfoeringData = journalfoeringData,
                dokumenter = listOf(hovedokument) + (vedlegg),
                dokumentType = dokumentType,
            )
        )

        return dokumentEnhet
    }

}
