package no.nav.klage.dokument.service

import no.nav.klage.gradle.plugin.xsd2java.xsd.Arkivmelding
import no.nav.klage.gradle.plugin.xsd2java.xsd.Mappe
import no.nav.klage.gradle.plugin.xsd2java.xsd.ObjectFactory
import org.junit.jupiter.api.Test
import java.io.StringWriter
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBElement
import javax.xml.bind.Marshaller

class ToXmlTest {

    @Test
    fun `test to xml`() {
        val arkivmelding = Arkivmelding()
        arkivmelding.system = "system"
        arkivmelding.meldingId = "meldingId"
        arkivmelding.tidspunkt = null
        arkivmelding.antallFiler = 1

        arkivmelding.mappe.add(
            Mappe()
        )

        val jaxbElement: JAXBElement<Arkivmelding> = ObjectFactory().createArkivmelding(arkivmelding)
        val jaxbContext = JAXBContext.newInstance(Arkivmelding::class.java)

        val marshaller: Marshaller = jaxbContext.createMarshaller()
        val sw = StringWriter()
        marshaller.marshal(jaxbElement, sw)

        println(sw.toString())
    }

}