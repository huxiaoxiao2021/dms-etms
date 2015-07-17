package com.jd.bluedragon.distribution.test.task;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import com.jd.bluedragon.distribution.api.request.SortingRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.TaskResponse;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;

public class TaskResourceTestCase {

    private final RestTemplate template = new RestTemplate();

    @Test
    public void test_add_task() {
        String url = "http://localhost:8080/services/tasks";

        TaskRequest request = new TaskRequest();

        request.setType(Task.TASK_TYPE_BOUNDARY);
        request.setSiteCode(1008);
        request.setSiteName("北京马驹桥分拣中心");
        request.setUserCode(1001);
        request.setUserName("王治澎");
        request.setKeyword1("146457167");

        SortingRequest sortingRequest1 = new SortingRequest();
        sortingRequest1.setBoxCode("146457167-1-1-Z");
        sortingRequest1.setPackageCode("146457167-1-1-Z");
        sortingRequest1.setWaybillCode("146457167");

        SortingRequest sortingRequest2 = new SortingRequest();
        sortingRequest2.setBoxCode("146457167-1-1-Z");
        sortingRequest2.setPackageCode("146457167-1-1-Z");
        sortingRequest2.setWaybillCode("146457167");

        List<SortingRequest> list = new ArrayList<SortingRequest>();
        list.add(sortingRequest1);
        list.add(sortingRequest2);

        request.setBody(JsonHelper.toJson(list));
        request.setBoxCode("456");
        request.setReceiveSiteCode(4444);
        TaskResponse response = this.template.postForObject(url, request, TaskResponse.class);

        System.out.println("response :: " + response.getId());
        System.out.println("code :: " + response.getCode());
        System.out.println("message :: " + response.getMessage());
    }

    public void test_get_task() {
        String url = "http://localhost:8080/services/tasks/{taskId}";

        TaskResponse response = this.template.getForObject(url, TaskResponse.class, 2001);

        System.out.println("id is " + response.getId());
        System.out.println("createTime is " + response.getCreateTime());
    }

}
