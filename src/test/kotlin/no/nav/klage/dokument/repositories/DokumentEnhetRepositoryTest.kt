package no.nav.klage.dokument.repositories

import no.nav.klage.dokument.db.TestPostgresqlContainer
import no.nav.klage.dokument.dokumentEnhetUtenBrevMottakereOgHovedDokument
import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.dokument.domain.kodeverk.PartIdType
import no.nav.klage.dokument.domain.kodeverk.Rolle
import no.nav.klage.dokument.domain.kodeverk.Tema
import no.nav.klage.dokument.domain.saksbehandler.SaksbehandlerIdent
import no.nav.klage.dokument.ferdigDistribuertDokumentEnhet
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime

@ActiveProfiles("local")
@JdbcTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
internal class DokumentEnhetRepositoryTest {

    companion object {
        @Container
        @JvmField
        val postgreSQLContainer: TestPostgresqlContainer = TestPostgresqlContainer.instance
    }

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Test
    fun `persist fullstendig dokumentenhet works`() {

        val dokumentEnhetRepository = DokumentEnhetRepository(jdbcTemplate)

        val dokumentEnhet = ferdigDistribuertDokumentEnhet()

        assertThat(dokumentEnhetRepository.save(dokumentEnhet)).isEqualTo(dokumentEnhet)
    }

    @Test
    fun `persist empty dokumentenhet works`() {

        val dokumentEnhetRepository = DokumentEnhetRepository(jdbcTemplate)

        val dokumentEnhet = dokumentEnhetUtenBrevMottakereOgHovedDokument()

        assertThat(dokumentEnhetRepository.save(dokumentEnhet)).isEqualTo(dokumentEnhet)
    }

    @Test
    fun `save, update and get works`() {

        val dokumentEnhetRepository = DokumentEnhetRepository(jdbcTemplate)

        val dokumentEnhet = DokumentEnhet(
            eier = SaksbehandlerIdent(navIdent = "A10101"),
            journalfoeringData = JournalfoeringData(
                sakenGjelder = PartId(
                    type = PartIdType.PERSON,
                    value = "20022012345"
                ),
                tema = Tema.OMS,
                sakFagsakId = null,
                sakFagsystem = null,
                kildeReferanse = "kildeReferanse",
                enhet = "Enhet",
                behandlingstema = "behandlingstema",
                tittel = "Tittel",
                brevKode = "brevKode",
                tilleggsopplysning = Tilleggsopplysning("key", "value")
            ),
            brevMottakere = emptyList(),
            hovedDokument = null,
            vedlegg = emptyList(),
            brevMottakerDistribusjoner = emptyList(),
            avsluttet = null,
        )

        assertThat(dokumentEnhetRepository.save(dokumentEnhet)).isEqualTo(dokumentEnhet)

        val oppdatertDokumentEnhet = dokumentEnhet.copy(
            brevMottakere = listOf(
                BrevMottaker(
                    partId = PartId(
                        type = PartIdType.PERSON,
                        value = "01011012345"
                    ),
                    navn = "Test Person",
                    rolle = Rolle.SOEKER
                ),
                BrevMottaker(
                    partId = PartId(
                        type = PartIdType.PERSON,
                        value = "20022012345"
                    ),
                    navn = "Mottaker Person",
                    rolle = Rolle.PROSESSFULLMEKTIG
                )
            ),
            hovedDokument = OpplastetDokument(
                mellomlagerId = "123",
                opplastet = LocalDateTime.now(),
                size = 1000L,
                name = "fil.pdf"
            ),
            vedlegg = listOf(
                OpplastetDokument(
                    mellomlagerId = "456",
                    opplastet = LocalDateTime.now(),
                    size = 1001L,
                    name = "fil2.pdf"
                )
            )
        )

        assertThat(dokumentEnhetRepository.saveOrUpdate(oppdatertDokumentEnhet)).isEqualTo(oppdatertDokumentEnhet)

        assertThat(dokumentEnhetRepository.findById(dokumentEnhet.id)).isEqualTo(oppdatertDokumentEnhet)
    }


}