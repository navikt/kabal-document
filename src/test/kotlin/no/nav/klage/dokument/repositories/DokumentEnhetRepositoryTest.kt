package no.nav.klage.dokument.repositories

import no.nav.klage.dokument.db.PostgresIntegrationTestBase
import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.kodeverk.DokumentType
import no.nav.klage.kodeverk.Fagsystem
import no.nav.klage.kodeverk.PartIdType
import no.nav.klage.kodeverk.Tema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest

import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.util.UUID

@ActiveProfiles("local")
@DataJpaTest
class DokumentEnhetRepositoryTest: PostgresIntegrationTestBase() {

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
    fun `update child property works as expected`() {
        val dokumentEnhet = dokumentEnhet
        dokumentEnhet.avsenderMottakerDistribusjoner = setOf(
            AvsenderMottakerDistribusjon(
                avsenderMottaker = dokumentEnhet.avsenderMottakere.first(),
                opplastetDokumentId = dokumentEnhet.hovedDokument!!.id,
            )
        )

        dokumentEnhetRepository.save(dokumentEnhet)

        val retrievedObject = dokumentEnhetRepository.getReferenceById(dokumentEnhet.id)

        retrievedObject.avsenderMottakerDistribusjoner.first().journalpostId = "JOURNALPOST_ID"

        dokumentEnhetRepository.save(retrievedObject)

        val retrievedObject2 = dokumentEnhetRepository.getReferenceById(dokumentEnhet.id)

        assertThat(retrievedObject2.avsenderMottakerDistribusjoner.first().journalpostId).isEqualTo(retrievedObject.avsenderMottakerDistribusjoner.first().journalpostId)
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
        avsenderMottakere = setOf(
            AvsenderMottaker(
                partId = PartId(
                    type = PartIdType.PERSON,
                    value = "01011012345"
                ),
                navn = "Test Person",
                adresse = null,
                tvingSentralPrint = false,
                localPrint = false,
                kanal = null,
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
        avsenderMottakerDistribusjoner = setOf(),
        avsluttet = null,
        journalfoerendeSaksbehandlerIdent = "S123456",
        modified = LocalDateTime.now(),
    )
}