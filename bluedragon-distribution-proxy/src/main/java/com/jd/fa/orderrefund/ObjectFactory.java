
package com.jd.fa.orderrefund;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.jd.fa.orderrefund package. 
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

    private final static QName _GetRecommandTypeResponse_QNAME = new QName("http://www.360buy.com", "getRecommandTypeResponse");
    private final static QName _GetRecommandType_QNAME = new QName("http://www.360buy.com", "getRecommandType");
    private final static QName _SendXmlMessage_QNAME = new QName("http://www.360buy.com", "sendXmlMessage");
    private final static QName _GetBlackUsersResponse_QNAME = new QName("http://www.360buy.com", "getBlackUsersResponse");
    private final static QName _GetRequestSheetsByOrderIdResponse_QNAME = new QName("http://www.360buy.com", "getRequestSheetsByOrderIdResponse");
    private final static QName _GetRequestSheetsByOrderId_QNAME = new QName("http://www.360buy.com", "getRequestSheetsByOrderId");
    private final static QName _GetBlackUsers_QNAME = new QName("http://www.360buy.com", "getBlackUsers");
    private final static QName _SendXmlMessageResponse_QNAME = new QName("http://www.360buy.com", "sendXmlMessageResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.jd.fa.orderrefund
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SendXmlMessage }
     * 
     */
    public SendXmlMessage createSendXmlMessage() {
        return new SendXmlMessage();
    }

    /**
     * Create an instance of {@link GetBlackUsersResponse }
     * 
     */
    public GetBlackUsersResponse createGetBlackUsersResponse() {
        return new GetBlackUsersResponse();
    }

    /**
     * Create an instance of {@link GetRequestSheetsByOrderIdResponse }
     * 
     */
    public GetRequestSheetsByOrderIdResponse createGetRequestSheetsByOrderIdResponse() {
        return new GetRequestSheetsByOrderIdResponse();
    }

    /**
     * Create an instance of {@link GetRequestSheetsByOrderId }
     * 
     */
    public GetRequestSheetsByOrderId createGetRequestSheetsByOrderId() {
        return new GetRequestSheetsByOrderId();
    }

    /**
     * Create an instance of {@link SendXmlMessageResponse }
     * 
     */
    public SendXmlMessageResponse createSendXmlMessageResponse() {
        return new SendXmlMessageResponse();
    }

    /**
     * Create an instance of {@link GetBlackUsers }
     * 
     */
    public GetBlackUsers createGetBlackUsers() {
        return new GetBlackUsers();
    }

    /**
     * Create an instance of {@link GetRecommandTypeResponse }
     * 
     */
    public GetRecommandTypeResponse createGetRecommandTypeResponse() {
        return new GetRecommandTypeResponse();
    }

    /**
     * Create an instance of {@link GetRecommandType }
     * 
     */
    public GetRecommandType createGetRecommandType() {
        return new GetRecommandType();
    }

    /**
     * Create an instance of {@link BlackUserQuery }
     * 
     */
    public BlackUserQuery createBlackUserQuery() {
        return new BlackUserQuery();
    }

    /**
     * Create an instance of {@link XmlMessage }
     * 
     */
    public XmlMessage createXmlMessage() {
        return new XmlMessage();
    }

    /**
     * Create an instance of {@link ValidOrderTemp }
     * 
     */
    public ValidOrderTemp createValidOrderTemp() {
        return new ValidOrderTemp();
    }

    /**
     * Create an instance of {@link ReqSheet }
     * 
     */
    public ReqSheet createReqSheet() {
        return new ReqSheet();
    }

    /**
     * Create an instance of {@link BlackUserModel }
     * 
     */
    public BlackUserModel createBlackUserModel() {
        return new BlackUserModel();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecommandTypeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.360buy.com", name = "getRecommandTypeResponse")
    public JAXBElement<GetRecommandTypeResponse> createGetRecommandTypeResponse(GetRecommandTypeResponse value) {
        return new JAXBElement<GetRecommandTypeResponse>(_GetRecommandTypeResponse_QNAME, GetRecommandTypeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecommandType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.360buy.com", name = "getRecommandType")
    public JAXBElement<GetRecommandType> createGetRecommandType(GetRecommandType value) {
        return new JAXBElement<GetRecommandType>(_GetRecommandType_QNAME, GetRecommandType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendXmlMessage }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.360buy.com", name = "sendXmlMessage")
    public JAXBElement<SendXmlMessage> createSendXmlMessage(SendXmlMessage value) {
        return new JAXBElement<SendXmlMessage>(_SendXmlMessage_QNAME, SendXmlMessage.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetBlackUsersResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.360buy.com", name = "getBlackUsersResponse")
    public JAXBElement<GetBlackUsersResponse> createGetBlackUsersResponse(GetBlackUsersResponse value) {
        return new JAXBElement<GetBlackUsersResponse>(_GetBlackUsersResponse_QNAME, GetBlackUsersResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRequestSheetsByOrderIdResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.360buy.com", name = "getRequestSheetsByOrderIdResponse")
    public JAXBElement<GetRequestSheetsByOrderIdResponse> createGetRequestSheetsByOrderIdResponse(GetRequestSheetsByOrderIdResponse value) {
        return new JAXBElement<GetRequestSheetsByOrderIdResponse>(_GetRequestSheetsByOrderIdResponse_QNAME, GetRequestSheetsByOrderIdResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRequestSheetsByOrderId }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.360buy.com", name = "getRequestSheetsByOrderId")
    public JAXBElement<GetRequestSheetsByOrderId> createGetRequestSheetsByOrderId(GetRequestSheetsByOrderId value) {
        return new JAXBElement<GetRequestSheetsByOrderId>(_GetRequestSheetsByOrderId_QNAME, GetRequestSheetsByOrderId.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetBlackUsers }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.360buy.com", name = "getBlackUsers")
    public JAXBElement<GetBlackUsers> createGetBlackUsers(GetBlackUsers value) {
        return new JAXBElement<GetBlackUsers>(_GetBlackUsers_QNAME, GetBlackUsers.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendXmlMessageResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.360buy.com", name = "sendXmlMessageResponse")
    public JAXBElement<SendXmlMessageResponse> createSendXmlMessageResponse(SendXmlMessageResponse value) {
        return new JAXBElement<SendXmlMessageResponse>(_SendXmlMessageResponse_QNAME, SendXmlMessageResponse.class, null, value);
    }

}
