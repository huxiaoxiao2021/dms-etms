package com.jd.bluedragon.distribution.popAbnormal.ws.client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.3.1
 * Thu Mar 29 18:35:13 CST 2012
 * Generated source version: 2.3.1
 * 
 */
 
@WebService(targetNamespace = "http://service.orderpackage.pop.jd.com/", name = "PopAbnormalOrderPackageWebService")
@XmlSeeAlso({ObjectFactory.class})
public interface PopAbnormalOrderPackageWebService {

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "savePopAbnormalOrderPackage", targetNamespace = "http://service.orderpackage.pop.jd.com/", className = "com.jd.bluedragon.distribution.popAbnormal.ws.client.SavePopAbnormalOrderPackage")
    @WebMethod
    @ResponseWrapper(localName = "savePopAbnormalOrderPackageResponse", targetNamespace = "http://service.orderpackage.pop.jd.com/", className = "com.jd.bluedragon.distribution.popAbnormal.ws.client.SavePopAbnormalOrderPackageResponse")
    public com.jd.bluedragon.distribution.popAbnormal.ws.client.AbnormalResult savePopAbnormalOrderPackage(
        @WebParam(name = "arg0", targetNamespace = "")
        com.jd.bluedragon.distribution.popAbnormal.ws.client.PopAbnormalOrderVo arg0
    );
}
