
package no.nav.klage.trygderetten.xsd.arkivmelding;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DNummerType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DNummerType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.arkivverket.no/standarder/noark5/arkivmelding}AbstraktPersonidentifikatorType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DNummer" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DNummerType", propOrder = {
    "dNummer"
})
public class DNummerType
    extends AbstraktPersonidentifikatorType
{

    @XmlElement(name = "DNummer", required = true)
    protected String dNummer;

    /**
     * Gets the value of the dNummer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNummer() {
        return dNummer;
    }

    /**
     * Sets the value of the dNummer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNummer(String value) {
        this.dNummer = value;
    }

}
