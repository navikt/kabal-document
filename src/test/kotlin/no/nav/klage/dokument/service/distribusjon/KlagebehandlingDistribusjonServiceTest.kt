package no.nav.klage.dokument.service.distribusjon

import brave.Tracer
import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import no.nav.klage.dokument.clients.dokdistfordeling.DokDistFordelingClient
import no.nav.klage.dokument.clients.joark.DefaultJoarkGateway
import no.nav.klage.dokument.clients.joark.JoarkClient
import no.nav.klage.dokument.clients.klagefileapi.FileApiClient
import no.nav.klage.dokument.clients.saf.graphql.SafGraphQlClient
import no.nav.klage.dokument.db.TestPostgresqlContainer
import no.nav.klage.dokument.service.DokumentEnhetService
import no.nav.klage.dokument.service.MellomlagerService
import no.nav.klage.dokument.util.AttachmentValidator
import no.nav.klage.dokument.util.PdfUtils
import no.nav.klage.dokument.util.TokenUtil
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*


@ActiveProfiles("local")
@Import(KlagebehandlingDistribusjonServiceTest.MyTestConfiguration::class)
@SpringBootTest(classes = [BrevMottakerDistribusjonService::class, DokumentEnhetDistribusjonService::class, DokumentEnhetService::class, BrevMottakerDistribusjonService::class])
@EnableJpaRepositories(basePackages = ["no.nav.klage.dokument.repositories"])
@EntityScan("no.nav.klage.oppgave.domain")
@AutoConfigureDataJpa
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@AutoConfigureTestEntityManager
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class KlagebehandlingDistribusjonServiceTest {

    companion object {
        @Container
        @JvmField
        val postgreSQLContainer: TestPostgresqlContainer = TestPostgresqlContainer.instance
    }

    @Configuration
    internal class MyTestConfiguration {


        @MockkBean(relaxed = true)
        lateinit var fileApiClient: FileApiClient

        @MockkBean(relaxed = true)
        lateinit var joarkClient: JoarkClient

        @MockkBean(relaxed = true)
        lateinit var tracer: Tracer

        @MockkBean(relaxed = true)
        lateinit var pdfUtils: PdfUtils

        @MockkBean(relaxed = true)
        lateinit var attachmentValidator: AttachmentValidator

        @MockkBean(relaxed = true)
        lateinit var safClient: SafGraphQlClient

        @MockkBean(relaxed = true)
        lateinit var tokenUtil: TokenUtil

    }


    @SpykBean
    lateinit var dokumentEnhetDistribusjonService: DokumentEnhetDistribusjonService

    @MockkBean
    lateinit var dokDistFordelingClient: DokDistFordelingClient

    private val klagebehandlingId = UUID.randomUUID()

    private val vedtakId = UUID.randomUUID()

    private val fnr = "12345678910"

    private val journalpostId = "5678"

    @SpykBean
    lateinit var brevMottakerDistribusjonService: BrevMottakerDistribusjonService

    @SpykBean
    lateinit var vedtakJournalfoeringService: BrevMottakerJournalfoeringService


    @MockkBean
    lateinit var mellomlagerService: MellomlagerService

    @SpykBean
    lateinit var joarkGateway: DefaultJoarkGateway

    @SpykBean
    lateinit var dokumentEnhetService: DokumentEnhetService

    /*
    @Test
    fun `save klagebehandling`() {
        mottakRepository.save(mottak)

        klagebehandlingRepository.save(klage)

        klagebehandlingDistribusjonService.distribuerKlagebehandling(klagebehandlingId)

        val klagebehandling =
            klagebehandlingRepository.findByIdOrNull(klagebehandlingId) ?: throw NullPointerException()
    }

    @Test
    fun `distribusjon av klagebehandling fører til dokdistReferanse, ferdig distribuert vedtak og avsluttet klagebehandling`() {
        val dokdistResponse = DistribuerJournalpostResponse(UUID.randomUUID())
        val dokarkivResponse = journalpostId
        val fileApiServiceResponse = ArkivertDokumentWithTitle(
            "title",
            ByteArray(8),
            MediaType.APPLICATION_PDF
        )

        every { journalpostGateway.createJournalpostAsSystemUser(any(), any(), any()) } returns dokarkivResponse

        every { dokDistFordelingClient.distribuerJournalpost(any()) } returns dokdistResponse

        every { kafkaVedtakEventRepository.save(any()) } returns null

        every { fileApiService.getUploadedDocumentAsSystemUser(any()) } returns fileApiServiceResponse

        every { fileApiService.deleteDocumentAsSystemUser(any()) } returns Unit

        mottakRepository.save(mottak)

        klagebehandlingRepository.save(klage)

        klagebehandlingDistribusjonService.distribuerKlagebehandling(klagebehandlingId)

        val result = klagebehandlingRepository.getOne(klagebehandlingId)
        val resultingVedtak = result.getVedtakOrException()
        val brevMottaker = resultingVedtak.brevmottakere.first()

        assertThat(brevMottaker.dokdistReferanse).isNotNull
        assertThat(brevMottaker.ferdigstiltIJoark).isNotNull
        assertThat(brevMottaker.dokdistReferanse).isNotNull
        assertThat(resultingVedtak.ferdigDistribuert).isNotNull
        assertThat(resultingVedtak.mellomlagerId).isNull()
        assertThat(result.avsluttet).isNotNull
    }
     */
}