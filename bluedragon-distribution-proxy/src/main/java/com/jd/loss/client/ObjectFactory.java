
package com.jd.loss.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.jd.loss.client package. 
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

    private final static QName _GetLossProductCountOrderId_QNAME = new QName("http://ws.ldms.pis.bk.jd.com/", "getLossProductCountOrderId");
    private final static QName _GetLossProductByOrderId_QNAME = new QName("http://ws.ldms.pis.bk.jd.com/", "getLossProductByOrderId");
    private final static QName _GetLossProductByOrderIdResponse_QNAME = new QName("http://ws.ldms.pis.bk.jd.com/", "getLossProductByOrderIdResponse");
    private final static QName _GetLossProductCountOrderIdResponse_QNAME = new QName("http://ws.ldms.pis.bk.jd.com/", "getLossProductCountOrderIdResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.jd.loss.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetLossProductCountOrderId }
     * 
     */
    public GetLossProductCountOrderId createGetLossProductCountOrderId() {
        return new GetLossProductCountOrderId();
    }

    /**
     * Create an instance of {@link GetLossProductByOrderIdResponse }
     * 
     */
    public GetLossProductByOrderIdResponse createGetLossProductByOrderIdResponse() {
        return new GetLossProductByOrderIdResponse();
    }

    /**
     * Create an instance of {@link GetLossProductByOrderId }
     * 
     */
    public GetLossProductByOrderId createGetLossProductByOrderId() {
        return new GetLossProductByOrderId();
    }

    /**
     * Create an instance of {@link GetLossProductCountOrderIdResponse }
     * 
     */
    public GetLossProductCountOrderIdResponse createGetLossProductCountOrderIdResponse() {
        return new GetLossProductCountOrderIdResponse();
    }

    /**
     * Create an instance of {@link LossProduct }
     * 
     */
    public LossProduct createLossProduct() {
        return new LossProduct();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetLossProductCountOrderId }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.ldms.pis.bk.jd.com/", name = "getLossProductCountOrderId")
    public JAXBElement<GetLossProductCountOrderId> createGetLossProductCountOrderId(GetLossProductCountOrderId value) {
        return new JAXBElement<GetLossProductCountOrderId>(_GetLossProductCountOrderId_QNAME, GetLossProductCountOrderId.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetLossProductByOrderId }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.ldms.pis.bk.jd.com/", name = "getLossProductByOrderId")
    public JAXBElement<GetLossProductByOrderId> createGetLossProductByOrderId(GetLossProductByOrderId value) {
        return new JAXBElement<GetLossProductByOrderId>(_GetLossProductByOrderId_QNAME, GetLossProductByOrderId.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetLossProductByOrderIdResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.ldms.pis.bk.jd.com/", name = "getLossProductByOrderIdResponse")
    public JAXBElement<GetLossProductByOrderIdResponse> createGetLossProductByOrderIdResponse(GetLossProductByOrderIdResponse value) {
        return new JAXBElement<GetLossProductByOrderIdResponse>(_GetLossProductByOrderIdResponse_QNAME, GetLossProductByOrderIdResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetLossProductCountOrderIdResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.ldms.pis.bk.jd.com/", name = "getLossProductCountOrderIdResponse")
    public JAXBElement<GetLossProductCountOrderIdResponse> createGetLossProductCountOrderIdResponse(GetLossProductCountOrderIdResponse value) {
        return new JAXBElement<GetLossProductCountOrderIdResponse>(_GetLossProductCountOrderIdResponse_QNAME, GetLossProductCountOrderIdResponse.class, null, value);
    }

}
