package no.nav.klage.dokument.service

import io.mockk.every
import io.mockk.mockk
import no.nav.klage.dokument.clients.pdl.graphql.HentPersonResponse
import no.nav.klage.dokument.clients.pdl.graphql.PdlClient
import no.nav.klage.dokument.clients.pdl.graphql.PdlPerson
import no.nav.klage.dokument.clients.pdl.graphql.PdlPersonDataWrapper
import no.nav.klage.dokument.clients.saf.graphql.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

class ArkivmeldingServiceTest {

    @Test
    fun `test to xml`() {
        val safGraphQlClient = mockk<SafGraphQlClient>()
        val pdlClient = mockk<PdlClient>()

        val arkivmeldingService = ArkivmeldingService(
            safGraphQlClient = safGraphQlClient,
            applicationName = "test",
            pdlClient = pdlClient,
        )

        // Mock the necessary methods in safGraphQlClient and pdlClient if needed
        every { safGraphQlClient.getJournalpostAsSystembruker(any()) } returns Journalpost(
            journalpostId = "vero",
            journalposttype = Journalposttype.I,
            journalstatus = Journalstatus.MOTTATT,
            tema = Tema.AAP,
            sak = null,
            bruker = Bruker(
                id = "neglegentur", type = "dui"
            ),
            avsenderMottaker = null,
            opprettetAvNavn = "nobis",
            skjerming = "inciderint",
            datoOpprettet = LocalDateTime.now(),
            dokumenter = listOf(
                DokumentInfo(
                    dokumentInfoId = "discere",
                    tittel = "id",
                    brevkode = "at",
                    skjerming = "viderer",
                    logiskeVedlegg = listOf(),
                    dokumentvarianter = listOf(),
                    datoFerdigstilt = LocalDateTime.now().minusMonths(2),
                    originalJournalpostId = "vulputate"
                )
            ),
            relevanteDatoer = listOf(),
            kanal = "possit",
            kanalnavn = "posidonium",
            utsendingsinfo = null,
            journalforendeEnhet = "iuvaret"
        )

        every { pdlClient.getPersonInfo(any()) } returns HentPersonResponse(
            data = PdlPersonDataWrapper(
                hentPerson = PdlPerson(
                    folkeregisteridentifikator = PdlPerson.Folkeregisteridentifikator(
                        identifikasjonsnummer = "12345678901"
                    ),
                    adressebeskyttelse = listOf(),
                    navn = listOf(
                        PdlPerson.Navn(
                            fornavn = "Ola",
                            mellomnavn = null,
                            etternavn = "Nordmann"
                        )
                    ),
                    kjoenn = listOf(),
                    sivilstand = listOf(),
                    vergemaalEllerFremtidsfullmakt = listOf(),
                    doedsfall = listOf(),
                    sikkerhetstiltak = listOf()
                )
            ),
            errors = listOf()
        )

        arkivmeldingService.generateArkivmelding(
            journalpostId = "123456",
            avsenderMottakerDistribusjonId = UUID.randomUUID()
        )

//        val arkivmelding = Arkivmelding()
//        arkivmelding.system = "system"
//        arkivmelding.meldingId = "meldingId"
//        arkivmelding.tidspunkt = null
//        arkivmelding.antallFiler = 1
//
//        arkivmelding.mappe.add(
//            Mappe()
//        )
//
//        val jaxbElement: JAXBElement<Arkivmelding> = ObjectFactory().createArkivmelding(arkivmelding)
//        val jaxbContext = JAXBContext.newInstance(Arkivmelding::class.java)
//
//        val marshaller: Marshaller = jaxbContext.createMarshaller()
//        val sw = StringWriter()
//        marshaller.marshal(jaxbElement, sw)
//
//        println(sw.toString())
    }

}