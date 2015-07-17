package com.jd.bluedragon.distribution.test.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import com.jd.bluedragon.distribution.api.request.BoxRequest;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReceiveLoss;
import com.jd.bluedragon.utils.JsonHelper;

public class JsonTestCase {

    private final RestTemplate template = new RestTemplate();

    public void test() {
        BoxRequest request1 = new BoxRequest();
        request1.setBoxCode("1897238723");
        request1.setBusinessType(10);
        request1.setQuantity(1);

        BoxRequest request2 = new BoxRequest();
        request2.setBoxCode("1897238723");
        request2.setBusinessType(20);
        request2.setQuantity(1);

        List<BoxRequest> boxes = new ArrayList<BoxRequest>();
        boxes.add(request1);
        boxes.add(request2);

        String url = "http://localhost:8080/services/boxes/test";
        BoxResponse response = this.template.postForObject(url, request1, BoxResponse.class);

        System.out.println("message is " + response.getMessage());
        System.out.println("box code are " + response.getBoxCodes());

        System.out.println("json is " + JsonHelper.toJson(boxes));

    }

    @Test
    public void test_json_serialize() {
        BoxRequest request1 = new BoxRequest();
        request1.setBoxCode("1897238723");
        request1.setBusinessType(10);
        request1.setQuantity(1);

        BoxRequest request2 = new BoxRequest();
        request2.setBoxCode("1897238723");
        request2.setBusinessType(20);
        request2.setQuantity(1);

        List<BoxRequest> boxes = new ArrayList<BoxRequest>();
        boxes.add(request1);
        boxes.add(request2);

        String json = JsonHelper.toJson(boxes);

        System.out.println("json is " + json);

        BoxRequest[] array = JsonHelper.jsonToArray(json, BoxRequest[].class);

        for (BoxRequest request : array) {
            System.out.println(request.getQuantity());
            System.out.println(request.getBoxCode());
            System.out.println(request.getBusinessType());
        }

        System.out.println("---------------------------- ");

        List<BoxRequest> list = Arrays.asList(array);
        for (BoxRequest request : list) {
            System.out.println(request.getQuantity());
            System.out.println(request.getBoxCode());
            System.out.println(request.getBusinessType());
        }

    }

    public static void main(String args[]){
    	ReverseReceiveLoss reverseReceiveLoss = new ReverseReceiveLoss();
    	
    	reverseReceiveLoss.setOrderId("1874499116");
    	reverseReceiveLoss.setReceiveType(1); //发货时没有这两个字段内容
    	reverseReceiveLoss.setUpdateDate(null); //因为没有这个时间
    	
    	
    	reverseReceiveLoss.setDmsId("2015");
    	reverseReceiveLoss.setDmsName("北京双树分拣中心");
    	reverseReceiveLoss.setStoreId("48349");
    	reverseReceiveLoss.setStoreName("北京15号库");
    	
	    reverseReceiveLoss.setIsLock(ReverseReceiveLoss.LOCK);
	    
	    
	    String jsonStrLock = JsonHelper.toJson(reverseReceiveLoss);
	    
	    reverseReceiveLoss.setUpdateDate("2014-09-23 11:20:24"); //因为没有这个时间
	    reverseReceiveLoss.setIsLock(ReverseReceiveLoss.UNLOCK);
	    String jsonStrUnlock = JsonHelper.toJson(reverseReceiveLoss);
	    
	    System.out.println(jsonStrLock);
	    System.out.println(jsonStrUnlock);
    }
}
