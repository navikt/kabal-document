package no.nav.klage.dokument.service

import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.dokument.domain.saksbehandler.SaksbehandlerIdent
import no.nav.klage.dokument.exceptions.DokumentEnhetFinalizedException
import no.nav.klage.dokument.exceptions.DokumentEnhetNotFoundException
import no.nav.klage.dokument.exceptions.DokumentEnhetNotValidException
import no.nav.klage.dokument.exceptions.MissingTilgangException
import no.nav.klage.dokument.repositories.DokumentEnhetRepository
import no.nav.klage.dokument.service.distribusjon.DokumentEnhetDistribusjonService
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
    private val mellomlagerService: MellomlagerService,
    private val dokumentEnhetDistribusjonService: DokumentEnhetDistribusjonService,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    //TODO: Logge hvem som gjør hva. Via logger eller lagre i db eller begge deler?

    fun slettMellomlagretHovedDokument(
        dokumentEnhetId: UUID,
        innloggetIdent: SaksbehandlerIdent
    ): DokumentEnhet {

        val dokumentEnhet = dokumentEnhetRepository.findById(dokumentEnhetId)
            ?: throw DokumentEnhetNotFoundException("Dokumentenhet finnes ikke")

        //verifyTilgangTilDokumentEnhet(dokumentEnhet, innloggetIdent)

        if (!dokumentEnhet.harHovedDokument()) {
            return dokumentEnhet
        }
        val oppdatertDokumentEnhet = dokumentEnhetRepository.saveOrUpdate(dokumentEnhet.copy(hovedDokument = null))
        mellomlagerService.deleteDocument(dokumentEnhet.hovedDokument!!.mellomlagerId)

        return oppdatertDokumentEnhet
    }

    fun mellomlagreNyttHovedDokument(
        dokumentEnhetId: UUID,
        fil: MultipartFile,
        innloggetIdent: SaksbehandlerIdent,
        systemUser: Boolean = false
    ): DokumentEnhet {

        val dokumentEnhet = dokumentEnhetRepository.findById(dokumentEnhetId)
            ?: throw DokumentEnhetNotFoundException("Dokumentenhet finnes ikke")

        //verifyTilgangTilDokumentEnhet(dokumentEnhet, innloggetIdent)

        attachmentValidator.validateAttachment(fil)
        if (dokumentEnhet.erAvsluttet()) throw DokumentEnhetFinalizedException("Klagebehandlingen er avsluttet")

        val mellomlagerId = if (systemUser) {
            mellomlagerService.uploadDocumentAsSystemUser(fil)
        } else {
            mellomlagerService.uploadDocument(fil)
        }

        val oppdatertDokumentEnhet = dokumentEnhetRepository.saveOrUpdate(
            dokumentEnhet.copy(
                hovedDokument = OpplastetDokument(
                    mellomlagerId = mellomlagerId,
                    opplastet = LocalDateTime.now(),
                    size = fil.size,
                    name = fil.name
                )
            )
        )

        //Sletter ikke det gamle før vi vet at det nye er lagret
        if (dokumentEnhet.harHovedDokument()) {
            if (systemUser) {
                mellomlagerService.deleteDocumentAsSystemUser(dokumentEnhet.hovedDokument!!.mellomlagerId)
            } else {
                mellomlagerService.deleteDocument(dokumentEnhet.hovedDokument!!.mellomlagerId)
            }
        }

        return oppdatertDokumentEnhet
    }

    fun ferdigstillDokumentEnhet(
        dokumentEnhetId: UUID
    ): DokumentEnhet {

        val dokumentEnhet = dokumentEnhetRepository.findById(dokumentEnhetId)
            ?: throw DokumentEnhetNotFoundException("Dokumentenhet finnes ikke")

        //verifyTilgangTilDokumentEnhet(dokumentEnhet, innloggetIdent)
        if (dokumentEnhet.erAvsluttet()) return dokumentEnhet //Vi går for idempotens og returnerer ingen feil her

        //Sjekker om fil er lastet opp til mellomlager
        if (!dokumentEnhet.harHovedDokument()) {
            throw DokumentEnhetNotValidException("Hoveddokument er ikke lastet opp")
        }

        return dokumentEnhetRepository.saveOrUpdate(
            dokumentEnhetDistribusjonService.distribuerDokumentEnhet(
                dokumentEnhet
            )
        )
    }

    fun hentMellomlagretHovedDokument(dokumentEnhetId: UUID, innloggetIdent: SaksbehandlerIdent): MellomlagretDokument {
        val dokumentEnhet = dokumentEnhetRepository.findById(dokumentEnhetId)
            ?: throw DokumentEnhetNotFoundException("Dokumentenhet finnes ikke")

        //verifyTilgangTilDokumentEnhet(dokumentEnhet, innloggetIdent)

        if (dokumentEnhet.harHovedDokument()) {
            throw DokumentEnhetNotFoundException("Hoveddokument er ikke lastet opp")
        }

        return mellomlagerService.getUploadedDocument(dokumentEnhet.hovedDokument!!.mellomlagerId)
    }

    private fun verifyTilgangTilDokumentEnhet(
        dokumentEnhet: DokumentEnhet,
        innloggetIdent: SaksbehandlerIdent
    ) {
        if (dokumentEnhet.eier != innloggetIdent) {
            secureLogger.error(
                "{} prøvde å ferdigstille dokumentenhet {}, men er ikke eier.",
                innloggetIdent,
                dokumentEnhet.id
            )
            throw MissingTilgangException("Vedtak kan kun ferdigstilles av eier")
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

    fun getDokumentEnhet(dokumentEnhetId: UUID, innloggetIdent: SaksbehandlerIdent): DokumentEnhet =
        dokumentEnhetRepository.findById(dokumentEnhetId)
            ?: throw DokumentEnhetNotFoundException("Dokumentenhet finnes ikke")

}
