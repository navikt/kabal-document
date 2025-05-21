
package no.nav.klage.trygderetten.xsd.metadata;

import jakarta.xml.bind.annotation.*;


/**
 * M001
 * 
 * <p>Java class for systemID complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="systemID"&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="&lt;http://www.arkivverket.no/standarder/noark5/metadatakatalog&gt;ID"&gt;
 *       &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "systemID", propOrder = {
    "value"
})
public class SystemID {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "label")
    protected String label;

    /**
     * M001-a
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabel(String value) {
        this.label = value;
    }

}
