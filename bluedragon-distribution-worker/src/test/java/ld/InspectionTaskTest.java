package ld;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.distribution.asynbuffer.service.AsynBufferService;
import com.jd.bluedragon.distribution.consumer.inspection.InspectionPackageConsumer;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.worker.InspectionTask;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Method;

/**
 * @ClassName InspectionTaskTest
 * @Description
 * @Author wyh
 * @Date 2020/9/21 17:32
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class InspectionTaskTest {

    @Autowired
    private AsynBufferService asynBufferService;

    @Autowired
    private InspectionPackageConsumer inspectionPackageConsumer;
    @Autowired
    private InspectionTask inspectionTask;

    @Test
    public void testSplitExecute() throws Exception {
        String taskBody = "{\n" +
                "    \"body\": \"{\\\"businessType\\\":10,\\\"exceptionType\\\":\\\"\\\",\\\"id\\\":0,\\\"operateTime\\\":\\\"2020-09-21 17:59:02.265\\\",\\\"operateType\\\":0,\\\"packageBarOrWaybillCode\\\":\\\"JDVA00119406544\\\",\\\"waybillCode\\\":\\\"JDVA00119406544\\\",\\\"receiveSiteCode\\\":0,\\\"siteCode\\\":910,\\\"siteName\\\":\\\"马驹桥分拣中心\\\",\\\"userCode\\\":21181471,\\\"userName\\\":\\\"梁丽娟\\\",\\\"pageNo\\\":1,\\\"pageSize\\\":2}\",\n" +
                "    \"businessType\": 10,\n" +
                "    \"createSiteCode\": 820864,\n" +
                "    \"executeTime\": 1600682192575,\n" +
                "    \"fingerprint\": \"92AABAF2F3DB16165E6F504C56165D34\",\n" +
                "    \"keyword1\": \"910\",\n" +
                "    \"keyword2\": \"JDVA00119406544\",\n" +
                "    \"operateTime\": 1600682342265,\n" +
                "    \"ownSign\": \"DMS\",\n" +
                "    \"sequenceName\": \"SEQ_TASK_INSPECTION\",\n" +
                "    \"tableName\": \"task_inspection\",\n" +
                "    \"type\": 1130\n" +
                "}";

        Task task = JSON.parseObject(taskBody, Task.class);

        asynBufferService.inspectionSplitWaybillProcess(task);
    }

    @Test
    public void inspectionTest() throws Exception {
        String taskBody = "{\n" +
                "    \"body\": \"[{\\\"businessType\\\":10,\\\"exceptionType\\\":\\\"\\\",\\\"id\\\":0,\\\"operateTime\\\":\\\"2020-09-21 17:59:02.265\\\",\\\"operateType\\\":0,\\\"packageBarOrWaybillCode\\\":\\\"JDVA00119406626-1-5-\\\",\\\"receiveSiteCode\\\":0,\\\"siteCode\\\":910,\\\"siteName\\\":\\\"马驹桥分拣中心\\\",\\\"userCode\\\":21181471,\\\"userName\\\":\\\"梁丽娟\\\"}]\",\n" +
                "    \"businessType\": 10,\n" +
                "    \"createSiteCode\": 820864,\n" +
                "    \"executeTime\": 1600682192575,\n" +
                "    \"fingerprint\": \"92AABAF2F3DB16165E6F504C56165D34\",\n" +
                "    \"keyword1\": \"910\",\n" +
                "    \"keyword2\": \"JDVA00119406626-1-5-\",\n" +
                "    \"operateTime\": 1600682342265,\n" +
                "    \"ownSign\": \"DMS\",\n" +
                "    \"sequenceName\": \"SEQ_TASK_INSPECTION\",\n" +
                "    \"tableName\": \"task_inspection\",\n" +
                "    \"type\": 1130\n" +
                "}";

        Task task = JSON.parseObject(taskBody, Task.class);

        asynBufferService.inspectionTaskProcess(task);
    }

    @Test
    public void inspectionPackageConsumerTest() throws Exception {

        String mqBody = "{\n" +
                "  \"packageCode\" : \"JDVA00119406626-1-5-\",\n" +
                "  \"waybillCode\" : \"JDVA00119406626\",\n" +
                "  \"operateUserId\" : 21181471,\n" +
                "  \"operateUser\" : \"梁丽娟\",\n" +
                "  \"inspectionTime\" : 1600682342265,\n" +
                "  \"operateSiteCode\" : 910,\n" +
                "  \"inspectionType\" : 10,\n" +
                "  \"operateType\" : 0,\n" +
                "  \"exceptionType\" : \"\",\n" +
                "  \"receiveSiteCode\" : 0,\n" +
                "  \"recordCreateTime\" : 1606117113469\n" +
                "}";

        Message message = new Message();
        message.setText(mqBody);
        message.setTopic("dms_inspection");
        inspectionPackageConsumer.consume(message);
    }

    @Test
    public void executeSingleTaskTest() throws Exception{
        String json = "{\n" +
                "  \"shieldsCarCode\" : \"\",\n" +
                "  \"carCode\" : \"\",\n" +
                "  \"sealBoxCode\" : \"\",\n" +
                "  \"packOrBox\" : \"BC1001201118100000000707\",\n" +
                "  \"turnoverBoxCode\" : \"\",\n" +
                "  \"queueNo\" : \"\",\n" +
                "  \"departureCarId\" : \"\",\n" +
                "  \"shieldsCarTime\" : \"\",\n" +
                "  \"id\" : 4,\n" +
                "  \"businessType\" : 10,\n" +
                "  \"userCode\" : 10053,\n" +
                "  \"userName\" : \"刑松\",\n" +
                "  \"siteCode\" : 910,\n" +
                "  \"siteName\" : \"北京马驹桥分拣中心\",\n" +
                "  \"operateTime\" : \"2020-12-13 10:45:55.157\"\n" +
                "}";
        Task task = JsonHelper.fromJson(json, Task.class);
        Method executeSingleTask = InspectionTask.class.getDeclaredMethod("executeSingleTask", Task.class, String.class);
        executeSingleTask.setAccessible(true);
        executeSingleTask.invoke(inspectionTask, task, "DMS");
    }
}
