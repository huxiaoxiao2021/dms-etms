package com.jd.bluedragon.distribution.test.task;

import java.util.Date;
import java.util.List;
import java.util.Random;

import com.jd.bluedragon.distribution.api.request.QualityControlRequest;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.Md5Helper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.JsonHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/distribution-core-context.xml" })
public class TaskTestCase {

    @Autowired
    private TaskService taskService;

    @Test
    public void test_add_task() {
    	Task tTask = new Task();
    	tTask.setBoxCode("111");
    	tTask.setBody("222");
    	tTask.setCreateSiteCode(1);
    	tTask.setKeyword2(String.valueOf(10));
    	tTask.setReceiveSiteCode(2);
    	tTask.setType(Task.TASK_TYPE_SEND_DELIVERY);
    	tTask.setTableName(Task.getTableName(Task.TASK_TYPE_SEND_DELIVERY));
    	tTask.setOwnSign("DMS");
    	tTask.setKeyword1("1");//1 回传运单状态
    	tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_SEND));
        System.out.println("11111");
        System.out.println("Add:" + this.taskService.add(tTask));
        System.out.println("22222");
    }

    //@Test
    public void testFindTasksStatusByBoxcode(){
    	Task task = new Task();
        task.setCreateSiteCode(1000);
        task.setType(Task.TASK_TYPE_SORTING);
        task.setBoxCode("asdf");
    	List<Task> result = taskService.findTasks(task);
    	
    	System.out.println(" result == "+result);
    }
    
    //@Test
    public void testTrunk(){
    	String jsonVal = "[{\"sealBoxCode\":\"\",\"boxCode\":\"\",\"packageBarOrWaybillCode\":\"1\",\"exceptionType\":\"\",\"receiveSiteCode\":0,\"id\":1,\"businessType\":50,\"userCode\":1,\"userName\":\"测试用户\",\"siteCode\":1000,\"siteName\":\"北京马驹桥分拣中心\",\"operateTime\":\"2012-03-15 15:52:53.817\"}," +
    			"{\"sealBoxCode\":\"\",\"boxCode\":\"\",\"packageBarOrWaybillCode\":\"3\",\"exceptionType\":\"\",\"receiveSiteCode\":0,\"id\":2,\"businessType\":50,\"userCode\":1,\"userName\":\"测试用户\",\"siteCode\":1000,\"siteName\":\"北京马驹桥分拣中心\",\"operateTime\":\"2012-03-15 15:52:55.877\"}]";
    	Object[] array = JsonHelper.jsonToArray(jsonVal, Object[].class);
    	for( int i=0;i<array.length;i++)
    		System.out.println(array[i].toString()+"---");
    }
    
    public static void main(String[] args){
    	InspectionRequest[] arrays = JsonHelper.jsonToArray("[{\n  \"sealBoxCode\" : \"\",\n  \"boxCode\" : \"\",\n  \"packageBarOrWaybillCode\" : \"4330552537-2-2-7\",\n  \"exceptionType\" : \"\",\n  \"operateType\" : 0,\n  \"receiveSiteCode\" : 0,\n  \"id\" : 27801,\n  \"businessType\" : 10,\n  \"userCode\" : 85951,\n  \"userName\" : \"姜超\",\n  \"siteCode\" : 2505,\n  \"siteName\" : \"南京分拨中心\",\n  \"operateTime\" : \"2014-10-31 16:18:28.000\"\n}]",
				InspectionRequest[].class);
    	System.out.println(arrays.toString());
    }
    
}
