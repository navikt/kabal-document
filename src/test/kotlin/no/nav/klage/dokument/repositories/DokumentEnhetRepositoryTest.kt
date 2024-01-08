package no.nav.klage.dokument.repositories

import no.nav.klage.dokument.db.TestPostgresqlContainer
import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.kodeverk.DokumentType
import no.nav.klage.kodeverk.Fagsystem
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
import java.util.*

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
        firstDokumentEnhet.modified = LocalDateTime.now()
        val secondDokumentEnhet = dokumentEnhetRepository.getReferenceById(dokumentEnhet.id)

        assertThat(firstDokumentEnhet).isEqualTo(secondDokumentEnhet)
    }

    val dokumentEnhet = DokumentEnhet(
        journalfoeringData = JournalfoeringData(
            sakenGjelder = PartId(type = PartIdType.PERSON, value = ""),
            tema = Tema.OMS,
            sakFagsakId = "123",
            sakFagsystem = Fagsystem.AO01,
            kildeReferanse = "",
            enhet = "",
            behandlingstema = "",
            tittel = "",
            brevKode = "",
            tilleggsopplysning = null,
            inngaaendeKanal = null,
            datoMottatt = null,
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
        vedlegg = setOf(
            OpplastetVedlegg(
                mellomlagerId = "456",
                name = "fil2.pdf",
                index = 0,
                sourceReference = UUID.randomUUID(),
            )
        ),
        hovedDokument = OpplastetHoveddokument(
            mellomlagerId = "4567",
            name = "fil1.pdf",
            sourceReference = UUID.randomUUID(),
        ),
        dokumentType = DokumentType.VEDTAK,
        brevMottakerDistribusjoner = setOf(),
        avsluttet = null,
        journalfoerendeSaksbehandlerIdent = "S123456",
        modified = LocalDateTime.now()
    )
}