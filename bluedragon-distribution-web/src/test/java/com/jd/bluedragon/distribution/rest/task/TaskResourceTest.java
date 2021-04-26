package com.jd.bluedragon.distribution.rest.task;

import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.TaskResponse;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * 类的描述
 *
 * @author hujiping
 * @date 2021/4/20 3:36 下午
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:distribution-web-context.xml"})
public class TaskResourceTest {

    private final Logger logger = LoggerFactory.getLogger(TaskResourceTest.class);

    @Autowired
    private TaskResource taskResource;

    private String body = "";

    @Before
    public void setUp() throws Exception {
        body = "[\t{\n" +
                "    \"demo\":\"\",\n" +
                "    \"weight\":\"0\",\n" +
                "    \"operateTime\":\"2021-04-21 16:25:48.319\",\n" +
                "    \"batchCode\":\"639110-238519-20210419084451823\",\n" +
                "    \"taskType\":1301,\n" +
                "    \"shieldsCarCode\":\"\",\n" +
                "    \"boxCode\":\"JD0040940879423-1-1-\",\n" +
                "    \"waybillCode\":\"\",\n" +
                "    \"packageCode\":\"JD0040940879423-1-1-\",\n" +
                "    \"id\":3463,\n" +
                "    \"railwayNo\":\"\",\n" +
                "    \"sendUser\":\"\",\n" +
                "    \"userName\":\"唐情\",\n" +
                "    \"receiveSiteCode\":0,\n" +
                "    \"siteName\":\"佛山狮山分拣中心\",\n" +
                "    \"transName\":\"\",\n" +
                "    \"goodsType\":\"\",\n" +
                "    \"sealBoxCode\":\"\",\n" +
                "    \"exceptionType\":\"\",\n" +
                "    \"turnoverBoxCode\":\"\",\n" +
                "    \"operateType\":0,\n" +
                "    \"userCode\":20340488,\n" +
                "    \"carCode\":\"\",\n" +
                "    \"businessType\":10,\n" +
                "    \"sendUserCode\":\"\",\n" +
                "    \"siteCode\":639110,\n" +
                "    \"airNo\":\"\",\n" +
                "    \"num\":0,\n" +
                "    \"volume\":\"0\"\n" +
                "}]";
    }

    @Test
    public void add() {
        TaskRequest request = new TaskRequest();
        request.setType(Task.TASK_TYPE_OFFLINE);
        request.setBody(body);

        try {
            TaskResponse taskResponse = taskResource.add(request);
            Assert.assertTrue(true);
        }catch (Exception e){
            logger.error("离线任务上传异常!",e);
            Assert.fail();
        }
    }
}
