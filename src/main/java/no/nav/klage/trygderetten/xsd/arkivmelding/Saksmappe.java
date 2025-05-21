
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
 * <p>Java class for saksmappe complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="saksmappe"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.arkivverket.no/standarder/noark5/arkivmelding}mappe"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="saksaar" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}saksaar" minOccurs="0"/&gt;
 *         &lt;element name="sakssekvensnummer" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}sakssekvensnummer" minOccurs="0"/&gt;
 *         &lt;element name="saksdato" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}saksdato" minOccurs="0"/&gt;
 *         &lt;element name="administrativEnhet" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}administrativEnhet" minOccurs="0"/&gt;
 *         &lt;element name="saksansvarlig" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}saksansvarlig" minOccurs="0"/&gt;
 *         &lt;element name="journalenhet" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}journalenhet" minOccurs="0"/&gt;
 *         &lt;element name="saksstatus" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}saksstatus" minOccurs="0"/&gt;
 *         &lt;element name="utlaantDato" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}utlaantDato" minOccurs="0"/&gt;
 *         &lt;element name="utlaantTil" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}utlaantTil" minOccurs="0"/&gt;
 *         &lt;element name="presedens" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}presedens" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "saksmappe", propOrder = {
    "saksaar",
    "sakssekvensnummer",
    "saksdato",
    "administrativEnhet",
    "saksansvarlig",
    "journalenhet",
    "saksstatus",
    "utlaantDato",
    "utlaantTil",
    "presedens"
})
public class Saksmappe
    extends Mappe
{

    protected BigInteger saksaar;
    protected BigInteger sakssekvensnummer;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar saksdato;
    protected String administrativEnhet;
    protected String saksansvarlig;
    protected String journalenhet;
    protected String saksstatus;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar utlaantDato;
    protected String utlaantTil;
    protected List<Presedens> presedens;

    /**
     * Gets the value of the saksaar property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSaksaar() {
        return saksaar;
    }

    /**
     * Sets the value of the saksaar property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSaksaar(BigInteger value) {
        this.saksaar = value;
    }

    /**
     * Gets the value of the sakssekvensnummer property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSakssekvensnummer() {
        return sakssekvensnummer;
    }

    /**
     * Sets the value of the sakssekvensnummer property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSakssekvensnummer(BigInteger value) {
        this.sakssekvensnummer = value;
    }

    /**
     * Gets the value of the saksdato property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSaksdato() {
        return saksdato;
    }

    /**
     * Sets the value of the saksdato property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSaksdato(XMLGregorianCalendar value) {
        this.saksdato = value;
    }

    /**
     * Gets the value of the administrativEnhet property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdministrativEnhet() {
        return administrativEnhet;
    }

    /**
     * Sets the value of the administrativEnhet property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdministrativEnhet(String value) {
        this.administrativEnhet = value;
    }

    /**
     * Gets the value of the saksansvarlig property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSaksansvarlig() {
        return saksansvarlig;
    }

    /**
     * Sets the value of the saksansvarlig property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSaksansvarlig(String value) {
        this.saksansvarlig = value;
    }

    /**
     * Gets the value of the journalenhet property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJournalenhet() {
        return journalenhet;
    }

    /**
     * Sets the value of the journalenhet property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJournalenhet(String value) {
        this.journalenhet = value;
    }

    /**
     * Gets the value of the saksstatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSaksstatus() {
        return saksstatus;
    }

    /**
     * Sets the value of the saksstatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSaksstatus(String value) {
        this.saksstatus = value;
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
     * Gets the value of the presedens property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the presedens property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPresedens().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Presedens }
     * 
     * 
     */
    public List<Presedens> getPresedens() {
        if (presedens == null) {
            presedens = new ArrayList<>();
        }
        return this.presedens;
    }

}
