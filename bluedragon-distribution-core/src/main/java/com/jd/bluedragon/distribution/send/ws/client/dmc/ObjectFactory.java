
package com.jd.bluedragon.distribution.send.ws.client.dmc;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.jd.bluedragon.distribution.send.ws.client.dmc package. 
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

    private final static QName _ShouHuoServiceResponse_QNAME = new QName("http://tms.360buy.com", "shouHuoServiceResponse");
    private final static QName _GetOrgCenterListResponse_QNAME = new QName("http://tms.360buy.com", "getOrgCenterListResponse");
    private final static QName _ShouHuoService_QNAME = new QName("http://tms.360buy.com", "shouHuoService");
    private final static QName _GetPackageInfoResponse_QNAME = new QName("http://tms.360buy.com", "getPackageInfoResponse");
    private final static QName _JudgeReinvestByOrderId_QNAME = new QName("http://tms.360buy.com", "judgeReinvestByOrderId");
    private final static QName _GetOrgCenterList_QNAME = new QName("http://tms.360buy.com", "getOrgCenterList");
    private final static QName _OrderShouHuoBackServiceResponse_QNAME = new QName("http://tms.360buy.com", "orderShouHuoBackServiceResponse");
    private final static QName _GetExceptionShouHuoService_QNAME = new QName("http://tms.360buy.com", "getExceptionShouHuoService");
    private final static QName _GetPackageInfo_QNAME = new QName("http://tms.360buy.com", "getPackageInfo");
    private final static QName _GetExceptionShouHuoServiceResponse_QNAME = new QName("http://tms.360buy.com", "getExceptionShouHuoServiceResponse");
    private final static QName _OrderShouHuoBackService_QNAME = new QName("http://tms.360buy.com", "orderShouHuoBackService");
    private final static QName _JudgeReinvestByOrderIdResponse_QNAME = new QName("http://tms.360buy.com", "judgeReinvestByOrderIdResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.jd.bluedragon.distribution.send.ws.client.dmc
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetOrgCenterListResponse }
     * 
     */
    public GetOrgCenterListResponse createGetOrgCenterListResponse() {
        return new GetOrgCenterListResponse();
    }

    /**
     * Create an instance of {@link ShouHuoServiceResponse }
     * 
     */
    public ShouHuoServiceResponse createShouHuoServiceResponse() {
        return new ShouHuoServiceResponse();
    }

    /**
     * Create an instance of {@link ShouHuoService }
     * 
     */
    public ShouHuoService createShouHuoService() {
        return new ShouHuoService();
    }

    /**
     * Create an instance of {@link OrderShouHuoBackServiceResponse }
     * 
     */
    public OrderShouHuoBackServiceResponse createOrderShouHuoBackServiceResponse() {
        return new OrderShouHuoBackServiceResponse();
    }

    /**
     * Create an instance of {@link GetOrgCenterList }
     * 
     */
    public GetOrgCenterList createGetOrgCenterList() {
        return new GetOrgCenterList();
    }

    /**
     * Create an instance of {@link JudgeReinvestByOrderId }
     * 
     */
    public JudgeReinvestByOrderId createJudgeReinvestByOrderId() {
        return new JudgeReinvestByOrderId();
    }

    /**
     * Create an instance of {@link GetPackageInfoResponse }
     * 
     */
    public GetPackageInfoResponse createGetPackageInfoResponse() {
        return new GetPackageInfoResponse();
    }

    /**
     * Create an instance of {@link JudgeReinvestByOrderIdResponse }
     * 
     */
    public JudgeReinvestByOrderIdResponse createJudgeReinvestByOrderIdResponse() {
        return new JudgeReinvestByOrderIdResponse();
    }

    /**
     * Create an instance of {@link OrderShouHuoBackService }
     * 
     */
    public OrderShouHuoBackService createOrderShouHuoBackService() {
        return new OrderShouHuoBackService();
    }

    /**
     * Create an instance of {@link GetExceptionShouHuoServiceResponse }
     * 
     */
    public GetExceptionShouHuoServiceResponse createGetExceptionShouHuoServiceResponse() {
        return new GetExceptionShouHuoServiceResponse();
    }

    /**
     * Create an instance of {@link GetPackageInfo }
     * 
     */
    public GetPackageInfo createGetPackageInfo() {
        return new GetPackageInfo();
    }

    /**
     * Create an instance of {@link GetExceptionShouHuoService }
     * 
     */
    public GetExceptionShouHuoService createGetExceptionShouHuoService() {
        return new GetExceptionShouHuoService();
    }

    /**
     * Create an instance of {@link Result }
     * 
     */
    public Result createResult() {
        return new Result();
    }

    /**
     * Create an instance of {@link Reinvest }
     * 
     */
    public Reinvest createReinvest() {
        return new Reinvest();
    }

    /**
     * Create an instance of {@link OrgCenter }
     * 
     */
    public OrgCenter createOrgCenter() {
        return new OrgCenter();
    }

    /**
     * Create an instance of {@link BusinessBean }
     * 
     */
    public BusinessBean createBusinessBean() {
        return new BusinessBean();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ShouHuoServiceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tms.360buy.com", name = "shouHuoServiceResponse")
    public JAXBElement<ShouHuoServiceResponse> createShouHuoServiceResponse(ShouHuoServiceResponse value) {
        return new JAXBElement<ShouHuoServiceResponse>(_ShouHuoServiceResponse_QNAME, ShouHuoServiceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetOrgCenterListResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tms.360buy.com", name = "getOrgCenterListResponse")
    public JAXBElement<GetOrgCenterListResponse> createGetOrgCenterListResponse(GetOrgCenterListResponse value) {
        return new JAXBElement<GetOrgCenterListResponse>(_GetOrgCenterListResponse_QNAME, GetOrgCenterListResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ShouHuoService }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tms.360buy.com", name = "shouHuoService")
    public JAXBElement<ShouHuoService> createShouHuoService(ShouHuoService value) {
        return new JAXBElement<ShouHuoService>(_ShouHuoService_QNAME, ShouHuoService.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPackageInfoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tms.360buy.com", name = "getPackageInfoResponse")
    public JAXBElement<GetPackageInfoResponse> createGetPackageInfoResponse(GetPackageInfoResponse value) {
        return new JAXBElement<GetPackageInfoResponse>(_GetPackageInfoResponse_QNAME, GetPackageInfoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link JudgeReinvestByOrderId }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tms.360buy.com", name = "judgeReinvestByOrderId")
    public JAXBElement<JudgeReinvestByOrderId> createJudgeReinvestByOrderId(JudgeReinvestByOrderId value) {
        return new JAXBElement<JudgeReinvestByOrderId>(_JudgeReinvestByOrderId_QNAME, JudgeReinvestByOrderId.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetOrgCenterList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tms.360buy.com", name = "getOrgCenterList")
    public JAXBElement<GetOrgCenterList> createGetOrgCenterList(GetOrgCenterList value) {
        return new JAXBElement<GetOrgCenterList>(_GetOrgCenterList_QNAME, GetOrgCenterList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OrderShouHuoBackServiceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tms.360buy.com", name = "orderShouHuoBackServiceResponse")
    public JAXBElement<OrderShouHuoBackServiceResponse> createOrderShouHuoBackServiceResponse(OrderShouHuoBackServiceResponse value) {
        return new JAXBElement<OrderShouHuoBackServiceResponse>(_OrderShouHuoBackServiceResponse_QNAME, OrderShouHuoBackServiceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetExceptionShouHuoService }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tms.360buy.com", name = "getExceptionShouHuoService")
    public JAXBElement<GetExceptionShouHuoService> createGetExceptionShouHuoService(GetExceptionShouHuoService value) {
        return new JAXBElement<GetExceptionShouHuoService>(_GetExceptionShouHuoService_QNAME, GetExceptionShouHuoService.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPackageInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tms.360buy.com", name = "getPackageInfo")
    public JAXBElement<GetPackageInfo> createGetPackageInfo(GetPackageInfo value) {
        return new JAXBElement<GetPackageInfo>(_GetPackageInfo_QNAME, GetPackageInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetExceptionShouHuoServiceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tms.360buy.com", name = "getExceptionShouHuoServiceResponse")
    public JAXBElement<GetExceptionShouHuoServiceResponse> createGetExceptionShouHuoServiceResponse(GetExceptionShouHuoServiceResponse value) {
        return new JAXBElement<GetExceptionShouHuoServiceResponse>(_GetExceptionShouHuoServiceResponse_QNAME, GetExceptionShouHuoServiceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OrderShouHuoBackService }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tms.360buy.com", name = "orderShouHuoBackService")
    public JAXBElement<OrderShouHuoBackService> createOrderShouHuoBackService(OrderShouHuoBackService value) {
        return new JAXBElement<OrderShouHuoBackService>(_OrderShouHuoBackService_QNAME, OrderShouHuoBackService.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link JudgeReinvestByOrderIdResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tms.360buy.com", name = "judgeReinvestByOrderIdResponse")
    public JAXBElement<JudgeReinvestByOrderIdResponse> createJudgeReinvestByOrderIdResponse(JudgeReinvestByOrderIdResponse value) {
        return new JAXBElement<JudgeReinvestByOrderIdResponse>(_JudgeReinvestByOrderIdResponse_QNAME, JudgeReinvestByOrderIdResponse.class, null, value);
    }

}
