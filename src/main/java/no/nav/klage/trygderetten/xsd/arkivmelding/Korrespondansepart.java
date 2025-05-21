
package no.nav.klage.trygderetten.xsd.arkivmelding;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for korrespondansepart complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="korrespondansepart"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="korrespondanseparttype" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}korrespondanseparttype"/&gt;
 *         &lt;element name="korrespondansepartNavn" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}korrespondansepartNavn"/&gt;
 *         &lt;choice minOccurs="0"&gt;
 *           &lt;element name="organisasjonsnummer" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}EnhetsidentifikatorType" minOccurs="0"/&gt;
 *           &lt;element name="foedselsnummer" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}FoedselsnummerType" minOccurs="0"/&gt;
 *           &lt;element name="DNummer" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}DNummerType" minOccurs="0"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="postadresse" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}postadresse" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="postnummer" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}postnummer" minOccurs="0"/&gt;
 *         &lt;element name="poststed" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}poststed" minOccurs="0"/&gt;
 *         &lt;element name="land" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}land" minOccurs="0"/&gt;
 *         &lt;element name="epostadresse" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}epostadresse" minOccurs="0"/&gt;
 *         &lt;element name="telefonnummer" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}telefonnummer" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="kontaktperson" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}kontaktperson" minOccurs="0"/&gt;
 *         &lt;element name="administrativEnhet" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}administrativEnhet" minOccurs="0"/&gt;
 *         &lt;element name="saksbehandler" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}saksbehandler" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "korrespondansepart", propOrder = {
    "korrespondanseparttype",
    "korrespondansepartNavn",
    "organisasjonsnummer",
    "foedselsnummer",
    "dNummer",
    "postadresse",
    "postnummer",
    "poststed",
    "land",
    "epostadresse",
    "telefonnummer",
    "kontaktperson",
    "administrativEnhet",
    "saksbehandler"
})
public class Korrespondansepart {

    @XmlElement(required = true)
    protected String korrespondanseparttype;
    @XmlElement(required = true)
    protected String korrespondansepartNavn;
    protected EnhetsidentifikatorType organisasjonsnummer;
    protected FoedselsnummerType foedselsnummer;
    @XmlElement(name = "DNummer")
    protected DNummerType dNummer;
    protected List<String> postadresse;
    protected String postnummer;
    protected String poststed;
    protected String land;
    protected String epostadresse;
    protected List<String> telefonnummer;
    protected String kontaktperson;
    protected String administrativEnhet;
    protected String saksbehandler;

    /**
     * Gets the value of the korrespondanseparttype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKorrespondanseparttype() {
        return korrespondanseparttype;
    }

    /**
     * Sets the value of the korrespondanseparttype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKorrespondanseparttype(String value) {
        this.korrespondanseparttype = value;
    }

    /**
     * Gets the value of the korrespondansepartNavn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKorrespondansepartNavn() {
        return korrespondansepartNavn;
    }

    /**
     * Sets the value of the korrespondansepartNavn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKorrespondansepartNavn(String value) {
        this.korrespondansepartNavn = value;
    }

    /**
     * Gets the value of the organisasjonsnummer property.
     * 
     * @return
     *     possible object is
     *     {@link EnhetsidentifikatorType }
     *     
     */
    public EnhetsidentifikatorType getOrganisasjonsnummer() {
        return organisasjonsnummer;
    }

    /**
     * Sets the value of the organisasjonsnummer property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnhetsidentifikatorType }
     *     
     */
    public void setOrganisasjonsnummer(EnhetsidentifikatorType value) {
        this.organisasjonsnummer = value;
    }

    /**
     * Gets the value of the foedselsnummer property.
     * 
     * @return
     *     possible object is
     *     {@link FoedselsnummerType }
     *     
     */
    public FoedselsnummerType getFoedselsnummer() {
        return foedselsnummer;
    }

    /**
     * Sets the value of the foedselsnummer property.
     * 
     * @param value
     *     allowed object is
     *     {@link FoedselsnummerType }
     *     
     */
    public void setFoedselsnummer(FoedselsnummerType value) {
        this.foedselsnummer = value;
    }

    /**
     * Gets the value of the dNummer property.
     * 
     * @return
     *     possible object is
     *     {@link DNummerType }
     *     
     */
    public DNummerType getDNummer() {
        return dNummer;
    }

    /**
     * Sets the value of the dNummer property.
     * 
     * @param value
     *     allowed object is
     *     {@link DNummerType }
     *     
     */
    public void setDNummer(DNummerType value) {
        this.dNummer = value;
    }

    /**
     * Gets the value of the postadresse property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the postadresse property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPostadresse().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getPostadresse() {
        if (postadresse == null) {
            postadresse = new ArrayList<>();
        }
        return this.postadresse;
    }

    /**
     * Gets the value of the postnummer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostnummer() {
        return postnummer;
    }

    /**
     * Sets the value of the postnummer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostnummer(String value) {
        this.postnummer = value;
    }

    /**
     * Gets the value of the poststed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPoststed() {
        return poststed;
    }

    /**
     * Sets the value of the poststed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPoststed(String value) {
        this.poststed = value;
    }

    /**
     * Gets the value of the land property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLand() {
        return land;
    }

    /**
     * Sets the value of the land property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLand(String value) {
        this.land = value;
    }

    /**
     * Gets the value of the epostadresse property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEpostadresse() {
        return epostadresse;
    }

    /**
     * Sets the value of the epostadresse property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEpostadresse(String value) {
        this.epostadresse = value;
    }

    /**
     * Gets the value of the telefonnummer property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the telefonnummer property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTelefonnummer().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getTelefonnummer() {
        if (telefonnummer == null) {
            telefonnummer = new ArrayList<>();
        }
        return this.telefonnummer;
    }

    /**
     * Gets the value of the kontaktperson property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKontaktperson() {
        return kontaktperson;
    }

    /**
     * Sets the value of the kontaktperson property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKontaktperson(String value) {
        this.kontaktperson = value;
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
     * Gets the value of the saksbehandler property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSaksbehandler() {
        return saksbehandler;
    }

    /**
     * Sets the value of the saksbehandler property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSaksbehandler(String value) {
        this.saksbehandler = value;
    }

}
