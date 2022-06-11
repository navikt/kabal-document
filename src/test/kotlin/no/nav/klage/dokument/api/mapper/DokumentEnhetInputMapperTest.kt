package no.nav.klage.dokument.api.mapper

import no.nav.klage.dokument.api.input.BrevMottakerInput
import no.nav.klage.dokument.api.input.JournalfoeringDataInput
import no.nav.klage.dokument.api.input.PartIdInput
import no.nav.klage.dokument.domain.dokument.BrevMottaker
import no.nav.klage.dokument.domain.dokument.JournalfoeringData
import no.nav.klage.dokument.domain.dokument.PartId
import no.nav.klage.dokument.domain.kodeverk.Rolle
import no.nav.klage.dokument.exceptions.DokumentEnhetNotValidException
import no.nav.klage.kodeverk.Fagsystem
import no.nav.klage.kodeverk.PartIdType
import no.nav.klage.kodeverk.Tema
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DokumentEnhetInputMapperTest {


    val dokumentEnhetInputMapper = DokumentEnhetInputMapper()

    private val NAVN = "NAVN"
    private val INPUT_TYPE = "PERSON"
    private val INPUT_ROLLE = "HOVEDADRESSAT"
    private val VALUE = "VALUE"
    private val SAK_FAGSAK_ID = "SAK_FAGSAK_ID"
    private val SAK_FAGSYSTEM_ID = "1"
    private val KILDE_REFERANSE = "KILDE_REFERANSE"
    private val ENHET = "ENHET"
    private val BEHANDLINGSTEMA = "BEHANDLINGSTEMA"
    private val TITTEL = "TITTEL"
    private val BREVKODE = "BREVKODE"

    @Test
    fun `mapBrevMottakerInput works as expected`() {
        val brevMottakerInput = BrevMottakerInput(
            partId = PartIdInput(
                type = INPUT_TYPE,
                partIdTypeId = INPUT_TYPE,
                value = VALUE,
            ),
            navn = NAVN,
            rolle = INPUT_ROLLE,
        )

        val expectedOutput = BrevMottaker(
            partId = PartId(
                type = PartIdType.PERSON,
                value = VALUE,
            ),
            navn = NAVN,
            rolle = Rolle.HOVEDADRESSAT

        )

        val output = dokumentEnhetInputMapper.mapBrevMottakerInput(brevMottakerInput)

        assertEquals(expectedOutput.partId.type, output.partId.type)
        assertEquals(expectedOutput.partId.value, output.partId.value)
        assertEquals(expectedOutput.navn, output.navn)
        assertEquals(expectedOutput.rolle, output.rolle)
    }

    @Test
    fun `mapBrevMottakerInput casts exception on input error`() {
        val brevMottakerInput = BrevMottakerInput(
            partId = PartIdInput(
                type = INPUT_TYPE + "blabla",
                partIdTypeId = INPUT_TYPE + "blabla",
                value = VALUE,
            ),
            navn = NAVN,
            rolle = INPUT_ROLLE,
        )

        assertThrows<DokumentEnhetNotValidException> { dokumentEnhetInputMapper.mapBrevMottakerInput(brevMottakerInput) }
    }

    @Test
    fun `mapJournalfoeringDataInput works as expected`() {
        val input = JournalfoeringDataInput(
            sakenGjelder = PartIdInput(
                type = INPUT_TYPE,
                partIdTypeId = INPUT_TYPE,
                value = VALUE,
            ),
            tema = "AAP",
            temaId = "1",
            sakFagsakId = SAK_FAGSAK_ID,
            sakFagsystem = "FS36",
            sakFagsystemId = SAK_FAGSYSTEM_ID,
            kildeReferanse = KILDE_REFERANSE,
            enhet = ENHET,
            behandlingstema = BEHANDLINGSTEMA,
            tittel = TITTEL,
            brevKode = BREVKODE,
            tilleggsopplysning = null
        )

        val output = dokumentEnhetInputMapper.mapJournalfoeringDataInput(input)

        val expectedOutput = JournalfoeringData(
            sakenGjelder = PartId(
                type = PartIdType.PERSON,
                value = VALUE,
            ),
            tema = Tema.AAP,
            sakFagsakId = SAK_FAGSAK_ID,
            sakFagsystem = Fagsystem.FS36,
            kildeReferanse = KILDE_REFERANSE,
            enhet = ENHET,
            behandlingstema = BEHANDLINGSTEMA,
            tittel = TITTEL,
            brevKode = BREVKODE,
            tilleggsopplysning = null
        )

        assertEquals(expectedOutput.sakenGjelder.type, output.sakenGjelder.type)
        assertEquals(expectedOutput.sakenGjelder.value, output.sakenGjelder.value)
        assertEquals(expectedOutput.tema, output.tema)
        assertEquals(expectedOutput.sakFagsakId, output.sakFagsakId)
        assertEquals(expectedOutput.sakFagsystem, output.sakFagsystem)
        assertEquals(expectedOutput.kildeReferanse, output.kildeReferanse)
        assertEquals(expectedOutput.enhet, output.enhet)
        assertEquals(expectedOutput.behandlingstema, output.behandlingstema)
        assertEquals(expectedOutput.tittel, output.tittel)
        assertEquals(expectedOutput.brevKode, output.brevKode)
    }
}