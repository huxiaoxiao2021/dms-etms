package com.jd.bluedragon.distribution.test.auto.service;

import com.jd.bluedragon.distribution.auto.service.SortingPrepareService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.test.TestBase;
import com.jd.bluedragon.utils.DateHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class SortingPrepareServiceTest extends TestBase {

    private static final Log logger= LogFactory.getLog(SortingPrepareServiceTest.class);

    @Autowired
    private SortingPrepareService sortingPrepareService;

    @Autowired
    private TaskService taskService;

    /**
     * 自动分拣机任务处理
     * @throws Exception
     */
    @Test
    public void testHandler() throws Exception {
        /*List<Task> list= taskService.findLimitedTasks(7777, 100, "WTW-TEST");
        if(null==list){
            log.info("没有获取到所需要的数据");
            return;
        }
        for (Task task:list){
            log.info(task.toString());
            Assert.assertEquals(true,sortingPrepareService.handler(task));
        }*/
    }
    @Test
    public void testSimpleHandler(){
        Task task=new Task();
        task.setBody("{\n" +
                "  \"timeStamp\" : \"2014-10-23 15:19:52\",\n" +
                "  \"weight\" : 0.021,\n" +
                "  \"barcode\" : \"61673311221-1-2-\",\n" +
                "  \"sortCenterNo\" : 910,\n" +
                "  \"chute\" : \"49\",\n" +
                "  \"operatorName\" : \"zhangsan\",\n" +
                "  \"operatorID\" : 1111,\n" +
                "  \"erpAccount\" : \"zhangsan4\",\n" +
                "  \"sortCenterName\" : \"广州麻涌分拣中心\"\n" +
                "}");
        Assert.assertEquals(true,sortingPrepareService.handler(task));
    }

    @Test
    public void testCurrentTime(){
        Date current=new Date(System.currentTimeMillis());
        System.out.println(DateHelper.formatDate(current, "YYYYMMddHHmmssSSS"));

        Date cu2=new Date();
        System.out.println(DateHelper.formatDate(cu2, "yyyyMMDDHHmmssSSS"));
    }
}