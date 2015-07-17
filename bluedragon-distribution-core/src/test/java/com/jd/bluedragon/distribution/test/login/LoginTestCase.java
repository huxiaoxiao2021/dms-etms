package com.jd.bluedragon.distribution.test.login;

import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.junit.Test;

import com.jd.bluedragon.distribution.api.request.BaseRequest;
import com.jd.bluedragon.distribution.api.response.BaseResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;

public class LoginTestCase {

	@Test
	public void test_login() throws Exception {
	            String uri = "http://localhost:8080/services/bases/login";
	            ClientRequest request = new ClientRequest(uri);
	            request.accept(MediaType.APPLICATION_JSON);
	            //request.header("Content-Encoding", "gzip,deflate,compress");
	            
	            BaseRequest bq = new BaseRequest();
	    		bq.setErpAccount("bjwangzhipeng");
	    		bq.setPassword("chenzhe123");
	    		
	    		System.out.println(JsonHelper.toJson(bq));
	    		
	    		request.body(MediaType.APPLICATION_JSON, bq);

	            ClientResponse<BaseResponse> response = request.post(BaseResponse.class);
	            if (200 == response.getStatus()) {
	            	BaseResponse br = response.getEntity();
	            	System.out.println(br.getCode());
	            	System.out.println(br.getMessage());
	            }
	}
}
