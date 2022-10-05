package no.nav.klage.dokument.clients.joark

import io.mockk.every
import io.mockk.mockk
import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.dokument.util.PdfUtils
import no.nav.klage.kodeverk.Fagsystem
import no.nav.klage.kodeverk.PartIdType
import no.nav.klage.kodeverk.Tema
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import java.time.LocalDateTime
import java.util.*

internal class JoarkMapperTest {

    private val pdfUtils = mockk<PdfUtils>()

    private val joarkMapper = JoarkMapper(
        pdfUtils = pdfUtils
    )

    private val FNR = "FNR"
    private val SAKFAGSAKID = "SAKFAGSAKID"
    private val KILDEREFERANSE = "KILDEREFERANSE"
    private val ENHET = "ENHET"
    private val BEHANDLINGSTEMA = "BEHANDLINGSTEMA"
    private val TITTEL = "TITTEL"
    private val BREVKODE = "BREVKODE"
    private val OPPLASTET_DOKUMENT_ID = UUID.randomUUID()
    private val MELLOMLAGER_ID = UUID.randomUUID()
    private val BREVMOTTAGER_ID = UUID.randomUUID()
    private val OPPLASTET = LocalDateTime.now()
    private val SIZE = 16L
    private val DOKUMENT_NAME = "DOKUMENT_NAME"
    private val MELLOMLAGER_TITLE = "MELLOMLAGER_TITLE"
    private val MELLOMLAGER_VEDLEGG_TITLE = "MELLOMLAGER_VEDLEGG_TITLE"
    private val NAVN = "NAVN"
    private val TEMA = Tema.OMS
    private val SAKFAGSYSTEM = Fagsystem.K9
    private val PDFA = "PDFA"
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
        tilleggsopplysning = null
    )

    private val opplastetDokument = OpplastetDokument(
        id = OPPLASTET_DOKUMENT_ID,
        mellomlagerId = MELLOMLAGER_ID.toString(),
        opplastet = OPPLASTET,
        size = SIZE,
        name = DOKUMENT_NAME
    )

    private val mellomlagretDokument = MellomlagretDokument(
        title = MELLOMLAGER_TITLE,
        content = ByteArray(SIZE.toInt()),
        contentType = MediaType.APPLICATION_JSON
    )

    private val mellomlagretVedleggDokument = MellomlagretDokument(
        title = MELLOMLAGER_VEDLEGG_TITLE,
        content = ByteArray(SIZE.toInt()),
        contentType = MediaType.APPLICATION_JSON
    )

    private val brevMottaker = BrevMottaker(
        id = BREVMOTTAGER_ID,
        partId = sakenGjelder,
        navn = NAVN,
    )

    private val hovedDokument = Dokument(
        tittel = MELLOMLAGER_TITLE,
        brevkode = BREVKODE,
        dokumentVarianter = listOf(
            DokumentVariant(
                filnavn = MELLOMLAGER_TITLE,
                filtype = PDFA,
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
                filtype = PDFA,
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
        avsenderMottaker = AvsenderMottaker(
            id = FNR,
            idType = AvsenderMottakerIdType.FNR,
            navn = NAVN,
            land = null
        ),
        journalfoerendeEnhet = ENHET,
        eksternReferanseId = "${OPPLASTET_DOKUMENT_ID}_$BREVMOTTAGER_ID",
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
        tilleggsopplysninger = emptyList()
    )

    private val expectedJournalpostWithOneDocument = Journalpost(
        journalposttype = JournalpostType.UTGAAENDE,
        tema = TEMA,
        behandlingstema = BEHANDLINGSTEMA,
        tittel = TITTEL,
        avsenderMottaker = AvsenderMottaker(
            id = FNR,
            idType = AvsenderMottakerIdType.FNR,
            navn = NAVN,
            land = null
        ),
        journalfoerendeEnhet = ENHET,
        eksternReferanseId = "${OPPLASTET_DOKUMENT_ID}_$BREVMOTTAGER_ID",
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
        tilleggsopplysninger = emptyList()
    )

    @Test
    fun `createJournalpost works as expected with one documents`() {
        every { pdfUtils.pdfByteArrayIsPdfa(any()) } returns true

        val resultingJournalpost = joarkMapper.createJournalpost(
            journalfoeringData = journalfoeringData,
            opplastetDokument = opplastetDokument,
            hovedDokument = mellomlagretDokument,
            brevMottaker = brevMottaker
        )

        assertEquals(expectedJournalpostWithOneDocument, resultingJournalpost)
    }

    @Test
    fun `createJournalpost works as expected with two documents`() {
        every { pdfUtils.pdfByteArrayIsPdfa(any()) } returns true

        val resultingJournalpost = joarkMapper.createJournalpost(
            journalfoeringData = journalfoeringData,
            opplastetDokument = opplastetDokument,
            hovedDokument = mellomlagretDokument,
            vedleggDokumentList = listOf(mellomlagretVedleggDokument),
            brevMottaker = brevMottaker
        )

        assertEquals(expectedJournalpostWithTwoDocuments, resultingJournalpost)
    }
}