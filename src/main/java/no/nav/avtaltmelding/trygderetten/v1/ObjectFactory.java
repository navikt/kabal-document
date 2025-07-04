//
// This file was generated by the Eclipse Implementation of JAXB, v4.0.3 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
//


package no.nav.avtaltmelding.trygderetten.v1;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;

import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the no.nav.avtaltmelding.trygderetten.v1 package. 
 * <p>An ObjectFactory allows you to programmatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _NavMappe_QNAME = new QName("http://www.nav.no/avtaltmelding/trygderetten", "navMappe");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: no.nav.avtaltmelding.trygderetten.v1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link NavMappe }
     * 
     * @return
     *     the new instance of {@link NavMappe }
     */
    public NavMappe createNavMappe() {
        return new NavMappe();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NavMappe }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link NavMappe }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.nav.no/avtaltmelding/trygderetten", name = "navMappe")
    public JAXBElement<NavMappe> createNavMappe(NavMappe value) {
        return new JAXBElement<>(_NavMappe_QNAME, NavMappe.class, null, value);
    }

}
