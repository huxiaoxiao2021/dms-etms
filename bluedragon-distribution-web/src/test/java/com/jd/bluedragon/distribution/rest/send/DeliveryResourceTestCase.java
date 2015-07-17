package com.jd.bluedragon.distribution.rest.send;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.jd.bluedragon.distribution.api.request.DeliveryRequest;
import com.jd.bluedragon.utils.DateHelper;

@RunWith(SpringJUnit4ClassRunner.class)
public class DeliveryResourceTestCase {

	String urlRoot = "http://localhost:8989/services/";
	
	@Test
	public void testCancelDeliveryInfo(){
		DeliveryRequest request = new DeliveryRequest();
		request.setBoxCode("");
		request.setUserCode(111);
		request.setUserName("haha");
		request.setOperateTime(DateHelper.formatDate(new java.util.Date()));
		request.setReceiveSiteCode(10);
		
		String url = urlRoot + "/delivery/cancel";
		RestTemplate template = new RestTemplate();

        try {
            String re = template.postForObject(url, request, String.class);
            System.out.println("re=" + re);
        }catch( Exception e ){
        	e.printStackTrace();
        }
	}


}
