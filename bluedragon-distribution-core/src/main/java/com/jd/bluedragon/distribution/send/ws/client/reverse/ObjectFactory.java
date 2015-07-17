
package com.jd.bluedragon.distribution.send.ws.client.reverse;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.jd.bluedragon.distribution.send.ws.client.reverse package. 
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

    private final static QName _GetReturnDataByType_QNAME = new QName("http://service.webservice.revoke.dmc.jd.com/", "getReturnDataByType");
    private final static QName _GetReturnDataByTypeResponse_QNAME = new QName("http://service.webservice.revoke.dmc.jd.com/", "getReturnDataByTypeResponse");
    private final static QName _SendReturnDataToDMC_QNAME = new QName("http://service.webservice.revoke.dmc.jd.com/", "sendReturnDataToDMC");
    private final static QName _SyncOrderState_QNAME = new QName("http://service.webservice.revoke.dmc.jd.com/", "syncOrderState");
    private final static QName _SyncOrderStateResponse_QNAME = new QName("http://service.webservice.revoke.dmc.jd.com/", "syncOrderStateResponse");
    private final static QName _SendReturnDataToDMCResponse_QNAME = new QName("http://service.webservice.revoke.dmc.jd.com/", "sendReturnDataToDMCResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.jd.bluedragon.distribution.send.ws.client.reverse
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetReturnDataByType }
     * 
     */
    public GetReturnDataByType createGetReturnDataByType() {
        return new GetReturnDataByType();
    }

    /**
     * Create an instance of {@link SendReturnDataToDMC }
     * 
     */
    public SendReturnDataToDMC createSendReturnDataToDMC() {
        return new SendReturnDataToDMC();
    }

    /**
     * Create an instance of {@link GetReturnDataByTypeResponse }
     * 
     */
    public GetReturnDataByTypeResponse createGetReturnDataByTypeResponse() {
        return new GetReturnDataByTypeResponse();
    }

    /**
     * Create an instance of {@link SendReturnDataToDMCResponse }
     * 
     */
    public SendReturnDataToDMCResponse createSendReturnDataToDMCResponse() {
        return new SendReturnDataToDMCResponse();
    }

    /**
     * Create an instance of {@link SyncOrderStateResponse }
     * 
     */
    public SyncOrderStateResponse createSyncOrderStateResponse() {
        return new SyncOrderStateResponse();
    }

    /**
     * Create an instance of {@link SyncOrderState }
     * 
     */
    public SyncOrderState createSyncOrderState() {
        return new SyncOrderState();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetReturnDataByType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.webservice.revoke.dmc.jd.com/", name = "getReturnDataByType")
    public JAXBElement<GetReturnDataByType> createGetReturnDataByType(GetReturnDataByType value) {
        return new JAXBElement<GetReturnDataByType>(_GetReturnDataByType_QNAME, GetReturnDataByType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetReturnDataByTypeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.webservice.revoke.dmc.jd.com/", name = "getReturnDataByTypeResponse")
    public JAXBElement<GetReturnDataByTypeResponse> createGetReturnDataByTypeResponse(GetReturnDataByTypeResponse value) {
        return new JAXBElement<GetReturnDataByTypeResponse>(_GetReturnDataByTypeResponse_QNAME, GetReturnDataByTypeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendReturnDataToDMC }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.webservice.revoke.dmc.jd.com/", name = "sendReturnDataToDMC")
    public JAXBElement<SendReturnDataToDMC> createSendReturnDataToDMC(SendReturnDataToDMC value) {
        return new JAXBElement<SendReturnDataToDMC>(_SendReturnDataToDMC_QNAME, SendReturnDataToDMC.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SyncOrderState }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.webservice.revoke.dmc.jd.com/", name = "syncOrderState")
    public JAXBElement<SyncOrderState> createSyncOrderState(SyncOrderState value) {
        return new JAXBElement<SyncOrderState>(_SyncOrderState_QNAME, SyncOrderState.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SyncOrderStateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.webservice.revoke.dmc.jd.com/", name = "syncOrderStateResponse")
    public JAXBElement<SyncOrderStateResponse> createSyncOrderStateResponse(SyncOrderStateResponse value) {
        return new JAXBElement<SyncOrderStateResponse>(_SyncOrderStateResponse_QNAME, SyncOrderStateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendReturnDataToDMCResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.webservice.revoke.dmc.jd.com/", name = "sendReturnDataToDMCResponse")
    public JAXBElement<SendReturnDataToDMCResponse> createSendReturnDataToDMCResponse(SendReturnDataToDMCResponse value) {
        return new JAXBElement<SendReturnDataToDMCResponse>(_SendReturnDataToDMCResponse_QNAME, SendReturnDataToDMCResponse.class, null, value);
    }

}
