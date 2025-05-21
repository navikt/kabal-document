
package no.nav.klage.trygderetten.xsd.arkivmelding;

import jakarta.xml.bind.annotation.*;

import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for gradering complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="gradering"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="grad" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}grad"/&gt;
 *         &lt;element name="graderingsdato" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}graderingsdato"/&gt;
 *         &lt;element name="gradertAv" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}gradertAv"/&gt;
 *         &lt;element name="nedgraderingsdato" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}nedgraderingsdato" minOccurs="0"/&gt;
 *         &lt;element name="nedgradertAv" type="{http://www.arkivverket.no/standarder/noark5/metadatakatalog}nedgradertAv" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "gradering", propOrder = {
    "grad",
    "graderingsdato",
    "gradertAv",
    "nedgraderingsdato",
    "nedgradertAv"
})
public class Gradering {

    @XmlElement(required = true)
    protected String grad;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar graderingsdato;
    @XmlElement(required = true)
    protected String gradertAv;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar nedgraderingsdato;
    protected String nedgradertAv;

    /**
     * Gets the value of the grad property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGrad() {
        return grad;
    }

    /**
     * Sets the value of the grad property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGrad(String value) {
        this.grad = value;
    }

    /**
     * Gets the value of the graderingsdato property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getGraderingsdato() {
        return graderingsdato;
    }

    /**
     * Sets the value of the graderingsdato property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setGraderingsdato(XMLGregorianCalendar value) {
        this.graderingsdato = value;
    }

    /**
     * Gets the value of the gradertAv property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGradertAv() {
        return gradertAv;
    }

    /**
     * Sets the value of the gradertAv property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGradertAv(String value) {
        this.gradertAv = value;
    }

    /**
     * Gets the value of the nedgraderingsdato property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getNedgraderingsdato() {
        return nedgraderingsdato;
    }

    /**
     * Sets the value of the nedgraderingsdato property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setNedgraderingsdato(XMLGregorianCalendar value) {
        this.nedgraderingsdato = value;
    }

    /**
     * Gets the value of the nedgradertAv property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNedgradertAv() {
        return nedgradertAv;
    }

    /**
     * Sets the value of the nedgradertAv property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNedgradertAv(String value) {
        this.nedgradertAv = value;
    }

}
