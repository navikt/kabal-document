package no.nav.klage.dokument.service


import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.xml.bind.JAXBElement
import no.arkivverket.standarder.noark5.arkivmelding.v2.*
import no.nav.avtaltmelding.trygderetten.v1.NavMappe
import no.nav.klage.dokument.clients.ereg.EregClient
import no.nav.klage.dokument.clients.pdl.graphql.HentPersonResponse
import no.nav.klage.dokument.clients.pdl.graphql.PdlClient
import no.nav.klage.dokument.clients.pdl.graphql.PdlPerson
import no.nav.klage.dokument.clients.pdl.graphql.PdlPersonDataWrapper
import no.nav.klage.dokument.clients.saf.graphql.*
import no.nav.klage.dokument.clients.saf.graphql.Journalpost
import no.nav.klage.dokument.util.*
import no.nav.klage.oppgave.clients.ereg.NoekkelInfoOmOrganisasjon
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigInteger
import java.time.LocalDateTime
import java.time.Month
import no.arkivverket.standarder.noark5.arkivmelding.v2.Journalpost as ArkivJournalpost
import no.nav.klage.kodeverk.Tema as KodeverkTema


class ArkivmeldingServiceTest {

    val safGraphQlClient = mockk<SafGraphQlClient>()
    val pdlClient = mockk<PdlClient>()
    val eregClient = mockk<EregClient>()

    val BESTILLINGS_ID = "bestillingsId"
    val JOURNALPOST_ID_1 = "987654321"
    val JOURNALPOST_ID_2 = "587654321"
    val JOURNALPOST_ID_3 = "597654321"
    val JOURNALPOST_ID_4 = "197654321"
    val ARKIV_SAKNUMMER_1 = "111111"
    val ARKIV_SAKNUMMER_2 = "222222"
    val FIXED_LOCAL_DATE_TIME = LocalDateTime.of(2025, Month.MAY, 26, 7, 32)
    val DATO_OPPRETTET_SAK_1 = FIXED_LOCAL_DATE_TIME
    val DATO_OPPRETTET_SAK_2 = FIXED_LOCAL_DATE_TIME.minusMonths(5)
    val DATO_OPPRETTET_JOURNALPOST_1 = FIXED_LOCAL_DATE_TIME.minusDays(1)
    val DATO_OPPRETTET_JOURNALPOST_2 = FIXED_LOCAL_DATE_TIME.minusMonths(3)
    val DATO_JOURNALFOERT_1 = FIXED_LOCAL_DATE_TIME.minusDays(2)
    val DATO_JOURNALFOERT_2 = FIXED_LOCAL_DATE_TIME.minusDays(26)
    val OPPRETTET_AV_NAVN_1 = "Sak Sakbehandlersen oppretter"
    val OPPRETTET_AV_NAVN_2 = "Sak Sakbehandlersen oppretter 2"
    val BRUKER_ID_FNR = "20026900000"
    val BRUKER_TYPE_FNR = BrukerType.FNR
    val BRUKER_ID_ORGNR = "999999999"
    val BRUKER_TYPE_ORGNR = BrukerType.ORGNR
    val BRUKER_ID_AKTOER_ID = "aktoerId"
    val BRUKER_TYPE_AKTOER_ID = "AKTOERID"
    val TITTEL_JOURNALPOST_1 = "Eksepdisjonsbrev til Trygderetten journalpost"
    val TITTEL_JOURNALPOST_2 = "Eksepdisjonsbrev til Trygderetten journalpost 2"
    val JOURNALFOERT_AV_NAVN_1 = "Sak Sakbehandlersen journalfører"
    val JOURNALFOERT_AV_NAVN_2 = "Sak Sakbehandlersen journalfører 2"
    val TEMA = Tema.DAG
    val TEMA_NAVN = TEMA.name
    val JOURNALFOERENDE_ENHET_1 = "1234"
    val JOURNALFOERENDE_ENHET_2 = "4321"

    val DOKUMENT_INFO_ID_HOVEDDOK = "1234567"
    val TITTEL_HOVEDDOK = "Eksepedisjonsbrev til Trygderetten"

    val AVSENDER_MOTTAKER_NAVN_ORIG_JP = "avsenderMottakerNavnOrigJp"
    val DOKUMENT_INFO_ID_VEDLEGG = "7654321"
    val TITTEL_VEDLEGG = "Dokumentasjon til klage"
    val TITTEL_VEDLEGG_UTGAAENDE = "Dokumentasjon til klage, Til $AVSENDER_MOTTAKER_NAVN_ORIG_JP"
    val TITTEL_VEDLEGG_INNGAAENDE = "Dokumentasjon til klage, Fra $AVSENDER_MOTTAKER_NAVN_ORIG_JP"

    val ORIGINAL_JPID_VEDLEGG = "1111111111"

    val DOKUMENT_INFO_ID_VEDLEGG_2 = "9876543"
    val EREG_NAVN = "Bedrift AS"
    val PDL_FORNAVN = "Bjarne"
    val PDL_ETTERNAVN = "Betjent"
    val PDL_SAMMENSATT_NAVN = "Bjarne Betjent"

    val JOURNALFOERT_AV_NAVN_ORIG_JP = "ajournalfoertAvNavnOrigJp"
    val DATO_JOURNALFOERT_ORIG_JP: LocalDateTime? = FIXED_LOCAL_DATE_TIME.minusDays(5)

    val arkivmeldingService = ArkivmeldingService(
        safGraphQlClient = safGraphQlClient,
        applicationName = "test",
        pdlClient = pdlClient,
        eregClient = eregClient,
    )

    @Test
    fun `test Arkivmelding generation with no original journalpost`() {
        val journalpost1 = getJournalpost(
            brukerOrgNummer = null, originalJournalpostIdForVedlegg = null
        )
        every { safGraphQlClient.getJournalpostAsSystembruker(any()) } returns journalpost1
        every { safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(brukerId = any()) } returns listOf(
            journalpost1,
            getJournalpost2(journalposttype = Journalposttype.I),
        )
        every { pdlClient.getPersonInfo(any()) } returns hentPersonResponse

        val arkivmelding = arkivmeldingService.generateArkivmelding(
            journalpostId = JOURNALPOST_ID_1,
            bestillingsId = BESTILLINGS_ID
        )

        assertArkivmelding(arkivmelding = arkivmelding, brukerIsOrganisasjon = false)
        verify(exactly = 1) {
            safGraphQlClient.getJournalpostAsSystembruker(JOURNALPOST_ID_1)
        }
        verify(exactly = 0) {
            safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(any())
        }
        verify(exactly = 1) {
            pdlClient.getPersonInfo(BRUKER_ID_FNR)
        }
        verify(exactly = 0) {
            eregClient.hentNoekkelInformasjonOmOrganisasjon(any())
        }
    }

    @Test
    fun `test Arkivmelding generation with no original journalpost, bruker is organisasjon`() {
        val journalpost1 = getJournalpost(
            brukerOrgNummer = BRUKER_ID_ORGNR, originalJournalpostIdForVedlegg = null
        )
        every { safGraphQlClient.getJournalpostAsSystembruker(any()) } returns journalpost1
        every { safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(brukerId = any()) } returns listOf(
            journalpost1,
            getJournalpost2(journalposttype = Journalposttype.I),
        )
        every { pdlClient.getPersonInfo(any()) } returns hentPersonResponse
        every { eregClient.hentNoekkelInformasjonOmOrganisasjon(any()) } returns hentOrganisasjonResponse

        val arkivmelding = arkivmeldingService.generateArkivmelding(
            journalpostId = JOURNALPOST_ID_1,
            bestillingsId = BESTILLINGS_ID
        )

        assertArkivmelding(arkivmelding = arkivmelding, brukerIsOrganisasjon = true)
        verify(exactly = 1) {
            safGraphQlClient.getJournalpostAsSystembruker(JOURNALPOST_ID_1)
        }
        verify(exactly = 0) {
            safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(any())
        }
        verify(exactly = 0) {
            pdlClient.getPersonInfo(BRUKER_ID_FNR)
        }
        verify(exactly = 1) {
            eregClient.hentNoekkelInformasjonOmOrganisasjon(any())
        }
    }

    @Test
    fun `test Arkivmelding generation with inngående vedlegg with original journalpost, values should be adjusted accordingly`() {
        val journalpost1 = getJournalpost(
            brukerOrgNummer = null, originalJournalpostIdForVedlegg = JOURNALPOST_ID_2
        )
        every { safGraphQlClient.getJournalpostAsSystembruker(any()) } returns journalpost1
        every { safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(brukerId = any()) } returns listOf(
            journalpost1,
            getJournalpost2(journalposttype = Journalposttype.I),
        )
        every { pdlClient.getPersonInfo(any()) } returns hentPersonResponse
        every { eregClient.hentNoekkelInformasjonOmOrganisasjon(any()) } returns hentOrganisasjonResponse

        val arkivmelding = arkivmeldingService.generateArkivmelding(
            journalpostId = JOURNALPOST_ID_1,
            bestillingsId = BESTILLINGS_ID
        )

        assertArkivmelding(
            arkivmelding = arkivmelding,
            brukerIsOrganisasjon = false,
            vedleggJournalpostType = Journalposttype.I
        )
    }

    @Test
    fun `test Arkivmelding generation with utgående vedlegg with original journalpost, values should be adjusted accordingly`() {
        val journalpost1 = getJournalpost(
            brukerOrgNummer = null, originalJournalpostIdForVedlegg = JOURNALPOST_ID_2
        )
        every { safGraphQlClient.getJournalpostAsSystembruker(any()) } returns journalpost1
        every { safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(brukerId = any()) } returns listOf(
            journalpost1,
            getJournalpost2(journalposttype = Journalposttype.U),
        )
        every { pdlClient.getPersonInfo(any()) } returns hentPersonResponse
        every { eregClient.hentNoekkelInformasjonOmOrganisasjon(any()) } returns hentOrganisasjonResponse

        val arkivmelding = arkivmeldingService.generateArkivmelding(
            journalpostId = JOURNALPOST_ID_1,
            bestillingsId = BESTILLINGS_ID
        )

        assertArkivmelding(
            arkivmelding = arkivmelding,
            brukerIsOrganisasjon = false,
            vedleggJournalpostType = Journalposttype.U
        )
    }

    @Test
    fun `Case when dokument does not have variantformat SLADDET (variantformat is ARKIV) and filtype is not PNG or JPEG, should set variantformat to Produksjonsformat`() {
        val journalpost1 = getJournalpost(
            brukerOrgNummer = null, originalJournalpostIdForVedlegg = null,
            dokumentVarianter = listOf(
                Dokumentvariant(
                    variantformat = Variantformat.ARKIV,
                    filtype = Filtype.XLSX,
                ),
                Dokumentvariant(
                    variantformat = Variantformat.PRODUKSJON,
                    filtype = Filtype.PDF,
                )
            ),
        )

        every { safGraphQlClient.getJournalpostAsSystembruker(any()) } returns journalpost1
        every { safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(brukerId = any()) } returns listOf(
            journalpost1,
            getJournalpost2(journalposttype = Journalposttype.U),
        )
        every { pdlClient.getPersonInfo(any()) } returns hentPersonResponse
        every { eregClient.hentNoekkelInformasjonOmOrganisasjon(any()) } returns hentOrganisasjonResponse

        val arkivmelding = arkivmeldingService.generateArkivmelding(
            journalpostId = JOURNALPOST_ID_1,
            bestillingsId = BESTILLINGS_ID
        )

        val dokumentobjektHoveddokument =
            arkivmelding.mappe.first().registrering.first().dokumentbeskrivelse.first().dokumentobjekt.first()
        assertThat(dokumentobjektHoveddokument.variantformat).isEqualTo(PRODUKSJONSFORMAT)
        assertThat(dokumentobjektHoveddokument.referanseDokumentfil).contains(PRODUKSJONSFORMAT)
    }

    @Test
    fun `Case when dokument does not have variantformat SLADDET (variantformat is ARKIV) and filtype is PNG, Should set variantformat to Arkivformat`() {
        val journalpost1 = getJournalpost(
            brukerOrgNummer = null, originalJournalpostIdForVedlegg = null,
            dokumentVarianter = listOf(
                Dokumentvariant(
                    variantformat = Variantformat.ARKIV,
                    filtype = Filtype.PNG,
                ),
                Dokumentvariant(
                    variantformat = Variantformat.PRODUKSJON,
                    filtype = Filtype.PDF,
                )
            ),
        )

        every { safGraphQlClient.getJournalpostAsSystembruker(any()) } returns journalpost1
        every { safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(brukerId = any()) } returns listOf(
            journalpost1,
            getJournalpost2(journalposttype = Journalposttype.U),
        )
        every { pdlClient.getPersonInfo(any()) } returns hentPersonResponse
        every { eregClient.hentNoekkelInformasjonOmOrganisasjon(any()) } returns hentOrganisasjonResponse

        val arkivmelding = arkivmeldingService.generateArkivmelding(
            journalpostId = JOURNALPOST_ID_1,
            bestillingsId = BESTILLINGS_ID
        )

        val dokumentobjektHoveddokument =
            arkivmelding.mappe.first().registrering.first().dokumentbeskrivelse.first().dokumentobjekt.first()
        assertThat(dokumentobjektHoveddokument.variantformat).isEqualTo(ARKIVFORMAT)
        assertThat(dokumentobjektHoveddokument.referanseDokumentfil).contains(ARKIVFORMAT)
    }

    @Test
    fun `Case when dokument does not have variantformat SLADDET (variantformat is ARKIV) and filtype is JPEG, Should set variantformat to Arkivformat`() {
        val journalpost1 = getJournalpost(
            brukerOrgNummer = null, originalJournalpostIdForVedlegg = null,
            dokumentVarianter = listOf(
                Dokumentvariant(
                    variantformat = Variantformat.ARKIV,
                    filtype = Filtype.JPEG,
                ),
                Dokumentvariant(
                    variantformat = Variantformat.PRODUKSJON,
                    filtype = Filtype.PDF,
                )
            ),
        )

        every { safGraphQlClient.getJournalpostAsSystembruker(any()) } returns journalpost1
        every { safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(brukerId = any()) } returns listOf(
            journalpost1,
            getJournalpost2(journalposttype = Journalposttype.U),
        )
        every { pdlClient.getPersonInfo(any()) } returns hentPersonResponse
        every { eregClient.hentNoekkelInformasjonOmOrganisasjon(any()) } returns hentOrganisasjonResponse

        val arkivmelding = arkivmeldingService.generateArkivmelding(
            journalpostId = JOURNALPOST_ID_1,
            bestillingsId = BESTILLINGS_ID
        )

        val dokumentobjektHoveddokument =
            arkivmelding.mappe.first().registrering.first().dokumentbeskrivelse.first().dokumentobjekt.first()
        assertThat(dokumentobjektHoveddokument.variantformat).isEqualTo(ARKIVFORMAT)
        assertThat(dokumentobjektHoveddokument.referanseDokumentfil).contains(ARKIVFORMAT)
    }

    @Test
    fun `test Arkivmelding generation where vedlegg has no dokumentstatus`() {
        val journalpost1 = getJournalpost(
            brukerOrgNummer = null, originalJournalpostIdForVedlegg = JOURNALPOST_ID_2, vedleggStatus = null
        )
        every { safGraphQlClient.getJournalpostAsSystembruker(any()) } returns journalpost1
        every { safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(brukerId = any()) } returns listOf(
            journalpost1,
            getJournalpost2(journalposttype = Journalposttype.I),
        )
        every { pdlClient.getPersonInfo(any()) } returns hentPersonResponse

        val arkivmelding = arkivmeldingService.generateArkivmelding(
            journalpostId = JOURNALPOST_ID_1,
            bestillingsId = BESTILLINGS_ID
        )

        assertArkivmelding(
            arkivmelding = arkivmelding,
            brukerIsOrganisasjon = false,
            vedleggJournalpostType = Journalposttype.I
        )
    }

    @Test
    fun `Case when vedlegg does not have dokumentstatus FERDIGSTILT, that vedlegg should not be mapped`() {
        val journalpost1 = getJournalpost(
            brukerOrgNummer = null,
            originalJournalpostIdForVedlegg = null,
            vedleggStatus = Dokumentstatus.UNDER_REDIGERING
        )
        every { safGraphQlClient.getJournalpostAsSystembruker(any()) } returns journalpost1
        every { safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(brukerId = any()) } returns listOf(
            journalpost1,
            getJournalpost2(journalposttype = Journalposttype.I),
        )
        every { pdlClient.getPersonInfo(any()) } returns hentPersonResponse

        val arkivmelding = arkivmeldingService.generateArkivmelding(
            journalpostId = JOURNALPOST_ID_1,
            bestillingsId = BESTILLINGS_ID
        )

        assertThat(arkivmelding.antallFiler).isEqualTo(1)
        assertThat(arkivmelding.mappe.first().registrering.first().dokumentbeskrivelse.size).isEqualTo(1)
    }

    @Test
    fun `Case for satt originalJournalPostId men ukjent journalfører`() {
        val journalpost1 = getJournalpost(
            brukerOrgNummer = null, originalJournalpostIdForVedlegg = JOURNALPOST_ID_2
        )
        every { safGraphQlClient.getJournalpostAsSystembruker(any()) } returns journalpost1
        every { safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(brukerId = any()) } returns listOf(
            journalpost1,
            getJournalpost2(journalposttype = Journalposttype.I, journalfoertAvNavn = null, opprettetAvNavn = null),
        )
        every { pdlClient.getPersonInfo(any()) } returns hentPersonResponse

        val arkivmelding = arkivmeldingService.generateArkivmelding(
            journalpostId = JOURNALPOST_ID_1,
            bestillingsId = BESTILLINGS_ID,
        )

        assertThat(arkivmelding.mappe.first().registrering.first().dokumentbeskrivelse.last().opprettetAv).isEqualTo(
            UKJENT
        )
    }

    @Test
    fun `Når sak mangler opprettetDato, sett opprettetDato fra eldste vedlegg sortert etter journalpostens dokumentbeskrivelse opprettetDato`() {
        //Trenger følgende:
        // Journalpost med 2 vedlegg.
        // Selve journalposten har sak uten opprettetDato.

        val journalpost1 = getJournalpostForDateTest()
        val treDagerSiden = FIXED_LOCAL_DATE_TIME.minusDays(3)
        val femDagerSiden = FIXED_LOCAL_DATE_TIME.minusDays(5)
        every { safGraphQlClient.getJournalpostAsSystembruker(any()) } returns journalpost1
        every { safGraphQlClient.getDokumentoversiktBrukerAsSystembruker(brukerId = any()) } returns listOf(
            journalpost1,
            getJournalpost2(
                journalposttype = Journalposttype.I,
                journalfoertAvNavn = null,
                opprettetAvNavn = null,
                journalpostId = JOURNALPOST_ID_2,
                dateJournalfoert = femDagerSiden,
                dokumentInfoId = DOKUMENT_INFO_ID_VEDLEGG,
            ),
            getJournalpost2(
                journalposttype = Journalposttype.I,
                journalfoertAvNavn = null,
                opprettetAvNavn = null,
                journalpostId = JOURNALPOST_ID_3,
                dateJournalfoert = treDagerSiden,
                dokumentInfoId = DOKUMENT_INFO_ID_VEDLEGG_2,
            ),
        )
        every { pdlClient.getPersonInfo(any()) } returns hentPersonResponse

        val arkivmelding = arkivmeldingService.generateArkivmelding(
            journalpostId = JOURNALPOST_ID_1,
            bestillingsId = BESTILLINGS_ID,
        )

        assertThat(arkivmelding.mappe.first().opprettetDato).isEqualTo(convertLocalDateTimeToXmlGregorianCalendar(femDagerSiden))
    }

    private fun assertArkivmelding(
        arkivmelding: Arkivmelding, brukerIsOrganisasjon: Boolean,
        vedleggJournalpostType: Journalposttype? = null
    ) {
        assertThat(arkivmelding.meldingId).isEqualTo(BESTILLINGS_ID)
        assertThat(arkivmelding.tidspunkt).isNotNull
        assertThat(arkivmelding.antallFiler).isEqualTo(2)
        assertThat(arkivmelding.mappe.size).isEqualTo(1)
        assertSaksmappe(
            saksmappe = arkivmelding.mappe.first() as Saksmappe,
            brukerIsOrganisasjon = brukerIsOrganisasjon,
            vedleggJournalpostType = vedleggJournalpostType
        )
    }

    private fun assertSaksmappe(
        saksmappe: Saksmappe, brukerIsOrganisasjon: Boolean,
        vedleggJournalpostType: Journalposttype? = null
    ) {
        assertThat(saksmappe.tittel).isEqualTo(KodeverkTema.valueOf(TEMA.name).beskrivelse)
        assertThat(saksmappe.opprettetDato).isEqualTo(convertLocalDateTimeToXmlGregorianCalendar(DATO_OPPRETTET_SAK_1))
        assertThat(saksmappe.opprettetAv).isEqualTo(OPPRETTET_AV_NAVN_1)
        val navMappe = extractNavMappe(saksmappe.virksomhetsspesifikkeMetadata)
        assertThat(navMappe.saksnummer).isEqualTo(ARKIV_SAKNUMMER_1)
        assertSakspart(partList = saksmappe.part, brukerIsOrganisasjon = brukerIsOrganisasjon)
        assertThat(saksmappe.saksdato).isEqualTo(convertLocalDateTimeToXmlGregorianCalendar(DATO_OPPRETTET_SAK_1))
        assertThat(saksmappe.administrativEnhet).isEqualTo(NAV_KLAGEINSTANS_NAVN)
        assertThat(saksmappe.saksansvarlig).isEqualTo(OPPRETTET_AV_NAVN_1)
        assertThat(saksmappe.journalenhet).isEqualTo(JOURNALFOERENDE_ENHET_1)
        assertThat(saksmappe.saksstatus).isEqualTo(UNDER_BEHANDLING)
        assertThat(saksmappe.registrering.size).isEqualTo(1)
        assertJournalpost(
            journalpost = saksmappe.registrering.first() as ArkivJournalpost,
            vedleggJournalpostType = vedleggJournalpostType
        )
    }

    private fun assertSakspart(partList: MutableList<Part>, brukerIsOrganisasjon: Boolean) {
        assertThat(partList.size).isEqualTo(2)
        val sakspartAMP: Part = partList.first()
        assertThat(sakspartAMP.partID).isNull()
        assertThat(sakspartAMP.partNavn).isEqualTo(NAV_KLAGEINSTANS_NAVN)
        assertThat(sakspartAMP.partRolle).isEqualTo(SAKSPART_ROLLE_AMP)
        assertThat(sakspartAMP.kontaktperson).isEqualTo(OPPRETTET_AV_NAVN_1)
        assertThat(sakspartAMP.organisasjonsnummer?.organisasjonsnummer).isEqualTo(NAV_KLAGEINSTANS_ORGNR)
        assertThat(sakspartAMP.foedselsnummer).isNull()

        val sakspartDAP: Part = partList.last()
        if (brukerIsOrganisasjon) {
            assertThat(sakspartDAP.foedselsnummer).isNull()
            assertThat(sakspartDAP.organisasjonsnummer.organisasjonsnummer).isEqualTo(BRUKER_ID_ORGNR)
        } else {
            assertThat(sakspartDAP.foedselsnummer.foedselsnummer).isEqualTo(BRUKER_ID_FNR)
            assertThat(sakspartDAP.organisasjonsnummer).isNull()
        }
        assertThat(sakspartDAP.partNavn).isEqualTo(if (brukerIsOrganisasjon) EREG_NAVN else PDL_SAMMENSATT_NAVN)
        assertThat(sakspartDAP.partRolle).isEqualTo(SAKSPART_ROLLE_DAP)
        assertThat(sakspartDAP.kontaktperson).isNull()
    }

    private fun assertJournalpost(
        journalpost: ArkivJournalpost,
        vedleggJournalpostType: Journalposttype? = null
    ) {
        assertThat(journalpost.opprettetDato).isEqualTo(
            convertLocalDateTimeToXmlGregorianCalendar(
                DATO_OPPRETTET_JOURNALPOST_1
            )
        )
        assertThat(journalpost.opprettetAv).isEqualTo(OPPRETTET_AV_NAVN_1)
        assertThat(journalpost.tittel).isEqualTo(TITTEL_JOURNALPOST_1)
        assertKorrespondansepart(journalpost.korrespondansepart)
        assertThat(journalpost.journalposttype).isEqualTo(UTGAAENDE_DOKUMENT)
        assertThat(journalpost.journalstatus).isEqualTo(EKSPEDERT)
        assertThat(journalpost.journaldato).isEqualTo(convertLocalDateTimeToXmlGregorianCalendar(DATO_JOURNALFOERT_1))
        assertDokumentbeskrivelse(
            dokumentbeskrivelse = journalpost.dokumentbeskrivelse,
            vedleggJournalpostType = vedleggJournalpostType
        )
    }


    private fun assertDokumentbeskrivelse(
        dokumentbeskrivelse: MutableList<Dokumentbeskrivelse>,
        vedleggJournalpostType: Journalposttype? = null
    ) {
        assertThat(dokumentbeskrivelse.size).isEqualTo(2)
        val dokumentbeskrivelseHoveddokument = dokumentbeskrivelse.first()
        val dokumentbeskrivelseVedlegg = dokumentbeskrivelse.last()
        assertDokumentbeskrivelseHoveddokument(dokumentbeskrivelseHoveddokument)
        assertDokumentbeskrivelseVedlegg(dokumentbeskrivelseVedlegg, vedleggJournalpostType = vedleggJournalpostType)
    }

    private fun assertDokumentbeskrivelseHoveddokument(dokumentbeskrivelseHoveddokument: Dokumentbeskrivelse) {
        assertThat(dokumentbeskrivelseHoveddokument.tilknyttetRegistreringSom).isEqualTo(HOVEDDOKUMENT)
        assertThat(dokumentbeskrivelseHoveddokument.dokumentnummer).isEqualTo(BigInteger.ONE)
        assertThat(dokumentbeskrivelseHoveddokument.tittel).isEqualTo(TITTEL_HOVEDDOK)
        assertThat(dokumentbeskrivelseHoveddokument.opprettetDato).isEqualTo(
            convertLocalDateTimeToXmlGregorianCalendar(
                DATO_JOURNALFOERT_1
            )
        )
        assertThat(dokumentbeskrivelseHoveddokument.dokumentobjekt.size).isEqualTo(1)
        assertDokumentobjektHoveddokument(dokumentbeskrivelseHoveddokument.dokumentobjekt.first())
        assertThat(dokumentbeskrivelseHoveddokument.dokumenttype).isEqualTo(DOKUMENTASJON)
        assertThat(dokumentbeskrivelseHoveddokument.dokumentstatus).isEqualTo(DOKUMENTET_ER_FERDIGSTILT)
        assertThat(dokumentbeskrivelseHoveddokument.tilknyttetDato).isNotNull()
        assertThat(dokumentbeskrivelseHoveddokument.opprettetAv).isEqualTo(JOURNALFOERT_AV_NAVN_1)
        assertThat(dokumentbeskrivelseHoveddokument.tilknyttetAv).isEqualTo(JOURNALFOERT_AV_NAVN_1)
    }

    private fun assertDokumentbeskrivelseVedlegg(
        dokumentbeskrivelseVedlegg: Dokumentbeskrivelse,
        vedleggJournalpostType: Journalposttype? = null
    ) {
        assertThat(dokumentbeskrivelseVedlegg.tilknyttetRegistreringSom).isEqualTo(VEDLEGG)
        assertThat(dokumentbeskrivelseVedlegg.dokumentnummer).isEqualTo(BigInteger.TWO)
        assertThat(dokumentbeskrivelseVedlegg.tittel).isEqualTo(if (vedleggJournalpostType == Journalposttype.U) TITTEL_VEDLEGG_UTGAAENDE else if (vedleggJournalpostType == Journalposttype.I) TITTEL_VEDLEGG_INNGAAENDE else TITTEL_VEDLEGG)
        assertThat(dokumentbeskrivelseVedlegg.opprettetDato).isEqualTo(
            convertLocalDateTimeToXmlGregorianCalendar(
                if (vedleggJournalpostType != null) DATO_JOURNALFOERT_2 else DATO_JOURNALFOERT_1
            )
        )
        assertThat(dokumentbeskrivelseVedlegg.dokumentobjekt.size).isEqualTo(1)
        assertDokumentobjektVedlegg(
            dokumentobjektVedlegg = dokumentbeskrivelseVedlegg.dokumentobjekt.first(),
            vedleggIsFromDifferentJournalpost = vedleggJournalpostType != null
        )
        assertThat(dokumentbeskrivelseVedlegg.dokumenttype).isEqualTo(DOKUMENTASJON)
        assertThat(dokumentbeskrivelseVedlegg.dokumentstatus).isEqualTo(DOKUMENTET_ER_FERDIGSTILT)
        assertThat(dokumentbeskrivelseVedlegg.tilknyttetDato).isNotNull()
        assertThat(dokumentbeskrivelseVedlegg.opprettetAv).isEqualTo(if (vedleggJournalpostType != null) OPPRETTET_AV_NAVN_2 else JOURNALFOERT_AV_NAVN_1)
        assertThat(dokumentbeskrivelseVedlegg.tilknyttetAv).isEqualTo(JOURNALFOERT_AV_NAVN_1)
    }

    private fun assertDokumentobjektHoveddokument(dokumentobjektHoveddokument: Dokumentobjekt) {
        assertThat(dokumentobjektHoveddokument.versjonsnummer).isEqualTo(BigInteger.ONE)
        assertThat(dokumentobjektHoveddokument.variantformat).isEqualTo(DOKUMENT_HVOR_DELER_AV_INNHOLDET_ER_SKJERMET)
        assertThat(dokumentobjektHoveddokument.format).isEqualTo(Filtype.PDF.name.lowercase())
        assertThat(dokumentobjektHoveddokument.opprettetDato).isEqualTo(
            convertLocalDateTimeToXmlGregorianCalendar(
                DATO_JOURNALFOERT_1
            )
        )
        assertThat(dokumentobjektHoveddokument.opprettetAv).isEqualTo(JOURNALFOERT_AV_NAVN_1)
        assertThat(dokumentobjektHoveddokument.referanseDokumentfil).isEqualTo(
            String.format(
                REFERANSE_DOKUMENTFIL_FORMAT,
                JOURNALPOST_ID_1,
                DOKUMENT_INFO_ID_HOVEDDOK,
                DOKUMENT_HVOR_DELER_AV_INNHOLDET_ER_SKJERMET,
                Filtype.PDF.name.lowercase()
            )
        )
    }

    private fun assertDokumentobjektVedlegg(
        dokumentobjektVedlegg: Dokumentobjekt,
        vedleggIsFromDifferentJournalpost: Boolean = false
    ) {
        assertThat(dokumentobjektVedlegg.versjonsnummer).isEqualTo(BigInteger.ONE)
        assertThat(dokumentobjektVedlegg.variantformat).isEqualTo(ARKIVFORMAT)
        assertThat(dokumentobjektVedlegg.format).isEqualTo(Filtype.JPEG.name.lowercase())
        assertThat(dokumentobjektVedlegg.opprettetDato).isEqualTo(
            convertLocalDateTimeToXmlGregorianCalendar(
                if (vedleggIsFromDifferentJournalpost) DATO_JOURNALFOERT_2 else
                    DATO_JOURNALFOERT_1
            )
        )
        assertThat(dokumentobjektVedlegg.opprettetAv).isEqualTo(if (vedleggIsFromDifferentJournalpost) OPPRETTET_AV_NAVN_2 else JOURNALFOERT_AV_NAVN_1)
        assertThat(dokumentobjektVedlegg.referanseDokumentfil).isEqualTo(
            String.format(
                REFERANSE_DOKUMENTFIL_FORMAT,
                JOURNALPOST_ID_1,
                DOKUMENT_INFO_ID_VEDLEGG,
                ARKIVFORMAT,
                Filtype.JPEG.name.lowercase()
            )
        )
    }

    private fun assertKorrespondansepart(korrespondansepartList: List<Korrespondansepart>) {
        assertThat(korrespondansepartList.size).isEqualTo(2)
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

    val hentPersonResponse = HentPersonResponse(
        data = PdlPersonDataWrapper(
            hentPerson = PdlPerson(
                folkeregisteridentifikator = listOf(
                    PdlPerson.Folkeregisteridentifikator(
                        identifikasjonsnummer = BRUKER_ID_FNR
                    )
                ),
                adressebeskyttelse = listOf(),
                navn = listOf(
                    PdlPerson.Navn(
                        fornavn = PDL_FORNAVN,
                        mellomnavn = null,
                        etternavn = PDL_ETTERNAVN,
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

    val hentOrganisasjonResponse = NoekkelInfoOmOrganisasjon(
        navn = NoekkelInfoOmOrganisasjon.Navn(
            sammensattnavn = EREG_NAVN
        ),
        organisasjonsnummer = BRUKER_ID_ORGNR,
        enhetstype = "",
        opphoersdato = null,
        adresse = null
    )


    fun getJournalpost(
        brukerOrgNummer: String?,
        originalJournalpostIdForVedlegg: String?,
        dokumentVarianter: List<Dokumentvariant>? = null,
        vedleggStatus: Dokumentstatus? = Dokumentstatus.FERDIGSTILT,
    ): Journalpost {
        val bruker = if (brukerOrgNummer != null) {
            Bruker(
                id = brukerOrgNummer,
                type = BRUKER_TYPE_ORGNR
            )
        } else {
            Bruker(
                id = BRUKER_ID_FNR,
                type = BRUKER_TYPE_FNR
            )
        }
        return Journalpost(
            journalpostId = JOURNALPOST_ID_1,
            journalposttype = Journalposttype.U,
            journalstatus = Journalstatus.FERDIGSTILT,
            tema = TEMA,
            sak = Sak(
                datoOpprettet = DATO_OPPRETTET_SAK_1,
                fagsakId = ARKIV_SAKNUMMER_1,
            ),
            bruker = bruker,
            avsenderMottaker = null,
            opprettetAvNavn = OPPRETTET_AV_NAVN_1,
            datoOpprettet = DATO_OPPRETTET_JOURNALPOST_1,
            dokumenter = listOf(
                DokumentInfo(
                    dokumentInfoId = DOKUMENT_INFO_ID_HOVEDDOK,
                    tittel = TITTEL_HOVEDDOK,
                    brevkode = null,
                    skjerming = null,
                    dokumentvarianter = dokumentVarianter ?: listOf(
                        Dokumentvariant(
                            variantformat = Variantformat.ARKIV,
                            filtype = Filtype.PNG,
                        ),
                        Dokumentvariant(
                            variantformat = Variantformat.SLADDET,
                            filtype = Filtype.PDF,
                        )
                    ),
                    datoFerdigstilt = LocalDateTime.now().minusMonths(2),
                    originalJournalpostId = null,
                    dokumentstatus = Dokumentstatus.FERDIGSTILT,
                ),
                DokumentInfo(
                    dokumentInfoId = DOKUMENT_INFO_ID_VEDLEGG,
                    tittel = TITTEL_VEDLEGG,
                    brevkode = null,
                    skjerming = null,
                    dokumentvarianter = listOf(
                        Dokumentvariant(
                            variantformat = Variantformat.ARKIV,
                            filtype = Filtype.JPEG,
                        ),
                        Dokumentvariant(
                            variantformat = Variantformat.PRODUKSJON,
                            filtype = Filtype.PDF,
                        )
                    ),
                    datoFerdigstilt = LocalDateTime.now().minusMonths(3),
                    originalJournalpostId = originalJournalpostIdForVedlegg,
                    dokumentstatus = vedleggStatus,
                )
            ),
            relevanteDatoer = listOf(
                RelevantDato(
                    dato = DATO_JOURNALFOERT_1, datotype = Datotype.DATO_JOURNALFOERT,

                    )
            ),
            journalforendeEnhet = JOURNALFOERENDE_ENHET_1,
            tittel = TITTEL_JOURNALPOST_1,
            journalfortAvNavn = JOURNALFOERT_AV_NAVN_1,
            skjerming = null
        )
    }

    fun getJournalpost2(
        journalposttype: Journalposttype = Journalposttype.I,
        journalfoertAvNavn: String? = JOURNALFOERT_AV_NAVN_2,
        opprettetAvNavn: String? = OPPRETTET_AV_NAVN_2,
        journalpostId: String = JOURNALPOST_ID_2,
        dateJournalfoert: LocalDateTime = DATO_JOURNALFOERT_2,
        dokumentInfoId: String = DOKUMENT_INFO_ID_VEDLEGG,
    ): Journalpost {
        return Journalpost(
            journalpostId = journalpostId,
            journalposttype = journalposttype,
            journalstatus = Journalstatus.EKSPEDERT,
            tema = TEMA,
            sak = Sak(
                datoOpprettet = DATO_OPPRETTET_SAK_2,
                fagsakId = ARKIV_SAKNUMMER_2,
            ),
            bruker = Bruker(
                id = BRUKER_ID_FNR, type = BRUKER_TYPE_FNR
            ),
            avsenderMottaker = AvsenderMottaker(
                navn = AVSENDER_MOTTAKER_NAVN_ORIG_JP,
                erLikBruker = true,
                id = null,
                type = null,
                land = null
            ),
            opprettetAvNavn = opprettetAvNavn,
            skjerming = null,
            datoOpprettet = DATO_OPPRETTET_JOURNALPOST_2,
            dokumenter = listOf(
                DokumentInfo(
                    dokumentInfoId = dokumentInfoId,
                    tittel = TITTEL_VEDLEGG,
                    brevkode = null,
                    skjerming = null,
                    dokumentvarianter = listOf(
                        Dokumentvariant(
                            variantformat = Variantformat.SLADDET,
                            filtype = Filtype.PDF,
                        )
                    ),
                    datoFerdigstilt = LocalDateTime.now().minusMonths(3),
                    originalJournalpostId = null,
                    dokumentstatus = Dokumentstatus.FERDIGSTILT,
                )
            ),
            relevanteDatoer = listOf(
                RelevantDato(
                    dato = dateJournalfoert, datotype = Datotype.DATO_JOURNALFOERT

                )
            ),
            journalforendeEnhet = JOURNALFOERENDE_ENHET_2,
            tittel = TITTEL_JOURNALPOST_2,
            journalfortAvNavn = journalfoertAvNavn
        )
    }

    fun getJournalpostForDateTest(): Journalpost {
        val bruker =
            Bruker(
                id = BRUKER_ID_FNR,
                type = BRUKER_TYPE_FNR
            )

        return Journalpost(
            journalpostId = JOURNALPOST_ID_1,
            journalposttype = Journalposttype.U,
            journalstatus = Journalstatus.FERDIGSTILT,
            tema = TEMA,
            sak = Sak(
                datoOpprettet = null,
                fagsakId = ARKIV_SAKNUMMER_1,
            ),
            bruker = bruker,
            avsenderMottaker = null,
            opprettetAvNavn = OPPRETTET_AV_NAVN_1,
            datoOpprettet = DATO_OPPRETTET_JOURNALPOST_1,
            dokumenter = listOf(
                DokumentInfo(
                    dokumentInfoId = DOKUMENT_INFO_ID_HOVEDDOK,
                    tittel = TITTEL_HOVEDDOK,
                    brevkode = null,
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
                    datoFerdigstilt = LocalDateTime.now().minusMonths(2),
                    originalJournalpostId = null,
                    dokumentstatus = Dokumentstatus.FERDIGSTILT,
                ),
                DokumentInfo(
                    dokumentInfoId = DOKUMENT_INFO_ID_VEDLEGG,
                    tittel = TITTEL_VEDLEGG,
                    brevkode = null,
                    skjerming = null,
                    dokumentvarianter = listOf(
                        Dokumentvariant(
                            variantformat = Variantformat.ARKIV,
                            filtype = Filtype.JPEG,
                        ),
                        Dokumentvariant(
                            variantformat = Variantformat.PRODUKSJON,
                            filtype = Filtype.PDF,
                        )
                    ),
                    datoFerdigstilt = LocalDateTime.now().minusMonths(3),
                    originalJournalpostId = JOURNALPOST_ID_2,
                    dokumentstatus = Dokumentstatus.FERDIGSTILT,
                ),
                DokumentInfo(
                    dokumentInfoId = DOKUMENT_INFO_ID_VEDLEGG_2,
                    tittel = TITTEL_VEDLEGG,
                    brevkode = null,
                    skjerming = null,
                    dokumentvarianter = listOf(
                        Dokumentvariant(
                            variantformat = Variantformat.ARKIV,
                            filtype = Filtype.JPEG,
                        ),
                        Dokumentvariant(
                            variantformat = Variantformat.PRODUKSJON,
                            filtype = Filtype.PDF,
                        )
                    ),
                    datoFerdigstilt = LocalDateTime.now().minusMonths(3),
                    originalJournalpostId = JOURNALPOST_ID_3,
                    dokumentstatus = Dokumentstatus.FERDIGSTILT,
                )
            ),
            relevanteDatoer = listOf(
                RelevantDato(
                    dato = DATO_JOURNALFOERT_1, datotype = Datotype.DATO_JOURNALFOERT,
                )
            ),
            journalforendeEnhet = JOURNALFOERENDE_ENHET_1,
            tittel = TITTEL_JOURNALPOST_1,
            journalfortAvNavn = JOURNALFOERT_AV_NAVN_1,
            skjerming = null
        )
    }
}