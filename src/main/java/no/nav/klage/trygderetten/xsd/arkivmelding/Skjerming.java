
package no.nav.klage.trygderetten.xsd.arkivmelding;

import jakarta.xml.bind.annotation.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for skjerming complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="skjerming"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="tilgangsrestriksjon" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}tilgangsrestriksjon"/&gt;
 *         &lt;element name="skjermingshjemmel" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}skjermingshjemmel"/&gt;
 *         &lt;element name="skjermingMetadata" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}skjermingMetadata" maxOccurs="unbounded"/&gt;
 *         &lt;element name="skjermingDokument" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}skjermingDokument" minOccurs="0"/&gt;
 *         &lt;element name="skjermingsvarighet" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}skjermingsvarighet" minOccurs="0"/&gt;
 *         &lt;element name="skjermingOpphoererDato" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}skjermingOpphoererDato" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "skjerming", propOrder = {
    "tilgangsrestriksjon",
    "skjermingshjemmel",
    "skjermingMetadata",
    "skjermingDokument",
    "skjermingsvarighet",
    "skjermingOpphoererDato"
})
public class Skjerming {

    @XmlElement(required = true)
    protected String tilgangsrestriksjon;
    @XmlElement(required = true)
    protected String skjermingshjemmel;
    @XmlElement(required = true)
    protected List<String> skjermingMetadata;
    protected String skjermingDokument;
    protected BigInteger skjermingsvarighet;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar skjermingOpphoererDato;

    /**
     * Gets the value of the tilgangsrestriksjon property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTilgangsrestriksjon() {
        return tilgangsrestriksjon;
    }

    /**
     * Sets the value of the tilgangsrestriksjon property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTilgangsrestriksjon(String value) {
        this.tilgangsrestriksjon = value;
    }

    /**
     * Gets the value of the skjermingshjemmel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSkjermingshjemmel() {
        return skjermingshjemmel;
    }

    /**
     * Sets the value of the skjermingshjemmel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSkjermingshjemmel(String value) {
        this.skjermingshjemmel = value;
    }

    /**
     * Gets the value of the skjermingMetadata property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the skjermingMetadata property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSkjermingMetadata().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSkjermingMetadata() {
        if (skjermingMetadata == null) {
            skjermingMetadata = new ArrayList<>();
        }
        return this.skjermingMetadata;
    }

    /**
     * Gets the value of the skjermingDokument property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSkjermingDokument() {
        return skjermingDokument;
    }

    /**
     * Sets the value of the skjermingDokument property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSkjermingDokument(String value) {
        this.skjermingDokument = value;
    }

    /**
     * Gets the value of the skjermingsvarighet property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSkjermingsvarighet() {
        return skjermingsvarighet;
    }

    /**
     * Sets the value of the skjermingsvarighet property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSkjermingsvarighet(BigInteger value) {
        this.skjermingsvarighet = value;
    }

    /**
     * Gets the value of the skjermingOpphoererDato property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSkjermingOpphoererDato() {
        return skjermingOpphoererDato;
    }

    /**
     * Sets the value of the skjermingOpphoererDato property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSkjermingOpphoererDato(XMLGregorianCalendar value) {
        this.skjermingOpphoererDato = value;
    }

}
