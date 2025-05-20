package no.nav.klage.dokument.service

import no.nav.klage.dokument.clients.pdl.graphql.PdlClient
import no.nav.klage.dokument.clients.pdl.graphql.PdlPerson
import no.nav.klage.dokument.clients.saf.graphql.DokumentInfo
import no.nav.klage.dokument.clients.saf.graphql.Journalpost
import no.nav.klage.dokument.clients.saf.graphql.SafGraphQlClient
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.gradle.plugin.xsd2java.xsd.Arkivmelding
import no.nav.klage.gradle.plugin.xsd2java.xsd.EnhetsidentifikatorType
import no.nav.klage.gradle.plugin.xsd2java.xsd.FoedselsnummerType
import no.nav.klage.gradle.plugin.xsd2java.xsd.Mappe
import no.nav.klage.gradle.plugin.xsd2java.xsd.NavMappe
import no.nav.klage.gradle.plugin.xsd2java.xsd.Part
import no.nav.klage.kodeverk.Tema
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import java.util.*
import javax.xml.datatype.DatatypeConfigurationException
import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar


@Service
class ArkivmeldingService(
    private val safGraphQlClient: SafGraphQlClient,
    @Value("\${spring.application.name}")
    private val applicationName: String,
    private val pdlClient: PdlClient,
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        const val NAV_KLAGEINSTANS = "NAV Klageinstans"
        const val TRYGDERETTEN = "TRYGDERETTEN"
        const val SAKSPART_ROLLE_DAP = "DAP"
        const val SAKSPART_ROLLE_AMP = "AMP"
        const val TRYGDERETTEN_ORGNR = "974761084"
        const val NAV_KLAGEINSTANS_ORGNR = "991078045"
    }

    fun generateArkivmelding(journalpostId: String, avsenderMottakerDistribusjonId: UUID): String? {
        val journalpost = getJournalpostAsSaksbehandler(journalpostId = journalpostId)
        val arkivmelding = Arkivmelding()
        arkivmelding.system = applicationName
        arkivmelding.meldingId = avsenderMottakerDistribusjonId.toString()
        arkivmelding.tidspunkt = getNow()
        arkivmelding.antallFiler = journalpost.dokumenter?.size ?: throw RuntimeException("No files in journalpost")
        arkivmelding.mappe.add(Mappe().apply {
            tittel = Tema.valueOf(journalpost.tema!!.name).beskrivelse
            opprettetDato = convertLocalDateTimeToXmlGregorianCalendar(
                journalpost.sak?.datoOpprettet ?: getOldestDateFromDokumenter(journalpost.dokumenter)
            )
            opprettetAv = journalpost.opprettetAvNavn
            virksomhetsspesifikkeMetadata = NavMappe().apply {
                saksnummer = journalpost.sak?.fagsakId //Arkivsaksnummer?
            }
            part.add(Part().apply {
                partNavn = NAV_KLAGEINSTANS
                partRolle = SAKSPART_ROLLE_AMP
                organisasjonsnummer = EnhetsidentifikatorType().apply { organisasjonsnummer = NAV_KLAGEINSTANS_ORGNR }
                kontaktperson = journalpost.opprettetAvNavn
            }
            )
            part.add(Part().apply {
                partNavn = getFulltNavnFraPdl(journalpost.bruker.id)
                partRolle = SAKSPART_ROLLE_DAP
                foedselsnummer = //funksjon for å sjekke dersom aktørid FoedselsnummerType().apply { foedselsnummer = journalpost.bruker.id }
                kontaktperson = journalpost.opprettetAvNavn
            }
            )
        }
        )


        return arkivmelding.toString()
    }

    private fun getFulltNavnFraPdl(id: String): String {
        val personInfo = pdlClient.getPersonInfo(ident = id)
        return getSammensattNavn(personInfo.data?.hentPerson?.navn?.firstOrNull())
            ?: throw RuntimeException("Fant ikke navn i PDL for id=$id")
    }

    private fun getSammensattNavn(navn: PdlPerson.Navn?): String? {
        val mellomnavn = navn?.mellomnavn?.let { " ${it.trim()}" } ?: ""
        return navn?.let { "${it.fornavn}${mellomnavn} ${it.etternavn}" }
    }

    private fun getOldestDateFromDokumenter(dokumenter: List<DokumentInfo>): LocalDateTime {
        val now = LocalDateTime.now()
        return dokumenter.minByOrNull { it.datoFerdigstilt ?: now }?.datoFerdigstilt
            ?: throw RuntimeException("No dokumenter in journalpost")
    }

    fun getJournalpostAsSaksbehandler(
        journalpostId: String,
    ): Journalpost {
        return runWithTimingAndLogging({
            safGraphQlClient.getJournalpostAsSystembruker(journalpostId = journalpostId)
        }, this::getJournalpostAsSaksbehandler.name)
    }

    fun <T> runWithTimingAndLogging(block: () -> T, method: String): T {
        val start = System.currentTimeMillis()
        try {
            return block.invoke()
        } finally {
            val end = System.currentTimeMillis()
            logger.debug("Time it took to call saf using $method: ${end - start} millis")
        }
    }

    fun getNow(): XMLGregorianCalendar? {
        val now: XMLGregorianCalendar?
        try {
            now = DatatypeFactory.newInstance().newXMLGregorianCalendar(GregorianCalendar())
        } catch (e: DatatypeConfigurationException) {
            throw RuntimeException("Kunne ikke hente dagens dato", e)
        }
        return now
    }

    fun convertLocalDateTimeToXmlGregorianCalendar(localDateTime: LocalDateTime): XMLGregorianCalendar? {
        try {
            return DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(localDateTime.format(ISO_LOCAL_DATE_TIME))
        } catch (e: DatatypeConfigurationException) {
            throw RuntimeException(
                "Kunne ikke konvertere fra localDateTime til XmlGregorianCalendar. Forsøkte å konvertere localDateTime=$localDateTime",
                e
            )
        }
    }
}