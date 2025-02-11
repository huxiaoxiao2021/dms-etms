
/*
 * 
 */

package com.jd.fa.refundService;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.3.1
 * Wed Sep 04 13:46:09 GMT+08:00 2013
 * Generated source version: 2.3.1
 * 
 */


@WebServiceClient(name = "RefundServiceNew", 
                  wsdlLocation = "http://fn.360buy.com/refund/Service/RefundServiceNew.asmx?WSDL",
                  targetNamespace = "http://tempuri.org/") 
public class RefundServiceNew extends Service {

    public final static URL WSDL_LOCATION;
    public final static QName SERVICE = new QName("http://tempuri.org/", "RefundServiceNew");
    public final static QName RefundServiceNewSoap12 = new QName("http://tempuri.org/", "RefundServiceNewSoap12");
    public final static QName RefundServiceNewSoap = new QName("http://tempuri.org/", "RefundServiceNewSoap");
    static {
        URL url = null;
        try {
            url = new URL("http://fn.360buy.com/refund/Service/RefundServiceNew.asmx?WSDL");
        } catch (MalformedURLException e) {
            System.err.println("Can not initialize the default wsdl from http://fn.360buy.com/refund/Service/RefundServiceNew.asmx?WSDL");
            // e.printStackTrace();
        }
        WSDL_LOCATION = url;
    }

    public RefundServiceNew(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public RefundServiceNew(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public RefundServiceNew() {
        super(WSDL_LOCATION, SERVICE);
    }
    
//    public RefundServiceNew(WebServiceFeature ... features) {
//        super(WSDL_LOCATION, SERVICE, features);
//    }
//    public RefundServiceNew(URL wsdlLocation, WebServiceFeature ... features) {
//        super(wsdlLocation, SERVICE, features);
//    }
//
//    public RefundServiceNew(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
//        super(wsdlLocation, serviceName, features);
//    }

    /**
     * 
     * @return
     *     returns RefundServiceNewSoap
     */
    @WebEndpoint(name = "RefundServiceNewSoap12")
    public RefundServiceNewSoap getRefundServiceNewSoap12() {
        return super.getPort(RefundServiceNewSoap12, RefundServiceNewSoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns RefundServiceNewSoap
     */
    @WebEndpoint(name = "RefundServiceNewSoap12")
    public RefundServiceNewSoap getRefundServiceNewSoap12(WebServiceFeature... features) {
        return super.getPort(RefundServiceNewSoap12, RefundServiceNewSoap.class, features);
    }
    /**
     * 
     * @return
     *     returns RefundServiceNewSoap
     */
    @WebEndpoint(name = "RefundServiceNewSoap")
    public RefundServiceNewSoap getRefundServiceNewSoap() {
        return super.getPort(RefundServiceNewSoap, RefundServiceNewSoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns RefundServiceNewSoap
     */
    @WebEndpoint(name = "RefundServiceNewSoap")
    public RefundServiceNewSoap getRefundServiceNewSoap(WebServiceFeature... features) {
        return super.getPort(RefundServiceNewSoap, RefundServiceNewSoap.class, features);
    }

}
