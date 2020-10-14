package com.jd.bluedragon.distribution.test.seal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.jd.bluedragon.distribution.api.request.SealBoxRequest;
import com.jd.bluedragon.distribution.api.request.SealVehicleRequest;
import com.jd.bluedragon.distribution.api.response.SealBoxResponse;

public class SealResourceTestCase {

    private final RestTemplate template = new RestTemplate();
    @Test
    public void test_add_seal_box() {
        String url = "http://localhost:8080/services/seal/box";

        SealBoxRequest request = new SealBoxRequest();
        request.setSealCode("1009");
        request.setBoxCode("1009");
        request.setSiteCode(1009);
        request.setSiteName("北京马驹桥分拣中心");
        request.setUserCode(123);
        request.setUserName("888");

        SealBoxResponse response = this.template.postForObject(url, request, SealBoxResponse.class);

        System.out.println("message is " + response.getMessage());
        System.out.println("code are " + response.getCode());
    }
    @Test
    public void test_add_seal_vehicle() {
        String url = "http://localhost:8080/services/seal/vehicle";

        SealVehicleRequest request = new SealVehicleRequest();
        request.setVehicleCode("京J010977");
        request.setSealCode("1009");
        request.setSiteCode(1009);
        request.setSiteName("北京马驹桥分拣中心");
        request.setUserCode(123);
        request.setUserName("888");
        request.setDriver("悟空");
        request.setDriverCode(123);

        SealBoxResponse response = this.template.postForObject(url, request, SealBoxResponse.class);

        System.out.println("message is " + response.getMessage());
        System.out.println("code are " + response.getCode());
    }

    @Test
    public void test_udpate_seal_box() {
//        String url = "http://10.10.224.118:20050/services/seal/box";
//
//        SealBoxRequest request = new SealBoxRequest();
//        request.setSealCode("1009");
//        request.setBoxCode("123456789N1S1");
//        request.setSiteCode(1001);
//        request.setUserCode(1002);
//        request.setUserName("777");
//
//        this.template.put(url, request);
    }

    public void test_udpate_seal_vehicle() {
        String url = "http://localhost:8080/services/seal/vehicle";

        SealVehicleRequest request = new SealVehicleRequest();
        request.setSealCode("1009");
        //        request.setSiteCode("1001");
        //        request.setSiteName("北京马驹桥分拣中心");
        //        request.setUserCode("八戒");
        request.setUserName("777");

        this.template.put(url, request);
    }

    public void test_get_seal() {
        String url = "http://localhost:8080/services/seal/box/{sealCode}";

        SealBoxResponse response = this.template.getForObject(url, SealBoxResponse.class, "1003");

        System.out.println("id is " + response.getId());
    }

}
