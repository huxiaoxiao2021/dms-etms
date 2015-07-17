
package com.jd.wms.packExchange;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;

/**
 * This class was generated by Apache CXF 2.3.1
 * Tue May 21 15:52:40 GMT+08:00 2013
 * Generated source version: 2.3.1
 * 
 */

public final class BaseWebService_SimpleWebServiceImplPort_Client {

    private static final QName SERVICE_NAME = new QName("http://wms3.360buy.com", "SimpleWebServiceSoap");

    private BaseWebService_SimpleWebServiceImplPort_Client() {
    }

    public static void main(String args[]) throws Exception {
        URL wsdlURL = SimpleWebServiceSoap.WSDL_LOCATION;
        if (args.length > 0) { 
            File wsdlFile = new File(args[0]);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(args[0]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
      
        SimpleWebServiceSoap ss = new SimpleWebServiceSoap(wsdlURL, SERVICE_NAME);
        BaseWebService port = ss.getSimpleWebServiceImplPort();  
        
        {
        System.out.println("Invoking queryWs...");
        java.lang.String _queryWs_arg0 = "";
        java.lang.String _queryWs_arg1 = "";
        com.jd.wms.packExchange.Result _queryWs__return = port.queryWs(_queryWs_arg0, _queryWs_arg1);
        System.out.println("queryWs.result=" + _queryWs__return);


        }
        {
        System.out.println("Invoking processWs...");
        java.lang.String _processWs_arg0 = "";
        java.lang.String _processWs_arg1 = "";
        com.jd.wms.packExchange.Result _processWs__return = port.processWs(_processWs_arg0, _processWs_arg1);
        System.out.println("processWs.result=" + _processWs__return);


        }

        System.exit(0);
    }

}
