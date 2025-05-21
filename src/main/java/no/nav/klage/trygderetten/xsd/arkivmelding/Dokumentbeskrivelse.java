
package no.nav.klage.trygderetten.xsd.arkivmelding;

import jakarta.xml.bind.annotation.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for dokumentbeskrivelse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dokumentbeskrivelse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="systemID" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}GUID" minOccurs="0"/&gt;
 *         &lt;element name="dokumenttype" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}dokumenttype"/&gt;
 *         &lt;element name="dokumentstatus" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}dokumentstatus"/&gt;
 *         &lt;element name="tittel" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}tittel"/&gt;
 *         &lt;element name="beskrivelse" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}beskrivelse" minOccurs="0"/&gt;
 *         &lt;element name="forfatter" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}forfatter" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="opprettetDato" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}opprettetDato" minOccurs="0"/&gt;
 *         &lt;element name="opprettetAv" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}opprettetAv" minOccurs="0"/&gt;
 *         &lt;element name="dokumentmedium" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}dokumentmedium" minOccurs="0"/&gt;
 *         &lt;element name="oppbevaringssted" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}oppbevaringssted" minOccurs="0"/&gt;
 *         &lt;element name="referanseArkivdel" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}referanseArkivdel" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="tilknyttetRegistreringSom" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}tilknyttetRegistreringSom"/&gt;
 *         &lt;element name="dokumentnummer" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}dokumentnummer" minOccurs="0"/&gt;
 *         &lt;element name="tilknyttetDato" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}tilknyttetDato" minOccurs="0"/&gt;
 *         &lt;element name="tilknyttetAv" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}tilknyttetAv" minOccurs="0"/&gt;
 *         &lt;element name="part" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}part" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="merknad" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}merknad" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="skjerming" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}skjerming" minOccurs="0"/&gt;
 *         &lt;element name="gradering" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}gradering" minOccurs="0"/&gt;
 *         &lt;element name="dokumentobjekt" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}dokumentobjekt" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dokumentbeskrivelse", propOrder = {
    "systemID",
    "dokumenttype",
    "dokumentstatus",
    "tittel",
    "beskrivelse",
    "forfatter",
    "opprettetDato",
    "opprettetAv",
    "dokumentmedium",
    "oppbevaringssted",
    "referanseArkivdel",
    "tilknyttetRegistreringSom",
    "dokumentnummer",
    "tilknyttetDato",
    "tilknyttetAv",
    "part",
    "merknad",
    "skjerming",
    "gradering",
    "dokumentobjekt"
})
public class Dokumentbeskrivelse {

    protected String systemID;
    @XmlElement(required = true)
    protected String dokumenttype;
    @XmlElement(required = true)
    protected String dokumentstatus;
    @XmlElement(required = true)
    protected String tittel;
    protected String beskrivelse;
    protected List<String> forfatter;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar opprettetDato;
    protected String opprettetAv;
    protected String dokumentmedium;
    protected String oppbevaringssted;
    protected List<String> referanseArkivdel;
    @XmlElement(required = true)
    protected String tilknyttetRegistreringSom;
    protected BigInteger dokumentnummer;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar tilknyttetDato;
    protected String tilknyttetAv;
    protected List<Part> part;
    protected List<Merknad> merknad;
    protected Skjerming skjerming;
    protected Gradering gradering;
    protected List<Dokumentobjekt> dokumentobjekt;

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
     * Gets the value of the dokumenttype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDokumenttype() {
        return dokumenttype;
    }

    /**
     * Sets the value of the dokumenttype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDokumenttype(String value) {
        this.dokumenttype = value;
    }

    /**
     * Gets the value of the dokumentstatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDokumentstatus() {
        return dokumentstatus;
    }

    /**
     * Sets the value of the dokumentstatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDokumentstatus(String value) {
        this.dokumentstatus = value;
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
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOppbevaringssted() {
        return oppbevaringssted;
    }

    /**
     * Sets the value of the oppbevaringssted property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOppbevaringssted(String value) {
        this.oppbevaringssted = value;
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
     * Gets the value of the tilknyttetRegistreringSom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTilknyttetRegistreringSom() {
        return tilknyttetRegistreringSom;
    }

    /**
     * Sets the value of the tilknyttetRegistreringSom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTilknyttetRegistreringSom(String value) {
        this.tilknyttetRegistreringSom = value;
    }

    /**
     * Gets the value of the dokumentnummer property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDokumentnummer() {
        return dokumentnummer;
    }

    /**
     * Sets the value of the dokumentnummer property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDokumentnummer(BigInteger value) {
        this.dokumentnummer = value;
    }

    /**
     * Gets the value of the tilknyttetDato property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTilknyttetDato() {
        return tilknyttetDato;
    }

    /**
     * Sets the value of the tilknyttetDato property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTilknyttetDato(XMLGregorianCalendar value) {
        this.tilknyttetDato = value;
    }

    /**
     * Gets the value of the tilknyttetAv property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTilknyttetAv() {
        return tilknyttetAv;
    }

    /**
     * Sets the value of the tilknyttetAv property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTilknyttetAv(String value) {
        this.tilknyttetAv = value;
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
     * Gets the value of the dokumentobjekt property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the dokumentobjekt property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDokumentobjekt().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Dokumentobjekt }
     * 
     * 
     */
    public List<Dokumentobjekt> getDokumentobjekt() {
        if (dokumentobjekt == null) {
            dokumentobjekt = new ArrayList<>();
        }
        return this.dokumentobjekt;
    }

}
