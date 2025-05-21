
package no.nav.klage.trygderetten.xsd.arkivmelding;

import jakarta.xml.bind.annotation.*;

import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for avskrivning complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="avskrivning"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="avskrivningsdato" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}avskrivningsdato"/&gt;
 *         &lt;element name="avskrevetAv" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}avskrevetAv"/&gt;
 *         &lt;element name="avskrivningsmaate" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}avskrivningsmaate"/&gt;
 *         &lt;element name="referanseAvskrivesAvJournalpost" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}referanseAvskrivesAvJournalpost" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "avskrivning", propOrder = {
    "avskrivningsdato",
    "avskrevetAv",
    "avskrivningsmaate",
    "referanseAvskrivesAvJournalpost"
})
public class Avskrivning {

    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar avskrivningsdato;
    @XmlElement(required = true)
    protected String avskrevetAv;
    @XmlElement(required = true)
    protected String avskrivningsmaate;
    protected String referanseAvskrivesAvJournalpost;

    /**
     * Gets the value of the avskrivningsdato property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getAvskrivningsdato() {
        return avskrivningsdato;
    }

    /**
     * Sets the value of the avskrivningsdato property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setAvskrivningsdato(XMLGregorianCalendar value) {
        this.avskrivningsdato = value;
    }

    /**
     * Gets the value of the avskrevetAv property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAvskrevetAv() {
        return avskrevetAv;
    }

    /**
     * Sets the value of the avskrevetAv property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAvskrevetAv(String value) {
        this.avskrevetAv = value;
    }

    /**
     * Gets the value of the avskrivningsmaate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAvskrivningsmaate() {
        return avskrivningsmaate;
    }

    /**
     * Sets the value of the avskrivningsmaate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAvskrivningsmaate(String value) {
        this.avskrivningsmaate = value;
    }

    /**
     * Gets the value of the referanseAvskrivesAvJournalpost property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferanseAvskrivesAvJournalpost() {
        return referanseAvskrivesAvJournalpost;
    }

    /**
     * Sets the value of the referanseAvskrivesAvJournalpost property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferanseAvskrivesAvJournalpost(String value) {
        this.referanseAvskrivesAvJournalpost = value;
    }

}
