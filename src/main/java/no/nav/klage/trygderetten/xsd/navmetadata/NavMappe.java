
package no.nav.klage.trygderetten.xsd.navmetadata;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for navMappe complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="navMappe"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="saksnummer" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "navMappe", propOrder = {
    "saksnummer"
})
@jakarta.xml.bind.annotation.XmlRootElement
public class NavMappe {

    @XmlElement(required = true)
    protected String saksnummer;

    /**
     * Gets the value of the saksnummer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSaksnummer() {
        return saksnummer;
    }

    /**
     * Sets the value of the saksnummer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSaksnummer(String value) {
        this.saksnummer = value;
    }

}
