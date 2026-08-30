package no.nav.klage.dokument.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.xml.bind.JAXBElement
import no.arkivverket.standarder.noark5.arkivmelding.v2.Arkivmelding
import no.arkivverket.standarder.noark5.arkivmelding.v2.Dokumentbeskrivelse
import no.arkivverket.standarder.noark5.arkivmelding.v2.Dokumentobjekt
import no.arkivverket.standarder.noark5.arkivmelding.v2.Korrespondansepart
import no.arkivverket.standarder.noark5.arkivmelding.v2.Part
import no.arkivverket.standarder.noark5.arkivmelding.v2.Saksmappe
import no.nav.avtaltmelding.trygderetten.v1.NavMappe
import no.nav.klage.dokument.api.input.TrygderettenMetadataInput
import no.nav.klage.dokument.clients.ereg.EregClient
import no.nav.klage.dokument.clients.ereg.NoekkelInfoOmOrganisasjon
import no.nav.klage.dokument.clients.klageunleashproxy.KlageUnleashProxyClient
import no.nav.klage.dokument.clients.pdl.graphql.HentPersonResponse
import no.nav.klage.dokument.clients.pdl.graphql.PdlClient
import no.nav.klage.dokument.clients.pdl.graphql.PdlPerson
import no.nav.klage.dokument.clients.pdl.graphql.PdlPersonDataWrapper
import no.nav.klage.dokument.clients.saf.graphql.AvsenderMottaker
import no.nav.klage.dokument.clients.saf.graphql.Bruker
import no.nav.klage.dokument.clients.saf.graphql.BrukerType
import no.nav.klage.dokument.clients.saf.graphql.Datotype
import no.nav.klage.dokument.clients.saf.graphql.DokumentInfo
import no.nav.klage.dokument.clients.saf.graphql.Dokumentstatus
import no.nav.klage.dokument.clients.saf.graphql.Dokumentvariant
import no.nav.klage.dokument.clients.saf.graphql.Filtype
import no.nav.klage.dokument.clients.saf.graphql.Journalpost
import no.nav.klage.dokument.clients.saf.graphql.Journalposttype
import no.nav.klage.dokument.clients.saf.graphql.Journalstatus
import no.nav.klage.dokument.clients.saf.graphql.RelevantDato
import no.nav.klage.dokument.clients.saf.graphql.SafGraphQlClient
import no.nav.klage.dokument.clients.saf.graphql.Sak
import no.nav.klage.dokument.clients.saf.graphql.Tema
import no.nav.klage.dokument.clients.saf.graphql.Variantformat
import no.nav.klage.dokument.domain.dokument.Adresse
import no.nav.klage.dokument.domain.dokument.PartId
import no.nav.klage.dokument.domain.dokument.Representant
import no.nav.klage.dokument.util.ARKIVFORMAT
import no.nav.klage.dokument.util.AVSENDER
import no.nav.klage.dokument.util.DOKUMENTASJON
import no.nav.klage.dokument.util.DOKUMENTET_ER_FERDIGSTILT
import no.nav.klage.dokument.util.DOKUMENT_HVOR_DELER_AV_INNHOLDET_ER_SKJERMET
import no.nav.klage.dokument.util.EKSPEDERT
import no.nav.klage.dokument.util.HOVEDDOKUMENT
import no.nav.klage.dokument.util.MOTTAKER
import no.nav.klage.dokument.util.NAV_KLAGEINSTANS_NAVN
import no.nav.klage.dokument.util.NAV_KLAGEINSTANS_ORGNR
import no.nav.klage.dokument.util.PRODUKSJONSFORMAT
import no.nav.klage.dokument.util.REFERANSE_DOKUMENTFIL_FORMAT
import no.nav.klage.dokument.util.SAKSPART_ROLLE_AMP
import no.nav.klage.dokument.util.SAKSPART_ROLLE_DAP
import no.nav.klage.dokument.util.TRYGDERETTEN_NAVN
import no.nav.klage.dokument.util.UKJENT
import no.nav.klage.dokument.util.UNDER_BEHANDLING
import no.nav.klage.dokument.util.UTGAAENDE_DOKUMENT
import no.nav.klage.dokument.util.VEDLEGG
import no.nav.klage.dokument.util.convertLocalDateTimeToXmlGregorianCalendar
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.kodeverk.PartIdType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.xmlunit.builder.Input
import org.xmlunit.validation.Languages
import org.xmlunit.validation.ValidationResult
import org.xmlunit.validation.Validator
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import no.arkivverket.standarder.noark5.arkivmelding.v2.Journalpost as ArkivJournalpost
import no.nav.klage.kodeverk.Tema as KodeverkTema

class AvtalemeldingServiceTest {
    val safGraphQlClient = mockk<SafGraphQlClient>()
    val pdlClient = mockk<PdlClient>()
    val eregClient = mockk<EregClient>()
    val klageUnleashProxyClient =
        mockk<KlageUnleashProxyClient>().apply {
            every { isEnabled(any()) } returns false
        }

    val bestillingsId = "bestillingsId"
    val journalpostId1 = "987654321"
    val journalpostId2 = "587654321"
    val journalpostId3 = "597654321"
    val arkivSaknummer1 = "111111"
    val arkivArkivsaknummer1 = "111111A"
    val arkivSaknummer2 = "222222"
    val arkivArkivsaknummer2 = "222222A"
    val fixedLocalDateTime = LocalDateTime.of(2025, Month.MAY, 26, 7, 32)
    val datoOpprettetSak1 = fixedLocalDateTime
    val datoOpprettetSak2 = fixedLocalDateTime.minusMonths(5)
    val datoOpprettetJournalpost1 = fixedLocalDateTime.minusDays(1)
    val datoOpprettetJournalpost2 = fixedLocalDateTime.minusMonths(3)
    val datoJournalfoert1 = fixedLocalDateTime.minusDays(2)
    val datoJournalfoert2 = fixedLocalDateTime.minusDays(26)
    val opprettetAvNavn1 = "Sak Sakbehandlersen oppretter"
    val opprettetAvNavn2 = "Sak Sakbehandlersen oppretter 2"
    val brukerIdFnr = "20026900000"
    val brukerTypeFnr = BrukerType.FNR
    val brukerIdOrgnr = "999999999"
    val brukerTypeOrgnr = BrukerType.ORGNR
    val tittelJournalpost1 = "Eksepdisjonsbrev til Trygderetten journalpost"
    val tittelJournalpost2 = "Eksepdisjonsbrev til Trygderetten journalpost 2"
    val journalfoertAvNavn1 = "Sak Sakbehandlersen journalfører"
    val journalfoertAvNavn2 = "Sak Sakbehandlersen journalfører 2"
    val tema = Tema.DAG
    val journalfoerendeEnhet1 = "1234"
    val journalfoerendeEnhet2 = "4321"

    val dokumentInfoIdHoveddok = "1234567"
    val tittelHoveddok = "Ekspedisjonsbrev til Trygderetten"

    val avsenderMottakerNavnOrigJp = "avsenderMottakerNavnOrigJp"
    val dokumentInfoIdVedlegg = "7654321"
    val tittelVedlegg = "Dokumentasjon til klage"
    val tittelVedleggUtgaaende = "Dokumentasjon til klage, Til $avsenderMottakerNavnOrigJp"
    val tittelVedleggInngaaende = "Dokumentasjon til klage, Fra $avsenderMottakerNavnOrigJp"

    val dokumentInfoIdVedlegg2 = "9876543"
    val eregNavn = "Bedrift AS"
    val pdlFornavn = "Bjarne"
    val pdlEtternavn = "Betjent"
    val pdlSammensattNavn = "Bjarne Betjent"

    val avtalemeldingService =
        AvtalemeldingService(
            safGraphQlClient = safGraphQlClient,
            applicationName = "test",
            pdlClient = pdlClient,
            eregClient = eregClient,
            klageUnleashProxyClient = klageUnleashProxyClient,
        )

    val logger = getLogger(AvtalemeldingServiceTest::class.java)

    val trygderettenMetadataInput =
        TrygderettenMetadataInput(
            kravfremsettelsesdato = LocalDate.of(2025, 11, 1),
            paaanketVedtaksdato = LocalDate.of(2026, 2, 20),
            tidligereITROgOpphevetHenvist = true,
            gjenopptak = false,
            forsterketRett = true,
            ettersendelse = false,
            lovhenvisning = setOf("ftrl. § 12-7"),
            representant =
                Representant(
                    partId =
                        PartId(
                            type = PartIdType.PERSON,
                            value = "01011012345",
                        ),
                    navn = "Representant Representantsen",
                    adresse =
                        Adresse(
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

    @Test
    fun `xml is valid against schema`() {
        val journalpost1 =
            getJournalpost(
                brukerOrgNummer = null,
                originalJournalpostIdForVedlegg = null,
            )
        every { safGraphQlClient.getJournalpostAsSystembruker(any()) } returns journalpost1
        every { safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(brukerId = any()) } returns
            listOf(
                journalpost1,
                getJournalpost2(journalposttype = Journalposttype.I),
            )
        every { pdlClient.getPersonInfo(any()) } returns hentPersonResponse

        val (arkivsaksnummer, avtalemeldingXml) =
            avtalemeldingService.generateMarshalledAvtalemelding(
                journalpostId = journalpostId1,
                bestillingsId = bestillingsId,
                trygderettenMetadata = null,
            )

        val v: Validator = Validator.forLanguage(Languages.W3C_XML_SCHEMA_NS_URI)
        v.setSchemaSources(
            Input.fromStream(javaClass.getResourceAsStream("/schema/metadatakatalog.xsd")).build(),
            Input.fromStream(javaClass.getResourceAsStream("/schema/arkivmelding.xsd")).build(),
            Input.fromStream(javaClass.getResourceAsStream("/schema/nav_virksomhet_metadata.xsd")).build(),
        )

        val validationResult: ValidationResult? = v.validateInstance(Input.fromString(avtalemeldingXml).build())
        assertThat(validationResult?.isValid).isTrue
    }

    @Test
    fun `v2 xml is valid against v2 schema`() {
        val journalpost1 =
            getJournalpost(
                brukerOrgNummer = null,
                originalJournalpostIdForVedlegg = null,
            )
        every { safGraphQlClient.getJournalpostAsSystembruker(any()) } returns journalpost1
        every { safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(brukerId = any()) } returns
            listOf(
                journalpost1,
                getJournalpost2(journalposttype = Journalposttype.I),
            )
        every { pdlClient.getPersonInfo(any()) } returns hentPersonResponse
        every { klageUnleashProxyClient.isEnabled("nav-tr-v2") } returns true

        val (arkivsaksnummer, avtalemeldingXml) =
            avtalemeldingService.generateMarshalledAvtalemelding(
                journalpostId = journalpostId1,
                bestillingsId = bestillingsId,
                trygderettenMetadata = trygderettenMetadataInput,
            )

        val v = Validator.forLanguage(Languages.W3C_XML_SCHEMA_NS_URI)
        v.setSchemaSources(
            Input.fromStream(javaClass.getResourceAsStream("/schema/metadatakatalog.xsd")).build(),
            Input.fromStream(javaClass.getResourceAsStream("/schema/arkivmelding.xsd")).build(),
            Input.fromStream(javaClass.getResourceAsStream("/schema/nav_virksomhet_metadata_v2.xsd")).build(),
        )

        val validationResult = v.validateInstance(Input.fromString(avtalemeldingXml).build())
        validationResult?.problems?.forEach { logger.warn("Validation problem: {}", it) }
        assertThat(validationResult?.isValid).isTrue
    }

    @Test
    fun `input with no external journalpost reference generates expected avtalemelding`() {
        val journalpost1 =
            getJournalpost(
                brukerOrgNummer = null,
                originalJournalpostIdForVedlegg = null,
            )
        every { safGraphQlClient.getJournalpostAsSystembruker(any()) } returns journalpost1
        every { safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(brukerId = any()) } returns
            listOf(
                journalpost1,
                getJournalpost2(journalposttype = Journalposttype.I),
            )
        every { pdlClient.getPersonInfo(any()) } returns hentPersonResponse

        val (arkivsaksnummer, avtalemelding) =
            avtalemeldingService.generateAvtalemelding(
                journalpostId = journalpostId1,
                bestillingsId = bestillingsId,
                trygderettenMetadata = null,
            )

        assertAvtalemelding(avtalemelding = avtalemelding)
        verify(exactly = 1) {
            safGraphQlClient.getJournalpostAsSystembruker(journalpostId1)
        }
        verify(exactly = 0) {
            safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(any())
        }
        verify(exactly = 1) {
            pdlClient.getPersonInfo(brukerIdFnr)
        }
        verify(exactly = 0) {
            eregClient.hentNoekkelInformasjonOmOrganisasjon(any())
        }
    }

    @Test
    fun `input where bruker is organization, with no external journalpost reference, generates expected avtalemelding`() {
        val journalpost1 =
            getJournalpost(
                brukerOrgNummer = brukerIdOrgnr,
                originalJournalpostIdForVedlegg = null,
            )
        every { safGraphQlClient.getJournalpostAsSystembruker(any()) } returns journalpost1
        every { safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(brukerId = any()) } returns
            listOf(
                journalpost1,
                getJournalpost2(journalposttype = Journalposttype.I),
            )
        every { pdlClient.getPersonInfo(any()) } returns hentPersonResponse
        every { eregClient.hentNoekkelInformasjonOmOrganisasjon(any()) } returns hentOrganisasjonResponse

        val (arkivsaksnummer, avtalemelding) =
            avtalemeldingService.generateAvtalemelding(
                journalpostId = journalpostId1,
                bestillingsId = bestillingsId,
                trygderettenMetadata = null,
            )

        assertAvtalemelding(avtalemelding = avtalemelding, brukerIsOrganisasjon = true)
        verify(exactly = 1) {
            safGraphQlClient.getJournalpostAsSystembruker(journalpostId1)
        }
        verify(exactly = 0) {
            safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(any())
        }
        verify(exactly = 0) {
            pdlClient.getPersonInfo(brukerIdFnr)
        }
        verify(exactly = 1) {
            eregClient.hentNoekkelInformasjonOmOrganisasjon(any())
        }
    }

    @Test
    fun `external inngaaende journalpost reference generates expected dokumentbeskrivelseVedlegg and gets dokumentoversiktbruker`() {
        val journalpost1 =
            getJournalpost(
                brukerOrgNummer = null,
                originalJournalpostIdForVedlegg = journalpostId2,
            )
        every { safGraphQlClient.getJournalpostAsSystembruker(any()) } returns journalpost1
        every { safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(brukerId = any()) } returns
            listOf(
                journalpost1,
                getJournalpost2(journalposttype = Journalposttype.I),
            )
        every { pdlClient.getPersonInfo(any()) } returns hentPersonResponse
        every { eregClient.hentNoekkelInformasjonOmOrganisasjon(any()) } returns hentOrganisasjonResponse

        val (arkivsaksnummer, avtalemelding) =
            avtalemeldingService.generateAvtalemelding(
                journalpostId = journalpostId1,
                bestillingsId = bestillingsId,
                trygderettenMetadata = null,
            )

        assertDokumentbeskrivelseVedlegg(
            dokumentbeskrivelseVedlegg =
                avtalemelding.mappe
                    .first()
                    .registrering
                    .first()
                    .dokumentbeskrivelse
                    .last(),
            vedleggJournalpostType = Journalposttype.I,
            vedleggIsFromDifferentJournalpost = true,
        )

        verify(exactly = 1) {
            safGraphQlClient.getJournalpostAsSystembruker(journalpostId1)
        }
        verify(exactly = 1) {
            safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(any())
        }
        verify(exactly = 1) {
            pdlClient.getPersonInfo(brukerIdFnr)
        }
        verify(exactly = 0) {
            eregClient.hentNoekkelInformasjonOmOrganisasjon(any())
        }
    }

    @Test
    fun `input with external utgaaende journalpost reference generates expected dokumentbeskrivelseVedlegg`() {
        val journalpost1 =
            getJournalpost(
                brukerOrgNummer = null,
                originalJournalpostIdForVedlegg = journalpostId2,
            )
        every { safGraphQlClient.getJournalpostAsSystembruker(any()) } returns journalpost1
        every { safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(brukerId = any()) } returns
            listOf(
                journalpost1,
                getJournalpost2(journalposttype = Journalposttype.U),
            )
        every { pdlClient.getPersonInfo(any()) } returns hentPersonResponse
        every { eregClient.hentNoekkelInformasjonOmOrganisasjon(any()) } returns hentOrganisasjonResponse

        val (arkivsaksnummer, avtalemelding) =
            avtalemeldingService.generateAvtalemelding(
                journalpostId = journalpostId1,
                bestillingsId = bestillingsId,
                trygderettenMetadata = null,
            )

        assertDokumentbeskrivelseVedlegg(
            dokumentbeskrivelseVedlegg =
                avtalemelding.mappe
                    .first()
                    .registrering
                    .first()
                    .dokumentbeskrivelse
                    .last(),
            vedleggJournalpostType = Journalposttype.U,
            vedleggIsFromDifferentJournalpost = true,
        )
    }

    @Test
    fun `input with no variantformat SLADDET and filtype not PNG or JPEG results in PRODUKSJONSFORMAT in output`() {
        val journalpost1 =
            getJournalpost(
                brukerOrgNummer = null,
                originalJournalpostIdForVedlegg = null,
                dokumentVarianter =
                    listOf(
                        Dokumentvariant(
                            variantformat = Variantformat.ARKIV,
                            filtype = Filtype.XLSX,
                        ),
                        Dokumentvariant(
                            variantformat = Variantformat.PRODUKSJON,
                            filtype = Filtype.PDF,
                        ),
                    ),
            )

        every { safGraphQlClient.getJournalpostAsSystembruker(any()) } returns journalpost1
        every { safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(brukerId = any()) } returns
            listOf(
                journalpost1,
                getJournalpost2(journalposttype = Journalposttype.U),
            )
        every { pdlClient.getPersonInfo(any()) } returns hentPersonResponse
        every { eregClient.hentNoekkelInformasjonOmOrganisasjon(any()) } returns hentOrganisasjonResponse

        val (arkivsaksnummer, avtalemelding) =
            avtalemeldingService.generateAvtalemelding(
                journalpostId = journalpostId1,
                bestillingsId = bestillingsId,
                trygderettenMetadata = null,
            )

        val dokumentobjektHoveddokument =
            avtalemelding.mappe
                .first()
                .registrering
                .first()
                .dokumentbeskrivelse
                .first()
                .dokumentobjekt
                .first()
        assertThat(dokumentobjektHoveddokument.variantformat).isEqualTo(PRODUKSJONSFORMAT)
    }

    @Test
    fun `input with no variantformat SLADDET and filtype PNG results in ARKIVFORMAT in output`() {
        val journalpost1 =
            getJournalpost(
                brukerOrgNummer = null,
                originalJournalpostIdForVedlegg = null,
                dokumentVarianter =
                    listOf(
                        Dokumentvariant(
                            variantformat = Variantformat.ARKIV,
                            filtype = Filtype.PNG,
                        ),
                        Dokumentvariant(
                            variantformat = Variantformat.PRODUKSJON,
                            filtype = Filtype.PDF,
                        ),
                    ),
            )

        every { safGraphQlClient.getJournalpostAsSystembruker(any()) } returns journalpost1
        every { safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(brukerId = any()) } returns
            listOf(
                journalpost1,
                getJournalpost2(journalposttype = Journalposttype.U),
            )
        every { pdlClient.getPersonInfo(any()) } returns hentPersonResponse
        every { eregClient.hentNoekkelInformasjonOmOrganisasjon(any()) } returns hentOrganisasjonResponse

        val (arkivsaksnummer, avtalemelding) =
            avtalemeldingService.generateAvtalemelding(
                journalpostId = journalpostId1,
                bestillingsId = bestillingsId,
                trygderettenMetadata = null,
            )

        val dokumentobjektHoveddokument =
            avtalemelding.mappe
                .first()
                .registrering
                .first()
                .dokumentbeskrivelse
                .first()
                .dokumentobjekt
                .first()
        assertThat(dokumentobjektHoveddokument.variantformat).isEqualTo(ARKIVFORMAT)
    }

    @Test
    fun `input with no variantformat SLADDET and filtype JPEG results in ARKIVFORMAT in output`() {
        val journalpost1 =
            getJournalpost(
                brukerOrgNummer = null,
                originalJournalpostIdForVedlegg = null,
                dokumentVarianter =
                    listOf(
                        Dokumentvariant(
                            variantformat = Variantformat.ARKIV,
                            filtype = Filtype.JPEG,
                        ),
                        Dokumentvariant(
                            variantformat = Variantformat.PRODUKSJON,
                            filtype = Filtype.PDF,
                        ),
                    ),
            )

        every { safGraphQlClient.getJournalpostAsSystembruker(any()) } returns journalpost1
        every { safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(brukerId = any()) } returns
            listOf(
                journalpost1,
                getJournalpost2(journalposttype = Journalposttype.U),
            )
        every { pdlClient.getPersonInfo(any()) } returns hentPersonResponse
        every { eregClient.hentNoekkelInformasjonOmOrganisasjon(any()) } returns hentOrganisasjonResponse

        val (arkivsaksnummer, avtalemelding) =
            avtalemeldingService.generateAvtalemelding(
                journalpostId = journalpostId1,
                bestillingsId = bestillingsId,
                trygderettenMetadata = null,
            )

        val dokumentobjektHoveddokument =
            avtalemelding.mappe
                .first()
                .registrering
                .first()
                .dokumentbeskrivelse
                .first()
                .dokumentobjekt
                .first()
        assertThat(dokumentobjektHoveddokument.variantformat).isEqualTo(ARKIVFORMAT)
    }

    @Test
    fun `input with variantformat SLADDET results in DOKUMENT_HVOR_DELER_AV_INNHOLDET_ER_SKJERMET in output`() {
        val journalpost1 =
            getJournalpost(
                brukerOrgNummer = null,
                originalJournalpostIdForVedlegg = null,
                dokumentVarianter =
                    listOf(
                        Dokumentvariant(
                            variantformat = Variantformat.SLADDET,
                            filtype = Filtype.JPEG,
                        ),
                        Dokumentvariant(
                            variantformat = Variantformat.PRODUKSJON,
                            filtype = Filtype.PDF,
                        ),
                    ),
            )

        every { safGraphQlClient.getJournalpostAsSystembruker(any()) } returns journalpost1
        every { safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(brukerId = any()) } returns
            listOf(
                journalpost1,
                getJournalpost2(journalposttype = Journalposttype.U),
            )
        every { pdlClient.getPersonInfo(any()) } returns hentPersonResponse
        every { eregClient.hentNoekkelInformasjonOmOrganisasjon(any()) } returns hentOrganisasjonResponse

        val (arkivsaksnummer, avtalemelding) =
            avtalemeldingService.generateAvtalemelding(
                journalpostId = journalpostId1,
                bestillingsId = bestillingsId,
                trygderettenMetadata = null,
            )

        val dokumentobjektHoveddokument =
            avtalemelding.mappe
                .first()
                .registrering
                .first()
                .dokumentbeskrivelse
                .first()
                .dokumentobjekt
                .first()
        assertThat(dokumentobjektHoveddokument.variantformat).isEqualTo(DOKUMENT_HVOR_DELER_AV_INNHOLDET_ER_SKJERMET)
    }

    @Test
    fun `input where vedlegg has no dokumentstatus is handled as if it was ferdigstilt`() {
        val journalpost1 =
            getJournalpost(
                brukerOrgNummer = null,
                originalJournalpostIdForVedlegg = null,
                vedleggStatus = null,
            )
        every { safGraphQlClient.getJournalpostAsSystembruker(any()) } returns journalpost1
        every { safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(brukerId = any()) } returns
            listOf(
                journalpost1,
                getJournalpost2(journalposttype = Journalposttype.I),
            )
        every { pdlClient.getPersonInfo(any()) } returns hentPersonResponse

        val (arkivsaksnummer, avtalemelding) =
            avtalemeldingService.generateAvtalemelding(
                journalpostId = journalpostId1,
                bestillingsId = bestillingsId,
                trygderettenMetadata = null,
            )

        assertAvtalemelding(
            avtalemelding = avtalemelding,
        )
    }

    @Test
    fun `input where vedlegg has different dokumentstatus than FERDIGSTILT is not mapped`() {
        val journalpost1 =
            getJournalpost(
                brukerOrgNummer = null,
                originalJournalpostIdForVedlegg = null,
                vedleggStatus = Dokumentstatus.UNDER_REDIGERING,
            )
        every { safGraphQlClient.getJournalpostAsSystembruker(any()) } returns journalpost1
        every { safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(brukerId = any()) } returns
            listOf(
                journalpost1,
                getJournalpost2(journalposttype = Journalposttype.I),
            )
        every { pdlClient.getPersonInfo(any()) } returns hentPersonResponse

        val (arkivsaksnummer, avtalemelding) =
            avtalemeldingService.generateAvtalemelding(
                journalpostId = journalpostId1,
                bestillingsId = bestillingsId,
                trygderettenMetadata = null,
            )

        assertThat(avtalemelding.antallFiler).isEqualTo(1)
        assertThat(
            avtalemelding.mappe
                .first()
                .registrering
                .first()
                .dokumentbeskrivelse,
        ).hasSize(1)
    }

    @Test
    fun `input with external journalpost reference without opprettetAv results in UKJENT opprettetAv`() {
        val journalpost1 =
            getJournalpost(
                brukerOrgNummer = null,
                originalJournalpostIdForVedlegg = journalpostId2,
            )
        every { safGraphQlClient.getJournalpostAsSystembruker(any()) } returns journalpost1
        every { safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(brukerId = any()) } returns
            listOf(
                journalpost1,
                getJournalpost2(journalposttype = Journalposttype.I, journalfoertAvNavn = null, opprettetAvNavn = null),
            )
        every { pdlClient.getPersonInfo(any()) } returns hentPersonResponse

        val (arkivsaksnummer, avtalemelding) =
            avtalemeldingService.generateAvtalemelding(
                journalpostId = journalpostId1,
                bestillingsId = bestillingsId,
                trygderettenMetadata = null,
            )

        assertThat(
            avtalemelding.mappe
                .first()
                .registrering
                .first()
                .dokumentbeskrivelse
                .last()
                .opprettetAv,
        ).isEqualTo(
            UKJENT,
        )
    }

    @Test
    fun `input with sak wihtout opprettetDato results in oldest opprettetDato from vedlegg`() {
        val journalpost1 = getJournalpostForDateTest()
        val treDagerSiden = fixedLocalDateTime.minusDays(3)
        val femDagerSiden = fixedLocalDateTime.minusDays(5)
        every { safGraphQlClient.getJournalpostAsSystembruker(any()) } returns journalpost1
        every { safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(brukerId = any()) } returns
            listOf(
                journalpost1,
                getJournalpost2(
                    journalposttype = Journalposttype.I,
                    journalfoertAvNavn = null,
                    opprettetAvNavn = null,
                    journalpostId = journalpostId2,
                    dateJournalfoert = femDagerSiden,
                    dokumentInfoId = dokumentInfoIdVedlegg,
                ),
                getJournalpost2(
                    journalposttype = Journalposttype.I,
                    journalfoertAvNavn = null,
                    opprettetAvNavn = null,
                    journalpostId = journalpostId3,
                    dateJournalfoert = treDagerSiden,
                    dokumentInfoId = dokumentInfoIdVedlegg2,
                ),
            )
        every { pdlClient.getPersonInfo(any()) } returns hentPersonResponse

        val (arkivsaksnummer, avtalemelding) =
            avtalemeldingService.generateAvtalemelding(
                journalpostId = journalpostId1,
                bestillingsId = bestillingsId,
                trygderettenMetadata = null,
            )

        assertThat(avtalemelding.mappe.first().opprettetDato).isEqualTo(
            convertLocalDateTimeToXmlGregorianCalendar(
                femDagerSiden,
            ),
        )
    }

    private fun assertAvtalemelding(
        avtalemelding: Arkivmelding,
        brukerIsOrganisasjon: Boolean = false,
    ) {
        assertThat(avtalemelding.meldingId).isEqualTo(bestillingsId)
        assertThat(avtalemelding.tidspunkt).isNotNull
        assertThat(avtalemelding.antallFiler).isEqualTo(2)
        assertThat(avtalemelding.mappe).hasSize(1)
        assertSaksmappe(
            saksmappe = avtalemelding.mappe.first() as Saksmappe,
            brukerIsOrganisasjon = brukerIsOrganisasjon,
        )
    }

    private fun assertSaksmappe(
        saksmappe: Saksmappe,
        brukerIsOrganisasjon: Boolean,
    ) {
        assertThat(saksmappe.tittel).isEqualTo(KodeverkTema.valueOf(tema.name).beskrivelse)
        assertThat(saksmappe.opprettetDato).isEqualTo(convertLocalDateTimeToXmlGregorianCalendar(datoOpprettetSak1))
        assertThat(saksmappe.opprettetAv).isEqualTo(opprettetAvNavn1)
        val navMappe = extractNavMappe(saksmappe.virksomhetsspesifikkeMetadata)
        assertThat(navMappe.saksnummer).isEqualTo(arkivSaknummer1)
        assertSakspart(partList = saksmappe.part, brukerIsOrganisasjon = brukerIsOrganisasjon)
        assertThat(saksmappe.saksdato).isEqualTo(convertLocalDateTimeToXmlGregorianCalendar(datoOpprettetSak1))
        assertThat(saksmappe.administrativEnhet).isEqualTo(NAV_KLAGEINSTANS_NAVN)
        assertThat(saksmappe.saksansvarlig).isEqualTo(opprettetAvNavn1)
        assertThat(saksmappe.journalenhet).isEqualTo(journalfoerendeEnhet1)
        assertThat(saksmappe.saksstatus).isEqualTo(UNDER_BEHANDLING)
        assertThat(saksmappe.registrering).hasSize(1)
        assertJournalpost(
            journalpost = saksmappe.registrering.first() as ArkivJournalpost,
        )
    }

    private fun assertSakspart(
        partList: MutableList<Part>,
        brukerIsOrganisasjon: Boolean,
    ) {
        assertThat(partList).hasSize(2)
        val sakspartAMP: Part = partList.first()
        assertThat(sakspartAMP.partID).isNull()
        assertThat(sakspartAMP.partNavn).isEqualTo(NAV_KLAGEINSTANS_NAVN)
        assertThat(sakspartAMP.partRolle).isEqualTo(SAKSPART_ROLLE_AMP)
        assertThat(sakspartAMP.kontaktperson).isEqualTo(opprettetAvNavn1)
        assertThat(sakspartAMP.organisasjonsnummer?.organisasjonsnummer).isEqualTo(NAV_KLAGEINSTANS_ORGNR)
        assertThat(sakspartAMP.foedselsnummer).isNull()

        val sakspartDAP: Part = partList.last()
        if (brukerIsOrganisasjon) {
            assertThat(sakspartDAP.foedselsnummer).isNull()
            assertThat(sakspartDAP.organisasjonsnummer.organisasjonsnummer).isEqualTo(brukerIdOrgnr)
        } else {
            assertThat(sakspartDAP.foedselsnummer.foedselsnummer).isEqualTo(brukerIdFnr)
            assertThat(sakspartDAP.organisasjonsnummer).isNull()
        }
        assertThat(sakspartDAP.partNavn).isEqualTo(if (brukerIsOrganisasjon) eregNavn else pdlSammensattNavn)
        assertThat(sakspartDAP.partRolle).isEqualTo(SAKSPART_ROLLE_DAP)
        assertThat(sakspartDAP.kontaktperson).isNull()
    }

    private fun assertJournalpost(journalpost: ArkivJournalpost) {
        assertThat(journalpost.opprettetDato).isEqualTo(
            convertLocalDateTimeToXmlGregorianCalendar(
                datoOpprettetJournalpost1,
            ),
        )
        assertThat(journalpost.opprettetAv).isEqualTo(opprettetAvNavn1)
        assertThat(journalpost.tittel).isEqualTo(tittelJournalpost1)
        assertKorrespondansepart(journalpost.korrespondansepart)
        assertThat(journalpost.journalposttype).isEqualTo(UTGAAENDE_DOKUMENT)
        assertThat(journalpost.journalstatus).isEqualTo(EKSPEDERT)
        assertThat(journalpost.journaldato).isEqualTo(convertLocalDateTimeToXmlGregorianCalendar(datoJournalfoert1))
        assertDokumentbeskrivelse(
            dokumentbeskrivelse = journalpost.dokumentbeskrivelse,
        )
    }

    private fun assertDokumentbeskrivelse(dokumentbeskrivelse: MutableList<Dokumentbeskrivelse>) {
        assertThat(dokumentbeskrivelse).hasSize(2)
        val dokumentbeskrivelseHoveddokument = dokumentbeskrivelse.first()
        val dokumentbeskrivelseVedlegg = dokumentbeskrivelse.last()
        assertDokumentbeskrivelseHoveddokument(dokumentbeskrivelseHoveddokument)
        assertDokumentbeskrivelseVedlegg(dokumentbeskrivelseVedlegg)
    }

    private fun assertDokumentbeskrivelseHoveddokument(dokumentbeskrivelseHoveddokument: Dokumentbeskrivelse) {
        assertThat(dokumentbeskrivelseHoveddokument.tilknyttetRegistreringSom).isEqualTo(HOVEDDOKUMENT)
        assertThat(dokumentbeskrivelseHoveddokument.dokumentnummer).isEqualTo(BigInteger.ONE)
        assertThat(dokumentbeskrivelseHoveddokument.tittel).isEqualTo(tittelHoveddok)
        assertThat(dokumentbeskrivelseHoveddokument.opprettetDato).isEqualTo(
            convertLocalDateTimeToXmlGregorianCalendar(
                datoJournalfoert1,
            ),
        )
        assertThat(dokumentbeskrivelseHoveddokument.dokumentobjekt).hasSize(1)
        assertDokumentobjektHoveddokument(dokumentbeskrivelseHoveddokument.dokumentobjekt.first())
        assertThat(dokumentbeskrivelseHoveddokument.dokumenttype).isEqualTo(DOKUMENTASJON)
        assertThat(dokumentbeskrivelseHoveddokument.dokumentstatus).isEqualTo(DOKUMENTET_ER_FERDIGSTILT)
        assertThat(dokumentbeskrivelseHoveddokument.tilknyttetDato).isNotNull()
        assertThat(dokumentbeskrivelseHoveddokument.opprettetAv).isEqualTo(journalfoertAvNavn1)
        assertThat(dokumentbeskrivelseHoveddokument.tilknyttetAv).isEqualTo(journalfoertAvNavn1)
    }

    private fun assertDokumentbeskrivelseVedlegg(
        dokumentbeskrivelseVedlegg: Dokumentbeskrivelse,
        vedleggJournalpostType: Journalposttype? = null,
        vedleggIsFromDifferentJournalpost: Boolean = false,
    ) {
        val correctTittel =
            if (vedleggIsFromDifferentJournalpost) {
                when (vedleggJournalpostType) {
                    Journalposttype.U -> tittelVedleggUtgaaende
                    Journalposttype.I -> tittelVedleggInngaaende
                    else -> tittelVedleggUtgaaende
                }
            } else {
                tittelVedlegg
            }
        assertThat(dokumentbeskrivelseVedlegg.tilknyttetRegistreringSom).isEqualTo(VEDLEGG)
        assertThat(dokumentbeskrivelseVedlegg.dokumentnummer).isEqualTo(BigInteger.TWO)
        assertThat(dokumentbeskrivelseVedlegg.tittel).isEqualTo(correctTittel)
        assertThat(dokumentbeskrivelseVedlegg.opprettetDato).isEqualTo(
            convertLocalDateTimeToXmlGregorianCalendar(
                if (vedleggIsFromDifferentJournalpost) datoJournalfoert2 else datoJournalfoert1,
            ),
        )
        assertThat(dokumentbeskrivelseVedlegg.dokumentobjekt).hasSize(1)
        assertDokumentobjektVedlegg(
            dokumentobjektVedlegg = dokumentbeskrivelseVedlegg.dokumentobjekt.first(),
            vedleggIsFromDifferentJournalpost = vedleggIsFromDifferentJournalpost,
        )
        assertThat(dokumentbeskrivelseVedlegg.dokumenttype).isEqualTo(DOKUMENTASJON)
        assertThat(dokumentbeskrivelseVedlegg.dokumentstatus).isEqualTo(DOKUMENTET_ER_FERDIGSTILT)
        assertThat(dokumentbeskrivelseVedlegg.tilknyttetDato).isNotNull()
        assertThat(
            dokumentbeskrivelseVedlegg.opprettetAv,
        ).isEqualTo(if (vedleggIsFromDifferentJournalpost) opprettetAvNavn2 else journalfoertAvNavn1)
        assertThat(dokumentbeskrivelseVedlegg.tilknyttetAv).isEqualTo(journalfoertAvNavn1)
    }

    private fun assertDokumentobjektHoveddokument(dokumentobjektHoveddokument: Dokumentobjekt) {
        assertThat(dokumentobjektHoveddokument.versjonsnummer).isEqualTo(BigInteger.ONE)
        assertThat(dokumentobjektHoveddokument.variantformat).isEqualTo(DOKUMENT_HVOR_DELER_AV_INNHOLDET_ER_SKJERMET)
        assertThat(dokumentobjektHoveddokument.format).isEqualTo(Filtype.PDF.name.lowercase())
        assertThat(dokumentobjektHoveddokument.opprettetDato).isEqualTo(
            convertLocalDateTimeToXmlGregorianCalendar(
                datoJournalfoert1,
            ),
        )
        assertThat(dokumentobjektHoveddokument.opprettetAv).isEqualTo(journalfoertAvNavn1)
        assertThat(dokumentobjektHoveddokument.referanseDokumentfil).isEqualTo(
            String.format(
                REFERANSE_DOKUMENTFIL_FORMAT,
                journalpostId1,
                dokumentInfoIdHoveddok,
                Filtype.PDF.name.lowercase(),
            ),
        )
    }

    private fun assertDokumentobjektVedlegg(
        dokumentobjektVedlegg: Dokumentobjekt,
        vedleggIsFromDifferentJournalpost: Boolean = false,
    ) {
        assertThat(dokumentobjektVedlegg.versjonsnummer).isEqualTo(BigInteger.ONE)
        assertThat(dokumentobjektVedlegg.variantformat).isEqualTo(ARKIVFORMAT)
        assertThat(dokumentobjektVedlegg.format).isEqualTo(Filtype.JPEG.name.lowercase())
        assertThat(dokumentobjektVedlegg.opprettetDato).isEqualTo(
            convertLocalDateTimeToXmlGregorianCalendar(
                if (vedleggIsFromDifferentJournalpost) {
                    datoJournalfoert2
                } else {
                    datoJournalfoert1
                },
            ),
        )
        assertThat(
            dokumentobjektVedlegg.opprettetAv,
        ).isEqualTo(if (vedleggIsFromDifferentJournalpost) opprettetAvNavn2 else journalfoertAvNavn1)
        assertThat(dokumentobjektVedlegg.referanseDokumentfil).isEqualTo(
            String.format(
                REFERANSE_DOKUMENTFIL_FORMAT,
                journalpostId1,
                dokumentInfoIdVedlegg,
                Filtype.JPEG.name.lowercase(),
            ),
        )
    }

    private fun assertKorrespondansepart(korrespondansepartList: List<Korrespondansepart>) {
        assertThat(korrespondansepartList).hasSize(2)
        val mottaker = korrespondansepartList.first()
        assertThat(mottaker.korrespondanseparttype).isEqualTo(MOTTAKER)
        assertThat(mottaker.korrespondansepartNavn).isEqualTo(TRYGDERETTEN_NAVN)

        val avsender = korrespondansepartList.last()
        assertThat(avsender.korrespondanseparttype).isEqualTo(AVSENDER)
        assertThat(avsender.korrespondansepartNavn).isEqualTo(NAV_KLAGEINSTANS_NAVN)
    }

    private fun extractNavMappe(virksomhetsspesifikkeMetadata: Any): NavMappe {
        val navMappeElement: JAXBElement<NavMappe> =
            (virksomhetsspesifikkeMetadata as JAXBElement<*>).getValue() as JAXBElement<NavMappe>
        return navMappeElement.getValue()
    }

    val hentPersonResponse =
        HentPersonResponse(
            data =
                PdlPersonDataWrapper(
                    hentPerson =
                        PdlPerson(
                            folkeregisteridentifikator =
                                listOf(
                                    PdlPerson.Folkeregisteridentifikator(
                                        identifikasjonsnummer = brukerIdFnr,
                                    ),
                                ),
                            adressebeskyttelse = listOf(),
                            navn =
                                listOf(
                                    PdlPerson.Navn(
                                        fornavn = pdlFornavn,
                                        mellomnavn = null,
                                        etternavn = pdlEtternavn,
                                    ),
                                ),
                            kjoenn = listOf(),
                            sivilstand = listOf(),
                            vergemaalEllerFremtidsfullmakt = listOf(),
                            doedsfall = listOf(),
                            sikkerhetstiltak = listOf(),
                        ),
                ),
            errors = listOf(),
        )

    val hentOrganisasjonResponse =
        NoekkelInfoOmOrganisasjon(
            navn =
                NoekkelInfoOmOrganisasjon.Navn(
                    sammensattnavn = eregNavn,
                ),
            organisasjonsnummer = brukerIdOrgnr,
            enhetstype = "",
            opphoersdato = null,
            adresse = null,
        )

    fun getJournalpost(
        brukerOrgNummer: String?,
        originalJournalpostIdForVedlegg: String?,
        dokumentVarianter: List<Dokumentvariant>? = null,
        vedleggStatus: Dokumentstatus? = Dokumentstatus.FERDIGSTILT,
    ): Journalpost {
        val bruker =
            if (brukerOrgNummer != null) {
                Bruker(
                    id = brukerOrgNummer,
                    type = brukerTypeOrgnr,
                )
            } else {
                Bruker(
                    id = brukerIdFnr,
                    type = brukerTypeFnr,
                )
            }
        return Journalpost(
            journalpostId = journalpostId1,
            journalposttype = Journalposttype.U,
            journalstatus = Journalstatus.FERDIGSTILT,
            tema = tema,
            sak =
                Sak(
                    datoOpprettet = datoOpprettetSak1,
                    fagsakId = arkivSaknummer1,
                    arkivsaksnummer = arkivSaknummer1,
                ),
            bruker = bruker,
            avsenderMottaker = null,
            opprettetAvNavn = opprettetAvNavn1,
            datoOpprettet = datoOpprettetJournalpost1,
            dokumenter =
                listOf(
                    DokumentInfo(
                        dokumentInfoId = dokumentInfoIdHoveddok,
                        tittel = tittelHoveddok,
                        brevkode = null,
                        skjerming = null,
                        dokumentvarianter =
                            dokumentVarianter ?: listOf(
                                Dokumentvariant(
                                    variantformat = Variantformat.ARKIV,
                                    filtype = Filtype.PNG,
                                ),
                                Dokumentvariant(
                                    variantformat = Variantformat.SLADDET,
                                    filtype = Filtype.PDF,
                                ),
                            ),
                        datoFerdigstilt = LocalDateTime.now().minusMonths(2),
                        originalJournalpostId = null,
                        dokumentstatus = Dokumentstatus.FERDIGSTILT,
                    ),
                    DokumentInfo(
                        dokumentInfoId = dokumentInfoIdVedlegg,
                        tittel = tittelVedlegg,
                        brevkode = null,
                        skjerming = null,
                        dokumentvarianter =
                            listOf(
                                Dokumentvariant(
                                    variantformat = Variantformat.ARKIV,
                                    filtype = Filtype.JPEG,
                                ),
                                Dokumentvariant(
                                    variantformat = Variantformat.PRODUKSJON,
                                    filtype = Filtype.PDF,
                                ),
                            ),
                        datoFerdigstilt = LocalDateTime.now().minusMonths(3),
                        originalJournalpostId = originalJournalpostIdForVedlegg,
                        dokumentstatus = vedleggStatus,
                    ),
                ),
            relevanteDatoer =
                listOf(
                    RelevantDato(
                        dato = datoJournalfoert1,
                        datotype = Datotype.DATO_JOURNALFOERT,
                    ),
                ),
            journalforendeEnhet = journalfoerendeEnhet1,
            tittel = tittelJournalpost1,
            journalfortAvNavn = journalfoertAvNavn1,
            skjerming = null,
        )
    }

    fun getJournalpost2(
        journalposttype: Journalposttype = Journalposttype.I,
        journalfoertAvNavn: String? = journalfoertAvNavn2,
        opprettetAvNavn: String? = opprettetAvNavn2,
        journalpostId: String = journalpostId2,
        dateJournalfoert: LocalDateTime = datoJournalfoert2,
        dokumentInfoId: String = dokumentInfoIdVedlegg,
    ): Journalpost =
        Journalpost(
            journalpostId = journalpostId,
            journalposttype = journalposttype,
            journalstatus = Journalstatus.EKSPEDERT,
            tema = tema,
            sak =
                Sak(
                    datoOpprettet = datoOpprettetSak2,
                    fagsakId = arkivSaknummer2,
                    arkivsaksnummer = arkivSaknummer2,
                ),
            bruker =
                Bruker(
                    id = brukerIdFnr,
                    type = brukerTypeFnr,
                ),
            avsenderMottaker =
                AvsenderMottaker(
                    navn = avsenderMottakerNavnOrigJp,
                    erLikBruker = true,
                    id = null,
                    type = null,
                    land = null,
                ),
            opprettetAvNavn = opprettetAvNavn,
            skjerming = null,
            datoOpprettet = datoOpprettetJournalpost2,
            dokumenter =
                listOf(
                    DokumentInfo(
                        dokumentInfoId = dokumentInfoId,
                        tittel = tittelVedlegg,
                        brevkode = null,
                        skjerming = null,
                        dokumentvarianter =
                            listOf(
                                Dokumentvariant(
                                    variantformat = Variantformat.SLADDET,
                                    filtype = Filtype.PDF,
                                ),
                            ),
                        datoFerdigstilt = LocalDateTime.now().minusMonths(3),
                        originalJournalpostId = null,
                        dokumentstatus = Dokumentstatus.FERDIGSTILT,
                    ),
                ),
            relevanteDatoer =
                listOf(
                    RelevantDato(
                        dato = dateJournalfoert,
                        datotype = Datotype.DATO_JOURNALFOERT,
                    ),
                ),
            journalforendeEnhet = journalfoerendeEnhet2,
            tittel = tittelJournalpost2,
            journalfortAvNavn = journalfoertAvNavn,
        )

    fun getJournalpostForDateTest(): Journalpost {
        val bruker =
            Bruker(
                id = brukerIdFnr,
                type = brukerTypeFnr,
            )

        return Journalpost(
            journalpostId = journalpostId1,
            journalposttype = Journalposttype.U,
            journalstatus = Journalstatus.FERDIGSTILT,
            tema = tema,
            sak =
                Sak(
                    datoOpprettet = null,
                    fagsakId = arkivSaknummer1,
                    arkivsaksnummer = arkivSaknummer1,
                ),
            bruker = bruker,
            avsenderMottaker = null,
            opprettetAvNavn = opprettetAvNavn1,
            datoOpprettet = datoOpprettetJournalpost1,
            dokumenter =
                listOf(
                    DokumentInfo(
                        dokumentInfoId = dokumentInfoIdHoveddok,
                        tittel = tittelHoveddok,
                        brevkode = null,
                        skjerming = null,
                        dokumentvarianter =
                            listOf(
                                Dokumentvariant(
                                    variantformat = Variantformat.ARKIV,
                                    filtype = Filtype.PNG,
                                ),
                                Dokumentvariant(
                                    variantformat = Variantformat.SLADDET,
                                    filtype = Filtype.PDF,
                                ),
                            ),
                        datoFerdigstilt = LocalDateTime.now().minusMonths(2),
                        originalJournalpostId = null,
                        dokumentstatus = Dokumentstatus.FERDIGSTILT,
                    ),
                    DokumentInfo(
                        dokumentInfoId = dokumentInfoIdVedlegg,
                        tittel = tittelVedlegg,
                        brevkode = null,
                        skjerming = null,
                        dokumentvarianter =
                            listOf(
                                Dokumentvariant(
                                    variantformat = Variantformat.ARKIV,
                                    filtype = Filtype.JPEG,
                                ),
                                Dokumentvariant(
                                    variantformat = Variantformat.PRODUKSJON,
                                    filtype = Filtype.PDF,
                                ),
                            ),
                        datoFerdigstilt = LocalDateTime.now().minusMonths(3),
                        originalJournalpostId = journalpostId2,
                        dokumentstatus = Dokumentstatus.FERDIGSTILT,
                    ),
                    DokumentInfo(
                        dokumentInfoId = dokumentInfoIdVedlegg2,
                        tittel = tittelVedlegg,
                        brevkode = null,
                        skjerming = null,
                        dokumentvarianter =
                            listOf(
                                Dokumentvariant(
                                    variantformat = Variantformat.ARKIV,
                                    filtype = Filtype.JPEG,
                                ),
                                Dokumentvariant(
                                    variantformat = Variantformat.PRODUKSJON,
                                    filtype = Filtype.PDF,
                                ),
                            ),
                        datoFerdigstilt = LocalDateTime.now().minusMonths(3),
                        originalJournalpostId = journalpostId3,
                        dokumentstatus = Dokumentstatus.FERDIGSTILT,
                    ),
                ),
            relevanteDatoer =
                listOf(
                    RelevantDato(
                        dato = datoJournalfoert1,
                        datotype = Datotype.DATO_JOURNALFOERT,
                    ),
                ),
            journalforendeEnhet = journalfoerendeEnhet1,
            tittel = tittelJournalpost1,
            journalfortAvNavn = journalfoertAvNavn1,
            skjerming = null,
        )
    }
}
