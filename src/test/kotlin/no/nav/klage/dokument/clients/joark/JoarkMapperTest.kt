package no.nav.klage.dokument.clients.joark

import no.nav.klage.dokument.domain.dokument.AvsenderMottaker
import no.nav.klage.dokument.domain.dokument.JournalfoeringData
import no.nav.klage.dokument.domain.dokument.OpplastetHoveddokument
import no.nav.klage.dokument.domain.dokument.PartId
import no.nav.klage.kodeverk.Fagsystem
import no.nav.klage.kodeverk.PartIdType
import no.nav.klage.kodeverk.Tema
import java.util.*

internal class JoarkMapperTest {

    private val joarkMapper = JoarkMapper()

    private val FNR = "FNR"
    private val SAKFAGSAKID = "SAKFAGSAKID"
    private val KILDEREFERANSE = "KILDEREFERANSE"
    private val ENHET = "ENHET"
    private val BEHANDLINGSTEMA = "BEHANDLINGSTEMA"
    private val TITTEL = "TITTEL"
    private val BREVKODE = "BREVKODE"
    private val OPPLASTET_DOKUMENT_ID = UUID.randomUUID()
    private val MELLOMLAGER_ID = UUID.randomUUID()
    private val AVSENDERMOTTAGER_ID = UUID.randomUUID()
    private val SIZE = 16L
    private val DOKUMENT_NAME = "DOKUMENT_NAME"
    private val MELLOMLAGER_TITLE = "MELLOMLAGER_TITLE"
    private val MELLOMLAGER_VEDLEGG_TITLE = "MELLOMLAGER_VEDLEGG_TITLE"
    private val NAVN = "NAVN"
    private val TEMA = Tema.OMS
    private val SAKFAGSYSTEM = Fagsystem.K9
    private val PDF = "PDF"
    private val ARKIV = "ARKIV"

    private val sakenGjelder = PartId(
        type = PartIdType.PERSON,
        value = FNR
    )

    private val journalfoeringData = JournalfoeringData(
        sakenGjelder = sakenGjelder,
        tema = TEMA,
        sakFagsakId = SAKFAGSAKID,
        sakFagsystem = SAKFAGSYSTEM,
        kildeReferanse = KILDEREFERANSE,
        enhet = ENHET,
        behandlingstema = BEHANDLINGSTEMA,
        tittel = TITTEL,
        brevKode = BREVKODE,
        tilleggsopplysning = null,
        inngaaendeKanal = null,
        datoMottatt = null,
    )

    private val opplastetHovedDokument = OpplastetHoveddokument(
        id = OPPLASTET_DOKUMENT_ID,
        mellomlagerId = MELLOMLAGER_ID.toString(),
        name = DOKUMENT_NAME,
        sourceReference = UUID.randomUUID(),
    )

//    private val mellomlagretDokument = JournalfoeringService.MellomlagretDokument(
//        title = MELLOMLAGER_TITLE,
//        file = ByteArray(SIZE.toInt()),
//        contentType = MediaType.APPLICATION_JSON
//    )
//
//    private val mellomlagretVedleggDokument = JournalfoeringService.MellomlagretDokument(
//        title = MELLOMLAGER_VEDLEGG_TITLE,
//        file = ByteArray(SIZE.toInt()),
//        contentType = MediaType.APPLICATION_JSON
//    )

    private val avsenderMottaker = AvsenderMottaker(
        id = AVSENDERMOTTAGER_ID,
        partId = sakenGjelder,
        navn = NAVN,
        adresse = null,
        tvingSentralPrint = false,
        localPrint = false,
        kanal = null,
    )

    private val hovedDokument = Dokument(
        tittel = MELLOMLAGER_TITLE,
        brevkode = BREVKODE,
        dokumentVarianter = listOf(
            DokumentVariant(
                filnavn = MELLOMLAGER_TITLE,
                filtype = PDF,
                fysiskDokument = Base64.getEncoder().encodeToString(ByteArray(SIZE.toInt())),
                variantformat = ARKIV
            )
        )
    )

    private val vedleggDokument = Dokument(
        tittel = MELLOMLAGER_VEDLEGG_TITLE,
        brevkode = BREVKODE,
        dokumentVarianter = listOf(
            DokumentVariant(
                filnavn = MELLOMLAGER_VEDLEGG_TITLE,
                filtype = PDF,
                fysiskDokument = Base64.getEncoder().encodeToString(ByteArray(SIZE.toInt())),
                variantformat = ARKIV
            )
        )
    )

    private val expectedJournalpostWithTwoDocuments = Journalpost(
        journalposttype = JournalpostType.UTGAAENDE,
        tema = TEMA,
        behandlingstema = BEHANDLINGSTEMA,
        tittel = TITTEL,
        avsenderMottaker = JournalpostAvsenderMottaker(
            id = FNR,
            idType = AvsenderMottakerIdType.FNR,
            navn = NAVN,
            land = null
        ),
        journalfoerendeEnhet = ENHET,
        eksternReferanseId = "${OPPLASTET_DOKUMENT_ID}_$AVSENDERMOTTAGER_ID",
        bruker = Bruker(
            id = FNR,
            idType = BrukerIdType.FNR
        ),
        sak = Sak(
            sakstype = Sakstype.FAGSAK,
            fagsaksystem = FagsaksSystem.K9,
            fagsakid = SAKFAGSAKID
        ),
        dokumenter = listOf(
            hovedDokument,
            vedleggDokument
        ),
        tilleggsopplysninger = emptyList(),
        datoMottatt = null,
        kanal = null,
    )

    private val expectedJournalpostWithOneDocument = Journalpost(
        journalposttype = JournalpostType.UTGAAENDE,
        tema = TEMA,
        behandlingstema = BEHANDLINGSTEMA,
        tittel = TITTEL,
        avsenderMottaker = JournalpostAvsenderMottaker(
            id = FNR,
            idType = AvsenderMottakerIdType.FNR,
            navn = NAVN,
            land = null
        ),
        journalfoerendeEnhet = ENHET,
        eksternReferanseId = "${OPPLASTET_DOKUMENT_ID}_$AVSENDERMOTTAGER_ID",
        bruker = Bruker(
            id = FNR,
            idType = BrukerIdType.FNR
        ),
        sak = Sak(
            sakstype = Sakstype.FAGSAK,
            fagsaksystem = FagsaksSystem.K9,
            fagsakid = SAKFAGSAKID
        ),
        dokumenter = listOf(
            hovedDokument
        ),
        tilleggsopplysninger = emptyList(),
        datoMottatt = null,
        kanal = null,
    )

//    @Test
//    fun `createJournalpost works as expected with one document`() {
//
//        val resultingJournalpost = joarkMapper.createJournalpost(
//            journalfoeringData = journalfoeringData,
//            opplastetHovedDokument = opplastetHovedDokument,
//            hovedDokument = mellomlagretDokument,
//            avsenderMottaker = avsenderMottaker
//        )
//
//        assertEquals(expectedJournalpostWithOneDocument, resultingJournalpost)
//    }
//
//    @Test
//    fun `createJournalpost works as expected with two documents`() {
//
//        val resultingJournalpost = joarkMapper.createJournalpost(
//            journalfoeringData = journalfoeringData,
//            opplastetHovedDokument = opplastetHovedDokument,
//            hovedDokument = mellomlagretDokument,
//            vedleggDokumentList = listOf(mellomlagretVedleggDokument),
//            avsenderMottaker = avsenderMottaker
//        )
//
//        assertEquals(expectedJournalpostWithTwoDocuments, resultingJournalpost)
//    }
}