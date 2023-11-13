package ld;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.distribution.asynbuffer.service.AsynBufferService;
import com.jd.bluedragon.distribution.consumer.inspection.InspectionPackageConsumer;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.worker.InspectionTask;
import com.jd.bluedragon.distribution.worker.work.WorkGridManagerSiteScanTask;
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
public class WorkGridManagerSiteScanTaskTest {


    @Autowired
    private WorkGridManagerSiteScanTask workGridManagerSiteScanTask;


    @Test
    public void executeSingleTaskTest() throws Exception{
        String json = "{\n" +
                "  \"keyword1\" : \"task_th_config_1\",\n" +
                "  \"keyword2\" : \"65454\",\n" +
                "  \"createSiteCode\" : \"65454\",\n" +
                "  \"receiveSiteCode\" : \"\",\n" +
                "  \"body\" : \"{\\n  \\\"taskConfigCode\\\" : \\\"task_th_config_1\\\",\\n  \\\"taskBatchCode\\\" : \\\"task_th_config_1_65454_20231112180958126\\\",\\n  \\\"siteCode\\\" : 65454,\\n  \\\"executeTime\\\" : 1699785600000,\\n  \\\"executeCount\\\" : 0\\n}\",\n" +
                "  \"status\" : 2,\n" +
                "  \"type\" : 1328,\n" +
                "  \"queueId\" : 0 \n" +
                "}";
        Task task = JsonHelper.fromJson(json, Task.class);
        Method executeSingleTask = WorkGridManagerSiteScanTask.class.getDeclaredMethod("executeSingleTask", Task.class, String.class);
        executeSingleTask.setAccessible(true);
        executeSingleTask.invoke(workGridManagerSiteScanTask, task, "DMS");
    }
}
