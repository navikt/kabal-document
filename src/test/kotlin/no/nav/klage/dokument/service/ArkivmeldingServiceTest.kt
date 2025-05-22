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
            journalpostId = "123456789",
            journalposttype = Journalposttype.I,
            journalstatus = Journalstatus.MOTTATT,
            tema = Tema.AAP,
            sak = Sak(
                datoOpprettet = null,
                fagsakId = "140322503",
                fagsaksystem = "aperiri"
            ),
            bruker = Bruker(
                id = "neglegentur", type = "dui"
            ),
            avsenderMottaker = null,
            opprettetAvNavn = "F_Z994864 E_Z994864",
            skjerming = "inciderint",
            datoOpprettet = LocalDateTime.now(),
            dokumenter = listOf(
                DokumentInfo(
                    dokumentInfoId = "54321",
                    tittel = "Eksepedisjonsbrev til Trygderetten",
                    brevkode = "at",
                    skjerming = null,
                    logiskeVedlegg = listOf(),
                    dokumentvarianter = listOf(
                        Dokumentvariant(
                            variantformat = Variantformat.ARKIV,
                            filtype = "PDF",
                            saksbehandlerHarTilgang = false,
                            skjerming = SkjermingType.POL

                        )
                    ),
                    datoFerdigstilt = LocalDateTime.now().minusMonths(2),
                    originalJournalpostId = "123456789",
                    dokumentstatus = "FERDIGSTILT",
                ),
                DokumentInfo(
                    dokumentInfoId = "987654321",
                    tittel = "id",
                    brevkode = "at",
                    skjerming = "viderer",
                    logiskeVedlegg = listOf(),
                    dokumentvarianter = listOf(
                        Dokumentvariant(
                            variantformat = Variantformat.SLADDET,
                            filtype = "pdf",
                            saksbehandlerHarTilgang = false,
                            skjerming = SkjermingType.POL

                        )
                    ),
                    datoFerdigstilt = LocalDateTime.now().minusMonths(3),
                    originalJournalpostId = "654987",
                    dokumentstatus = "FERDIGSTILT",
                )
            ),
            relevanteDatoer = listOf(
                RelevantDato(
                    dato = LocalDateTime.now(), datotype = Datotype.DATO_JOURNALFOERT

                )
            ),
            kanal = "possit",
            kanalnavn = "posidonium",
            utsendingsinfo = null,
            journalforendeEnhet = "iuvaret",
            tittel = "journalpost-tittel",
            journalfortAvNavn = "Saksbehandler som journalførte",
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

        val arkivmelding = arkivmeldingService.generateArkivmelding(
            journalpostId = "123456",
            avsenderMottakerDistribusjonId = UUID.randomUUID()
        )
//
        println(arkivmelding)
    }

}