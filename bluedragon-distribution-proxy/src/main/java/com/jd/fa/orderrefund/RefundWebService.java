package com.jd.fa.orderrefund;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.5.1
 * 2013-06-05T13:37:04.946+08:00
 * Generated source version: 2.5.1
 * 
 */
@WebService(targetNamespace = "http://www.360buy.com", name = "RefundWebService")
@XmlSeeAlso({ObjectFactory.class})
public interface RefundWebService {

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "sendXmlMessage", targetNamespace = "http://www.360buy.com", className = "com.jd.fa.orderrefund.SendXmlMessage")
    @WebMethod
    @ResponseWrapper(localName = "sendXmlMessageResponse", targetNamespace = "http://www.360buy.com", className = "com.jd.fa.orderrefund.SendXmlMessageResponse")
    public java.lang.String sendXmlMessage(
        @WebParam(name = "xmlMessage", targetNamespace = "")
        com.jd.fa.orderrefund.XmlMessage xmlMessage
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getRequestSheetsByOrderId", targetNamespace = "http://www.360buy.com", className = "com.jd.fa.orderrefund.GetRequestSheetsByOrderId")
    @WebMethod
    @ResponseWrapper(localName = "getRequestSheetsByOrderIdResponse", targetNamespace = "http://www.360buy.com", className = "com.jd.fa.orderrefund.GetRequestSheetsByOrderIdResponse")
    public java.util.List<com.jd.fa.orderrefund.ReqSheet> getRequestSheetsByOrderId(
        @WebParam(name = "orderId", targetNamespace = "")
        int orderId
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getBlackUsers", targetNamespace = "http://www.360buy.com", className = "com.jd.fa.orderrefund.GetBlackUsers")
    @WebMethod
    @ResponseWrapper(localName = "getBlackUsersResponse", targetNamespace = "http://www.360buy.com", className = "com.jd.fa.orderrefund.GetBlackUsersResponse")
    public com.jd.fa.orderrefund.BlackUserQuery getBlackUsers(
        @WebParam(name = "blackUserQuery", targetNamespace = "")
        com.jd.fa.orderrefund.BlackUserQuery blackUserQuery
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getRecommandType", targetNamespace = "http://www.360buy.com", className = "com.jd.fa.orderrefund.GetRecommandType")
    @WebMethod
    @ResponseWrapper(localName = "getRecommandTypeResponse", targetNamespace = "http://www.360buy.com", className = "com.jd.fa.orderrefund.GetRecommandTypeResponse")
    public com.jd.fa.orderrefund.ValidOrderTemp getRecommandType(
        @WebParam(name = "orderId", targetNamespace = "")
        int orderId
    );
}
