
/*
 * 
 */

package com.jd.wms.wm.domain.sms;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.2.4
 * Mon Apr 09 17:16:04 CST 2012
 * Generated source version: 2.2.4
 * 
 */


@WebServiceClient(name = "SMSWebService", 
                  wsdlLocation = "http://message.360buy.com/service.asmx?wsdl",
                  targetNamespace = "http://360buy.com/") 
public class SMSWebService extends Service {

    public final static URL WSDL_LOCATION;
    public final static QName SERVICE = new QName("http://360buy.com/", "SMSWebService");
    public final static QName SMSWebServiceSoap12 = new QName("http://360buy.com/", "SMSWebServiceSoap12");
    public final static QName SMSWebServiceSoap = new QName("http://360buy.com/", "SMSWebServiceSoap");
    static {
        URL url = null;
        try {
            url = new URL("http://message.360buy.com/service.asmx?wsdl");
        } catch (MalformedURLException e) {
            System.err.println("Can not initialize the default wsdl from http://message.360buy.com/service.asmx?wsdl");
            // e.printStackTrace();
        }
        WSDL_LOCATION = url;
    }

    public SMSWebService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public SMSWebService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public SMSWebService() {
        super(WSDL_LOCATION, SERVICE);
    }

    /**
     * 
     * @return
     *     returns SMSWebServiceSoap
     */
    @WebEndpoint(name = "SMSWebServiceSoap12")
    public SMSWebServiceSoap getSMSWebServiceSoap12() {
        return super.getPort(SMSWebServiceSoap12, SMSWebServiceSoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns SMSWebServiceSoap
     */
    @WebEndpoint(name = "SMSWebServiceSoap12")
    public SMSWebServiceSoap getSMSWebServiceSoap12(WebServiceFeature... features) {
        return super.getPort(SMSWebServiceSoap12, SMSWebServiceSoap.class, features);
    }
    /**
     * 
     * @return
     *     returns SMSWebServiceSoap
     */
    @WebEndpoint(name = "SMSWebServiceSoap")
    public SMSWebServiceSoap getSMSWebServiceSoap() {
        return super.getPort(SMSWebServiceSoap, SMSWebServiceSoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns SMSWebServiceSoap
     */
    @WebEndpoint(name = "SMSWebServiceSoap")
    public SMSWebServiceSoap getSMSWebServiceSoap(WebServiceFeature... features) {
        return super.getPort(SMSWebServiceSoap, SMSWebServiceSoap.class, features);
    }

}
