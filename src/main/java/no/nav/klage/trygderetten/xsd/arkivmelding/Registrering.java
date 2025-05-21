
package no.nav.klage.trygderetten.xsd.arkivmelding;

import jakarta.xml.bind.annotation.*;
import no.nav.klage.trygderetten.xsd.metadata.SystemID;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for registrering complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="registrering"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="systemID" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}systemID" minOccurs="0"/&gt;
 *         &lt;element name="opprettetDato" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}opprettetDato" minOccurs="0"/&gt;
 *         &lt;element name="opprettetAv" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}opprettetAv" minOccurs="0"/&gt;
 *         &lt;element name="arkivertDato" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}arkivertDato" minOccurs="0"/&gt;
 *         &lt;element name="arkivertAv" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}arkivertAv" minOccurs="0"/&gt;
 *         &lt;choice minOccurs="0"&gt;
 *           &lt;element name="referanseForelderMappe" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}GUID"/&gt;
 *           &lt;element name="referanseArkivdel" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}referanseArkivdel"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="part" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}part" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="skjerming" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}skjerming" minOccurs="0"/&gt;
 *         &lt;element name="gradering" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}gradering" minOccurs="0"/&gt;
 *         &lt;element name="dokumentbeskrivelse" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}dokumentbeskrivelse" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="registreringsID" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}registreringsID" minOccurs="0"/&gt;
 *         &lt;element name="tittel" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}tittel"/&gt;
 *         &lt;element name="offentligTittel" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}offentligTittel" minOccurs="0"/&gt;
 *         &lt;element name="beskrivelse" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}beskrivelse" minOccurs="0"/&gt;
 *         &lt;element name="noekkelord" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}noekkelord" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="forfatter" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}forfatter" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="dokumentmedium" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}dokumentmedium" minOccurs="0"/&gt;
 *         &lt;element name="oppbevaringssted" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}oppbevaringssted" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="virksomhetsspesifikkeMetadata" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/&gt;
 *         &lt;element name="merknad" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}merknad" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="kryssreferanse" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}kryssreferanse" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="korrespondansepart" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}korrespondansepart" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "registrering", propOrder = {
    "systemID",
    "opprettetDato",
    "opprettetAv",
    "arkivertDato",
    "arkivertAv",
    "referanseForelderMappe",
    "referanseArkivdel",
    "part",
    "skjerming",
    "gradering",
    "dokumentbeskrivelse",
    "registreringsID",
    "tittel",
    "offentligTittel",
    "beskrivelse",
    "noekkelord",
    "forfatter",
    "dokumentmedium",
    "oppbevaringssted",
    "virksomhetsspesifikkeMetadata",
    "merknad",
    "kryssreferanse",
    "korrespondansepart"
})
@XmlSeeAlso({
    Journalpost.class,
    Arkivnotat.class
})
public class Registrering {

    protected SystemID systemID;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar opprettetDato;
    protected String opprettetAv;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar arkivertDato;
    protected String arkivertAv;
    protected String referanseForelderMappe;
    protected String referanseArkivdel;
    protected List<Part> part;
    protected Skjerming skjerming;
    protected Gradering gradering;
    protected List<Dokumentbeskrivelse> dokumentbeskrivelse;
    protected String registreringsID;
    @XmlElement(required = true)
    protected String tittel;
    protected String offentligTittel;
    protected String beskrivelse;
    protected List<String> noekkelord;
    protected List<String> forfatter;
    protected String dokumentmedium;
    protected List<String> oppbevaringssted;
    protected Object virksomhetsspesifikkeMetadata;
    protected List<Merknad> merknad;
    protected List<Kryssreferanse> kryssreferanse;
    protected List<Korrespondansepart> korrespondansepart;

    /**
     * Gets the value of the systemID property.
     * 
     * @return
     *     possible object is
     *     {@link SystemID }
     *     
     */
    public SystemID getSystemID() {
        return systemID;
    }

    /**
     * Sets the value of the systemID property.
     * 
     * @param value
     *     allowed object is
     *     {@link SystemID }
     *     
     */
    public void setSystemID(SystemID value) {
        this.systemID = value;
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
     * Gets the value of the arkivertDato property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getArkivertDato() {
        return arkivertDato;
    }

    /**
     * Sets the value of the arkivertDato property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setArkivertDato(XMLGregorianCalendar value) {
        this.arkivertDato = value;
    }

    /**
     * Gets the value of the arkivertAv property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArkivertAv() {
        return arkivertAv;
    }

    /**
     * Sets the value of the arkivertAv property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArkivertAv(String value) {
        this.arkivertAv = value;
    }

    /**
     * Gets the value of the referanseForelderMappe property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferanseForelderMappe() {
        return referanseForelderMappe;
    }

    /**
     * Sets the value of the referanseForelderMappe property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferanseForelderMappe(String value) {
        this.referanseForelderMappe = value;
    }

    /**
     * Gets the value of the referanseArkivdel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferanseArkivdel() {
        return referanseArkivdel;
    }

    /**
     * Sets the value of the referanseArkivdel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferanseArkivdel(String value) {
        this.referanseArkivdel = value;
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
     * Gets the value of the dokumentbeskrivelse property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the dokumentbeskrivelse property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDokumentbeskrivelse().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Dokumentbeskrivelse }
     * 
     * 
     */
    public List<Dokumentbeskrivelse> getDokumentbeskrivelse() {
        if (dokumentbeskrivelse == null) {
            dokumentbeskrivelse = new ArrayList<>();
        }
        return this.dokumentbeskrivelse;
    }

    /**
     * Gets the value of the registreringsID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegistreringsID() {
        return registreringsID;
    }

    /**
     * Sets the value of the registreringsID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegistreringsID(String value) {
        this.registreringsID = value;
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
     * Gets the value of the forfatter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the forfatter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getForfatter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getForfatter() {
        if (forfatter == null) {
            forfatter = new ArrayList<>();
        }
        return this.forfatter;
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
     * Gets the value of the korrespondansepart property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the korrespondansepart property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKorrespondansepart().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Korrespondansepart }
     * 
     * 
     */
    public List<Korrespondansepart> getKorrespondansepart() {
        if (korrespondansepart == null) {
            korrespondansepart = new ArrayList<>();
        }
        return this.korrespondansepart;
    }

}
