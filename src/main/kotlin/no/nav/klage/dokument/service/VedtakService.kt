package no.nav.klage.dokument.service

import no.nav.klage.dokument.domain.dokument.DokumentEnhet
import no.nav.klage.dokument.domain.dokument.OpplastetDokument
import no.nav.klage.dokument.domain.saksbehandler.SaksbehandlerIdent
import no.nav.klage.dokument.exceptions.MissingTilgangException
import no.nav.klage.dokument.exceptions.VedtakFinalizedException
import no.nav.klage.dokument.exceptions.VedtakNotFoundException
import no.nav.klage.dokument.util.AttachmentValidator
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@Service
@Transactional
class VedtakService(
    private val attachmentValidator: AttachmentValidator,
    private val mellomlagerService: MellomlagerService
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    fun slettFilTilknyttetVedtak(
        dokumentEnhet: DokumentEnhet,
        innloggetIdent: SaksbehandlerIdent
    ): DokumentEnhet {

        //TODO: Burde man sjekket tilgang til EnhetOgTema, ikke bare enhet?
        //tilgangService.verifyInnloggetSaksbehandlersTilgangTilEnhet(klagebehandling.tildeling!!.enhet!!)

        if (dokumentEnhet.hovedDokument == null) {
            return dokumentEnhet
        }
        slettMellomlagretDokument(
            dokumentEnhet.hovedDokument,
            innloggetIdent
        )
        return dokumentEnhet.copy(hovedDokument = null)
    }

    fun knyttVedtaksFilTilVedtak(
        dokumentEnhet: DokumentEnhet,
        fil: MultipartFile,
        innloggetIdent: SaksbehandlerIdent
    ): DokumentEnhet {

        //TODO tilgangService.verifyInnloggetSaksbehandlersTilgangTilEnhet(klagebehandling.tildeling!!.enhet!!)
        attachmentValidator.validateAttachment(fil)
        if (dokumentEnhet.avsluttetAvSaksbehandler != null) throw VedtakFinalizedException("Klagebehandlingen er avsluttet")

        if (dokumentEnhet.hovedDokument != null) {
            slettMellomlagretDokument(
                dokumentEnhet.hovedDokument,
                innloggetIdent
            )
        }

        val mellomlagerId = mellomlagerService.uploadDocument(fil)
        return dokumentEnhet.copy(
            hovedDokument = OpplastetDokument(
                mellomlagerId,
                LocalDateTime.now(),
                fil.size,
                fil.name
            )
        )
    }

    fun ferdigstillVedtak(
        dokumentEnhet: DokumentEnhet,
        innloggetIdent: SaksbehandlerIdent
    ): DokumentEnhet {
        verifyTilgangTilAaFerdigstilleVedtak(dokumentEnhet, innloggetIdent)
        if (dokumentEnhet.avsluttetAvSaksbehandler != null) throw VedtakFinalizedException("Klagebehandlingen er avsluttet")

        //Sjekker om fil er lastet opp til mellomlager
        if (dokumentEnhet.hovedDokument == null) {
            throw VedtakNotFoundException("Vennligst last opp vedtaksdokument på nytt")
        }

        //Her settes en markør som så brukes async i kallet klagebehandlingRepository.findByAvsluttetIsNullAndAvsluttetAvSaksbehandlerIsNotNull
        return dokumentEnhet.copy(avsluttetAvSaksbehandler = LocalDateTime.now())
    }

    private fun verifyTilgangTilAaFerdigstilleVedtak(
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

    fun slettMellomlagretDokument(
        opplastetDokument: OpplastetDokument,
        utfoerendeSaksbehandlerIdent: SaksbehandlerIdent
    ) {
        mellomlagerService.deleteDocument(opplastetDokument.mellomlagerId)
    }
}
