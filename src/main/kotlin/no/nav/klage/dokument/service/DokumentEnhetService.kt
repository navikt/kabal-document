package no.nav.klage.dokument.service

import no.nav.klage.dokument.api.input.DokumentEnhetWithDokumentreferanserInput
import no.nav.klage.dokument.api.mapper.DokumentEnhetInputMapper
import no.nav.klage.dokument.clients.joark.JournalpostResponse
import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.dokument.repositories.BrevMottakerDistribusjonRepository
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
    private val brevMottakerDistribusjonRepository: BrevMottakerDistribusjonRepository

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

        if (dokumentEnhet.isAvsluttet()) {
            logger.debug("Dokumentenhet {} already finalized.", dokumentEnhetId)
            return dokumentEnhet //Vi går for idempotens og returnerer ingen feil her
        }

        dokumentEnhet.brevMottakerDistribusjoner.forEach { brevMottakerDistribusjon ->
            if (brevMottakerDistribusjon.journalpostId == null) {
                try {
                    logger.debug(
                        "Creating journalpost for brevMottakerDistribusjon {} in dokumentEnhet {}",
                        brevMottakerDistribusjon.id,
                        dokumentEnhet.id
                    )
                    val journalpostResponse = createJournalpost(
                        brevMottakerDistribusjon = brevMottakerDistribusjon,
                        dokumentEnhet = dokumentEnhet
                    )
                    brevMottakerDistribusjon.journalpostId = journalpostResponse.journalpostId

                    journalpostResponse.dokumenter.forEachIndexed { index, dokument ->
                        if (index == 0) {
                            dokumentEnhet.hovedDokument?.dokumentInfoReferenceList?.add(
                                DokumentInfoReference(
                                    journalpostId = journalpostResponse.journalpostId,
                                    dokumentInfoId = dokument.dokumentInfoId,
                                )
                            )
                        } else {
                            val currentDokumentEnhetVedlegg = dokumentEnhet.vedlegg.find { it.index == index - 1 }
                            currentDokumentEnhetVedlegg?.dokumentInfoReferenceList?.add(
                                DokumentInfoReference(
                                    journalpostId = journalpostResponse.journalpostId,
                                    dokumentInfoId = dokument.dokumentInfoId,
                                )
                            )
                        }
                    }


                    brevMottakerDistribusjon.modified = LocalDateTime.now()
                    brevMottakerDistribusjonRepository.save(brevMottakerDistribusjon)
                } catch (t: Throwable) {
                    logger.error(
                        "Failed to create journalpost for brevMottakerDistribusjon ${brevMottakerDistribusjon.id}",
                        t
                    )
                    throw t
                }
            } else {
                logger.debug(
                    "Journalpost for brevMottakerDistribusjon {} in dokumentEnhet {} already exists: {}",
                    brevMottakerDistribusjon.id,
                    dokumentEnhet.id,
                    brevMottakerDistribusjon.journalpostId
                )
            }
        }

        dokumentEnhet.brevMottakerDistribusjoner.forEach { brevMottakerDistribusjon ->
            val toJournalfoering = dokumentEnhet.journalfoerteVedlegg.filter { journalfoertVedlegg ->
                brevMottakerDistribusjon.journalfoerteVedlegg.none { it.journalfoertVedleggId == journalfoertVedlegg.id }
            }

            if (toJournalfoering.isNotEmpty()) {
                val tilknyttVedleggResponse = journalfoeringService.tilknyttVedleggAsSystemUser(
                    journalpostId = brevMottakerDistribusjon.journalpostId!!,
                    journalfoerteVedlegg = toJournalfoering,
                )

                val setAsJournalfoert = if (tilknyttVedleggResponse.feiledeDokumenter.isEmpty()) {
                    toJournalfoering
                } else {
                    logger.warn("Noen dokumenter kunne ikke bli tilknyttet: {}", tilknyttVedleggResponse)

                    toJournalfoering.filter { journalfoertVedlegg ->
                        tilknyttVedleggResponse.feiledeDokumenter.none { feiletDokument ->
                            feiletDokument.kildeJournalpostId == journalfoertVedlegg.kildeJournalpostId &&
                                    feiletDokument.dokumentInfoId == journalfoertVedlegg.dokumentInfoId
                        }
                    }
                }

                setAsJournalfoert.forEach { journalfoertVedlegg ->
                    brevMottakerDistribusjon.journalfoerteVedlegg.add(
                        JournalfoertVedleggId(
                            journalfoertVedleggId = journalfoertVedlegg.id
                        )
                    )
                    brevMottakerDistribusjonRepository.save(brevMottakerDistribusjon)
                }

                if (tilknyttVedleggResponse.feiledeDokumenter.isNotEmpty()) {
                    throw RuntimeException("Could not call tilknyttDokument for all candidates, throwing error in order to retry.")
                }
            }
        }

        dokumentEnhet.brevMottakerDistribusjoner.forEach { brevMottakerDistribusjon ->
            if (brevMottakerDistribusjon.ferdigstiltIJoark == null) {
                try {
                    logger.debug("Finalizing journalpost ${brevMottakerDistribusjon.journalpostId} for brevMottakerDistribusjon ${brevMottakerDistribusjon.id} in dokumentEnhet ${dokumentEnhet.id}")
                    brevMottakerDistribusjon.ferdigstiltIJoark =
                        journalfoeringService.ferdigstillJournalpostForBrevMottaker(brevMottakerDistribusjon = brevMottakerDistribusjon)
                    brevMottakerDistribusjon.modified = LocalDateTime.now()
                    brevMottakerDistribusjonRepository.save(brevMottakerDistribusjon)
                } catch (t: Throwable) {
                    logger.error(
                        "Failed to finalize journalpost for brevMottakerDistribusjon ${brevMottakerDistribusjon.id}, journalpost ${brevMottakerDistribusjon.journalpostId}",
                        t
                    )
                    throw t
                }
            } else {
                logger.debug("Journalpost ${brevMottakerDistribusjon.journalpostId} for brevMottakerDistribusjon ${brevMottakerDistribusjon.id} in dokumentEnhet ${dokumentEnhet.id} already finalized.")
            }
        }

        if (dokumentEnhet.shouldBeDistributed()) {
            dokumentEnhet.brevMottakerDistribusjoner.forEach { brevMottakerDistribusjon ->
                if (brevMottakerDistribusjon.dokdistReferanse == null) {
                    try {
                        logger.debug("Distributing journalpost ${brevMottakerDistribusjon.journalpostId} for brevMottakerDistribusjon ${brevMottakerDistribusjon.id} in dokumentEnhet ${dokumentEnhet.id}")
                        brevMottakerDistribusjon.dokdistReferanse =
                            dokumentDistribusjonService.distribuerJournalpostTilMottaker(
                                journalpostId = brevMottakerDistribusjon.journalpostId!!,
                                dokumentType = dokumentEnhet.dokumentType
                            )
                        brevMottakerDistribusjon.modified = LocalDateTime.now()
                        brevMottakerDistribusjonRepository.save(brevMottakerDistribusjon)
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
            val timestamp = LocalDateTime.now()
            dokumentEnhet.avsluttet = timestamp
            dokumentEnhet.modified = timestamp
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
    ): JournalpostResponse {
        return journalfoeringService.createJournalpostAsSystemUser(
            brevMottaker = brevMottakerDistribusjon.brevMottaker,
            hoveddokument = dokumentEnhet.hovedDokument!!,
            vedleggDokumentSet = dokumentEnhet.vedlegg,
            journalfoeringData = dokumentEnhet.journalfoeringData,
            journalfoerendeSaksbehandlerIdent = dokumentEnhet.journalfoerendeSaksbehandlerIdent,
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
        val dokumentType = DokumentType.of(input.dokumentTypeId)

        if (dokumentType == DokumentType.KJENNELSE_FRA_TRYGDERETTEN) {
            if (input.journalfoeringData.inngaaendeKanal == null) {
                throw Exception("Missing inngaendeKanal")
            }

            if (input.brevMottakere.size != 1) {
                throw Exception("brevMottakere.size must be exactly 1 for KJENNELSE_FRA_TRYGDERETTEN.")
            }
        }

        val journalfoeringData =
            dokumentEnhetInputMapper.mapJournalfoeringDataInput(input.journalfoeringData, dokumentType)
        val brevMottakere = dokumentEnhetInputMapper.mapBrevMottakereInput(input.brevMottakere)
        val hovedokument =
            dokumentEnhetInputMapper.mapDokumentInputToHoveddokument(input.dokumentreferanser.hoveddokument)
        val vedlegg = input.dokumentreferanser.vedlegg?.mapIndexed { index, document ->
            dokumentEnhetInputMapper.mapDokumentInputToVedlegg(document, index)
        }?.toSet() ?: emptySet()
        val journalfoerteVedlegg = input.dokumentreferanser.journalfoerteVedlegg?.mapIndexed { index, document ->
            dokumentEnhetInputMapper.mapDokumentInputToJournalfoertVedlegg(document, index)
        }?.toSet() ?: emptySet()
        val brevMottakerDistribusjoner = createBrevMottakerDistribusjoner(brevMottakere, hovedokument.id)
        return dokumentEnhetRepository.save(
            DokumentEnhet(
                journalfoeringData = journalfoeringData,
                brevMottakere = brevMottakere,
                brevMottakerDistribusjoner = brevMottakerDistribusjoner,
                hovedDokument = hovedokument,
                vedlegg = vedlegg,
                journalfoerteVedlegg = journalfoerteVedlegg,
                dokumentType = dokumentType,
                journalfoerendeSaksbehandlerIdent = input.journalfoerendeSaksbehandlerIdent,
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
