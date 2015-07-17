
/*
 * 
 */

package com.jd.bluedragon.distribution.popAbnormal.ws.client.waybill;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

/**
 * This class was generated by Apache CXF 2.3.1
 * Mon Dec 24 19:00:04 CST 2012
 * Generated source version: 2.3.1
 * 
 */


@WebServiceClient(name = "etmsWaybillWebService", 
                  wsdlLocation = "http://waybill.etms.360buy.com/services/EtmsWaybillWebService?wsdl",
                  targetNamespace = "http://impl.rpc.waybill.etms.jd.com") 
public class EtmsWaybillWebService extends Service {

    public final static URL WSDL_LOCATION;
    public final static QName SERVICE = new QName("http://impl.rpc.waybill.etms.jd.com", "etmsWaybillWebService");
    public final static QName EtmsWaybillWebServiceImplPort = new QName("http://impl.rpc.waybill.etms.jd.com", "EtmsWaybillWebServiceImplPort");
    static {
        URL url = null;
        try {
            url = new URL("http://waybill.etms.360buy.com/services/EtmsWaybillWebService?wsdl");
        } catch (MalformedURLException e) {
            System.err.println("Can not initialize the default wsdl from http://waybill.etms.360buy.com/services/EtmsWaybillWebService?wsdl");
            // e.printStackTrace();
        }
        WSDL_LOCATION = url;
    }

    public EtmsWaybillWebService(URL wsdlLocation) {
        super(wsdlLocation, EtmsWaybillWebService.SERVICE);
    }

    public EtmsWaybillWebService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public EtmsWaybillWebService() {
        super(EtmsWaybillWebService.WSDL_LOCATION, EtmsWaybillWebService.SERVICE);
    }
    
    /**
     * 
     * @return
     *     returns WaybillService
     */
    @WebEndpoint(name = "EtmsWaybillWebServiceImplPort")
    public WaybillService getEtmsWaybillWebServiceImplPort() {
        return super.getPort(EtmsWaybillWebService.EtmsWaybillWebServiceImplPort, WaybillService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns WaybillService
     */
    @WebEndpoint(name = "EtmsWaybillWebServiceImplPort")
    public WaybillService getEtmsWaybillWebServiceImplPort(WebServiceFeature... features) {
        return super.getPort(EtmsWaybillWebService.EtmsWaybillWebServiceImplPort, WaybillService.class, features);
    }

}
