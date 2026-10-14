package no.nav.klage.dokument.api.mapper

import no.nav.klage.dokument.api.input.DokumentEnhetWithDokumentreferanserInput.JournalfoeringDataInput
import no.nav.klage.dokument.api.input.DokumentEnhetWithDokumentreferanserInput.PartIdInput
import no.nav.klage.dokument.exceptions.DokumentEnhetNotValidException
import no.nav.klage.kodeverk.DokumentType
import no.nav.klage.kodeverk.Fagsystem
import no.nav.klage.kodeverk.PartIdType
import no.nav.klage.kodeverk.Sakstype
import no.nav.klage.kodeverk.Tema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DokumentEnhetInputMapperTest {
    private val mapper = DokumentEnhetInputMapper()

    private fun input(
        sakFagsakId: String?,
        sakstypeId: String?,
        sakFagsystemId: String = Fagsystem.FS36.id,
    ) = JournalfoeringDataInput(
        sakenGjelder = PartIdInput(type = null, partIdTypeId = PartIdType.PERSON.id, value = "20022012345"),
        temaId = Tema.OPP.id,
        sakFagsakId = sakFagsakId,
        sakFagsystemId = sakFagsystemId,
        sakstypeId = sakstypeId,
        kildeReferanse = "kildeReferanse",
        enhet = "4291",
        behandlingstema = "behandlingstema",
        tittel = "Tittel",
        brevKode = "brevKode",
        tilleggsopplysning = null,
        inngaaendeKanal = null,
        datoMottatt = null,
    )

    @Test
    fun `input without sakstypeId is mapped to FAGSAK`() {
        val result =
            mapper.mapJournalfoeringDataInput(
                input = input(sakFagsakId = "123", sakstypeId = null),
                dokumentType = DokumentType.VEDTAK,
            )

        assertThat(result.sakSakstype).isEqualTo(Sakstype.FAGSAK)
        assertThat(result.sakFagsakId).isEqualTo("123")
    }

    @Test
    fun `FAGSAK without sakFagsakId is rejected`() {
        assertThrows<DokumentEnhetNotValidException> {
            mapper.mapJournalfoeringDataInput(input = input(sakFagsakId = null, sakstypeId = null), dokumentType = DokumentType.VEDTAK)
        }
        assertThrows<DokumentEnhetNotValidException> {
            mapper.mapJournalfoeringDataInput(
                input = input(sakFagsakId = " ", sakstypeId = Sakstype.FAGSAK.id),
                dokumentType = DokumentType.VEDTAK,
            )
        }
    }

    @Test
    fun `GENERELL_SAK without sakFagsakId is accepted`() {
        val result =
            mapper.mapJournalfoeringDataInput(
                input = input(sakFagsakId = null, sakstypeId = Sakstype.GENERELL_SAK.id, sakFagsystemId = Fagsystem.FS22.id),
                dokumentType = DokumentType.VEDTAK,
            )

        assertThat(result.sakSakstype).isEqualTo(Sakstype.GENERELL_SAK)
        assertThat(result.sakFagsakId).isNull()
        assertThat(result.sakFagsystem).isEqualTo(Fagsystem.FS22)
    }

    @Test
    fun `ARKIVSAK is rejected`() {
        assertThrows<DokumentEnhetNotValidException> {
            mapper.mapJournalfoeringDataInput(
                input = input(sakFagsakId = "123", sakstypeId = Sakstype.ARKIVSAK.id),
                dokumentType = DokumentType.VEDTAK,
            )
        }
    }

    @Test
    fun `unknown sakstypeId is rejected`() {
        assertThrows<DokumentEnhetNotValidException> {
            mapper.mapJournalfoeringDataInput(input = input(sakFagsakId = "123", sakstypeId = "999"), dokumentType = DokumentType.VEDTAK)
        }
    }
}
