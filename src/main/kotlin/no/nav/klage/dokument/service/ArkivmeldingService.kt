package no.nav.klage.dokument.service

import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.JAXBElement
import jakarta.xml.bind.Marshaller
import no.nav.klage.dokument.clients.pdl.graphql.PdlClient
import no.nav.klage.dokument.clients.pdl.graphql.PdlPerson
import no.nav.klage.dokument.clients.saf.graphql.DokumentInfo
import no.nav.klage.dokument.clients.saf.graphql.Journalpost
import no.nav.klage.dokument.clients.saf.graphql.SafGraphQlClient
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.kodeverk.Tema
import no.nav.klage.trygderetten.xsd.arkivmelding.*
import no.nav.klage.trygderetten.xsd.navmetadata.NavMappe
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.StringWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import java.util.*
import javax.xml.datatype.DatatypeConfigurationException
import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar
import javax.xml.namespace.QName


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
        const val UNDER_BEHANDLING = "Under behandling"
        const val MOTTAKER = "Mottaker"
        const val AVSENDER = "Avsender"
        const val ARKIVMELDING_NAMESPACE = "http://www.arkivverket.no/standarder/noark5/arkivmelding"
    }

    fun generateArkivmelding(journalpostId: String, avsenderMottakerDistribusjonId: UUID): String? {
        val journalpost = getJournalpostAsSaksbehandler(journalpostId = journalpostId)

        val personInfo = pdlClient.getPersonInfo(ident = journalpost.bruker.id)

        val sakOpprettetDato = convertLocalDateTimeToXmlGregorianCalendar(
            journalpost.sak?.datoOpprettet ?: getOldestDateFromDokumenter(journalpost.dokumenter ?: emptyList())
        )

        val arkivmelding = Arkivmelding()
        arkivmelding.system = applicationName
        arkivmelding.meldingId = avsenderMottakerDistribusjonId.toString()
        arkivmelding.tidspunkt = getNow()
        arkivmelding.antallFiler = journalpost.dokumenter?.size ?: throw RuntimeException("No files in journalpost")

        arkivmelding.mappe.add(Saksmappe().apply {
            tittel = Tema.valueOf(journalpost.tema!!.name).beskrivelse
            opprettetDato = sakOpprettetDato
            opprettetAv = journalpost.opprettetAvNavn
            virksomhetsspesifikkeMetadata = getNavMappe(journalpost.sak?.fagsakId)
            part.add(Part().apply {
                partNavn = NAV_KLAGEINSTANS
                partRolle = SAKSPART_ROLLE_AMP
                organisasjonsnummer = EnhetsidentifikatorType().apply { organisasjonsnummer = NAV_KLAGEINSTANS_ORGNR }
                kontaktperson = journalpost.opprettetAvNavn
            }
            )
            part.add(Part().apply {
                partNavn = getSammensattNavn(personInfo.data?.hentPerson?.navn?.firstOrNull())
                partRolle = SAKSPART_ROLLE_DAP
                foedselsnummer = FoedselsnummerType().apply {
                    foedselsnummer = personInfo.data?.hentPerson?.folkeregisteridentifikator?.identifikasjonsnummer
                        ?: throw RuntimeException("Foedselsnummer not found")
                }
                kontaktperson = journalpost.opprettetAvNavn
            }
            )

            saksdato = sakOpprettetDato
            administrativEnhet = NAV_KLAGEINSTANS
            saksansvarlig = journalpost.opprettetAvNavn
            journalenhet = journalpost.journalforendeEnhet
            saksstatus = UNDER_BEHANDLING
//            registrering.add(Registrering().apply {
//                opprettetDato = convertLocalDateTimeToXmlGregorianCalendar(journalpost.datoOpprettet)
//                opprettetAv = journalpost.opprettetAvNavn
//                tittel = journalpost.tittel
//                //mottaker
//                korrespondansepart.add(Korrespondansepart().apply {
//                    korrespondanseparttype = MOTTAKER
//                    korrespondansepartNavn = TRYGDERETTEN
//                    organisasjonsnummer = EnhetsidentifikatorType().apply {
//                        organisasjonsnummer = TRYGDERETTEN_ORGNR
//                    }
//                }
//                )
//                //avsender
//                korrespondansepart.add(Korrespondansepart().apply {
//
//                }
//                )
//
//            }
//            )
        }
        )

        val jaxbElement: JAXBElement<Arkivmelding> = no.nav.klage.trygderetten.xsd.arkivmelding.ObjectFactory().createArkivmelding(arkivmelding)
        val jaxbContext = JAXBContext.newInstance(Arkivmelding::class.java, NavMappe::class.java)

        val marshaller: Marshaller = jaxbContext.createMarshaller()
        val sw = StringWriter()
        marshaller.marshal(jaxbElement, sw)

        return sw.toString()
    }

    private fun getNavMappe(fagsakId: String?): JAXBElement<*> {
        val navMappe = NavMappe().apply {
            saksnummer = fagsakId
        }

        val jaxbElement: JAXBElement<NavMappe> = no.nav.klage.trygderetten.xsd.navmetadata.ObjectFactory().createNavMappe(navMappe)

        return JAXBElement(
            QName(ARKIVMELDING_NAMESPACE, "virksomhetsspesifikkeMetadata"),
            JAXBElement::class.java,
            jaxbElement
        )
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