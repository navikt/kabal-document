package no.nav.klage.dokument.util

import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.JAXBElement
import jakarta.xml.bind.Marshaller
import no.arkivverket.standarder.noark5.arkivmelding.v2.Arkivmelding
import no.arkivverket.standarder.noark5.arkivmelding.v2.Dokumentbeskrivelse
import no.nav.avtaltmelding.trygderetten.v1.NavMappe
import no.nav.klage.dokument.clients.pdl.graphql.PdlPerson
import no.nav.klage.dokument.clients.saf.graphql.DokumentInfo
import no.nav.klage.dokument.clients.saf.graphql.Dokumentvariant
import no.nav.klage.dokument.clients.saf.graphql.Filtype
import no.nav.klage.dokument.clients.saf.graphql.Journalpost
import no.nav.klage.dokument.clients.saf.graphql.Journalposttype
import no.nav.klage.dokument.clients.saf.graphql.Variantformat
import java.io.StringWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import java.util.GregorianCalendar
import javax.xml.datatype.DatatypeConfigurationException
import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar
import javax.xml.namespace.QName


const val UKJENT_NAVN = "UKJENT NAVN"
const val ARKIVMELDING_NAMESPACE = "http://www.arkivverket.no/standarder/noark5/arkivmelding"
const val DOKUMENT_HVOR_DELER_AV_INNHOLDET_ER_SKJERMET = "Dokument hvor deler av innholdet er skjermet"
const val ARKIVFORMAT = "Arkivformat"
const val PRODUKSJONSFORMAT = "Produksjonsformat"



fun getOldestDateFromDokumentbeskrivelser(
    dokumentBeskrivelser: Collection<Dokumentbeskrivelse>
): XMLGregorianCalendar? {
    val now = getNow()
    return dokumentBeskrivelser.minBy { it.opprettetDato.toGregorianCalendar() }.opprettetDato ?: now
}

fun getDokumentbeskrivelseOpprettetDato(
    originalJournalpost: Journalpost?,
    newJournalpost: Journalpost,
    dokumentIsFromOldJournalpost: Boolean
): XMLGregorianCalendar {
    return if (dokumentIsFromOldJournalpost && originalJournalpost?.getDatoJournalfoert() != null) {
        convertLocalDateTimeToXmlGregorianCalendar(originalJournalpost.getDatoJournalfoert()!!)
    } else {
        convertLocalDateTimeToXmlGregorianCalendar(
            newJournalpost.getDatoJournalfoert() ?: throw RuntimeException("No journalfoeringData in journalpost")
        )
    }
}

fun convertLocalDateTimeToXmlGregorianCalendar(localDateTime: LocalDateTime): XMLGregorianCalendar {
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

fun getDokumentbeskrivelseTittel(
    dokumentInfo: DokumentInfo,
    originalJournalpost: Journalpost?,
    dokumentIsFromOldJournalpost: Boolean
): String {
    return if (dokumentIsFromOldJournalpost) {
        when (originalJournalpost!!.journalposttype) {
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

fun getDokumentbeskrivelseOpprettetAv(
    originalJournalpost: Journalpost?,
    newJournalpost: Journalpost
): String {
    return originalJournalpost?.journalfortAvNavn ?: newJournalpost.journalfortAvNavn
}

fun getSammensattNavn(navn: PdlPerson.Navn?): String? {
    val mellomnavn = navn?.mellomnavn?.let { " ${it.trim()}" } ?: ""
    return navn?.let { "${it.fornavn}${mellomnavn} ${it.etternavn}" }
}

fun getNavMappe(fagsakId: String?): JAXBElement<*> {
    val navMappe = NavMappe().apply {
        saksnummer = fagsakId
    }

    val jaxbElement: JAXBElement<NavMappe> =
        no.nav.avtaltmelding.trygderetten.v1.ObjectFactory().createNavMappe(navMappe)

    return JAXBElement(
        QName(ARKIVMELDING_NAMESPACE, "virksomhetsspesifikkeMetadata"),
        JAXBElement::class.java,
        jaxbElement
    )
}

fun getDokumentbeskrivelseReferanseDokumentFil(
    dokument: DokumentInfo,
    newJournalpost: Journalpost,
    gjeldendeDokumentVariant: Dokumentvariant
): String {
    return ("${newJournalpost.journalpostId}-${dokument.dokumentInfoId}-${
        getDokumentbeskrivelseVariantFormat(
            gjeldendeDokumentVariant
        )
    }.${gjeldendeDokumentVariant.filtype}")
}

fun getDokumentbeskrivelseVariantFormat(dokumentVariant: Dokumentvariant): String {
    return if (dokumentVariant.variantformat == Variantformat.SLADDET) {
        DOKUMENT_HVOR_DELER_AV_INNHOLDET_ER_SKJERMET
    } else if (dokumentVariant.filtype in listOf(
            Filtype.JPEG,
            Filtype.PNG,
        )
    ) {
        ARKIVFORMAT
    } else PRODUKSJONSFORMAT
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

fun marshalArkivmelding(arkivmelding: Arkivmelding): String {
    val jaxbContext = JAXBContext.newInstance(Arkivmelding::class.java, NavMappe::class.java)
    val marshaller: Marshaller = jaxbContext.createMarshaller()
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
    val sw = StringWriter()
    marshaller.marshal(arkivmelding, sw)

    return sw.toString()
}