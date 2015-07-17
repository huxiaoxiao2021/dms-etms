
package com.jd.bluedragon.distribution.popAbnormal.ws.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.jd.bluedragon.distribution.popAbnormal.ws.client package. 
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

    private final static QName _SavePopAbnormalOrderPackageResponse_QNAME = new QName("http://service.orderpackage.pop.jd.com/", "savePopAbnormalOrderPackageResponse");
    private final static QName _SavePopAbnormalOrderPackage_QNAME = new QName("http://service.orderpackage.pop.jd.com/", "savePopAbnormalOrderPackage");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.jd.bluedragon.distribution.popAbnormal.ws.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SavePopAbnormalOrderPackageResponse }
     * 
     */
    public SavePopAbnormalOrderPackageResponse createSavePopAbnormalOrderPackageResponse() {
        return new SavePopAbnormalOrderPackageResponse();
    }

    /**
     * Create an instance of {@link SavePopAbnormalOrderPackage }
     * 
     */
    public SavePopAbnormalOrderPackage createSavePopAbnormalOrderPackage() {
        return new SavePopAbnormalOrderPackage();
    }

    /**
     * Create an instance of {@link AbnormalResult }
     * 
     */
    public AbnormalResult createAbnormalResult() {
        return new AbnormalResult();
    }

    /**
     * Create an instance of {@link PopAbnormalOrderVo }
     * 
     */
    public PopAbnormalOrderVo createPopAbnormalOrderVo() {
        return new PopAbnormalOrderVo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SavePopAbnormalOrderPackageResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.orderpackage.pop.jd.com/", name = "savePopAbnormalOrderPackageResponse")
    public JAXBElement<SavePopAbnormalOrderPackageResponse> createSavePopAbnormalOrderPackageResponse(SavePopAbnormalOrderPackageResponse value) {
        return new JAXBElement<SavePopAbnormalOrderPackageResponse>(_SavePopAbnormalOrderPackageResponse_QNAME, SavePopAbnormalOrderPackageResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SavePopAbnormalOrderPackage }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.orderpackage.pop.jd.com/", name = "savePopAbnormalOrderPackage")
    public JAXBElement<SavePopAbnormalOrderPackage> createSavePopAbnormalOrderPackage(SavePopAbnormalOrderPackage value) {
        return new JAXBElement<SavePopAbnormalOrderPackage>(_SavePopAbnormalOrderPackage_QNAME, SavePopAbnormalOrderPackage.class, null, value);
    }

}
