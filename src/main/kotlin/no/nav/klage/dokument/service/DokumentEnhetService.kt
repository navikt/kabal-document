package no.nav.klage.dokument.service

import no.nav.klage.dokument.api.input.DokumentEnhetWithDokumentreferanserInput
import no.nav.klage.dokument.api.mapper.DokumentEnhetInputMapper
import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.dokument.exceptions.DokumentEnhetNotValidException
import no.nav.klage.dokument.repositories.DokumentEnhetRepository
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import no.nav.klage.kodeverk.DokumentType
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class DokumentEnhetService(
    private val dokumentEnhetRepository: DokumentEnhetRepository,
    private val dokumentEnhetInputMapper: DokumentEnhetInputMapper,
    private val journalfoeringService: JournalfoeringService,
    private val dokumentDistribusjonService: DokumentDistribusjonService,
    private val mellomlagerService: MellomlagerService,

    ) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    fun ferdigstillDokumentEnhet(
        dokumentEnhetId: UUID
    ): DokumentEnhet {
        val dokumentEnhet = dokumentEnhetRepository.getReferenceById(dokumentEnhetId)

        if (dokumentEnhet.isAvsluttet()) return dokumentEnhet //Vi går for idempotens og returnerer ingen feil her

        //Sjekker om fil er lastet opp til mellomlager
        if (!dokumentEnhet.harHovedDokument()) {
            throw DokumentEnhetNotValidException("Hoveddokument er ikke lastet opp")
        }

        dokumentEnhet.brevMottakerDistribusjoner.forEach { brevMottakerDistribusjon ->
            if (brevMottakerDistribusjon.journalpostId == null) {
                try {
                    logger.debug("Creating journalpost for brevMottakerDistribusjon ${brevMottakerDistribusjon.id} in dokumentEnhet ${dokumentEnhet.id}")
                    brevMottakerDistribusjon.journalpostId =
                        createJournalpost(
                            brevMottakerDistribusjon = brevMottakerDistribusjon,
                            dokumentEnhet = dokumentEnhet
                        )
                    dokumentEnhetRepository.save(dokumentEnhet)
                } catch (t: Throwable) {
                    logger.error(
                        "Failed to create journalpost for brevMottakerDistribusjon ${brevMottakerDistribusjon.id}",
                        t
                    )
                    throw t
                }
            } else {
                logger.debug("Journalpost for brevMottakerDistribusjon ${brevMottakerDistribusjon.id} in dokumentEnhet ${dokumentEnhet.id} already exists: ${brevMottakerDistribusjon.journalpostId}")
            }
        }

        dokumentEnhet.brevMottakerDistribusjoner.forEach { brevMottakerDistribusjon ->
            if (brevMottakerDistribusjon.ferdigstiltIJoark == null) {
                logger.debug("Finalizing journalpost ${brevMottakerDistribusjon.journalpostId} for brevMottakerDistribusjon ${brevMottakerDistribusjon.id} in dokumentEnhet ${dokumentEnhet.id}")
                brevMottakerDistribusjon.ferdigstiltIJoark =
                    journalfoeringService.ferdigstillJournalpostForBrevMottaker(brevMottakerDistribusjon = brevMottakerDistribusjon)
                dokumentEnhetRepository.save(dokumentEnhet)
            } else {
                logger.debug("Journalpost ${brevMottakerDistribusjon.journalpostId} for brevMottakerDistribusjon ${brevMottakerDistribusjon.id} in dokumentEnhet ${dokumentEnhet.id} already finalized.")
            }
        }

        if (dokumentEnhet.shouldBeDistributed) {
            dokumentEnhet.brevMottakerDistribusjoner.forEach { brevMottakerDistribusjon ->
                if (brevMottakerDistribusjon.dokdistReferanse == null) {
                    try {
                        logger.debug("Distributing journalpost ${brevMottakerDistribusjon.journalpostId} for brevMottakerDistribusjon ${brevMottakerDistribusjon.id} in dokumentEnhet ${dokumentEnhet.id}")
                        brevMottakerDistribusjon.dokdistReferanse =
                            dokumentDistribusjonService.distribuerJournalpostTilMottaker(
                                journalpostId = brevMottakerDistribusjon.journalpostId!!,
                                dokumentType = dokumentEnhet.dokumentType
                            )
                        dokumentEnhetRepository.save(dokumentEnhet)
                    } catch (t: Throwable) {
                        logger.error(
                            "Failed to distribute journalpost for brevMottakerDistribusjon ${brevMottakerDistribusjon.id}",
                            t
                        )
                        throw t
                    }
                } else {
                    logger.debug("Dokdist for brevMottakerDistribusjon ${brevMottakerDistribusjon.id} in dokumentEnhet ${dokumentEnhet.id} already exists, with id ${brevMottakerDistribusjon.dokdistReferanse}")
                }
            }
        }

        if (dokumentEnhet.isProcessedForAll()) {
            logger.debug("dokumentEnhet ${dokumentEnhet.id} er behandlet for alle mottakere, markerer som ferdig")
            dokumentEnhet.avsluttet = LocalDateTime.now()
            dokumentEnhetRepository.save(dokumentEnhet)
            slettMellomlagretDokument(dokumentEnhet)
        } else {
            logger.debug("dokumentEnhet ${dokumentEnhet.id} er ikke distribuert til alle, markerer ikke som ferdig")
        }

        return dokumentEnhet
    }

    fun createJournalpost(
        brevMottakerDistribusjon: BrevMottakerDistribusjon,
        dokumentEnhet: DokumentEnhet
    ): String {
        return journalfoeringService.createJournalpostAsSystemUser(
            brevMottaker = brevMottakerDistribusjon.brevMottaker,
            hoveddokument = dokumentEnhet.hovedDokument!!,
            vedleggDokumentList = dokumentEnhet.vedlegg,
            journalfoeringData = dokumentEnhet.journalfoeringData
        )
    }

    fun createBrevMottakerDistribusjoner(
        brevMottakere: Set<BrevMottaker>,
        hovedDokumentId: UUID
    ): Set<BrevMottakerDistribusjon> {
        return brevMottakere.map { brevMottaker ->
            createBrevMottakerDistribusjon(
                brevMottaker = brevMottaker,
                hovedDokumentId = hovedDokumentId
            )
        }.toSet()
    }

    private fun createBrevMottakerDistribusjon(
        brevMottaker: BrevMottaker,
        hovedDokumentId: UUID
    ): BrevMottakerDistribusjon {
        return BrevMottakerDistribusjon(
            brevMottaker = brevMottaker,
            opplastetDokumentId = hovedDokumentId,
        )
    }

    fun opprettDokumentEnhetMedDokumentreferanser(
        input: DokumentEnhetWithDokumentreferanserInput
    ): DokumentEnhet {
        logger.debug("Creating dokumentEnhet")
        val journalfoeringData = dokumentEnhetInputMapper.mapJournalfoeringDataInput(input.journalfoeringData)
        val brevMottakere = dokumentEnhetInputMapper.mapBrevMottakereInput(input.brevMottakere)
        val hovedokument =
            dokumentEnhetInputMapper.mapDokumentInputToHoveddokument(input.dokumentreferanser.hoveddokument)
        val vedlegg = input.dokumentreferanser.vedlegg?.map {
            dokumentEnhetInputMapper.mapDokumentInputToVedlegg(it)
        } ?: emptyList()
        val brevMottakerDistribusjoner = createBrevMottakerDistribusjoner(brevMottakere, hovedokument.id)
        val dokumentType = DokumentType.of(input.dokumentTypeId)
        return dokumentEnhetRepository.save(
            DokumentEnhet(
                journalfoeringData = journalfoeringData,
                brevMottakere = brevMottakere,
                brevMottakerDistribusjoner = brevMottakerDistribusjoner,
                hovedDokument = hovedokument,
                vedlegg = vedlegg,
                dokumentType = dokumentType,
                shouldBeDistributed = dokumentType != DokumentType.NOTAT
            )
        )
    }

    private fun slettMellomlagretDokument(dokumentEnhet: DokumentEnhet) {
        try {
            logger.debug("Sletter mellomlagret fil i dokumentEnhet ${dokumentEnhet.id}")
            dokumentEnhet.hovedDokument?.let { mellomlagerService.deleteDocumentAsSystemUser(mellomlagerId = it.mellomlagerId) }
        } catch (t: Throwable) {
            logger.warn("Klarte ikke å slette mellomlagret dokument ${dokumentEnhet.hovedDokument?.mellomlagerId}")
        }
    }
}
