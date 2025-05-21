
package no.nav.klage.trygderetten.xsd.arkivmelding;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for kryssreferanse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="kryssreferanse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="referanseTilKlasse" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}referanseTilKlasse" minOccurs="0"/&gt;
 *         &lt;element name="referanseTilMappe" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}referanseTilMappe" minOccurs="0"/&gt;
 *         &lt;element name="referanseTilRegistrering" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}referanseTilRegistrering" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "kryssreferanse", propOrder = {
    "referanseTilKlasse",
    "referanseTilMappe",
    "referanseTilRegistrering"
})
public class Kryssreferanse {

    protected String referanseTilKlasse;
    protected String referanseTilMappe;
    protected String referanseTilRegistrering;

    /**
     * Gets the value of the referanseTilKlasse property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferanseTilKlasse() {
        return referanseTilKlasse;
    }

    /**
     * Sets the value of the referanseTilKlasse property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferanseTilKlasse(String value) {
        this.referanseTilKlasse = value;
    }

    /**
     * Gets the value of the referanseTilMappe property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferanseTilMappe() {
        return referanseTilMappe;
    }

    /**
     * Sets the value of the referanseTilMappe property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferanseTilMappe(String value) {
        this.referanseTilMappe = value;
    }

    /**
     * Gets the value of the referanseTilRegistrering property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferanseTilRegistrering() {
        return referanseTilRegistrering;
    }

    /**
     * Sets the value of the referanseTilRegistrering property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferanseTilRegistrering(String value) {
        this.referanseTilRegistrering = value;
    }

}
