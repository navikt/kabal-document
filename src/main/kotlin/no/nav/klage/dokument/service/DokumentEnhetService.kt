package no.nav.klage.dokument.service

import no.nav.klage.dokument.api.input.DokumentEnhetWithDokumentreferanserInput
import no.nav.klage.dokument.api.mapper.DokumentEnhetInputMapper
import no.nav.klage.dokument.clients.joark.JournalpostResponse
import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.dokument.repositories.AvsenderMottakerDistribusjonRepository
import no.nav.klage.dokument.repositories.DokumentEnhetRepository
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.isInngaaende
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
    private val avsenderMottakerDistribusjonRepository: AvsenderMottakerDistribusjonRepository

) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    fun ferdigstillDokumentEnhet(
        dokumentEnhetId: UUID
    ): DokumentEnhet {
        val dokumentEnhet = dokumentEnhetRepository.getReferenceById(dokumentEnhetId)

        if (dokumentEnhet.isAvsluttet()) {
            logger.debug("Dokumentenhet {} already finalized.", dokumentEnhetId)
            return dokumentEnhet //Vi går for idempotens og returnerer ingen feil her
        }

        dokumentEnhet.avsenderMottakerDistribusjoner.forEach { avsenderMottakerDistribusjon ->
            if (avsenderMottakerDistribusjon.journalpostId == null) {
                try {
                    logger.debug(
                        "Creating journalpost for avsenderMottakerDistribusjon {} in dokumentEnhet {}",
                        avsenderMottakerDistribusjon.id,
                        dokumentEnhet.id
                    )
                    val journalpostResponse = createJournalpost(
                        avsenderMottakerDistribusjon = avsenderMottakerDistribusjon,
                        dokumentEnhet = dokumentEnhet
                    )
                    avsenderMottakerDistribusjon.journalpostId = journalpostResponse.journalpostId

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


                    avsenderMottakerDistribusjon.modified = LocalDateTime.now()
                    avsenderMottakerDistribusjonRepository.save(avsenderMottakerDistribusjon)
                } catch (t: Throwable) {
                    logger.error(
                        "Failed to create journalpost for avsenderMottakerDistribusjon ${avsenderMottakerDistribusjon.id}",
                        t
                    )
                    throw t
                }
            } else {
                logger.debug(
                    "Journalpost for avsenderMottakerDistribusjon {} in dokumentEnhet {} already exists: {}",
                    avsenderMottakerDistribusjon.id,
                    dokumentEnhet.id,
                    avsenderMottakerDistribusjon.journalpostId
                )
            }
        }

        dokumentEnhet.avsenderMottakerDistribusjoner.forEach { avsenderMottakerDistribusjon ->
            val toJournalfoering = dokumentEnhet.journalfoerteVedlegg.filter { journalfoertVedlegg ->
                avsenderMottakerDistribusjon.journalfoerteVedlegg.none { it.journalfoertVedleggId == journalfoertVedlegg.id }
            }

            if (toJournalfoering.isNotEmpty()) {
                val tilknyttVedleggResponse = journalfoeringService.tilknyttVedleggAsSystemUser(
                    journalpostId = avsenderMottakerDistribusjon.journalpostId!!,
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
                    avsenderMottakerDistribusjon.journalfoerteVedlegg.add(
                        JournalfoertVedleggId(
                            journalfoertVedleggId = journalfoertVedlegg.id
                        )
                    )
                    avsenderMottakerDistribusjonRepository.save(avsenderMottakerDistribusjon)
                }

                if (tilknyttVedleggResponse.feiledeDokumenter.isNotEmpty()) {
                    throw RuntimeException("Could not call tilknyttDokument for all candidates, throwing error in order to retry.")
                }
            }
        }

        dokumentEnhet.avsenderMottakerDistribusjoner.forEach { avsenderMottakerDistribusjon ->
            if (avsenderMottakerDistribusjon.ferdigstiltIJoark == null) {
                try {
                    logger.debug("Finalizing journalpost ${avsenderMottakerDistribusjon.journalpostId} for avsenderMottakerDistribusjon ${avsenderMottakerDistribusjon.id} in dokumentEnhet ${dokumentEnhet.id}")
                    avsenderMottakerDistribusjon.ferdigstiltIJoark =
                        journalfoeringService.ferdigstillJournalpostForAvsenderMottakerDistribusjon(avsenderMottakerDistribusjon = avsenderMottakerDistribusjon)
                    avsenderMottakerDistribusjon.modified = LocalDateTime.now()
                    avsenderMottakerDistribusjonRepository.save(avsenderMottakerDistribusjon)
                } catch (t: Throwable) {
                    logger.error(
                        "Failed to finalize journalpost for avsenderMottakerDistribusjon ${avsenderMottakerDistribusjon.id}, journalpost ${avsenderMottakerDistribusjon.journalpostId}",
                        t
                    )
                    throw t
                }
            } else {
                logger.debug("Journalpost ${avsenderMottakerDistribusjon.journalpostId} for avsenderMottakerDistribusjon ${avsenderMottakerDistribusjon.id} in dokumentEnhet ${dokumentEnhet.id} already finalized.")
            }
        }

        if (dokumentEnhet.shouldBeDistributed()) {
            dokumentEnhet.avsenderMottakerDistribusjoner.forEach { avsenderMottakerDistribusjon ->
                if (avsenderMottakerDistribusjon.shouldBeDistributed()) {
                    if (avsenderMottakerDistribusjon.dokdistReferanse == null) {
                        try {
                            logger.debug("Distributing journalpost ${avsenderMottakerDistribusjon.journalpostId} for avsenderMottakerDistribusjon ${avsenderMottakerDistribusjon.id} in dokumentEnhet ${dokumentEnhet.id}")
                            avsenderMottakerDistribusjon.dokdistReferanse =
                                dokumentDistribusjonService.distribuerJournalpostTilMottaker(
                                    journalpostId = avsenderMottakerDistribusjon.journalpostId!!,
                                    dokumentType = dokumentEnhet.dokumentType,
                                    tvingSentralPrint = avsenderMottakerDistribusjon.avsenderMottaker.tvingSentralPrint,
                                    adresse = avsenderMottakerDistribusjon.avsenderMottaker.adresse,
                                    avsenderMottakerDistribusjonId = avsenderMottakerDistribusjon.id,
                                )
                            avsenderMottakerDistribusjon.modified = LocalDateTime.now()
                            avsenderMottakerDistribusjonRepository.save(avsenderMottakerDistribusjon)
                        } catch (t: Throwable) {
                            logger.error(
                                "Failed to distribute journalpost for avsenderMottakerDistribusjon ${avsenderMottakerDistribusjon.id}",
                                t
                            )
                            throw t
                        }
                    } else {
                        logger.debug("Dokdist for avsenderMottakerDistribusjon ${avsenderMottakerDistribusjon.id} in dokumentEnhet ${dokumentEnhet.id} already exists, with id ${avsenderMottakerDistribusjon.dokdistReferanse}")
                    }
                }
            }
        }

        if (dokumentEnhet.isProcessedForAll()) {
            logger.debug("dokumentEnhet ${dokumentEnhet.id} er behandlet for alle mottakere, markerer som ferdig")
            val timestamp = LocalDateTime.now()
            dokumentEnhet.avsluttet = timestamp
            dokumentEnhet.modified = timestamp
            dokumentEnhetRepository.save(dokumentEnhet)
        } else {
            logger.debug("dokumentEnhet ${dokumentEnhet.id} er ikke distribuert til alle, markerer ikke som ferdig")
        }

        return dokumentEnhet
    }

    fun createJournalpost(
        avsenderMottakerDistribusjon: AvsenderMottakerDistribusjon,
        dokumentEnhet: DokumentEnhet
    ): JournalpostResponse {
        return journalfoeringService.createJournalpostAsSystemUser(
            avsenderMottaker = avsenderMottakerDistribusjon.avsenderMottaker,
            hoveddokument = dokumentEnhet.hovedDokument!!,
            vedleggDokumentSet = dokumentEnhet.vedlegg,
            journalfoeringData = dokumentEnhet.journalfoeringData,
            journalfoerendeSaksbehandlerIdent = dokumentEnhet.journalfoerendeSaksbehandlerIdent,
        )
    }

    fun createAvsenderMottakerDistribusjoner(
        avsenderMottakere: Set<AvsenderMottaker>,
        hovedDokumentId: UUID
    ): Set<AvsenderMottakerDistribusjon> {
        return avsenderMottakere.map { avsenderMottaker ->
            createAvsenderMottakerDistribusjon(
                avsenderMottaker = avsenderMottaker,
                hovedDokumentId = hovedDokumentId
            )
        }.toSet()
    }

    private fun createAvsenderMottakerDistribusjon(
        avsenderMottaker: AvsenderMottaker,
        hovedDokumentId: UUID
    ): AvsenderMottakerDistribusjon {
        return AvsenderMottakerDistribusjon(
            avsenderMottaker = avsenderMottaker,
            opplastetDokumentId = hovedDokumentId,
        )
    }

    fun opprettDokumentEnhetMedDokumentreferanser(
        input: DokumentEnhetWithDokumentreferanserInput
    ): DokumentEnhet {
        logger.debug("Creating dokumentEnhet")
        val dokumentType = DokumentType.of(input.dokumentTypeId)

        if (dokumentType.isInngaaende()) {
            if (input.journalfoeringData.inngaaendeKanal == null) {
                throw Exception("Missing inngaendeKanal")
            }

            if (input.avsenderMottakerList.size != 1) {
                throw Exception("avsenderMottakerList.size must be exactly 1 for ${dokumentType.navn}.")
            }
        }

        val journalfoeringData =
            dokumentEnhetInputMapper.mapJournalfoeringDataInput(input.journalfoeringData, dokumentType)
        val avsenderMottakere = dokumentEnhetInputMapper.mapAvsenderMottakerInputList(input.avsenderMottakerList)
        val hovedokument =
            dokumentEnhetInputMapper.mapDokumentInputToHoveddokument(input.dokumentreferanser.hoveddokument)
        val vedlegg = input.dokumentreferanser.vedlegg?.mapIndexed { index, document ->
            dokumentEnhetInputMapper.mapDokumentInputToVedlegg(document, index)
        }?.toSet() ?: emptySet()
        val journalfoerteVedlegg = input.dokumentreferanser.journalfoerteVedlegg?.mapIndexed { index, document ->
            dokumentEnhetInputMapper.mapDokumentInputToJournalfoertVedlegg(document, index)
        }?.toSet() ?: emptySet()
        val avsenderMottakerDistribusjoner = createAvsenderMottakerDistribusjoner(avsenderMottakere, hovedokument.id)
        return dokumentEnhetRepository.save(
            DokumentEnhet(
                journalfoeringData = journalfoeringData,
                avsenderMottakere = avsenderMottakere,
                avsenderMottakerDistribusjoner = avsenderMottakerDistribusjoner,
                hovedDokument = hovedokument,
                vedlegg = vedlegg,
                journalfoerteVedlegg = journalfoerteVedlegg,
                dokumentType = dokumentType,
                journalfoerendeSaksbehandlerIdent = input.journalfoerendeSaksbehandlerIdent,
            )
        )
    }
}
