
package no.nav.klage.trygderetten.xsd.arkivmelding;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FoedselsnummerType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FoedselsnummerType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.arkivverket.no/standarder/noark5/arkivmelding}AbstraktPersonidentifikatorType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="foedselsnummer" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FoedselsnummerType", propOrder = {
    "foedselsnummer"
})
public class FoedselsnummerType
    extends AbstraktPersonidentifikatorType
{

    @XmlElement(required = true)
    protected String foedselsnummer;

    /**
     * Gets the value of the foedselsnummer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFoedselsnummer() {
        return foedselsnummer;
    }

    /**
     * Sets the value of the foedselsnummer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFoedselsnummer(String value) {
        this.foedselsnummer = value;
    }

}
