package com.jd.wms.wm.domain.sms;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.2.4
 * Mon Apr 09 17:16:04 CST 2012
 * Generated source version: 2.2.4
 * 
 */
 
@WebService(targetNamespace = "http://360buy.com/", name = "SMSWebServiceSoap")
@XmlSeeAlso({ObjectFactory.class})
public interface SMSWebServiceSoap {

    @RequestWrapper(localName = "SendMail", targetNamespace = "http://360buy.com/", className = "com.jd.wms.wm.domain.sms.SendMail")
    @ResponseWrapper(localName = "SendMailResponse", targetNamespace = "http://360buy.com/", className = "com.jd.wms.wm.domain.sms.SendMailResponse")
    @WebMethod(operationName = "SendMail", action = "http://360buy.com/SendMail")
    public void sendMail(
        @WebParam(name = "mail", targetNamespace = "http://360buy.com/")
        com.jd.wms.wm.domain.sms.Mail mail
    );

    @WebResult(name = "IsSendSMSResult", targetNamespace = "http://360buy.com/")
    @RequestWrapper(localName = "IsSendSMS", targetNamespace = "http://360buy.com/", className = "com.jd.wms.wm.domain.sms.IsSendSMS")
    @ResponseWrapper(localName = "IsSendSMSResponse", targetNamespace = "http://360buy.com/", className = "com.jd.wms.wm.domain.sms.IsSendSMSResponse")
    @WebMethod(operationName = "IsSendSMS", action = "http://360buy.com/IsSendSMS")
    public boolean isSendSMS(
        @WebParam(name = "sms", targetNamespace = "http://360buy.com/")
        com.jd.wms.wm.domain.sms.SMS sms
    );

    @RequestWrapper(localName = "SendSMS", targetNamespace = "http://360buy.com/", className = "com.jd.wms.wm.domain.sms.SendSMS")
    @ResponseWrapper(localName = "SendSMSResponse", targetNamespace = "http://360buy.com/", className = "com.jd.wms.wm.domain.sms.SendSMSResponse")
    @WebMethod(operationName = "SendSMS", action = "http://360buy.com/SendSMS")
    public void sendSMS(
        @WebParam(name = "sms", targetNamespace = "http://360buy.com/")
        com.jd.wms.wm.domain.sms.SMS sms
    );

    @WebResult(name = "IsSendSMS1Result", targetNamespace = "http://360buy.com/")
    @RequestWrapper(localName = "IsSendSMS1", targetNamespace = "http://360buy.com/", className = "com.jd.wms.wm.domain.sms.IsSendSMS1")
    @ResponseWrapper(localName = "IsSendSMS1Response", targetNamespace = "http://360buy.com/", className = "com.jd.wms.wm.domain.sms.IsSendSMS1Response")
    @WebMethod(operationName = "IsSendSMS1", action = "http://360buy.com/IsSendSMS1")
    public boolean isSendSMS1(
        @WebParam(name = "sms1", targetNamespace = "http://360buy.com/")
        com.jd.wms.wm.domain.sms.SMS1 sms1
    );

    @RequestWrapper(localName = "SendSMS1", targetNamespace = "http://360buy.com/", className = "com.jd.wms.wm.domain.sms.SendSMS1")
    @ResponseWrapper(localName = "SendSMS1Response", targetNamespace = "http://360buy.com/", className = "com.jd.wms.wm.domain.sms.SendSMS1Response")
    @WebMethod(operationName = "SendSMS1", action = "http://360buy.com/SendSMS1")
    public void sendSMS1(
        @WebParam(name = "sms1", targetNamespace = "http://360buy.com/")
        com.jd.wms.wm.domain.sms.SMS1 sms1
    );
}
