
package no.nav.klage.trygderetten.xsd.arkivmelding;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for arkivnotat complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="arkivnotat"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.arkivverket.no/standarder/noark5/arkivmelding}registrering"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dokumentetsDato" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}dokumentetsDato" minOccurs="0"/&gt;
 *         &lt;element name="mottattDato" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}mottattDato" minOccurs="0"/&gt;
 *         &lt;element name="sendtDato" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}sendtDato" minOccurs="0"/&gt;
 *         &lt;element name="forfallsdato" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}forfallsdato" minOccurs="0"/&gt;
 *         &lt;element name="offentlighetsvurdertDato" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}offentlighetsvurdertDato" minOccurs="0"/&gt;
 *         &lt;element name="antallVedlegg" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}antallVedlegg" minOccurs="0"/&gt;
 *         &lt;element name="utlaantDato" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}utlaantDato" minOccurs="0"/&gt;
 *         &lt;element name="utlaantTil" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}utlaantTil" minOccurs="0"/&gt;
 *         &lt;element name="dokumentflyt" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}dokumentflyt" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "arkivnotat", propOrder = {
    "dokumentetsDato",
    "mottattDato",
    "sendtDato",
    "forfallsdato",
    "offentlighetsvurdertDato",
    "antallVedlegg",
    "utlaantDato",
    "utlaantTil",
    "dokumentflyt"
})
public class Arkivnotat
    extends Registrering
{

    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dokumentetsDato;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar mottattDato;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar sendtDato;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar forfallsdato;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar offentlighetsvurdertDato;
    protected BigInteger antallVedlegg;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar utlaantDato;
    protected String utlaantTil;
    protected List<Dokumentflyt> dokumentflyt;

    /**
     * Gets the value of the dokumentetsDato property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDokumentetsDato() {
        return dokumentetsDato;
    }

    /**
     * Sets the value of the dokumentetsDato property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDokumentetsDato(XMLGregorianCalendar value) {
        this.dokumentetsDato = value;
    }

    /**
     * Gets the value of the mottattDato property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getMottattDato() {
        return mottattDato;
    }

    /**
     * Sets the value of the mottattDato property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setMottattDato(XMLGregorianCalendar value) {
        this.mottattDato = value;
    }

    /**
     * Gets the value of the sendtDato property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSendtDato() {
        return sendtDato;
    }

    /**
     * Sets the value of the sendtDato property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSendtDato(XMLGregorianCalendar value) {
        this.sendtDato = value;
    }

    /**
     * Gets the value of the forfallsdato property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getForfallsdato() {
        return forfallsdato;
    }

    /**
     * Sets the value of the forfallsdato property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setForfallsdato(XMLGregorianCalendar value) {
        this.forfallsdato = value;
    }

    /**
     * Gets the value of the offentlighetsvurdertDato property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getOffentlighetsvurdertDato() {
        return offentlighetsvurdertDato;
    }

    /**
     * Sets the value of the offentlighetsvurdertDato property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setOffentlighetsvurdertDato(XMLGregorianCalendar value) {
        this.offentlighetsvurdertDato = value;
    }

    /**
     * Gets the value of the antallVedlegg property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAntallVedlegg() {
        return antallVedlegg;
    }

    /**
     * Sets the value of the antallVedlegg property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAntallVedlegg(BigInteger value) {
        this.antallVedlegg = value;
    }

    /**
     * Gets the value of the utlaantDato property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getUtlaantDato() {
        return utlaantDato;
    }

    /**
     * Sets the value of the utlaantDato property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setUtlaantDato(XMLGregorianCalendar value) {
        this.utlaantDato = value;
    }

    /**
     * Gets the value of the utlaantTil property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUtlaantTil() {
        return utlaantTil;
    }

    /**
     * Sets the value of the utlaantTil property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUtlaantTil(String value) {
        this.utlaantTil = value;
    }

    /**
     * Gets the value of the dokumentflyt property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the dokumentflyt property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDokumentflyt().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Dokumentflyt }
     * 
     * 
     */
    public List<Dokumentflyt> getDokumentflyt() {
        if (dokumentflyt == null) {
            dokumentflyt = new ArrayList<>();
        }
        return this.dokumentflyt;
    }

}
