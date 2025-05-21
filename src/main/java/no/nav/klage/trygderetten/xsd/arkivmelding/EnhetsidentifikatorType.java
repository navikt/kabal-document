
package no.nav.klage.trygderetten.xsd.arkivmelding;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EnhetsidentifikatorType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EnhetsidentifikatorType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.arkivverket.no/standarder/noark5/arkivmelding}AbstraktNasjonalidentifikatorType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="organisasjonsnummer" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EnhetsidentifikatorType", propOrder = {
    "organisasjonsnummer"
})
public class EnhetsidentifikatorType
    extends AbstraktNasjonalidentifikatorType
{

    @XmlElement(required = true)
    protected String organisasjonsnummer;

    /**
     * Gets the value of the organisasjonsnummer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrganisasjonsnummer() {
        return organisasjonsnummer;
    }

    /**
     * Sets the value of the organisasjonsnummer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganisasjonsnummer(String value) {
        this.organisasjonsnummer = value;
    }

}
