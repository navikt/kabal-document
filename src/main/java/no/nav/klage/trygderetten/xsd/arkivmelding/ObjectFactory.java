
package no.nav.klage.trygderetten.xsd.arkivmelding;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;

import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the no.nav.klage.gradle.plugin.xsd2java.xsd.arkivmelding package. 
 * <p>An ObjectFactory allows you to programatically 
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

    private static final QName _Arkivmelding_QNAME = new QName("http://www.arkivverket.no/standarder/noark5/arkivmelding", "arkivmelding");
    private static final QName _Foedselsnummer_QNAME = new QName("http://www.arkivverket.no/standarder/noark5/arkivmelding", "Foedselsnummer");
    private static final QName _DNummer_QNAME = new QName("http://www.arkivverket.no/standarder/noark5/arkivmelding", "DNummer");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: no.nav.klage.gradle.plugin.xsd2java.xsd.arkivmelding
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Arkivmelding }
     * 
     */
    public Arkivmelding createArkivmelding() {
        return new Arkivmelding();
    }

    /**
     * Create an instance of {@link FoedselsnummerType }
     * 
     */
    public FoedselsnummerType createFoedselsnummerType() {
        return new FoedselsnummerType();
    }

    /**
     * Create an instance of {@link DNummerType }
     * 
     */
    public DNummerType createDNummerType() {
        return new DNummerType();
    }

    /**
     * Create an instance of {@link Mappe }
     * 
     */
    public Mappe createMappe() {
        return new Mappe();
    }

    /**
     * Create an instance of {@link Saksmappe }
     * 
     */
    public Saksmappe createSaksmappe() {
        return new Saksmappe();
    }

    /**
     * Create an instance of {@link Part }
     * 
     */
    public Part createPart() {
        return new Part();
    }

    /**
     * Create an instance of {@link Registrering }
     * 
     */
    public Registrering createRegistrering() {
        return new Registrering();
    }

    /**
     * Create an instance of {@link Journalpost }
     * 
     */
    public Journalpost createJournalpost() {
        return new Journalpost();
    }

    /**
     * Create an instance of {@link Korrespondansepart }
     * 
     */
    public Korrespondansepart createKorrespondansepart() {
        return new Korrespondansepart();
    }

    /**
     * Create an instance of {@link Avskrivning }
     * 
     */
    public Avskrivning createAvskrivning() {
        return new Avskrivning();
    }

    /**
     * Create an instance of {@link Arkivnotat }
     * 
     */
    public Arkivnotat createArkivnotat() {
        return new Arkivnotat();
    }

    /**
     * Create an instance of {@link Dokumentflyt }
     * 
     */
    public Dokumentflyt createDokumentflyt() {
        return new Dokumentflyt();
    }

    /**
     * Create an instance of {@link Dokumentbeskrivelse }
     * 
     */
    public Dokumentbeskrivelse createDokumentbeskrivelse() {
        return new Dokumentbeskrivelse();
    }

    /**
     * Create an instance of {@link Dokumentobjekt }
     * 
     */
    public Dokumentobjekt createDokumentobjekt() {
        return new Dokumentobjekt();
    }

    /**
     * Create an instance of {@link Kryssreferanse }
     * 
     */
    public Kryssreferanse createKryssreferanse() {
        return new Kryssreferanse();
    }

    /**
     * Create an instance of {@link Merknad }
     * 
     */
    public Merknad createMerknad() {
        return new Merknad();
    }

    /**
     * Create an instance of {@link Skjerming }
     * 
     */
    public Skjerming createSkjerming() {
        return new Skjerming();
    }

    /**
     * Create an instance of {@link Gradering }
     * 
     */
    public Gradering createGradering() {
        return new Gradering();
    }

    /**
     * Create an instance of {@link Presedens }
     * 
     */
    public Presedens createPresedens() {
        return new Presedens();
    }

    /**
     * Create an instance of {@link Klasse }
     * 
     */
    public Klasse createKlasse() {
        return new Klasse();
    }

    /**
     * Create an instance of {@link EnhetsidentifikatorType }
     * 
     */
    public EnhetsidentifikatorType createEnhetsidentifikatorType() {
        return new EnhetsidentifikatorType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Arkivmelding }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Arkivmelding }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.arkivverket.no/standarder/noark5/arkivmelding", name = "arkivmelding")
    public JAXBElement<Arkivmelding> createArkivmelding(Arkivmelding value) {
        return new JAXBElement<>(_Arkivmelding_QNAME, Arkivmelding.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FoedselsnummerType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link FoedselsnummerType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.arkivverket.no/standarder/noark5/arkivmelding", name = "Foedselsnummer")
    public JAXBElement<FoedselsnummerType> createFoedselsnummer(FoedselsnummerType value) {
        return new JAXBElement<>(_Foedselsnummer_QNAME, FoedselsnummerType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DNummerType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link DNummerType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.arkivverket.no/standarder/noark5/arkivmelding", name = "DNummer")
    public JAXBElement<DNummerType> createDNummer(DNummerType value) {
        return new JAXBElement<>(_DNummer_QNAME, DNummerType.class, null, value);
    }

}
