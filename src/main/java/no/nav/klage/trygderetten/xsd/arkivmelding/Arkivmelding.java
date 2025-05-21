
package no.nav.klage.trygderetten.xsd.arkivmelding;

import jakarta.xml.bind.annotation.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for arkivmelding complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="arkivmelding"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="system" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="meldingId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="tidspunkt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="antallFiler" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
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
@XmlType(name = "arkivmelding", propOrder = {
    "system",
    "meldingId",
    "tidspunkt",
    "antallFiler",
    "mappe",
    "registrering"
})
@jakarta.xml.bind.annotation.XmlRootElement
public class Arkivmelding {

    @XmlElement(required = true)
    protected String system;
    @XmlElement(required = true)
    protected String meldingId;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar tidspunkt;
    protected int antallFiler;
    protected List<Mappe> mappe;
    protected List<Registrering> registrering;

    /**
     * Gets the value of the system property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSystem() {
        return system;
    }

    /**
     * Sets the value of the system property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSystem(String value) {
        this.system = value;
    }

    /**
     * Gets the value of the meldingId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMeldingId() {
        return meldingId;
    }

    /**
     * Sets the value of the meldingId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMeldingId(String value) {
        this.meldingId = value;
    }

    /**
     * Gets the value of the tidspunkt property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTidspunkt() {
        return tidspunkt;
    }

    /**
     * Sets the value of the tidspunkt property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTidspunkt(XMLGregorianCalendar value) {
        this.tidspunkt = value;
    }

    /**
     * Gets the value of the antallFiler property.
     * 
     */
    public int getAntallFiler() {
        return antallFiler;
    }

    /**
     * Sets the value of the antallFiler property.
     * 
     */
    public void setAntallFiler(int value) {
        this.antallFiler = value;
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
