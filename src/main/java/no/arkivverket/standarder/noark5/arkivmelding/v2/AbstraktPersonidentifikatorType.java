//
// This file was generated by the Eclipse Implementation of JAXB, v4.0.3 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
//


package no.arkivverket.standarder.noark5.arkivmelding.v2;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AbstraktPersonidentifikatorType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="AbstraktPersonidentifikatorType">
 *   <complexContent>
 *     <extension base="{http://www.arkivverket.no/standarder/noark5/arkivmelding}AbstraktNasjonalidentifikatorType">
 *       <sequence>
 *       </sequence>
 *     </extension>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstraktPersonidentifikatorType")
@XmlSeeAlso({
    FoedselsnummerType.class
})
public abstract class AbstraktPersonidentifikatorType
    extends AbstraktNasjonalidentifikatorType
{


    @Override
    public AbstraktPersonidentifikatorType useBeskrivelse(String value) {
        setBeskrivelse(value);
        return this;
    }

    @Override
    public AbstraktPersonidentifikatorType useSystemID(String value) {
        setSystemID(value);
        return this;
    }

}
