package com.jd.bluedragon.distribution.rest.send;

import com.jd.bluedragon.distribution.test.AbstractTestCase;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.jd.bluedragon.distribution.api.request.DeliveryRequest;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.DateHelper;

public class DeliveryResourceTestCase extends AbstractTestCase {

	String urlRoot = "http://localhost:8088/services/";
	
	@Test
	public void testCancelDeliveryInfo(){
		DeliveryRequest request = new DeliveryRequest();
		request.setBoxCode("");
		request.setUserCode(111);
		request.setUserName("haha");
		request.setOperateTime(DateHelper.formatDate(new java.util.Date()));
		request.setReceiveSiteCode(10);
		
		String url = urlRoot + "/delivery/newpackagesend";

		RestTemplate template = new RestTemplate();

        try {
            String re = template.postForObject(url, request, String.class);
            System.out.println("re=" + re);
        }catch( Exception e ){
        	e.printStackTrace();
        }
	}

	@Test
	public void testNewPackageSend(){
		DeliveryRequest request = new DeliveryRequest();
		request.setBoxCode("");
		request.setUserCode(111);
		request.setUserName("haha");
		request.setOperateTime(DateHelper.formatDate(new java.util.Date()));
		request.setReceiveSiteCode(10);

		String url = urlRoot + "/delivery/newpackagesend";

		RestTemplate template = new RestTemplate();

		try {
			String re = template.postForObject(url, request, String.class);
			System.out.println("re=" + re);
		}catch( Exception e ){
			e.printStackTrace();
		}
	}

	@Test
	public void testTransitTask() {
		Task task = new Task();
		task.setId(31935491l);
		task.setKeyword1("5");
		task.setKeyword2("10");
		task.setCreateSiteCode(364605);
		task.setReceiveSiteCode(39);
		task.setBoxCode("BC010F002010F01600003001");
		task.setBody("364605-39-20180424170158010");
		task.setType(1300);
		task.setCreateTime(new Date());

		try {
//			deliveryService.findTransitSend(task);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
