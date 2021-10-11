package no.nav.klage.dokument.service

import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.dokument.domain.saksbehandler.SaksbehandlerIdent
import no.nav.klage.dokument.exceptions.DokumentEnhetFinalizedException
import no.nav.klage.dokument.exceptions.DokumentEnhetNotFoundException
import no.nav.klage.dokument.exceptions.MissingTilgangException
import no.nav.klage.dokument.repositories.DokumentEnhetRepository
import no.nav.klage.dokument.util.AttachmentValidator
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class DokumentEnhetService(
    private val dokumentEnhetRepository: DokumentEnhetRepository,
    private val attachmentValidator: AttachmentValidator,
    private val mellomlagerService: MellomlagerService
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    fun slettMellomlagretHovedDokument(
        dokumentEnhetId: UUID,
        innloggetIdent: SaksbehandlerIdent
    ): DokumentEnhet {

        val dokumentEnhet = dokumentEnhetRepository.findById(dokumentEnhetId)
            ?: throw DokumentEnhetNotFoundException("Dokumentenhet finnes ikke")

        //TODO: Burde man sjekket tilgang til EnhetOgTema, ikke bare enhet?
        //tilgangService.verifyInnloggetSaksbehandlersTilgangTilEnhet(klagebehandling.tildeling!!.enhet!!)

        if (dokumentEnhet.hovedDokument == null) {
            return dokumentEnhet
        }
        mellomlagerService.deleteDocument(dokumentEnhet.hovedDokument.mellomlagerId)
        return dokumentEnhetRepository.save(dokumentEnhet.copy(hovedDokument = null))
    }

    fun mellomlagreNyttHovedDokument(
        dokumentEnhetId: UUID,
        fil: MultipartFile,
        innloggetIdent: SaksbehandlerIdent
    ): DokumentEnhet {

        val dokumentEnhet = dokumentEnhetRepository.findById(dokumentEnhetId)
            ?: throw DokumentEnhetNotFoundException("Dokumentenhet finnes ikke")

        //TODO tilgangService.verifyInnloggetSaksbehandlersTilgangTilEnhet(klagebehandling.tildeling!!.enhet!!)
        attachmentValidator.validateAttachment(fil)
        if (dokumentEnhet.avsluttetAvSaksbehandler != null) throw DokumentEnhetFinalizedException("Klagebehandlingen er avsluttet")

        if (dokumentEnhet.hovedDokument != null) {
            mellomlagerService.deleteDocument(dokumentEnhet.hovedDokument.mellomlagerId)
        }

        val mellomlagerId = mellomlagerService.uploadDocument(fil)

        return dokumentEnhetRepository.save(
            dokumentEnhet.copy(
                hovedDokument = OpplastetDokument(
                    mellomlagerId,
                    LocalDateTime.now(),
                    fil.size,
                    fil.name
                )
            )
        )
    }

    fun ferdigstillDokumentEnhet(
        dokumentEnhetId: UUID,
        innloggetIdent: SaksbehandlerIdent
    ): DokumentEnhet {

        val dokumentEnhet = dokumentEnhetRepository.findById(dokumentEnhetId)
            ?: throw DokumentEnhetNotFoundException("Dokumentenhet finnes ikke")

        verifyTilgangTilAaFerdigstilleDokumentEnhet(dokumentEnhet, innloggetIdent)
        if (dokumentEnhet.avsluttetAvSaksbehandler != null) throw DokumentEnhetFinalizedException("Dokumentenheten er avsluttet")

        //Sjekker om fil er lastet opp til mellomlager
        if (dokumentEnhet.hovedDokument == null) {
            throw DokumentEnhetNotFoundException("Hoveddokument er ikke lastet opp")
        }

        //Her settes en markør som så brukes async i kallet klagebehandlingRepository.findByAvsluttetIsNullAndAvsluttetAvSaksbehandlerIsNotNull
        return dokumentEnhetRepository.save(dokumentEnhet.copy(avsluttetAvSaksbehandler = LocalDateTime.now()))
    }

    fun hentMellomlagretHovedDokument(dokumentEnhetId: UUID): MellomlagretDokument {
        val dokumentEnhet = dokumentEnhetRepository.findById(dokumentEnhetId)
            ?: throw DokumentEnhetNotFoundException("Dokumentenhet finnes ikke")
        if (dokumentEnhet.hovedDokument == null) {
            throw DokumentEnhetNotFoundException("Hoveddokument er ikke lastet opp")
        }

        return mellomlagerService.getUploadedDocument(dokumentEnhet.hovedDokument.mellomlagerId)
    }

    private fun verifyTilgangTilAaFerdigstilleDokumentEnhet(
        dokumentEnhet: DokumentEnhet,
        innloggetIdent: SaksbehandlerIdent
    ) {
        if (dokumentEnhet.eier != innloggetIdent) {
            secureLogger.error(
                "{} prøvde å fullføre vedtak for klagebehandling {}, men er ikke medunderskriver.",
                innloggetIdent,
                dokumentEnhet.id
            )
            throw MissingTilgangException("Vedtak kan kun ferdigstilles av medunderskriver")
        }
    }

    fun opprettDokumentEnhet(
        eier: SaksbehandlerIdent,
        brevMottakere: List<BrevMottaker>,
        journalfoeringData: JournalfoeringData
    ): DokumentEnhet {
        return dokumentEnhetRepository.save(
            DokumentEnhet(
                eier = eier,
                brevMottakere = brevMottakere,
                journalfoeringData = journalfoeringData
            )
        )
    }
}
