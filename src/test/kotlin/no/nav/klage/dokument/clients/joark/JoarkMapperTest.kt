package no.nav.klage.dokument.clients.joark

import no.nav.klage.dokument.domain.dokument.AvsenderMottaker
import no.nav.klage.dokument.domain.dokument.JournalfoeringData
import no.nav.klage.dokument.domain.dokument.OpplastetHoveddokument
import no.nav.klage.dokument.domain.dokument.PartId
import no.nav.klage.dokument.service.JournalfoeringService
import no.nav.klage.kodeverk.Fagsystem
import no.nav.klage.kodeverk.PartIdType
import no.nav.klage.kodeverk.Tema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID
import no.nav.klage.kodeverk.Sakstype as KodeverkSakstype

internal class JoarkMapperTest {
    private val joarkMapper = JoarkMapper()

    private val avsenderMottaker =
        AvsenderMottaker(
            partId = PartId(type = PartIdType.PERSON, value = "01011012345"),
            navn = "Test Person",
            adresse = null,
            tvingSentralPrint = false,
            localPrint = false,
            kanal = null,
        )

    private val hoveddokument =
        OpplastetHoveddokument(
            mellomlagerId = "123",
            name = "Tittel",
            sourceReference = UUID.randomUUID(),
        )

    private fun journalfoeringData(
        sakstype: KodeverkSakstype,
        fagsakId: String?,
        fagsystem: Fagsystem,
    ) = JournalfoeringData(
        sakenGjelder = PartId(type = PartIdType.PERSON, value = "20022012345"),
        tema = Tema.OPP,
        sakFagsakId = fagsakId,
        sakFagsystem = fagsystem,
        sakSakstype = sakstype,
        kildeReferanse = "kildeReferanse",
        enhet = "4291",
        behandlingstema = "behandlingstema",
        tittel = "Tittel",
        brevKode = "brevKode",
        tilleggsopplysning = null,
        journalpostType = JournalpostType.UTGAAENDE,
        inngaaendeKanal = null,
        datoMottatt = null,
    )

    private fun createSak(data: JournalfoeringData): Sak =
        joarkMapper
            .createPartialJournalpostWithoutDocuments(
                journalfoeringData = data,
                opplastetHovedDokument = hoveddokument,
                avsenderMottaker = avsenderMottaker,
            ).sak

    @Test
    fun `FAGSAK gives same Sak as before`() {
        val sak = createSak(journalfoeringData(sakstype = KodeverkSakstype.FAGSAK, fagsakId = "fagsak-1", fagsystem = Fagsystem.FS36))

        assertThat(sak).isEqualTo(
            Sak(
                sakstype = Sakstype.FAGSAK,
                fagsaksystem = FagsaksSystem.FS36,
                fagsakid = "fagsak-1",
                arkivsaksystem = null,
                arkivsaksnummer = null,
            ),
        )

        val json = JournalfoeringService.ourJacksonObjectMapper.writeValueAsString(sak)
        assertThat(json).isEqualTo("""{"sakstype":"FAGSAK","fagsaksystem":"FS36","fagsakid":"fagsak-1"}""")
    }

    @Test
    fun `FAGSAK with FS22 maps to FagsaksSystem FS22`() {
        val sak = createSak(journalfoeringData(sakstype = KodeverkSakstype.FAGSAK, fagsakId = "fagsak-1", fagsystem = Fagsystem.FS22))

        assertThat(sak.fagsaksystem).isEqualTo(FagsaksSystem.FS22)
    }

    @Test
    fun `FAGSAK without fagsakId fails explicitly`() {
        assertThrows<IllegalStateException> {
            createSak(journalfoeringData(sakstype = KodeverkSakstype.FAGSAK, fagsakId = null, fagsystem = Fagsystem.FS36))
        }
    }

    @Test
    fun `GENERELL_SAK gives Sak with only sakstype`() {
        val sak = createSak(journalfoeringData(sakstype = KodeverkSakstype.GENERELL_SAK, fagsakId = null, fagsystem = Fagsystem.FS22))

        assertThat(sak).isEqualTo(
            Sak(
                sakstype = Sakstype.GENERELL_SAK,
                fagsaksystem = null,
                fagsakid = null,
                arkivsaksystem = null,
                arkivsaksnummer = null,
            ),
        )

        val json = JournalfoeringService.ourJacksonObjectMapper.writeValueAsString(sak)
        assertThat(json).isEqualTo("""{"sakstype":"GENERELL_SAK"}""")
        assertThat(json).doesNotContain("fagsakid", "fagsaksystem", "arkivsaksnummer")
    }

    @Test
    fun `GENERELL_SAK journalpost keeps bruker and tema`() {
        val journalpost =
            joarkMapper.createPartialJournalpostWithoutDocuments(
                journalfoeringData =
                    journalfoeringData(
                        sakstype = KodeverkSakstype.GENERELL_SAK,
                        fagsakId = null,
                        fagsystem = Fagsystem.FS22,
                    ),
                opplastetHovedDokument = hoveddokument,
                avsenderMottaker = avsenderMottaker,
            )

        assertThat(journalpost.tema).isEqualTo(Tema.OPP)
        assertThat(journalpost.bruker).isEqualTo(Bruker(id = "20022012345", idType = BrukerIdType.FNR))

        val json = JournalfoeringService.ourJacksonObjectMapper.writeValueAsString(journalpost)
        assertThat(json).doesNotContain("fagsakid", "fagsaksystem", "arkivsaksnummer")
    }

    @Test
    fun `ARKIVSAK is rejected`() {
        assertThrows<IllegalStateException> {
            createSak(journalfoeringData(sakstype = KodeverkSakstype.ARKIVSAK, fagsakId = null, fagsystem = Fagsystem.FS22))
        }
    }
}
