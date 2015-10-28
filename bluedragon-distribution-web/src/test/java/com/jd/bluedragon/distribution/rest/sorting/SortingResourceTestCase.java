package com.jd.bluedragon.distribution.rest.sorting;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.jd.bluedragon.distribution.api.request.SealBoxRequest;
import com.jd.bluedragon.distribution.api.request.SortingRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.api.response.TaskResponse;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.CollectionHelper;
import com.jd.bluedragon.utils.JsonHelper;

public class SortingResourceTestCase {

    private final RestTemplate template = new RestTemplate();

    @Test(expected=Exception.class)
    public void test_cancel() {
        SortingRequest sortingRequest = new SortingRequest();
        sortingRequest.setSiteCode(1006);
        sortingRequest.setUserCode(18997);
        sortingRequest.setUserName("陈哲");
        sortingRequest.setPackageCode("170279480-2-2-");
        sortingRequest.setBusinessType(30);

        ClientRequest request = new ClientRequest(
                "http://dmswebtest.360buy.com/services/sorting/cancel");
        request.accept(MediaType.APPLICATION_JSON);

        request.body("application/json", "{ss:'sss'}");

        ClientResponse<SortingResponse> response;
        try {
            response = request.put(SortingResponse.class);
            Assert.assertNotNull(response);
            Assert.assertEquals(200, response.getStatus());
            if (response.getStatus() == 200) {
            	Assert.assertNotNull(response.getEntity());
                System.out.println(response.getEntity());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public void test() throws Exception {
        String url = "http://10.10.224.118:20050/services/sorting?siteCode=1002&boxCode=B1001100500002002";

        ClientRequest request = new ClientRequest(url);
        request.accept(MediaType.APPLICATION_JSON);

        SortingResponse[] responseArray = request.get(SortingResponse[].class).getEntity();

        for (SortingResponse response : responseArray) {
            System.out.println(response.getBoxCode());
        }
    }
    @Test
    public void test_add_sorting_task() {
        String url = "http://localhost:8080/services/tasks";

        TaskRequest request = new TaskRequest();

        request.setType(Task.TASK_TYPE_SORTING);
        request.setSiteCode(99);
        request.setSiteName("武汉分拣中心");
        request.setUserCode(1001);
        request.setUserName("王治澎");
        request.setReceiveSiteCode(100);
        request.setBoxCode("B010F001010F00200000001");
        request.setKeyword1("B010F001010F00200000001");

        SortingRequest sortingRequest1 = new SortingRequest();
        sortingRequest1.setSiteCode(99);
        sortingRequest1.setSiteName("武汉分拣中心");
        sortingRequest1.setUserCode(1001);
        sortingRequest1.setUserName("王治澎");
        sortingRequest1.setBoxCode("B010F001010F00200000001");
        sortingRequest1.setPackageCode("630000000001N1S3H1");
        sortingRequest1.setReceiveSiteCode(100);
        sortingRequest1.setReceiveSiteName("汉口自提点");
        sortingRequest1.setIsCancel(0);
        sortingRequest1.setBusinessType(10);

        SortingRequest sortingRequest2 = new SortingRequest();
        sortingRequest2.setSiteCode(99);
        sortingRequest2.setSiteName("武汉分拣中心");
        sortingRequest2.setUserCode(1001);
        sortingRequest2.setUserName("王治澎");
        sortingRequest2.setBoxCode("B010F001010F00200000001");
        sortingRequest1.setPackageCode("630000000001N2S3H1");
        sortingRequest2.setReceiveSiteCode(100);
        sortingRequest2.setReceiveSiteName("汉口自提点");
        sortingRequest2.setIsCancel(1);
        sortingRequest2.setBusinessType(10);

        SortingRequest sortingRequest3 = new SortingRequest();
        sortingRequest3.setSiteCode(99);
        sortingRequest3.setSiteName("武汉分拣中心");
        sortingRequest3.setUserCode(1001);
        sortingRequest3.setUserName("王治澎");
        sortingRequest3.setBoxCode("B010F001010F00200000001");
        sortingRequest1.setPackageCode("630000000001N3S3H1");
        sortingRequest3.setReceiveSiteCode(100);
        sortingRequest3.setReceiveSiteName("汉口自提点");
        sortingRequest3.setIsCancel(1);
        sortingRequest3.setBusinessType(10);

        //        SortingRequest sortingRequest4 = new SortingRequest();
        //        sortingRequest4.setSiteCode(99);
        //        sortingRequest4.setSiteName("武汉分拣中心");
        //        sortingRequest4.setUserCode(1001);
        //        sortingRequest4.setUserName("王治澎");
        //        sortingRequest4.setBoxCode("B027F001027Z00100002002");
        //        sortingRequest4.setPackageCode("123454787N1S1H");
        //        sortingRequest4.setReceiveSiteCode(100);
        //        sortingRequest4.setReceiveSiteName("汉口自提点");
        //        sortingRequest4.setIsCancel(1);
        //        sortingRequest4.setBusinessType(10);

        List<SortingRequest> list = new ArrayList<SortingRequest>();
        list.add(sortingRequest1);
        list.add(sortingRequest2);
        list.add(sortingRequest3);
        //        list.add(sortingRequest4);

        request.setBody(JsonHelper.toJson(list));

        TaskResponse response = this.template.postForObject(url, request, TaskResponse.class);

        System.out.println("code :: " + response.getCode());
        System.out.println("message :: " + response.getMessage());
    }

    public void test_add_pickup_sorting_task() {
        String url = "http://localhost/services/tasks";

        TaskRequest request = new TaskRequest();

        request.setType(Task.TASK_TYPE_SORTING);
        request.setSiteCode(511);
        request.setSiteName("武汉分拣中心");
        request.setUserCode(1001);
        request.setUserName("王治澎");
        request.setReceiveSiteCode(2);
        request.setBoxCode("650000000001N1S1H1");
        request.setKeyword1("650000000001N1S1H1");

        SortingRequest sortingRequest1 = new SortingRequest();
        sortingRequest1.setSiteCode(511);
        sortingRequest1.setSiteName("武汉分拣中心");
        sortingRequest1.setUserCode(1001);
        sortingRequest1.setUserName("王治澎");
        sortingRequest1.setBoxCode("650000000001N1S1H1");
        sortingRequest1.setPackageCode("650000000001N1S1H1");
        sortingRequest1.setReceiveSiteCode(2);
        sortingRequest1.setReceiveSiteName("亚运村");
        sortingRequest1.setIsCancel(0);
        sortingRequest1.setBusinessType(10);

        //        SortingRequest sortingRequest2 = new SortingRequest();
        //        sortingRequest2.setSiteCode(99);
        //        sortingRequest2.setSiteName("武汉分拣中心");
        //        sortingRequest2.setUserCode(1001);
        //        sortingRequest2.setUserName("王治澎");
        //        sortingRequest2.setBoxCode("T027Z001027F00100001001");
        //        sortingRequest2.setPackageCode("203104005-1-1-");
        //        sortingRequest2.setReceiveSiteCode(104);
        //        sortingRequest2.setReceiveSiteName("武汉售后组");
        //        sortingRequest2.setIsCancel(0);
        //        sortingRequest2.setBusinessType(20);

        List<SortingRequest> list = new ArrayList<SortingRequest>();
        list.add(sortingRequest1);
        // list.add(sortingRequest2);

        request.setBody(JsonHelper.toJson(list));

        TaskResponse response = this.template.postForObject(url, request, TaskResponse.class);

        System.out.println("code :: " + response.getCode());
        System.out.println("message :: " + response.getMessage());
    }

    public void test_add_seal_box_task() {
        String url = "http://localhost:80/services/tasks";

        TaskRequest request = new TaskRequest();

        request.setType(Task.TASK_TYPE_SEAL_BOX);
        request.setSiteCode(99);
        request.setSiteName("武汉分拣中心");
        request.setUserCode(1001);
        request.setUserName("王治澎");
        request.setKeyword1("146457167");

        SealBoxRequest sealRequest1 = new SealBoxRequest();
        sealRequest1.setSiteCode(99);
        sealRequest1.setSiteName("武汉分拣中心");
        sealRequest1.setUserCode(1001);
        sealRequest1.setUserName("王治澎");
        sealRequest1.setBoxCode("123456789N1S1");
        sealRequest1.setSealCode("1009");

        SealBoxRequest sealRequest2 = new SealBoxRequest();
        sealRequest2.setSiteCode(99);
        sealRequest2.setSiteName("武汉分拣中心");
        sealRequest2.setUserCode(1001);
        sealRequest2.setUserName("王治澎");
        sealRequest2.setBoxCode("123456780");
        sealRequest2.setSealCode("1008");

        List<SealBoxRequest> list = new ArrayList<SealBoxRequest>();
        list.add(sealRequest1);
        list.add(sealRequest2);

        request.setBody(JsonHelper.toJson(list));

        TaskResponse response = this.template.postForObject(url, request, TaskResponse.class);

        System.out.println("response :: " + response.getId());
        System.out.println("code :: " + response.getCode());
        System.out.println("message :: " + response.getMessage());
    }

    public void test_set() {
        SortingRequest sortingRequest1 = new SortingRequest();
        sortingRequest1.setSiteCode(99);
        sortingRequest1.setSiteName("武汉分拣中心");
        sortingRequest1.setUserCode(1001);
        sortingRequest1.setUserName("王治澎");
        sortingRequest1.setBoxCode("123456789N1S1");
        sortingRequest1.setPackageCode("123456789N1S1");
        sortingRequest1.setWaybillCode("123456789");
        sortingRequest1.setReceiveSiteCode(99);
        sortingRequest1.setReceiveSiteName("汉口自提点");
        sortingRequest1.setIsCancel(0);
        sortingRequest1.setBusinessType(10);

        SortingRequest sortingRequest2 = new SortingRequest();
        sortingRequest2.setSiteCode(1001);
        sortingRequest2.setSiteName("武汉分拣中心");
        sortingRequest2.setUserCode(1001);
        sortingRequest2.setUserName("王治澎");
        sortingRequest2.setBoxCode("123456780");
        sortingRequest2.setPackageCode("123456780");
        sortingRequest2.setReceiveSiteCode(99);
        sortingRequest2.setReceiveSiteName("汉口自提点");
        sortingRequest2.setIsCancel(1);
        sortingRequest2.setBusinessType(10);

        SortingRequest sortingRequest3 = new SortingRequest();
        sortingRequest3.setSiteCode(1001);
        sortingRequest3.setSiteName("武汉分拣中心");
        sortingRequest3.setUserCode(1001);
        sortingRequest3.setUserName("王治澎");
        sortingRequest3.setBoxCode("123456780");
        sortingRequest3.setPackageCode("123456780");
        sortingRequest3.setReceiveSiteCode(99);
        sortingRequest3.setReceiveSiteName("汉口自提点");
        sortingRequest3.setIsCancel(1);
        sortingRequest3.setBusinessType(10);

        List<SortingRequest> list = new ArrayList<SortingRequest>();
        list.add(sortingRequest1);
        list.add(sortingRequest2);
        list.add(sortingRequest3);

        System.out.println(list.size());

        Set<SortingRequest> sortingPackages = new CollectionHelper<SortingRequest>().toSet(list);

        System.out.println(sortingPackages.size());
    }
}
