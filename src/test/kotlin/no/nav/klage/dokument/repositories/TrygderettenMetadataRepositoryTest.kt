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
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@ActiveProfiles("local")
@DataJpaTest
class TrygderettenMetadataRepositoryTest : PostgresIntegrationTestBase() {

    @Autowired
    lateinit var testEntityManager: TestEntityManager

    @Autowired
    lateinit var dokumentEnhetRepository: DokumentEnhetRepository

    @Autowired
    lateinit var trygderettenMetadataRepository: TrygderettenMetadataRepository

    @Test
    fun `store and fetch TrygderettenMetadata with representant works as expected`() {
        val metadata = trygderettenMetadataFor(persistDokumentEnhet())

        trygderettenMetadataRepository.save(metadata)

        testEntityManager.flush()
        testEntityManager.clear()

        val retrieved = trygderettenMetadataRepository.findById(metadata.id).orElseThrow()

        assertThat(retrieved.id).isEqualTo(metadata.id)
        assertThat(retrieved.dokumentEnhetId).isEqualTo(metadata.dokumentEnhetId)
        assertThat(retrieved.kravfremsettelsesdato).isEqualTo(metadata.kravfremsettelsesdato)
        assertThat(retrieved.paaanketVedtaksdato).isEqualTo(metadata.paaanketVedtaksdato)
        assertThat(retrieved.tidligereITROgOpphevetHenvist).isEqualTo(metadata.tidligereITROgOpphevetHenvist)
        assertThat(retrieved.gjenopptak).isEqualTo(metadata.gjenopptak)
        assertThat(retrieved.forsterketRett).isEqualTo(metadata.forsterketRett)
        assertThat(retrieved.ettersendelse).isEqualTo(metadata.ettersendelse)
        assertThat(retrieved.lovhenvisning).isEqualTo(metadata.lovhenvisning)

        assertThat(retrieved.representant).isNotNull
        assertThat(retrieved.representant!!.navn).isEqualTo(metadata.representant!!.navn)
        assertThat(retrieved.representant!!.partId!!.type).isEqualTo(metadata.representant!!.partId!!.type)
        assertThat(retrieved.representant!!.partId!!.value).isEqualTo(metadata.representant!!.partId!!.value)
        assertThat(retrieved.representant!!.adresse).isNotNull
        assertThat(retrieved.representant!!.adresse!!.adresselinje1).isEqualTo(metadata.representant!!.adresse!!.adresselinje1)
        assertThat(retrieved.representant!!.adresse!!.land).isEqualTo(metadata.representant!!.adresse!!.land)
    }

    @Test
    fun `findByDokumentEnhetId returns stored TrygderettenMetadata`() {
        val metadata = trygderettenMetadataFor(persistDokumentEnhet())

        trygderettenMetadataRepository.save(metadata)

        testEntityManager.flush()
        testEntityManager.clear()

        val retrieved = trygderettenMetadataRepository.findByDokumentEnhetId(metadata.dokumentEnhetId)

        assertThat(retrieved).isNotNull
        assertThat(retrieved!!.id).isEqualTo(metadata.id)
    }

    @Test
    fun `findByDokumentEnhetId returns null when not found`() {
        assertThat(trygderettenMetadataRepository.findByDokumentEnhetId(UUID.randomUUID())).isNull()
    }

    @Test
    fun `store TrygderettenMetadata without representant works as expected`() {
        val metadata = TrygderettenMetadata(
            dokumentEnhetId = persistDokumentEnhet(),
            kravfremsettelsesdato = null,
            paaanketVedtaksdato = LocalDate.of(2026, 1, 15),
            tidligereITROgOpphevetHenvist = null,
            gjenopptak = null,
            forsterketRett = false,
            ettersendelse = true,
            lovhenvisning = setOf("ftrl. § 22-13"),
            representant = null,
        )

        trygderettenMetadataRepository.save(metadata)

        testEntityManager.flush()
        testEntityManager.clear()

        val retrieved = trygderettenMetadataRepository.findById(metadata.id).orElseThrow()

        assertThat(retrieved.representant).isNull()
        assertThat(retrieved.kravfremsettelsesdato).isNull()
        assertThat(retrieved.lovhenvisning).isEqualTo(setOf("ftrl. § 22-13"))
    }

    private fun persistDokumentEnhet(): UUID {
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
        dokumentEnhetRepository.save(dokumentEnhet)
        testEntityManager.flush()
        return dokumentEnhet.id
    }

    private fun trygderettenMetadataFor(dokumentEnhetId: UUID) = TrygderettenMetadata(
        dokumentEnhetId = dokumentEnhetId,
        kravfremsettelsesdato = LocalDate.of(2025, 11, 1),
        paaanketVedtaksdato = LocalDate.of(2026, 2, 20),
        tidligereITROgOpphevetHenvist = true,
        gjenopptak = false,
        forsterketRett = true,
        ettersendelse = false,
        lovhenvisning = setOf("ftrl. § 12-7"),
        representant = Representant(
            partId = PartId(
                type = PartIdType.PERSON,
                value = "01011012345",
            ),
            navn = "Representant Representantsen",
            adresse = Adresse(
                adressetype = "norskPostadresse",
                adresselinje1 = "Gateveien 1",
                adresselinje2 = null,
                adresselinje3 = null,
                postnummer = "0001",
                poststed = "OSLO",
                land = "NO",
            ),
        ),
    )
}
