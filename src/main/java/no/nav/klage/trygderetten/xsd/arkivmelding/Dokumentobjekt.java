
package no.nav.klage.trygderetten.xsd.arkivmelding;

import jakarta.xml.bind.annotation.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;


/**
 * <p>Java class for dokumentobjekt complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dokumentobjekt"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="versjonsnummer" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}versjonsnummer"/&gt;
 *         &lt;element name="variantformat" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}variantformat"/&gt;
 *         &lt;element name="format" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}format"/&gt;
 *         &lt;element name="formatDetaljer" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}formatDetaljer" minOccurs="0"/&gt;
 *         &lt;element name="mimeType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="opprettetDato" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}opprettetDato" minOccurs="0"/&gt;
 *         &lt;element name="opprettetAv" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}opprettetAv" minOccurs="0"/&gt;
 *         &lt;element name="referanseDokumentfil" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}referanseDokumentfil"/&gt;
 *         &lt;element name="sjekksum" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}sjekksum" minOccurs="0"/&gt;
 *         &lt;element name="sjekksumAlgoritme" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}sjekksumAlgoritme" minOccurs="0"/&gt;
 *         &lt;element name="filstoerrelse" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}filstoerrelse" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dokumentobjekt", propOrder = {
    "versjonsnummer",
    "variantformat",
    "format",
    "formatDetaljer",
    "mimeType",
    "opprettetDato",
    "opprettetAv",
    "referanseDokumentfil",
    "sjekksum",
    "sjekksumAlgoritme",
    "filstoerrelse"
})
public class Dokumentobjekt {

    @XmlElement(required = true)
    protected BigInteger versjonsnummer;
    @XmlElement(required = true)
    protected String variantformat;
    @XmlElement(required = true)
    protected String format;
    protected String formatDetaljer;
    protected String mimeType;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar opprettetDato;
    protected String opprettetAv;
    @XmlElement(required = true)
    protected String referanseDokumentfil;
    protected String sjekksum;
    protected String sjekksumAlgoritme;
    protected BigInteger filstoerrelse;

    /**
     * Gets the value of the versjonsnummer property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getVersjonsnummer() {
        return versjonsnummer;
    }

    /**
     * Sets the value of the versjonsnummer property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setVersjonsnummer(BigInteger value) {
        this.versjonsnummer = value;
    }

    /**
     * Gets the value of the variantformat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVariantformat() {
        return variantformat;
    }

    /**
     * Sets the value of the variantformat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVariantformat(String value) {
        this.variantformat = value;
    }

    /**
     * Gets the value of the format property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets the value of the format property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormat(String value) {
        this.format = value;
    }

    /**
     * Gets the value of the formatDetaljer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormatDetaljer() {
        return formatDetaljer;
    }

    /**
     * Sets the value of the formatDetaljer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormatDetaljer(String value) {
        this.formatDetaljer = value;
    }

    /**
     * Gets the value of the mimeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sets the value of the mimeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMimeType(String value) {
        this.mimeType = value;
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
     * Gets the value of the referanseDokumentfil property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferanseDokumentfil() {
        return referanseDokumentfil;
    }

    /**
     * Sets the value of the referanseDokumentfil property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferanseDokumentfil(String value) {
        this.referanseDokumentfil = value;
    }

    /**
     * Gets the value of the sjekksum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSjekksum() {
        return sjekksum;
    }

    /**
     * Sets the value of the sjekksum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSjekksum(String value) {
        this.sjekksum = value;
    }

    /**
     * Gets the value of the sjekksumAlgoritme property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSjekksumAlgoritme() {
        return sjekksumAlgoritme;
    }

    /**
     * Sets the value of the sjekksumAlgoritme property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSjekksumAlgoritme(String value) {
        this.sjekksumAlgoritme = value;
    }

    /**
     * Gets the value of the filstoerrelse property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFilstoerrelse() {
        return filstoerrelse;
    }

    /**
     * Sets the value of the filstoerrelse property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFilstoerrelse(BigInteger value) {
        this.filstoerrelse = value;
    }

}
