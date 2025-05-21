
package no.nav.klage.trygderetten.xsd.arkivmelding;

import jakarta.xml.bind.annotation.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for klasse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="klasse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="systemID" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}GUID" minOccurs="0"/&gt;
 *         &lt;element name="klassifikasjonssystem" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="klasseID" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}klasseID"/&gt;
 *         &lt;element name="tittel" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}tittel" minOccurs="0"/&gt;
 *         &lt;element name="beskrivelse" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}beskrivelse" minOccurs="0"/&gt;
 *         &lt;element name="noekkelord" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}noekkelord" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="opprettetDato" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}opprettetDato" minOccurs="0"/&gt;
 *         &lt;element name="opprettetAv" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}opprettetAv" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "klasse", propOrder = {
    "systemID",
    "klassifikasjonssystem",
    "klasseID",
    "tittel",
    "beskrivelse",
    "noekkelord",
    "opprettetDato",
    "opprettetAv"
})
public class Klasse {

    protected String systemID;
    @XmlElement(required = true)
    protected String klassifikasjonssystem;
    @XmlElement(required = true)
    protected String klasseID;
    protected String tittel;
    protected String beskrivelse;
    protected List<String> noekkelord;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar opprettetDato;
    protected String opprettetAv;

    /**
     * Gets the value of the systemID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSystemID() {
        return systemID;
    }

    /**
     * Sets the value of the systemID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSystemID(String value) {
        this.systemID = value;
    }

    /**
     * Gets the value of the klassifikasjonssystem property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKlassifikasjonssystem() {
        return klassifikasjonssystem;
    }

    /**
     * Sets the value of the klassifikasjonssystem property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKlassifikasjonssystem(String value) {
        this.klassifikasjonssystem = value;
    }

    /**
     * Gets the value of the klasseID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKlasseID() {
        return klasseID;
    }

    /**
     * Sets the value of the klasseID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKlasseID(String value) {
        this.klasseID = value;
    }

    /**
     * Gets the value of the tittel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTittel() {
        return tittel;
    }

    /**
     * Sets the value of the tittel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTittel(String value) {
        this.tittel = value;
    }

    /**
     * Gets the value of the beskrivelse property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBeskrivelse() {
        return beskrivelse;
    }

    /**
     * Sets the value of the beskrivelse property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBeskrivelse(String value) {
        this.beskrivelse = value;
    }

    /**
     * Gets the value of the noekkelord property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the noekkelord property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNoekkelord().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getNoekkelord() {
        if (noekkelord == null) {
            noekkelord = new ArrayList<>();
        }
        return this.noekkelord;
    }

    /**
     * Gets the value of the opprettetDato property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getOpprettetDato() {
        return opprettetDato;
    }

    /**
     * Sets the value of the opprettetDato property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setOpprettetDato(XMLGregorianCalendar value) {
        this.opprettetDato = value;
    }

    /**
     * Gets the value of the opprettetAv property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOpprettetAv() {
        return opprettetAv;
    }

    /**
     * Sets the value of the opprettetAv property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOpprettetAv(String value) {
        this.opprettetAv = value;
    }

}
