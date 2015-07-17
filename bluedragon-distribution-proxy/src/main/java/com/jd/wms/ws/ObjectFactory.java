
package com.jd.wms.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.jd.wms.ws package. 
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

    private final static QName _ForwardSaveHandleMessage_QNAME = new QName("http://interface-dtc-ws.360buy.com", "forwardSaveHandleMessage");
    private final static QName _DirectHandleMessageResponse_QNAME = new QName("http://interface-dtc-ws.360buy.com", "directHandleMessageResponse");
    private final static QName _ForwardHandleMessage_QNAME = new QName("http://interface-dtc-ws.360buy.com", "forwardHandleMessage");
    private final static QName _ForwardSaveHandleMessageResponse_QNAME = new QName("http://interface-dtc-ws.360buy.com", "forwardSaveHandleMessageResponse");
    private final static QName _DirectHandleMessage_QNAME = new QName("http://interface-dtc-ws.360buy.com", "directHandleMessage");
    private final static QName _ForwardHandleMessageResponse_QNAME = new QName("http://interface-dtc-ws.360buy.com", "forwardHandleMessageResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.jd.wms.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ForwardSaveHandleMessage }
     * 
     */
    public ForwardSaveHandleMessage createForwardSaveHandleMessage() {
        return new ForwardSaveHandleMessage();
    }

    /**
     * Create an instance of {@link ForwardHandleMessage }
     * 
     */
    public ForwardHandleMessage createForwardHandleMessage() {
        return new ForwardHandleMessage();
    }

    /**
     * Create an instance of {@link ForwardHandleMessageResponse }
     * 
     */
    public ForwardHandleMessageResponse createForwardHandleMessageResponse() {
        return new ForwardHandleMessageResponse();
    }

    /**
     * Create an instance of {@link DirectHandleMessage }
     * 
     */
    public DirectHandleMessage createDirectHandleMessage() {
        return new DirectHandleMessage();
    }

    /**
     * Create an instance of {@link DirectHandleMessageResponse }
     * 
     */
    public DirectHandleMessageResponse createDirectHandleMessageResponse() {
        return new DirectHandleMessageResponse();
    }

    /**
     * Create an instance of {@link Result }
     * 
     */
    public Result createResult() {
        return new Result();
    }

    /**
     * Create an instance of {@link ForwardSaveHandleMessageResponse }
     * 
     */
    public ForwardSaveHandleMessageResponse createForwardSaveHandleMessageResponse() {
        return new ForwardSaveHandleMessageResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ForwardSaveHandleMessage }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://interface-dtc-ws.360buy.com", name = "forwardSaveHandleMessage")
    public JAXBElement<ForwardSaveHandleMessage> createForwardSaveHandleMessage(ForwardSaveHandleMessage value) {
        return new JAXBElement<ForwardSaveHandleMessage>(_ForwardSaveHandleMessage_QNAME, ForwardSaveHandleMessage.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DirectHandleMessageResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://interface-dtc-ws.360buy.com", name = "directHandleMessageResponse")
    public JAXBElement<DirectHandleMessageResponse> createDirectHandleMessageResponse(DirectHandleMessageResponse value) {
        return new JAXBElement<DirectHandleMessageResponse>(_DirectHandleMessageResponse_QNAME, DirectHandleMessageResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ForwardHandleMessage }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://interface-dtc-ws.360buy.com", name = "forwardHandleMessage")
    public JAXBElement<ForwardHandleMessage> createForwardHandleMessage(ForwardHandleMessage value) {
        return new JAXBElement<ForwardHandleMessage>(_ForwardHandleMessage_QNAME, ForwardHandleMessage.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ForwardSaveHandleMessageResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://interface-dtc-ws.360buy.com", name = "forwardSaveHandleMessageResponse")
    public JAXBElement<ForwardSaveHandleMessageResponse> createForwardSaveHandleMessageResponse(ForwardSaveHandleMessageResponse value) {
        return new JAXBElement<ForwardSaveHandleMessageResponse>(_ForwardSaveHandleMessageResponse_QNAME, ForwardSaveHandleMessageResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DirectHandleMessage }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://interface-dtc-ws.360buy.com", name = "directHandleMessage")
    public JAXBElement<DirectHandleMessage> createDirectHandleMessage(DirectHandleMessage value) {
        return new JAXBElement<DirectHandleMessage>(_DirectHandleMessage_QNAME, DirectHandleMessage.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ForwardHandleMessageResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://interface-dtc-ws.360buy.com", name = "forwardHandleMessageResponse")
    public JAXBElement<ForwardHandleMessageResponse> createForwardHandleMessageResponse(ForwardHandleMessageResponse value) {
        return new JAXBElement<ForwardHandleMessageResponse>(_ForwardHandleMessageResponse_QNAME, ForwardHandleMessageResponse.class, null, value);
    }

}
