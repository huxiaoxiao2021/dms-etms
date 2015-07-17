package com.jd.bluedragon.distribution.test.spare;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import com.jd.bluedragon.distribution.api.request.SpareRequest;
import com.jd.bluedragon.distribution.api.response.SpareResponse;

public class SpareResourceTestCase {

    private final RestTemplate template = new RestTemplate();

	@Test
    public void test_add_spare() {
        // String url = "http://localhost:1111/services/spares";
		String url = "http://dms.etms.360buy.com/services/spares";
        // String url = "http://localhost/services/spares";

        SpareRequest request = new SpareRequest();

		request.setType("PB");
		request.setUserCode(33898);
		request.setUserName("王丽");
		request.setQuantity(300);

        SpareResponse response = this.template.postForObject(url, request, SpareResponse.class);

        System.out.println("code is " + response.getCode());
        System.out.println("message is " + response.getMessage());
        System.out.println("spare code are " + response.getSpareCodes());
    }

    public void test_reprint_spare() {
		String url = "http://localhost:222/services/spares/reprint";
        SpareRequest request = new SpareRequest();

		request.setSpareCode("PS2013032000001008");
        request.setUserCode(1001);
        request.setUserName("刘备");

        SpareResponse response = this.template.postForObject(url, request, SpareResponse.class);

        System.out.println("code is " + response.getCode());
        System.out.println("message is " + response.getMessage());
    }

}
