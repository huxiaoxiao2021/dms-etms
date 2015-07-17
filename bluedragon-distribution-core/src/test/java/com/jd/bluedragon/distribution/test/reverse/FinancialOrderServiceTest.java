package com.jd.bluedragon.distribution.test.reverse;

import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.jd.common.ws.AuthHeader;
import com.jd.common.ws.SOAPHeaderIntercepter;
import com.jd.fms.finance.client.FinancialSearchService;


public class FinancialOrderServiceTest {

	public static void main(String[] args) {
		    JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();  
		   
		    
	        factory.getInInterceptors().add(new LoggingInInterceptor());  
	        factory.getOutInterceptors().add(new LoggingOutInterceptor());  
	        
	        AuthHeader authHeader = new AuthHeader();
		    authHeader.setqName("www.360buy.com");
		    authHeader.setKey("AuthenticationHeader");
		    authHeader.setContent("123456");
		    authHeader.setSeed("123456");
		    SOAPHeaderIntercepter outInercepter = new com.jd.common.ws.SOAPHeaderIntercepter();
		    outInercepter.setAuthHeader(authHeader);
	        factory.getOutInterceptors().add(outInercepter);  

	        factory.setServiceClass(FinancialSearchService.class);  
	        factory.setAddress("http://ipatickets.test.360buy.com/ws/financialSearch");
	        Map<String, Object> properties = new HashMap<String, Object>();
	        properties.put("token", "123456");
			factory.setProperties(properties );
			Long a  = System.currentTimeMillis();
	        FinancialSearchService client = (FinancialSearchService) factory.create();  
	        
	       
			long aa = client.getFinancialOrderByOrderId(491392822);
			
			Long b  = System.currentTimeMillis();
			
			System.out.println("aa:" + aa);
			System.out.println((b-a));
			
			
	}
}

