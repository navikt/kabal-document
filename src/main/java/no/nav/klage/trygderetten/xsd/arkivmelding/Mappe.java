
package no.nav.klage.trygderetten.xsd.arkivmelding;

import jakarta.xml.bind.annotation.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for mappe complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mappe"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="systemID" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}GUID" minOccurs="0"/&gt;
 *         &lt;element name="mappeID" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}mappeID" minOccurs="0"/&gt;
 *         &lt;element name="ReferanseForeldermappe" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}GUID" minOccurs="0"/&gt;
 *         &lt;element name="tittel" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}tittel"/&gt;
 *         &lt;element name="offentligTittel" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}offentligTittel" minOccurs="0"/&gt;
 *         &lt;element name="beskrivelse" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}beskrivelse" minOccurs="0"/&gt;
 *         &lt;element name="noekkelord" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}noekkelord" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="dokumentmedium" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}dokumentmedium" minOccurs="0"/&gt;
 *         &lt;element name="oppbevaringssted" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}oppbevaringssted" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="opprettetDato" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}opprettetDato" minOccurs="0"/&gt;
 *         &lt;element name="opprettetAv" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}opprettetAv" minOccurs="0"/&gt;
 *         &lt;element name="avsluttetDato" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}avsluttetDato" minOccurs="0"/&gt;
 *         &lt;element name="avsluttetAv" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}avsluttetAv" minOccurs="0"/&gt;
 *         &lt;element name="referanseArkivdel" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}referanseArkivdel" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="virksomhetsspesifikkeMetadata" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/&gt;
 *         &lt;element name="part" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}part" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="kryssreferanse" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}kryssreferanse" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="merknad" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}merknad" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="skjerming" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}skjerming" minOccurs="0"/&gt;
 *         &lt;element name="gradering" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}gradering" minOccurs="0"/&gt;
 *         &lt;element name="klasse" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}klasse" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;choice&gt;
 *           &lt;element name="mappe" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}mappe" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element name="registrering" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}registrering" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mappe", propOrder = {
    "systemID",
    "mappeID",
    "referanseForeldermappe",
    "tittel",
    "offentligTittel",
    "beskrivelse",
    "noekkelord",
    "dokumentmedium",
    "oppbevaringssted",
    "opprettetDato",
    "opprettetAv",
    "avsluttetDato",
    "avsluttetAv",
    "referanseArkivdel",
    "virksomhetsspesifikkeMetadata",
    "part",
    "kryssreferanse",
    "merknad",
    "skjerming",
    "gradering",
    "klasse",
    "mappe",
    "registrering"
})
@XmlSeeAlso({
    Saksmappe.class
})
public class Mappe {

    protected String systemID;
    protected String mappeID;
    @XmlElement(name = "ReferanseForeldermappe")
    protected String referanseForeldermappe;
    @XmlElement(required = true)
    protected String tittel;
    protected String offentligTittel;
    protected String beskrivelse;
    protected List<String> noekkelord;
    protected String dokumentmedium;
    protected List<String> oppbevaringssted;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar opprettetDato;
    protected String opprettetAv;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar avsluttetDato;
    protected String avsluttetAv;
    protected List<String> referanseArkivdel;
    @jakarta.xml.bind.annotation.XmlAnyElement(lax = true)
    protected Object virksomhetsspesifikkeMetadata;
    protected List<Part> part;
    protected List<Kryssreferanse> kryssreferanse;
    protected List<Merknad> merknad;
    protected Skjerming skjerming;
    protected Gradering gradering;
    protected List<Klasse> klasse;
    protected List<Mappe> mappe;
    protected List<Registrering> registrering;

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
     * Gets the value of the mappeID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMappeID() {
        return mappeID;
    }

    /**
     * Sets the value of the mappeID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMappeID(String value) {
        this.mappeID = value;
    }

    /**
     * Gets the value of the referanseForeldermappe property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferanseForeldermappe() {
        return referanseForeldermappe;
    }

    /**
     * Sets the value of the referanseForeldermappe property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferanseForeldermappe(String value) {
        this.referanseForeldermappe = value;
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
     * Gets the value of the offentligTittel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOffentligTittel() {
        return offentligTittel;
    }

    /**
     * Sets the value of the offentligTittel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOffentligTittel(String value) {
        this.offentligTittel = value;
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
     * Gets the value of the dokumentmedium property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDokumentmedium() {
        return dokumentmedium;
    }

    /**
     * Sets the value of the dokumentmedium property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDokumentmedium(String value) {
        this.dokumentmedium = value;
    }

    /**
     * Gets the value of the oppbevaringssted property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the oppbevaringssted property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOppbevaringssted().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getOppbevaringssted() {
        if (oppbevaringssted == null) {
            oppbevaringssted = new ArrayList<>();
        }
        return this.oppbevaringssted;
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

    /**
     * Gets the value of the avsluttetDato property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getAvsluttetDato() {
        return avsluttetDato;
    }

    /**
     * Sets the value of the avsluttetDato property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setAvsluttetDato(XMLGregorianCalendar value) {
        this.avsluttetDato = value;
    }

    /**
     * Gets the value of the avsluttetAv property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAvsluttetAv() {
        return avsluttetAv;
    }

    /**
     * Sets the value of the avsluttetAv property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAvsluttetAv(String value) {
        this.avsluttetAv = value;
    }

    /**
     * Gets the value of the referanseArkivdel property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the referanseArkivdel property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReferanseArkivdel().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getReferanseArkivdel() {
        if (referanseArkivdel == null) {
            referanseArkivdel = new ArrayList<>();
        }
        return this.referanseArkivdel;
    }

    /**
     * Gets the value of the virksomhetsspesifikkeMetadata property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getVirksomhetsspesifikkeMetadata() {
        return virksomhetsspesifikkeMetadata;
    }

    /**
     * Sets the value of the virksomhetsspesifikkeMetadata property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setVirksomhetsspesifikkeMetadata(Object value) {
        this.virksomhetsspesifikkeMetadata = value;
    }

    /**
     * Gets the value of the part property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the part property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPart().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Part }
     * 
     * 
     */
    public List<Part> getPart() {
        if (part == null) {
            part = new ArrayList<>();
        }
        return this.part;
    }

    /**
     * Gets the value of the kryssreferanse property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the kryssreferanse property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKryssreferanse().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Kryssreferanse }
     * 
     * 
     */
    public List<Kryssreferanse> getKryssreferanse() {
        if (kryssreferanse == null) {
            kryssreferanse = new ArrayList<>();
        }
        return this.kryssreferanse;
    }

    /**
     * Gets the value of the merknad property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the merknad property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMerknad().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Merknad }
     * 
     * 
     */
    public List<Merknad> getMerknad() {
        if (merknad == null) {
            merknad = new ArrayList<>();
        }
        return this.merknad;
    }

    /**
     * Gets the value of the skjerming property.
     * 
     * @return
     *     possible object is
     *     {@link Skjerming }
     *     
     */
    public Skjerming getSkjerming() {
        return skjerming;
    }

    /**
     * Sets the value of the skjerming property.
     * 
     * @param value
     *     allowed object is
     *     {@link Skjerming }
     *     
     */
    public void setSkjerming(Skjerming value) {
        this.skjerming = value;
    }

    /**
     * Gets the value of the gradering property.
     * 
     * @return
     *     possible object is
     *     {@link Gradering }
     *     
     */
    public Gradering getGradering() {
        return gradering;
    }

    /**
     * Sets the value of the gradering property.
     * 
     * @param value
     *     allowed object is
     *     {@link Gradering }
     *     
     */
    public void setGradering(Gradering value) {
        this.gradering = value;
    }

    /**
     * Gets the value of the klasse property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the klasse property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKlasse().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Klasse }
     * 
     * 
     */
    public List<Klasse> getKlasse() {
        if (klasse == null) {
            klasse = new ArrayList<>();
        }
        return this.klasse;
    }

    /**
     * Gets the value of the mappe property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the mappe property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMappe().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Mappe }
     * 
     * 
     */
    public List<Mappe> getMappe() {
        if (mappe == null) {
            mappe = new ArrayList<>();
        }
        return this.mappe;
    }

    /**
     * Gets the value of the registrering property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the registrering property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegistrering().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Registrering }
     * 
     * 
     */
    public List<Registrering> getRegistrering() {
        if (registrering == null) {
            registrering = new ArrayList<>();
        }
        return this.registrering;
    }

}
