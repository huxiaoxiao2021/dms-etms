
package com.jd.wms.wm.domain.sms;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.2.4
 * Mon Apr 09 17:16:04 CST 2012
 * Generated source version: 2.2.4
 * 
 */

public final class SMSWebServiceSoap_SMSWebServiceSoap_Client {

    private static final QName SERVICE_NAME = new QName("http://360buy.com/", "SMSWebService");

    private SMSWebServiceSoap_SMSWebServiceSoap_Client() {
    }

    public static void main(String args[]) throws Exception {
        URL wsdlURL = SMSWebService.WSDL_LOCATION;
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
      
        SMSWebService ss = new SMSWebService(wsdlURL, SERVICE_NAME);
        SMSWebServiceSoap port = ss.getSMSWebServiceSoap();  
        
        {
        System.out.println("Invoking sendMail...");
        com.jd.wms.wm.domain.sms.Mail _sendMail_mail = null;
        port.sendMail(_sendMail_mail);


        }
        {
        System.out.println("Invoking isSendSMS...");
        com.jd.wms.wm.domain.sms.SMS _isSendSMS_sms = null;
        boolean _isSendSMS__return = port.isSendSMS(_isSendSMS_sms);
        System.out.println("isSendSMS.result=" + _isSendSMS__return);


        }
        {
        System.out.println("Invoking sendSMS...");
        com.jd.wms.wm.domain.sms.SMS _sendSMS_sms = null;
        port.sendSMS(_sendSMS_sms);


        }
        {
        System.out.println("Invoking isSendSMS1...");
        com.jd.wms.wm.domain.sms.SMS1 _isSendSMS1_sms1 = null;
        boolean _isSendSMS1__return = port.isSendSMS1(_isSendSMS1_sms1);
        System.out.println("isSendSMS1.result=" + _isSendSMS1__return);


        }
        {
        System.out.println("Invoking sendSMS1...");
        com.jd.wms.wm.domain.sms.SMS1 _sendSMS1_sms1 = null;
        port.sendSMS1(_sendSMS1_sms1);


        }

        System.exit(0);
    }

}
