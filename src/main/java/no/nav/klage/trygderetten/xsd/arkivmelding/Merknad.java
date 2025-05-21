
package no.nav.klage.trygderetten.xsd.arkivmelding;

import jakarta.xml.bind.annotation.*;

import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for merknad complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="merknad"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="merknadstekst" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}merknadstekst"/&gt;
 *         &lt;element name="merknadstype" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}merknadstype" minOccurs="0"/&gt;
 *         &lt;element name="merknadsdato" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}merknadsdato"/&gt;
 *         &lt;element name="merknadRegistrertAv" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}merknadRegistrertAv"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "merknad", propOrder = {
    "merknadstekst",
    "merknadstype",
    "merknadsdato",
    "merknadRegistrertAv"
})
public class Merknad {

    @XmlElement(required = true)
    protected String merknadstekst;
    protected String merknadstype;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar merknadsdato;
    @XmlElement(required = true)
    protected String merknadRegistrertAv;

    /**
     * Gets the value of the merknadstekst property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMerknadstekst() {
        return merknadstekst;
    }

    /**
     * Sets the value of the merknadstekst property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMerknadstekst(String value) {
        this.merknadstekst = value;
    }

    /**
     * Gets the value of the merknadstype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMerknadstype() {
        return merknadstype;
    }

    /**
     * Sets the value of the merknadstype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMerknadstype(String value) {
        this.merknadstype = value;
    }

    /**
     * Gets the value of the merknadsdato property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getMerknadsdato() {
        return merknadsdato;
    }

    /**
     * Sets the value of the merknadsdato property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setMerknadsdato(XMLGregorianCalendar value) {
        this.merknadsdato = value;
    }

    /**
     * Gets the value of the merknadRegistrertAv property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMerknadRegistrertAv() {
        return merknadRegistrertAv;
    }

    /**
     * Sets the value of the merknadRegistrertAv property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMerknadRegistrertAv(String value) {
        this.merknadRegistrertAv = value;
    }

}
