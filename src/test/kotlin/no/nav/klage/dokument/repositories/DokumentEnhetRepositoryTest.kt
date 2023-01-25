package no.nav.klage.dokument.repositories

import no.nav.klage.dokument.db.TestPostgresqlContainer
import no.nav.klage.dokument.domain.dokument.*
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
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime

@ActiveProfiles("local")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DokumentEnhetRepositoryTest {

    companion object {
        @Container
        @JvmField
        val postgreSQLContainer: TestPostgresqlContainer = TestPostgresqlContainer.instance
    }

    @Autowired
    lateinit var testEntityManager: TestEntityManager

    @Autowired
    lateinit var dokumentEnhetRepository: DokumentEnhetRepository

    @Test
    fun `store DokumentEnhet works as expected`() {
        val dokumentEnhet = dokumentEnhet

        dokumentEnhetRepository.save(dokumentEnhet)

        testEntityManager.flush()
        testEntityManager.clear()

        assertThat(dokumentEnhetRepository.getReferenceById(dokumentEnhet.id)).isEqualTo(dokumentEnhet)
    }

    @Test
    @Transactional
    fun `update child property works as expected`() {
        val dokumentEnhet = dokumentEnhet
        dokumentEnhet.brevMottakerDistribusjoner = setOf(
            BrevMottakerDistribusjon(
                brevMottaker = dokumentEnhet.brevMottakere.first(),
                opplastetDokumentId = dokumentEnhet.hovedDokument!!.id,
            )
        )

        dokumentEnhetRepository.save(dokumentEnhet)

        val retrievedObject = dokumentEnhetRepository.getReferenceById(dokumentEnhet.id)

        retrievedObject.brevMottakerDistribusjoner.first().journalpostId = "JOURNALPOST_ID"

        dokumentEnhetRepository.save(retrievedObject)

        val retrievedObject2 = dokumentEnhetRepository.getReferenceById(dokumentEnhet.id)

        assertThat(retrievedObject2.brevMottakerDistribusjoner.first().journalpostId).isEqualTo(retrievedObject.brevMottakerDistribusjoner.first().journalpostId)
    }

    @Test
    fun `update DokumentEnhet works as expected`() {
        dokumentEnhetRepository.save(dokumentEnhet)
        val firstDokumentEnhet = dokumentEnhetRepository.getReferenceById(dokumentEnhet.id)
        firstDokumentEnhet.shouldBeDistributed = false
        val secondDokumentEnhet = dokumentEnhetRepository.getReferenceById(dokumentEnhet.id)

        assertThat(firstDokumentEnhet).isEqualTo(secondDokumentEnhet)
    }

    val dokumentEnhet = DokumentEnhet(
        journalfoeringData = JournalfoeringData(
            sakenGjelder = PartId(type = PartIdType.PERSON, value = ""),
            tema = Tema.OMS,
            sakFagsakId = null,
            sakFagsystem = null,
            kildeReferanse = "",
            enhet = "",
            behandlingstema = "",
            tittel = "",
            brevKode = "",
            tilleggsopplysning = null
        ),
        brevMottakere = setOf(
            BrevMottaker(
                partId = PartId(
                    type = PartIdType.PERSON,
                    value = "01011012345"
                ),
                navn = "Test Person",
            ),
        ),
        vedlegg = listOf(
            OpplastetVedlegg(
                mellomlagerId = "456",
                opplastet = LocalDateTime.now(),
                size = 1001L,
                name = "fil2.pdf",
            )
        ),
        hovedDokument = OpplastetHoveddokument(
            mellomlagerId = "4567",
            opplastet = LocalDateTime.now(),
            size = 1001L,
            name = "fil1.pdf",
        ),
        dokumentType = DokumentType.VEDTAK,
        brevMottakerDistribusjoner = setOf(),
        avsluttet = null,
        modified = LocalDateTime.now()
    )
}