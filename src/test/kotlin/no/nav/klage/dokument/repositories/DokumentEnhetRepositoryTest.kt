package no.nav.klage.dokument.repositories

import no.nav.klage.dokument.db.TestPostgresqlContainer
import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.dokument.domain.kodeverk.Rolle
import no.nav.klage.dokument.domain.saksbehandler.SaksbehandlerIdent
import no.nav.klage.kodeverk.DokumentType
import no.nav.klage.kodeverk.PartIdType
import no.nav.klage.kodeverk.Tema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime

@ActiveProfiles("local")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
internal class DokumentEnhetRepositoryTest {
    companion object {
        @Container
        @JvmField
        val postgreSQLContainer: TestPostgresqlContainer = TestPostgresqlContainer.instance
    }

    @Autowired
    lateinit var testEntityManager: TestEntityManager

    @Autowired
    lateinit var dokumentEnhetRepository: DokumentEnhetRepository

    private val NAV_IDENT = "NAV_IDENT"
    private val FNR_1 = "12345678910"
    private val FNR_2 = "22345678910"
    private val KILDE_REFERANSE = "KILDE_REFERANSE"
    private val ENHET = "ENHET"
    private val BEHANDLINGSTEMA = "BEHANDLINGSTEMA"
    private val TITTEL = "TITTEL"
    private val BREVKODE = "BREVKODE"
    private val NAVN_1 = "NAVN_1"
    private val NAVN_2 = "NAVN_2"
    private val MELLOMLAGER_ID_1 = "MELLOMLAGER_ID_1"
    private val MELLOMLAGER_ID_2 = "MELLOMLAGER_ID_2"


    @Test
    fun `save and get works`() {
        val dokumentEnhet = DokumentEnhet(
            eier = SaksbehandlerIdent(navIdent = NAV_IDENT),
            journalfoeringData = JournalfoeringData(
                sakenGjelder = PartId(
                    type = PartIdType.PERSON,
                    value = FNR_1
                ),
                tema = Tema.FOR,
                sakFagsakId = null,
                sakFagsystem = null,
                kildeReferanse = KILDE_REFERANSE,
                enhet = ENHET,
                behandlingstema = BEHANDLINGSTEMA,
                tittel = TITTEL,
                brevKode = BREVKODE,
                tilleggsopplysning = null
            ),
            brevMottakere = listOf(
                BrevMottaker(
                    partId = PartId(
                        type = PartIdType.PERSON,
                        value = FNR_1,
                    ),
                    navn = NAVN_1,
                    rolle = Rolle.HOVEDADRESSAT,
                ),
                BrevMottaker(
                    partId = PartId(
                        type = PartIdType.PERSON,
                        value = FNR_2,
                    ),
                    navn = NAVN_2,
                    rolle = Rolle.KOPIADRESSAT,
                ),
            ),
            dokumenter = listOf(
                OpplastetDokument(
                    mellomlagerId = MELLOMLAGER_ID_1,
                    opplastet = LocalDateTime.now(),
                    size = 0,
                    name = NAVN_1,
                    type = OpplastetDokument.OpplastetDokumentType.HOVEDDOKUMENT,
                ),
                OpplastetDokument(
                    mellomlagerId = MELLOMLAGER_ID_2,
                    opplastet = LocalDateTime.now(),
                    size = 0,
                    name = NAVN_2,
                    type = OpplastetDokument.OpplastetDokumentType.VEDLEGG,
                ),
            ),
            dokumentType = DokumentType.VEDTAK,
            brevMottakerDistribusjoner = mutableListOf(),
            avsluttet = null,
            modified = LocalDateTime.now(),
        )

        dokumentEnhetRepository.save(dokumentEnhet)

        testEntityManager.flush()
        testEntityManager.clear()

        assertThat(dokumentEnhetRepository.getById(dokumentEnhet.id)).isEqualTo(dokumentEnhet)
    }
}
