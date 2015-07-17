
package com.jd.wms.wm.domain.sms;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.jd.wms.wm.domain.sms package. 
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

    private final static QName _AuthenticationHeader_QNAME = new QName("http://360buy.com/", "AuthenticationHeader");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.jd.wms.wm.domain.sms
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SendSMS1Response }
     * 
     */
    public SendSMS1Response createSendSMS1Response() {
        return new SendSMS1Response();
    }

    /**
     * Create an instance of {@link Mail }
     * 
     */
    public Mail createMail() {
        return new Mail();
    }

    /**
     * Create an instance of {@link SendSMS }
     * 
     */
    public SendSMS createSendSMS() {
        return new SendSMS();
    }

    /**
     * Create an instance of {@link IsSendSMSResponse }
     * 
     */
    public IsSendSMSResponse createIsSendSMSResponse() {
        return new IsSendSMSResponse();
    }

    /**
     * Create an instance of {@link IsSendSMS }
     * 
     */
    public IsSendSMS createIsSendSMS() {
        return new IsSendSMS();
    }

    /**
     * Create an instance of {@link SendMail }
     * 
     */
    public SendMail createSendMail() {
        return new SendMail();
    }

    /**
     * Create an instance of {@link SMS1 }
     * 
     */
    public SMS1 createSMS1() {
        return new SMS1();
    }

    /**
     * Create an instance of {@link SendMailResponse }
     * 
     */
    public SendMailResponse createSendMailResponse() {
        return new SendMailResponse();
    }

    /**
     * Create an instance of {@link SendSMS1 }
     * 
     */
    public SendSMS1 createSendSMS1() {
        return new SendSMS1();
    }

    /**
     * Create an instance of {@link IsSendSMS1 }
     * 
     */
    public IsSendSMS1 createIsSendSMS1() {
        return new IsSendSMS1();
    }

    /**
     * Create an instance of {@link SMS }
     * 
     */
    public SMS createSMS() {
        return new SMS();
    }

    /**
     * Create an instance of {@link IsSendSMS1Response }
     * 
     */
    public IsSendSMS1Response createIsSendSMS1Response() {
        return new IsSendSMS1Response();
    }

    /**
     * Create an instance of {@link SendSMSResponse }
     * 
     */
    public SendSMSResponse createSendSMSResponse() {
        return new SendSMSResponse();
    }

    /**
     * Create an instance of {@link AuthenticationHeader }
     * 
     */
    public AuthenticationHeader createAuthenticationHeader() {
        return new AuthenticationHeader();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AuthenticationHeader }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://360buy.com/", name = "AuthenticationHeader")
    public JAXBElement<AuthenticationHeader> createAuthenticationHeader(AuthenticationHeader value) {
        return new JAXBElement<AuthenticationHeader>(_AuthenticationHeader_QNAME, AuthenticationHeader.class, null, value);
    }

}
