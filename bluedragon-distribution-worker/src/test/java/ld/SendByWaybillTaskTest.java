package ld;
import com.google.common.collect.Lists;
import java.util.Date;

import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @ClassName InspectionTaskTest
 * @Description
 * @Author wyh
 * @Date 2020/9/21 17:32
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class SendByWaybillTaskTest {
    @Autowired
    private DeliveryService deliveryService;

    @Test
    public void doWaybillSendDeliveryTest() {
        Task task = new Task();
        task.setBoxCode("JD0003347224531");
        task.setReceiveSiteCode(39);
        task.setId(0L);
        task.setCreateTime(new Date());
        task.setExecuteCount(0);
        task.setExecuteTime(new Date());
        task.setType(1322);
        task.setStatus(0);
        task.setStatuses("");
        task.setYn(1);
        task.setKeyword1("10");
        task.setKeyword2("JD0003347224531");
        task.setBody("{\n" +
                "  \"sendCode\" : \"910-39-20201217090606791\",\n" +
                "  \"boxCode\" : \"JD0003347224531\",\n" +
                "  \"turnoverBoxCode\" : \"\",\n" +
                "  \"createSiteCode\" : 910,\n" +
                "  \"receiveSiteCode\" : 39,\n" +
                "  \"sendType\" : 10,\n" +
                "  \"createUser\" : \"刑松\",\n" +
                "  \"createUserCode\" : 10053,\n" +
                "  \"operateTime\" : 1608167365756,\n" +
                "  \"createTime\" : 1608167365756,\n" +
                "  \"yn\" : 1,\n" +
                "  \"transporttype\" : 0,\n" +
                "  \"bizSource\" : 21\n" +
                "}");
        task.setCreateSiteCode(0);
        task.setTableName("task_send");
        task.setFingerprint("7B9DFF366307692444E7C2E4DDF735A8");
        task.setParsedObject(new Object());
        task.setSequenceName("");
        task.setOwnSign("DMS");
        task.setBusinessType(0);
        task.setOperateType(0);
        task.setOperateTime(new Date());
        task.setQueueId(0);
        task.setStatusesList(null);
        task.setSubType(0);

        deliveryService.doWaybillSendDelivery(task);
    }
    @Test
    public void doSendByWaybillSplitTaskTest() {
        Task task = new Task();
        task.setBoxCode("JD0003347224531-1-100-");
        task.setReceiveSiteCode(39);
        task.setId(0L);
        task.setCreateTime(new Date());
        task.setExecuteCount(0);
        task.setExecuteTime(new Date());
        task.setType(1322);
        task.setStatus(0);
        task.setStatuses("");
        task.setYn(1);
        task.setKeyword1("10");
        task.setKeyword2("JD0003347224531-1-100-");
        task.setBody("{\n" +
                "  \"sendCode\" : \"910-39-20201217090606791\",\n" +
                "  \"boxCode\" : \"JD0003347224531-1-100-\",\n" +
                "  \"turnoverBoxCode\" : \"\",\n" +
                "  \"createSiteCode\" : 910,\n" +
                "  \"receiveSiteCode\" : 39,\n" +
                "  \"sendType\" : 10,\n" +
                "  \"createUser\" : \"刑松\",\n" +
                "  \"createUserCode\" : 10053,\n" +
                "  \"operateTime\" : 1608167365756,\n" +
                "  \"createTime\" : 1608167365756,\n" +
                "  \"yn\" : 1,\n" +
                "  \"transporttype\" : 0,\n" +
                "  \"bizSource\" : 21\n" +
                "}");
        task.setCreateSiteCode(0);
        task.setTableName("task_send");
        task.setFingerprint("7B9DFF366307692444E7C2E4DDF735A8");
        task.setParsedObject(new Object());
        task.setSequenceName("");
        task.setOwnSign("DMS");
        task.setBusinessType(0);
        task.setOperateType(0);
        task.setOperateTime(new Date());
        task.setQueueId(0);
        task.setStatusesList(null);
        task.setSubType(0);

        deliveryService.doSendByWaybillSplitTask(task);
    }
}
