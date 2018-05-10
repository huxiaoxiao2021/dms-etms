
package com.jd.fms.finance.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.jd.fms.finance.client package. 
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

    private final static QName _GetCaiWuMessageByOrderId_QNAME = new QName("http://server.financialapp.fms.jd.com/", "getCaiWuMessageByOrderId");
    private final static QName _GetFinancialOrderByOrderIdResponse_QNAME = new QName("http://server.financialapp.fms.jd.com/", "getFinancialOrderByOrderIdResponse");
    private final static QName _GetFinancialOrderByOrderId_QNAME = new QName("http://server.financialapp.fms.jd.com/", "getFinancialOrderByOrderId");
    private final static QName _GetCaiWuMessageByOrderIdResponse_QNAME = new QName("http://server.financialapp.fms.jd.com/", "getCaiWuMessageByOrderIdResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.jd.fms.finance.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetCaiWuMessageByOrderId }
     * 
     */
    public GetCaiWuMessageByOrderId createGetCaiWuMessageByOrderId() {
        return new GetCaiWuMessageByOrderId();
    }

    /**
     * Create an instance of {@link GetFinancialOrderByOrderIdResponse }
     * 
     */
    public GetFinancialOrderByOrderIdResponse createGetFinancialOrderByOrderIdResponse() {
        return new GetFinancialOrderByOrderIdResponse();
    }

    /**
     * Create an instance of {@link GroupModel }
     * 
     */
    public GroupModel createGroupModel() {
        return new GroupModel();
    }

    /**
     * Create an instance of {@link GetFinancialOrderByOrderId }
     * 
     */
    public GetFinancialOrderByOrderId createGetFinancialOrderByOrderId() {
        return new GetFinancialOrderByOrderId();
    }

    /**
     * Create an instance of {@link GetCaiWuMessageByOrderIdResponse }
     * 
     */
    public GetCaiWuMessageByOrderIdResponse createGetCaiWuMessageByOrderIdResponse() {
        return new GetCaiWuMessageByOrderIdResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCaiWuMessageByOrderId }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.financialapp.fms.jd.com/", name = "getCaiWuMessageByOrderId")
    public JAXBElement<GetCaiWuMessageByOrderId> createGetCaiWuMessageByOrderId(GetCaiWuMessageByOrderId value) {
        return new JAXBElement<GetCaiWuMessageByOrderId>(_GetCaiWuMessageByOrderId_QNAME, GetCaiWuMessageByOrderId.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFinancialOrderByOrderIdResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.financialapp.fms.jd.com/", name = "getFinancialOrderByOrderIdResponse")
    public JAXBElement<GetFinancialOrderByOrderIdResponse> createGetFinancialOrderByOrderIdResponse(GetFinancialOrderByOrderIdResponse value) {
        return new JAXBElement<GetFinancialOrderByOrderIdResponse>(_GetFinancialOrderByOrderIdResponse_QNAME, GetFinancialOrderByOrderIdResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFinancialOrderByOrderId }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.financialapp.fms.jd.com/", name = "getFinancialOrderByOrderId")
    public JAXBElement<GetFinancialOrderByOrderId> createGetFinancialOrderByOrderId(GetFinancialOrderByOrderId value) {
        return new JAXBElement<GetFinancialOrderByOrderId>(_GetFinancialOrderByOrderId_QNAME, GetFinancialOrderByOrderId.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCaiWuMessageByOrderIdResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.financialapp.fms.jd.com/", name = "getCaiWuMessageByOrderIdResponse")
    public JAXBElement<GetCaiWuMessageByOrderIdResponse> createGetCaiWuMessageByOrderIdResponse(GetCaiWuMessageByOrderIdResponse value) {
        return new JAXBElement<GetCaiWuMessageByOrderIdResponse>(_GetCaiWuMessageByOrderIdResponse_QNAME, GetCaiWuMessageByOrderIdResponse.class, null, value);
    }

}
