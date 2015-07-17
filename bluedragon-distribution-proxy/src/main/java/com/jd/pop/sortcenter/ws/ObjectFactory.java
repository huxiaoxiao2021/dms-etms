
package com.jd.pop.sortcenter.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.jd.pop.sortcenter.ws package. 
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

    private final static QName _SearchInfoForByOrderId_QNAME = new QName("http://offlineorder.service.order.pop.jd.com/", "searchInfoForByOrderId");
    private final static QName _SearchInfoForByOrderIdResponse_QNAME = new QName("http://offlineorder.service.order.pop.jd.com/", "searchInfoForByOrderIdResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.jd.pop.sortcenter.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SearchInfoForByOrderIdResponse }
     * 
     */
    public SearchInfoForByOrderIdResponse createSearchInfoForByOrderIdResponse() {
        return new SearchInfoForByOrderIdResponse();
    }

    /**
     * Create an instance of {@link SearchInfoForByOrderId }
     * 
     */
    public SearchInfoForByOrderId createSearchInfoForByOrderId() {
        return new SearchInfoForByOrderId();
    }

    /**
     * Create an instance of {@link VenderOperateInfo }
     * 
     */
    public VenderOperateInfo createVenderOperateInfo() {
        return new VenderOperateInfo();
    }

    /**
     * Create an instance of {@link VenderOperInfoResult }
     * 
     */
    public VenderOperInfoResult createVenderOperInfoResult() {
        return new VenderOperInfoResult();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchInfoForByOrderId }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://offlineorder.service.order.pop.jd.com/", name = "searchInfoForByOrderId")
    public JAXBElement<SearchInfoForByOrderId> createSearchInfoForByOrderId(SearchInfoForByOrderId value) {
        return new JAXBElement<SearchInfoForByOrderId>(_SearchInfoForByOrderId_QNAME, SearchInfoForByOrderId.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchInfoForByOrderIdResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://offlineorder.service.order.pop.jd.com/", name = "searchInfoForByOrderIdResponse")
    public JAXBElement<SearchInfoForByOrderIdResponse> createSearchInfoForByOrderIdResponse(SearchInfoForByOrderIdResponse value) {
        return new JAXBElement<SearchInfoForByOrderIdResponse>(_SearchInfoForByOrderIdResponse_QNAME, SearchInfoForByOrderIdResponse.class, null, value);
    }

}
