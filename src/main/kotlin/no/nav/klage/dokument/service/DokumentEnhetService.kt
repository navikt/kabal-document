package no.nav.klage.dokument.service

import no.nav.klage.dokument.clients.smarteditorapi.SmartEditorClient
import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.dokument.domain.saksbehandler.SaksbehandlerIdent
import no.nav.klage.dokument.exceptions.*
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
    private val smartEditorClient: SmartEditorClient,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    //TODO: Logge hvem som gjør hva. Via logger eller lagre i db eller begge deler?

    fun slettDokumentEnhet(
        dokumentEnhetId: UUID,
        innloggetIdent: SaksbehandlerIdent
    ) {
        val dokumentEnhet = dokumentEnhetRepository.findById(dokumentEnhetId) ?: return

        //verifyTilgangTilDokumentEnhet(dokumentEnhet, innloggetIdent)

        if (dokumentEnhet.harVedlegg()) {
            throw UlovligOperasjonException("Kan ikke slette DokumentEnhet med vedlegg")
        }

        if (dokumentEnhet.erAvsluttet()) {
            throw UlovligOperasjonException("Kan ikke slette DokumentEnhet som er avsluttet")
        }

        dokumentEnhetRepository.delete(dokumentEnhetId)
    }

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
        if (dokumentEnhet.erAvsluttet()) throw DokumentEnhetFinalizedException("Dokumentenheten er journalført og kan ikke endres")

        if (dokumentEnhet.hovedDokument != null && dokumentEnhet.hovedDokument.smartEditorId != null) {
            //Kan ikke oppdatere et smartEditor dokument med vanlig uploaded fil
            throw UlovligOperasjonException("Dette er et smarteditor dokument")
        }
        attachmentValidator.validateAttachment(fil)

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
                    name = fil.originalFilename ?: throw RuntimeException("missing original filename"),
                    dokumentType = dokumentEnhet.dokumentType,
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

    fun mellomlagreNyttHovedDokument(
        dokumentEnhetId: UUID,
        smartEditorId: UUID,
        innloggetIdent: SaksbehandlerIdent,
        systemUser: Boolean = false
    ): DokumentEnhet {

        val dokumentEnhet = dokumentEnhetRepository.findById(dokumentEnhetId)
            ?: throw DokumentEnhetNotFoundException("Dokumentenhet finnes ikke")

        //verifyTilgangTilDokumentEnhet(dokumentEnhet, innloggetIdent)

        if (dokumentEnhet.erAvsluttet()) throw DokumentEnhetFinalizedException("Dokumentenheten er journalført og kan ikke endres")

        if (dokumentEnhet.hovedDokument != null && dokumentEnhet.hovedDokument.smartEditorId == null) {
            //Kan ikke oppdatere et vanlig dokument med en smartEditorId
            throw UlovligOperasjonException("Dette er ikke et smarteditor dokument")
        }
        //TODO: Brukerkontekst eller systemkontekst??
        val fil = smartEditorClient.getDocumentAsPDF(smartEditorId)

        attachmentValidator.validateAttachment(fil)

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
                    name = fil.originalFilename ?: throw RuntimeException("missing original filename"),
                    smartEditorId = smartEditorId,
                    dokumentType = dokumentEnhet.dokumentType,
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

        //Sjekker om journalfoeringsData er satt
        if (!dokumentEnhet.harJournalfoeringData()) {
            throw DokumentEnhetNotValidException("JournalfoeringData er ikke satt")
        }

        //Sjekker om brevmottakere er satt
        if (!dokumentEnhet.harMinstEnBrevMottaker()) {
            throw DokumentEnhetNotValidException("Brevmottaker er ikke satt")
        }

        return dokumentEnhetRepository.saveOrUpdate(
            dokumentEnhetDistribusjonService.distribuerDokumentEnhet(dokumentEnhet)
        )
    }

    fun ferdigstillDokumentEnhet(
        dokumentEnhetId: UUID,
        brevMottakere: List<BrevMottaker>,
        journalfoeringData: JournalfoeringData
    ): DokumentEnhet {

        val dokumentEnhet = dokumentEnhetRepository.findById(dokumentEnhetId)
            ?: throw DokumentEnhetNotFoundException("Dokumentenhet finnes ikke")

        //verifyTilgangTilDokumentEnhet(dokumentEnhet, innloggetIdent)
        if (dokumentEnhet.erAvsluttet()) return dokumentEnhet //Vi går for idempotens og returnerer ingen feil her

        //Sjekker om fil er lastet opp til mellomlager
        if (!dokumentEnhet.harHovedDokument()) {
            throw DokumentEnhetNotValidException("Hoveddokument er ikke lastet opp")
        }

        val oppdatertDokumentEnhet =
            dokumentEnhet.copy(journalfoeringData = journalfoeringData, brevMottakere = brevMottakere)

        //Sjekker om journalfoeringsData er satt
        if (!oppdatertDokumentEnhet.harJournalfoeringData()) {
            throw DokumentEnhetNotValidException("JournalfoeringData er ikke satt")
        }

        //Sjekker om brevmottakere er satt
        if (!oppdatertDokumentEnhet.harMinstEnBrevMottaker()) {
            throw DokumentEnhetNotValidException("Brevmottaker er ikke satt")
        }

        return dokumentEnhetRepository.saveOrUpdate(
            dokumentEnhetDistribusjonService.distribuerDokumentEnhet(oppdatertDokumentEnhet)
        )

    }

    fun hentMellomlagretHovedDokument(dokumentEnhetId: UUID, innloggetIdent: SaksbehandlerIdent): MellomlagretDokument {
        val dokumentEnhet = dokumentEnhetRepository.findById(dokumentEnhetId)
            ?: throw DokumentEnhetNotFoundException("Dokumentenhet finnes ikke")

        //verifyTilgangTilDokumentEnhet(dokumentEnhet, innloggetIdent)

        if (dokumentEnhet.erAvsluttet()) throw DokumentEnhetFinalizedException("Dokumentenheten er journalført, hent dokumentet fra SAF i stedet")

        if (!dokumentEnhet.harHovedDokument()) {
            throw DokumentEnhetNotFoundException("Hoveddokument er ikke lastet opp")
        }

        return mellomlagerService.getUploadedDocument(dokumentEnhet.hovedDokument!!.mellomlagerId)
    }

    fun hentMellomlagretVedlegg(
        dokumentEnhetId: UUID,
        vedleggId: UUID,
        innloggetIdent: SaksbehandlerIdent
    ): MellomlagretDokument {
        val dokumentEnhet = dokumentEnhetRepository.findById(dokumentEnhetId)
            ?: throw DokumentEnhetNotFoundException("Dokumentenhet finnes ikke")

        //verifyTilgangTilDokumentEnhet(dokumentEnhet, innloggetIdent)

        if (dokumentEnhet.erAvsluttet()) throw DokumentEnhetFinalizedException("Dokumentenheten er journalført, hent dokumentet fra SAF i stedet")

        val vedlegg = dokumentEnhet.vedlegg.find { it.id == vedleggId }
            ?: throw DokumentEnhetNotFoundException("Vedlegg finnes ikke")

        return mellomlagerService.getUploadedDocument(vedlegg.mellomlagerId)
    }

    private fun verifyTilgangTilDokumentEnhet(
        dokumentEnhet: DokumentEnhet,
        innloggetIdent: SaksbehandlerIdent?
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
        dokumentType: String?,
        eksternReferanse: String?,
        brevMottakere: List<BrevMottaker>,
        journalfoeringData: JournalfoeringData?
    ): DokumentEnhet {
        return dokumentEnhetRepository.save(
            DokumentEnhet(
                eier = eier,
                brevMottakere = brevMottakere,
                journalfoeringData = journalfoeringData,
                dokumentType = dokumentType,
                eksternReferanse = eksternReferanse,
            )
        )
    }

    fun getDokumentEnhet(dokumentEnhetId: UUID, innloggetIdent: SaksbehandlerIdent?): DokumentEnhet =
        dokumentEnhetRepository.findById(dokumentEnhetId)
            ?: throw DokumentEnhetNotFoundException("Dokumentenhet finnes ikke")

    fun opprettDokumentEnhetOgMellomlagreNyttHoveddokument(
        innloggetIdent: SaksbehandlerIdent,
        dokumentType: String?,
        eksternReferanse: String?,
        brevMottakere: List<BrevMottaker>,
        journalfoeringData: JournalfoeringData?,
        fil: MultipartFile
    ): DokumentEnhet {
        val dokumentEnhet = opprettDokumentEnhet(
            eier = innloggetIdent,
            dokumentType = dokumentType,
            eksternReferanse = eksternReferanse,
            brevMottakere = brevMottakere,
            journalfoeringData = journalfoeringData
        )
        return mellomlagreNyttHovedDokument(dokumentEnhet.id, fil, innloggetIdent)
    }

    fun opprettDokumentEnhetOgMellomlagreNyttSmartEditorHoveddokument(
        innloggetIdent: SaksbehandlerIdent,
        dokumentType: String?,
        eksternReferanse: String?,
        brevMottakere: List<BrevMottaker>,
        journalfoeringData: JournalfoeringData?,
        smartEditorId: UUID
    ): DokumentEnhet {
        val dokumentEnhet =
            opprettDokumentEnhet(
                eier = innloggetIdent,
                dokumentType = dokumentType,
                eksternReferanse = eksternReferanse,
                brevMottakere = brevMottakere,
                journalfoeringData = journalfoeringData
            )
        return mellomlagreNyttHovedDokument(dokumentEnhet.id, smartEditorId, innloggetIdent)
    }

    fun fristillVedlegg(dokumentEnhetId: UUID, vedleggId: UUID, innloggetIdent: SaksbehandlerIdent): DokumentEnhet {
        val dokumentEnhet = dokumentEnhetRepository.findById(dokumentEnhetId)
            ?: throw DokumentEnhetNotFoundException("Dokumentenhet finnes ikke")

        //verifyTilgangTilDokumentEnhet(dokumentEnhet, innloggetIdent)

        if (dokumentEnhet.erAvsluttet()) throw DokumentEnhetFinalizedException("Dokumentenheten er journalført og kan ikke endres")

        val vedleggSomSkalFristilles = dokumentEnhet.vedlegg.find { it.id == vedleggId }
            ?: throw DokumentEnhetNotFoundException("Vedlegg finnes ikke")

        dokumentEnhetRepository.saveOrUpdate(
            dokumentEnhet.copy(
                vedlegg = dokumentEnhet.vedlegg.minus(
                    vedleggSomSkalFristilles
                )
            )
        )

        return dokumentEnhetRepository.save(
            DokumentEnhet(
                id = vedleggSomSkalFristilles.id,
                eier = dokumentEnhet.eier,
                journalfoeringData = dokumentEnhet.journalfoeringData?.copy(id = UUID.randomUUID()),
                brevMottakere = dokumentEnhet.brevMottakere.map { it.copy(id = UUID.randomUUID()) },
                hovedDokument = vedleggSomSkalFristilles,
                dokumentType = vedleggSomSkalFristilles.dokumentType,
                eksternReferanse = dokumentEnhet.eksternReferanse,
            )
        )
    }

    fun kobleVedlegg(
        dokumentEnhetId: UUID,
        dokumentEnhetSomSkalBliVedleggId: UUID,
        innloggetIdent: SaksbehandlerIdent
    ): DokumentEnhet {
        val dokumentEnhet = dokumentEnhetRepository.findById(dokumentEnhetId)
            ?: throw DokumentEnhetNotFoundException("Dokumentenhet finnes ikke")


        val dokumentEnhetSomSkalBliVedlegg = dokumentEnhetRepository.findById(dokumentEnhetSomSkalBliVedleggId)
            ?: throw DokumentEnhetNotFoundException("Dokumentenhet finnes ikke")

        //verifyTilgangTilDokumentEnhet(dokumentEnhet, innloggetIdent)

        if (dokumentEnhet.erAvsluttet() || dokumentEnhetSomSkalBliVedlegg.erAvsluttet()) throw DokumentEnhetFinalizedException(
            "Dokumentenheten er journalført og kan ikke endres"
        )

        if (!dokumentEnhet.harHovedDokument() || !dokumentEnhetSomSkalBliVedlegg.harHovedDokument()) {
            throw UlovligOperasjonException("Dokumentenhet har ikke hoveddokument")
        }

        if (dokumentEnhetSomSkalBliVedlegg.harVedlegg()) {
            throw UlovligOperasjonException("Dokumentenhet som skal kobles kan ikke ha vedlegg")
        }


        dokumentEnhetRepository.delete(dokumentEnhetSomSkalBliVedlegg.id)

        return dokumentEnhetRepository.saveOrUpdate(
            dokumentEnhet.copy(
                vedlegg = dokumentEnhet.vedlegg.plus(
                    dokumentEnhetSomSkalBliVedlegg.hovedDokument!!.copy(id = dokumentEnhetSomSkalBliVedlegg.id)
                )
            )
        )
    }

    fun findDokumentEnhet(eksternReferanse: String): List<DokumentEnhet> {
        return dokumentEnhetRepository.findByEksternReferanse(eksternReferanse)
    }
}
