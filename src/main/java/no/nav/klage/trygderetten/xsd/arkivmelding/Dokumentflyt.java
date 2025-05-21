
package no.nav.klage.trygderetten.xsd.arkivmelding;

import jakarta.xml.bind.annotation.*;

import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for dokumentflyt complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dokumentflyt"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="flytTil" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}flytTil"/&gt;
 *         &lt;element name="flytFra" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}flytFra"/&gt;
 *         &lt;element name="flytMottattDato" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}flytMottattDato"/&gt;
 *         &lt;element name="flytSendtDato" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}flytSendtDato"/&gt;
 *         &lt;element name="flytStatus" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}flytStatus"/&gt;
 *         &lt;element name="flytMerknad" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}flytMerknad" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dokumentflyt", propOrder = {
    "flytTil",
    "flytFra",
    "flytMottattDato",
    "flytSendtDato",
    "flytStatus",
    "flytMerknad"
})
public class Dokumentflyt {

    @XmlElement(required = true)
    protected String flytTil;
    @XmlElement(required = true)
    protected String flytFra;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar flytMottattDato;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar flytSendtDato;
    @XmlElement(required = true)
    protected String flytStatus;
    protected String flytMerknad;

    /**
     * Gets the value of the flytTil property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlytTil() {
        return flytTil;
    }

    /**
     * Sets the value of the flytTil property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlytTil(String value) {
        this.flytTil = value;
    }

    /**
     * Gets the value of the flytFra property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlytFra() {
        return flytFra;
    }

    /**
     * Sets the value of the flytFra property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlytFra(String value) {
        this.flytFra = value;
    }

    /**
     * Gets the value of the flytMottattDato property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFlytMottattDato() {
        return flytMottattDato;
    }

    /**
     * Sets the value of the flytMottattDato property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFlytMottattDato(XMLGregorianCalendar value) {
        this.flytMottattDato = value;
    }

    /**
     * Gets the value of the flytSendtDato property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFlytSendtDato() {
        return flytSendtDato;
    }

    /**
     * Sets the value of the flytSendtDato property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFlytSendtDato(XMLGregorianCalendar value) {
        this.flytSendtDato = value;
    }

    /**
     * Gets the value of the flytStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlytStatus() {
        return flytStatus;
    }

    /**
     * Sets the value of the flytStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlytStatus(String value) {
        this.flytStatus = value;
    }

    /**
     * Gets the value of the flytMerknad property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlytMerknad() {
        return flytMerknad;
    }

    /**
     * Sets the value of the flytMerknad property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlytMerknad(String value) {
        this.flytMerknad = value;
    }

}
