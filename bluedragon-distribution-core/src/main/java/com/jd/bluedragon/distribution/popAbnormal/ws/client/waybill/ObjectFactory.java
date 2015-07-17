
package com.jd.bluedragon.distribution.popAbnormal.ws.client.waybill;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.jd.bluedragon.distribution.popAbnormal.ws.client.waybill package. 
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

    private final static QName _SendOemPacks_QNAME = new QName("http://impl.rpc.waybill.etms.jd.com", "sendOemPacks");
    private final static QName _SendPopOrders_QNAME = new QName("http://impl.rpc.waybill.etms.jd.com", "sendPopOrders");
    private final static QName _SendWms3PacksResponse_QNAME = new QName("http://impl.rpc.waybill.etms.jd.com", "sendWms3PacksResponse");
    private final static QName _SendPopOrdersResponse_QNAME = new QName("http://impl.rpc.waybill.etms.jd.com", "sendPopOrdersResponse");
    private final static QName _BaseEntity_QNAME = new QName("http://impl.rpc.waybill.etms.jd.com", "baseEntity");
    private final static QName _SendOemPacksResponse_QNAME = new QName("http://impl.rpc.waybill.etms.jd.com", "sendOemPacksResponse");
    private final static QName _SendWms3Packs_QNAME = new QName("http://impl.rpc.waybill.etms.jd.com", "sendWms3Packs");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.jd.bluedragon.distribution.popAbnormal.ws.client.waybill
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SendPopOrders }
     * 
     */
    public SendPopOrders createSendPopOrders() {
        return new SendPopOrders();
    }

    /**
     * Create an instance of {@link SendOemPacks }
     * 
     */
    public SendOemPacks createSendOemPacks() {
        return new SendOemPacks();
    }

    /**
     * Create an instance of {@link SendWms3PacksResponse }
     * 
     */
    public SendWms3PacksResponse createSendWms3PacksResponse() {
        return new SendWms3PacksResponse();
    }

    /**
     * Create an instance of {@link SendOemPacksResponse }
     * 
     */
    public SendOemPacksResponse createSendOemPacksResponse() {
        return new SendOemPacksResponse();
    }

    /**
     * Create an instance of {@link BaseEntity }
     * 
     */
    public BaseEntity createBaseEntity() {
        return new BaseEntity();
    }

    /**
     * Create an instance of {@link SendPopOrdersResponse }
     * 
     */
    public SendPopOrdersResponse createSendPopOrdersResponse() {
        return new SendPopOrdersResponse();
    }

    /**
     * Create an instance of {@link SendWms3Packs }
     * 
     */
    public SendWms3Packs createSendWms3Packs() {
        return new SendWms3Packs();
    }

    /**
     * Create an instance of {@link OemPackDto }
     * 
     */
    public OemPackDto createOemPackDto() {
        return new OemPackDto();
    }

    /**
     * Create an instance of {@link OemOrderDto }
     * 
     */
    public OemOrderDto createOemOrderDto() {
        return new OemOrderDto();
    }

    /**
     * Create an instance of {@link PopOrderDto }
     * 
     */
    public PopOrderDto createPopOrderDto() {
        return new PopOrderDto();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendOemPacks }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.rpc.waybill.etms.jd.com", name = "sendOemPacks")
    public JAXBElement<SendOemPacks> createSendOemPacks(SendOemPacks value) {
        return new JAXBElement<SendOemPacks>(_SendOemPacks_QNAME, SendOemPacks.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendPopOrders }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.rpc.waybill.etms.jd.com", name = "sendPopOrders")
    public JAXBElement<SendPopOrders> createSendPopOrders(SendPopOrders value) {
        return new JAXBElement<SendPopOrders>(_SendPopOrders_QNAME, SendPopOrders.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendWms3PacksResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.rpc.waybill.etms.jd.com", name = "sendWms3PacksResponse")
    public JAXBElement<SendWms3PacksResponse> createSendWms3PacksResponse(SendWms3PacksResponse value) {
        return new JAXBElement<SendWms3PacksResponse>(_SendWms3PacksResponse_QNAME, SendWms3PacksResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendPopOrdersResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.rpc.waybill.etms.jd.com", name = "sendPopOrdersResponse")
    public JAXBElement<SendPopOrdersResponse> createSendPopOrdersResponse(SendPopOrdersResponse value) {
        return new JAXBElement<SendPopOrdersResponse>(_SendPopOrdersResponse_QNAME, SendPopOrdersResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BaseEntity }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.rpc.waybill.etms.jd.com", name = "baseEntity")
    public JAXBElement<BaseEntity> createBaseEntity(BaseEntity value) {
        return new JAXBElement<BaseEntity>(_BaseEntity_QNAME, BaseEntity.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendOemPacksResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.rpc.waybill.etms.jd.com", name = "sendOemPacksResponse")
    public JAXBElement<SendOemPacksResponse> createSendOemPacksResponse(SendOemPacksResponse value) {
        return new JAXBElement<SendOemPacksResponse>(_SendOemPacksResponse_QNAME, SendOemPacksResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendWms3Packs }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.rpc.waybill.etms.jd.com", name = "sendWms3Packs")
    public JAXBElement<SendWms3Packs> createSendWms3Packs(SendWms3Packs value) {
        return new JAXBElement<SendWms3Packs>(_SendWms3Packs_QNAME, SendWms3Packs.class, null, value);
    }

}
