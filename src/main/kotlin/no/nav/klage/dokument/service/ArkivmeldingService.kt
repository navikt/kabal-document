package no.nav.klage.dokument.service

import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.JAXBElement
import jakarta.xml.bind.Marshaller
import no.arkivverket.standarder.noark5.arkivmelding.v2.*
import no.nav.avtaltmelding.trygderetten.v1.NavMappe
import no.nav.klage.dokument.clients.pdl.graphql.PdlClient
import no.nav.klage.dokument.clients.pdl.graphql.PdlPerson
import no.nav.klage.dokument.clients.saf.graphql.DokumentInfo
import no.nav.klage.dokument.clients.saf.graphql.Journalpost
import no.nav.klage.dokument.clients.saf.graphql.Journalposttype
import no.nav.klage.dokument.clients.saf.graphql.SafGraphQlClient
import no.nav.klage.dokument.clients.saf.graphql.Variantformat
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.kodeverk.Tema
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.StringWriter
import java.math.BigInteger
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
        const val UTGAAENDE_DOKUMENT = "Utgående dokument"
        const val EKSPEDERT = "Ekspedert"
        const val DOKUMENTASJON = "Dokumentasjon"
        const val DOKUMENTET_ER_FERDIGSTILT = "Dokumentet er ferdigstilt"
        const val UKJENT_NAVN = "UKJENT NAVN"
        const val HOVEDDOKUMENT = "Hoveddokument"
        const val VEDLEGG = "Vedlegg"

    }

    fun generateArkivmelding(journalpostId: String, avsenderMottakerDistribusjonId: UUID): String? {
        val journalpost = getJournalpost(journalpostId = journalpostId)

        val personInfo = pdlClient.getPersonInfo(ident = journalpost.bruker.id)

        val sakOpprettetDato = convertLocalDateTimeToXmlGregorianCalendar(
            journalpost.sak?.datoOpprettet ?: getOldestDateFromDokumenter(journalpost.dokumenter ?: emptyList())
        )

        val datoArkivmeldingOpprettet = getNow()

        val arkivmelding = Arkivmelding()
        arkivmelding.system = applicationName
        arkivmelding.meldingId = avsenderMottakerDistribusjonId.toString()
        arkivmelding.tidspunkt = datoArkivmeldingOpprettet
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
            registrering.add(Journalpost().apply {
                opprettetDato = convertLocalDateTimeToXmlGregorianCalendar(journalpost.datoOpprettet)
                opprettetAv = journalpost.opprettetAvNavn
                tittel = journalpost.tittel
                //mottaker
                korrespondansepart.add(Korrespondansepart().apply {
                    korrespondanseparttype = MOTTAKER
                    korrespondansepartNavn = TRYGDERETTEN
                    organisasjonsnummer = EnhetsidentifikatorType().apply {
                        organisasjonsnummer = TRYGDERETTEN_ORGNR
                    }
                }
                )
                //avsender
                korrespondansepart.add(Korrespondansepart().apply {
                    korrespondanseparttype = AVSENDER
                    korrespondansepartNavn = NAV_KLAGEINSTANS
                    organisasjonsnummer = EnhetsidentifikatorType().apply {
                        organisasjonsnummer = NAV_KLAGEINSTANS_ORGNR
                    }
                }
                )
                journalposttype = EKSPEDERT
                journalstatus = UNDER_BEHANDLING
                journaldato = convertLocalDateTimeToXmlGregorianCalendar(
                    journalpost.getDatoJournalfoert() ?: throw RuntimeException("No journalfoeringData in journalpost")
                )
                //TODO: Må samle en collection her
//                dokumentbeskrivelse.addAll(getDokumentbeskrivelser(journalpost.dokumenter, datoArkivmeldingOpprettet, journalpost))
            }

            )
        }
        )

        val jaxbContext = JAXBContext.newInstance(Arkivmelding::class.java, NavMappe::class.java)

        val marshaller: Marshaller = jaxbContext.createMarshaller()
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
        val sw = StringWriter()
        marshaller.marshal(arkivmelding, sw)

        return sw.toString()
    }

    private fun getDokumentbeskrivelser(
        dokumenter: List<DokumentInfo>,
        datoArkivmeldingOpprettet: XMLGregorianCalendar?,
        newJournalpost: Journalpost
    ): Collection<Dokumentbeskrivelse> {
        var index = 1
         dokumenter.mapNotNull { dokument ->
            if (dokument.isFerdigstilt()) {
                val originalJournalpost = if (dokument.originalJournalpostId.isNotBlank()) {
                    getJournalpost(journalpostId = dokument.originalJournalpostId)
                } else null

                val dokumentbeskrivelse = Dokumentbeskrivelse().apply {
                    dokumenttype = DOKUMENTASJON
                    dokumentstatus = DOKUMENTET_ER_FERDIGSTILT
                    tittel = getDokumentbeskrivelseTittel(
                        dokumentInfo = dokument,
                        originalJournalpost = originalJournalpost,
                    )
                    opprettetDato = getDokumentbeskrivelseOpprettetDato(
                        originalJournalpost = originalJournalpost,
                        newJournalpost = newJournalpost,
                    )
                    opprettetAv = getDokumentbeskrivelseOpprettetAv(
                        originalJournalpost = originalJournalpost,
                        newJournalpost = newJournalpost,
                    )
                    tilknyttetRegistreringSom = if (originalJournalpost == null) {
                        HOVEDDOKUMENT
                    } else {
                        VEDLEGG
                    }
                    dokumentnummer = BigInteger.valueOf(index.toLong())
                    tilknyttetDato = datoArkivmeldingOpprettet
                    tilknyttetAv = newJournalpost.journalfortAvNavn
                    dokumentobjekt.add(Dokumentobjekt().apply {
                        versjonsnummer = BigInteger.valueOf(1.toLong())
                        variantformat = Variantformat.ARKIV.toString() //TODO: Må undersøke materialet.
                        format = "TODO" //TODO: Må undersøke materialet.
                        opprettetDato = getDokumentbeskrivelseOpprettetDato(
                            originalJournalpost = originalJournalpost,
                            newJournalpost = newJournalpost
                        )
                        opprettetAv = getDokumentbeskrivelseOpprettetAv(
                            originalJournalpost = originalJournalpost,
                            newJournalpost = newJournalpost,
                        )
                        referanseDokumentfil = "TODO"
                    }
                    )
                }

                index++
            } else null
        }
        TODO("Not yet implemented")
    }

    private fun getNavMappe(fagsakId: String?): JAXBElement<*> {
        val navMappe = NavMappe().apply {
            saksnummer = fagsakId
        }

        val jaxbElement: JAXBElement<NavMappe> = no.nav.avtaltmelding.trygderetten.v1.ObjectFactory().createNavMappe(navMappe)

        return JAXBElement(
            QName(ARKIVMELDING_NAMESPACE, "virksomhetsspesifikkeMetadata"),
            JAXBElement::class.java,
            jaxbElement
        )
    }

    private fun getDokumentbeskrivelseTittel(
        dokumentInfo: DokumentInfo,
        originalJournalpost: Journalpost?
    ): String {
        return if (originalJournalpost != null) {
            when (originalJournalpost.journalposttype) {
                Journalposttype.I -> {
                    val avsenderMottakerNavn = originalJournalpost.avsenderMottaker?.navn ?: UKJENT_NAVN
                    "${dokumentInfo.tittel}, Fra $avsenderMottakerNavn"
                }

                Journalposttype.U -> {
                    val avsenderMottakerNavn = originalJournalpost.avsenderMottaker?.navn ?: UKJENT_NAVN
                    "${dokumentInfo.tittel}, Til $avsenderMottakerNavn"
                }

                else -> dokumentInfo.tittel
            }
        } else {
            return dokumentInfo.tittel
        }
    }

    private fun getDokumentbeskrivelseOpprettetDato(
        originalJournalpost: Journalpost?,
        newJournalpost: Journalpost
    ): XMLGregorianCalendar? {
        return if (originalJournalpost != null && originalJournalpost.getDatoJournalfoert() != null) {
            convertLocalDateTimeToXmlGregorianCalendar(originalJournalpost.getDatoJournalfoert()!!)
        } else {
            convertLocalDateTimeToXmlGregorianCalendar(
                newJournalpost.getDatoJournalfoert() ?: throw RuntimeException("No journalfoeringData in journalpost")
            )
        }
    }

    private fun getDokumentbeskrivelseOpprettetAv(
        originalJournalpost: Journalpost?,
        newJournalpost: Journalpost
    ): String {
        return originalJournalpost?.journalfortAvNavn ?: newJournalpost.journalfortAvNavn

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

    fun getJournalpost(
        journalpostId: String,
    ): Journalpost {
        return runWithTimingAndLogging({
            safGraphQlClient.getJournalpostAsSystembruker(journalpostId = journalpostId)
        }, this::getJournalpost.name)
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