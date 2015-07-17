
package com.jd.bluedragon.distribution.send.domain.whems;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com package. 
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

    private final static QName _SendMsgContent_QNAME = new QName("http://jingdong.ws.whpost.cn", "content");
    private final static QName _SendMsgResponseReturn_QNAME = new QName("http://jingdong.ws.whpost.cn", "return");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SendMsg }
     * 
     */
    public SendMsg createSendMsg() {
        return new SendMsg();
    }

    /**
     * Create an instance of {@link SendMsgResponse }
     * 
     */
    public SendMsgResponse createSendMsgResponse() {
        return new SendMsgResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://jingdong.ws.whpost.cn", name = "content", scope = SendMsg.class)
    public JAXBElement<String> createSendMsgContent(String value) {
        return new JAXBElement<String>(_SendMsgContent_QNAME, String.class, SendMsg.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://jingdong.ws.whpost.cn", name = "return", scope = SendMsgResponse.class)
    public JAXBElement<String> createSendMsgResponseReturn(String value) {
        return new JAXBElement<String>(_SendMsgResponseReturn_QNAME, String.class, SendMsgResponse.class, value);
    }

}
