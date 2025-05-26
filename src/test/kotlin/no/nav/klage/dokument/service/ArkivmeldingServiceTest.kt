package no.nav.klage.dokument.service


import io.mockk.every
import io.mockk.mockk
import no.nav.klage.dokument.clients.joark.Dokument
import no.nav.klage.dokument.clients.pdl.graphql.HentPersonResponse
import no.nav.klage.dokument.clients.pdl.graphql.PdlClient
import no.nav.klage.dokument.clients.pdl.graphql.PdlPerson
import no.nav.klage.dokument.clients.pdl.graphql.PdlPersonDataWrapper
import no.nav.klage.dokument.clients.saf.graphql.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.Month
import java.util.*


class ArkivmeldingServiceTest {

    val safGraphQlClient = mockk<SafGraphQlClient>()
    val pdlClient = mockk<PdlClient>()

    val BESTILLINGS_ID = "bestillingsId"
    val JOURNALPOST_ID = "987654321"
    val ARKIV_SAKNUMMER = "111111"
    val FIXED_LOCAL_DATE_TIME = LocalDateTime.of(2025, Month.MAY, 26, 7, 32)
    val DATO_OPPRETTET_SAK = FIXED_LOCAL_DATE_TIME
    val DATO_OPPRETTET_JOURNALPOST = FIXED_LOCAL_DATE_TIME.minusDays(1)
    val DATO_JOURNALFOERT = FIXED_LOCAL_DATE_TIME.minusDays(2)
    val OPPRETTET_AV_NAVN = "Sak Sakbehandlersen"
    val BRUKER_ID_FNR = "20026900000"
    val BRUKER_TYPE_FNR = "FNR"
    val BRUKER_ID_ORGNR = "999999999"
    val BRUKER_TYPE_ORGNR = "ORGNR"
    val BRUKER_ID_AKTOER_ID = "aktoerId"
    val BRUKER_TYPE_AKTOER_ID = "AKTOERID"
    val TITTEL = "Klage på saksbehandling"
    val JOURNALFOERT_AV_NAVN = "Sak Sakbehandlersen"
    val TEMA = Tema.DAG
    val TEMA_NAVN = TEMA.name
    val JOURNALFOERENDE_ENHET = "1234"

    val DOKUMENT_INFO_ID_HOVEDDOK = "1234567"
    val TITTEL_HOVEDDOK = "Klage på saksbehandling"

    val DOKUMENT_INFO_ID_VEDLEGG = "7654321"
    val TITTEL_VEDLEGG = "Dokumentasjon til klage"
    val ORIGINAL_JPID_VEDLEGG = "1111111111"

    val DOKUMENT_INFO_ID_VEDLEGG_2 = "9876543"
    val EREG_NAVN = "Bedrift AS"
    val PDL_NAVN = "Bjarne Betjent"

    val AVSENDER_MOTTAKER_NAVN_ORIG_JP = "avsenderMottakerNavnOrigJp"
    val JOURNALFOERT_AV_NAVN_ORIG_JP = "ajournalfoertAvNavnOrigJp"
    val DATO_JOURNALFOERT_ORIG_JP: LocalDateTime? = FIXED_LOCAL_DATE_TIME.minusDays(5)

    val FILTYPE_PNG = "PNG"
    val FILTYPE_JPEG = "JPEG"
    val FILTYPE_PDF = "PDF"
    val FILTYPE_XLSX = "XLSX"

    val arkivmeldingService = ArkivmeldingService(
        safGraphQlClient = safGraphQlClient,
        applicationName = "test",
        pdlClient = pdlClient,
    )

    val hentPersonResponse = HentPersonResponse(
        data = PdlPersonDataWrapper(
            hentPerson = PdlPerson(
                folkeregisteridentifikator = PdlPerson.Folkeregisteridentifikator(
                    identifikasjonsnummer = "20026900000"
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

    val journalpost1 = Journalpost(
        journalpostId = "123456789",
        journalposttype = Journalposttype.I,
        journalstatus = Journalstatus.MOTTATT,
        tema = Tema.AAP,
        sak = Sak(
            datoOpprettet = null,
            fagsakId = "140322503",
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
                dokumentvarianter = listOf(
                    Dokumentvariant(
                        variantformat = Variantformat.ARKIV,
                        filtype = Filtype.PDF,
                    )
                ),
                datoFerdigstilt = LocalDateTime.now().minusMonths(2),
                originalJournalpostId = null,
                dokumentstatus = Dokumentstatus.FERDIGSTILT,
            ),
            DokumentInfo(
                dokumentInfoId = "987654321",
                tittel = "id",
                brevkode = "at",
                skjerming = "viderer",
                dokumentvarianter = listOf(
                    Dokumentvariant(
                        variantformat = Variantformat.SLADDET,
                        filtype = Filtype.PNG,
                    )
                ),
                datoFerdigstilt = LocalDateTime.now().minusMonths(3),
                originalJournalpostId = "654987",
                dokumentstatus = Dokumentstatus.FERDIGSTILT,
            )
        ),
        relevanteDatoer = listOf(
            RelevantDato(
                dato = LocalDateTime.now(), datotype = Datotype.DATO_JOURNALFOERT

            )
        ),
        journalforendeEnhet = "iuvaret",
        tittel = "journalpost-tittel",
        journalfortAvNavn = "Saksbehandler som journalførte",
    )

    val journalpost2 = Journalpost(
        journalpostId = "654987",
        journalposttype = Journalposttype.I,
        journalstatus = Journalstatus.MOTTATT,
        tema = Tema.AAP,
        sak = Sak(
            datoOpprettet = null,
            fagsakId = "140322503",
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
                dokumentInfoId = "987654321",
                tittel = "id",
                brevkode = "at",
                skjerming = "viderer",
                dokumentvarianter = listOf(
                    Dokumentvariant(
                        variantformat = Variantformat.SLADDET,
                        filtype = Filtype.PDF,
                    )
                ),
                datoFerdigstilt = LocalDateTime.now().minusMonths(3),
                originalJournalpostId = "654987",
                dokumentstatus = Dokumentstatus.FERDIGSTILT,
            )
        ),
        relevanteDatoer = listOf(
            RelevantDato(
                dato = LocalDateTime.now(), datotype = Datotype.DATO_JOURNALFOERT

            )
        ),
        journalforendeEnhet = "iuvaret",
        tittel = "journalpost-tittel",
        journalfortAvNavn = "Saksbehandler som journalførte",
    )

    @Test
    fun `test to xml`() {
        // Mock the necessary methods in safGraphQlClient and pdlClient if needed
        every { safGraphQlClient.getJournalpostAsSystembruker(any()) } returns journalpost1
        every { safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(fnr = any()) } returns listOf(
            journalpost1,
            journalpost2
        )
        every { pdlClient.getPersonInfo(any()) } returns hentPersonResponse

        val arkivmelding = arkivmeldingService.generateArkivmelding(
            journalpostId = "123456",
            avsenderMottakerDistribusjonId = UUID.randomUUID()
        )
//
        println(arkivmelding)
    }

    @Test
    fun shouldAssertAvtaltmelding() {
        every { safGraphQlClient.getJournalpostAsSystembruker(any()) } returns getJournalpost()
        every { safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(fnr = any()) } returns listOf(
            getJournalpost()
        )
        every { pdlClient.getPersonInfo(any()) } returns hentPersonResponse
        val stringOutput = arkivmeldingService.generateArkivmelding(
            journalpostId = "123456",
            avsenderMottakerDistribusjonId = UUID.randomUUID()
        )

        println(stringOutput)
    }

    private fun getJournalpost(): Journalpost {
        return Journalpost(
            journalpostId = JOURNALPOST_ID,
            journalposttype = Journalposttype.I,
            journalstatus = Journalstatus.MOTTATT,
            tema = TEMA,
            sak = Sak(
                datoOpprettet = DATO_OPPRETTET_SAK,
                //Dette med arkivsaksnummer er noe vi har med fra dokdist. Vurder hva som er beste bruk, og tilpass testen deretter.
                fagsakId = ARKIV_SAKNUMMER,
            ),
            bruker = Bruker(
                id = BRUKER_ID_FNR, type = BRUKER_TYPE_FNR
            ),
            avsenderMottaker = null,
            opprettetAvNavn = OPPRETTET_AV_NAVN,
            skjerming = "none",
            datoOpprettet = DATO_OPPRETTET_JOURNALPOST,
            dokumenter = getDokumenter(),
            relevanteDatoer = listOf(
                RelevantDato(
                    dato = DATO_JOURNALFOERT,
                    datotype = Datotype.DATO_JOURNALFOERT
                )
            ),
            journalforendeEnhet = JOURNALFOERENDE_ENHET,
            tittel = TITTEL,
            journalfortAvNavn = JOURNALFOERT_AV_NAVN
        )
    }
    private fun getDokumenter(): List<DokumentInfo> {
        return listOf(
            DokumentInfo(
                dokumentInfoId = DOKUMENT_INFO_ID_HOVEDDOK,
                tittel = TITTEL_HOVEDDOK,
                brevkode = "brevKode",
                skjerming = null,
                dokumentvarianter = listOf(
                    Dokumentvariant(
                        variantformat = Variantformat.ARKIV,
                        filtype = Filtype.PNG,
                    ),
                    Dokumentvariant(
                        variantformat = Variantformat.SLADDET,
                        filtype = Filtype.PDF,
                    )
                ),
                datoFerdigstilt = LocalDateTime.now(),
                originalJournalpostId = null,
                dokumentstatus = Dokumentstatus.FERDIGSTILT,
            ),
            DokumentInfo(
                dokumentInfoId = DOKUMENT_INFO_ID_VEDLEGG,
                tittel = TITTEL_VEDLEGG,
                brevkode = "brevKode",
                skjerming = null,
                dokumentvarianter = listOf(
                    Dokumentvariant(
                        variantformat = Variantformat.ARKIV,
                        filtype = Filtype.JPEG,

                    ),
                    Dokumentvariant(
                        variantformat = Variantformat.PRODUKSJON,
                        filtype = Filtype.PDF,

                        ),
                ),
                datoFerdigstilt = LocalDateTime.now(),
                originalJournalpostId = null,
                dokumentstatus = Dokumentstatus.FERDIGSTILT,
            ),
        )
    }

}