package com.jd.bluedragon.distribution.send.ws.client.dmc;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.3.1
 * Fri Mar 30 11:57:14 CST 2012
 * Generated source version: 2.3.1
 * 
 */
 
@WebService(targetNamespace = "http://tms.360buy.com", name = "DmsToTmsWebService")
@XmlSeeAlso({ObjectFactory.class})
public interface DmsToTmsWebService {

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "orderShouHuoBackService", targetNamespace = "http://tms.360buy.com", className = "com.jd.bluedragon.distribution.send.ws.client.dmc.OrderShouHuoBackService")
    @WebMethod
    @ResponseWrapper(localName = "orderShouHuoBackServiceResponse", targetNamespace = "http://tms.360buy.com", className = "com.jd.bluedragon.distribution.send.ws.client.dmc.OrderShouHuoBackServiceResponse")
    public com.jd.bluedragon.distribution.send.ws.client.dmc.Result orderShouHuoBackService(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "shouHuoService", targetNamespace = "http://tms.360buy.com", className = "com.jd.bluedragon.distribution.send.ws.client.dmc.ShouHuoService")
    @WebMethod
    @ResponseWrapper(localName = "shouHuoServiceResponse", targetNamespace = "http://tms.360buy.com", className = "com.jd.bluedragon.distribution.send.ws.client.dmc.ShouHuoServiceResponse")
    public com.jd.bluedragon.distribution.send.ws.client.dmc.Result shouHuoService(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getOrgCenterList", targetNamespace = "http://tms.360buy.com", className = "com.jd.bluedragon.distribution.send.ws.client.dmc.GetOrgCenterList")
    @WebMethod
    @ResponseWrapper(localName = "getOrgCenterListResponse", targetNamespace = "http://tms.360buy.com", className = "com.jd.bluedragon.distribution.send.ws.client.dmc.GetOrgCenterListResponse")
    public java.util.List<com.jd.bluedragon.distribution.send.ws.client.dmc.OrgCenter> getOrgCenterList(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getExceptionShouHuoService", targetNamespace = "http://tms.360buy.com", className = "com.jd.bluedragon.distribution.send.ws.client.dmc.GetExceptionShouHuoService")
    @WebMethod
    @ResponseWrapper(localName = "getExceptionShouHuoServiceResponse", targetNamespace = "http://tms.360buy.com", className = "com.jd.bluedragon.distribution.send.ws.client.dmc.GetExceptionShouHuoServiceResponse")
    public java.lang.String getExceptionShouHuoService(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getPackageInfo", targetNamespace = "http://tms.360buy.com", className = "com.jd.bluedragon.distribution.send.ws.client.dmc.GetPackageInfo")
    @WebMethod
    @ResponseWrapper(localName = "getPackageInfoResponse", targetNamespace = "http://tms.360buy.com", className = "com.jd.bluedragon.distribution.send.ws.client.dmc.GetPackageInfoResponse")
    public java.lang.String getPackageInfo(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "judgeReinvestByOrderId", targetNamespace = "http://tms.360buy.com", className = "com.jd.bluedragon.distribution.send.ws.client.dmc.JudgeReinvestByOrderId")
    @WebMethod
    @ResponseWrapper(localName = "judgeReinvestByOrderIdResponse", targetNamespace = "http://tms.360buy.com", className = "com.jd.bluedragon.distribution.send.ws.client.dmc.JudgeReinvestByOrderIdResponse")
    public com.jd.bluedragon.distribution.send.ws.client.dmc.Reinvest judgeReinvestByOrderId(
        @WebParam(name = "arg0", targetNamespace = "")
        com.jd.bluedragon.distribution.send.ws.client.dmc.BusinessBean arg0
    );
}
