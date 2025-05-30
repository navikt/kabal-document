package no.nav.klage.dokument.clients.joark

import no.nav.klage.dokument.domain.dokument.AvsenderMottaker
import no.nav.klage.dokument.domain.dokument.JournalfoeringData
import no.nav.klage.dokument.domain.dokument.OpplastetHoveddokument
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.kodeverk.PartIdType
import org.springframework.stereotype.Service

@Service
class JoarkMapper {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    fun createPartialJournalpostWithoutDocuments(
        journalfoeringData: JournalfoeringData,
        opplastetHovedDokument: OpplastetHoveddokument,
        avsenderMottaker: AvsenderMottaker
    ): JournalpostPartial {

        val kanal = if (journalfoeringData.journalpostType == JournalpostType.INNGAAENDE) {
            journalfoeringData.inngaaendeKanal
        } else if (avsenderMottaker.localPrint) {
            Kanal.L
        } else if (journalfoeringData.journalpostType == JournalpostType.UTGAAENDE) {
            avsenderMottaker.kanal
        } else null

        val partialJournalpostWithoutDocuments = JournalpostPartial(
            avsenderMottaker = null,
            journalposttype = journalfoeringData.journalpostType,
            tema = journalfoeringData.tema,
            behandlingstema = journalfoeringData.behandlingstema,
            sak = createSak(journalfoeringData),
            kanal = kanal,
            tittel = journalfoeringData.tittel,
            journalfoerendeEnhet = journalfoeringData.enhet,
            eksternReferanseId = "${opplastetHovedDokument.id}_${avsenderMottaker.id}",
            datoMottatt = journalfoeringData.datoMottatt,
            bruker = createBruker(journalfoeringData),
            tilleggsopplysninger = journalfoeringData.tilleggsopplysning?.let {
                listOf(
                    Tilleggsopplysning(
                        nokkel = it.key, verdi = it.value
                    )
                )
            } ?: emptyList()
        )

        if (journalfoeringData.journalpostType in listOf(JournalpostType.UTGAAENDE, JournalpostType.INNGAAENDE)) {
            partialJournalpostWithoutDocuments.avsenderMottaker = createJournalpostAvsenderMottager(avsenderMottaker)
        }

        return partialJournalpostWithoutDocuments
    }

    private fun createJournalpostAvsenderMottager(avsenderMottaker: AvsenderMottaker): JournalpostAvsenderMottaker {
        return if (avsenderMottaker.partId != null) {
            JournalpostAvsenderMottaker(
                id = avsenderMottaker.partId.value,
                idType = if (avsenderMottaker.partId.type == PartIdType.PERSON) {
                    AvsenderMottakerIdType.FNR
                } else {
                    AvsenderMottakerIdType.ORGNR
                },
                land = null,
                navn = avsenderMottaker.navn,
            )
        } else {
            JournalpostAvsenderMottaker(
                id = null,
                idType = null,
                land = avsenderMottaker.adresse?.land,
                navn = avsenderMottaker.navn!!,
            )
        }
    }

    private fun createSak(journalfoeringData: JournalfoeringData): Sak =
        Sak(
            sakstype = Sakstype.FAGSAK,
            fagsaksystem = FagsaksSystem.valueOf(journalfoeringData.sakFagsystem.name),
            fagsakid = journalfoeringData.sakFagsakId
        )

    private fun createBruker(journalfoeringData: JournalfoeringData): Bruker =
        Bruker(
            journalfoeringData.sakenGjelder.value,
            if (journalfoeringData.sakenGjelder.type == PartIdType.VIRKSOMHET) BrukerIdType.ORGNR else BrukerIdType.FNR
        )

    fun createUpdateDocumentTitleJournalpostInput(
        dokumentInfoId: String,
        title: String
    ): UpdateDocumentTitleJournalpostInput {
        return UpdateDocumentTitleJournalpostInput(
            dokumenter = listOf(
                UpdateDocumentTitleDokumentInput(
                    dokumentInfoId = dokumentInfoId,
                    tittel = title
                )
            )
        )
    }
}