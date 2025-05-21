
package no.nav.klage.trygderetten.xsd.arkivmelding;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AbstraktNasjonalidentifikatorType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstraktNasjonalidentifikatorType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.arkivverket.no/standarder/noark5/arkivmelding}AbstraktResourceType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="systemID" type="{http://www.arkivverket.no/standarder/noark5/arkivmelding}GUID" minOccurs="0"/&gt;
 *         &lt;element name="beskrivelse" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstraktNasjonalidentifikatorType", propOrder = {
    "systemID",
    "beskrivelse"
})
@XmlSeeAlso({
    EnhetsidentifikatorType.class,
    AbstraktPersonidentifikatorType.class
})
public abstract class AbstraktNasjonalidentifikatorType
    extends AbstraktResourceType
{

    protected String systemID;
    protected String beskrivelse;

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

}
