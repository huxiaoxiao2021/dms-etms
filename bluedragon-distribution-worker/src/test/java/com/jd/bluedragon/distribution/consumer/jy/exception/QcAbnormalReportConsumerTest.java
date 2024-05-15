package com.jd.bluedragon.distribution.consumer.jy.exception;

import com.jd.bluedragon.distribution.consumer.abnormalorder.QcAbnormalReportReportConsumer;
import com.jd.jmq.common.message.Message;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 异常提报(新)消费质控simple_wp_abnormal_record主题单元测试
 * @author lvyuan21
 * @date 2024-04-12 14:26:00
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class QcAbnormalReportConsumerTest {

    private static final Logger logger = LoggerFactory.getLogger(QcAbnormalReportConsumerTest.class);
    
    @Autowired
    private QcAbnormalReportReportConsumer qcAbnormalReportReportConsumer;
    
    @Test
    public void testConsume() {
        try {
            String jsonStr = "{\n" +
                    "\"id\" : 1728703779,\n" +
                    "\"abnormalDocumentNum\" : \"JD0120164450112\",\n" +
                    "\"packageNumber\" : \"JD0120164450112-1-5-\",\n" +
                    "\"abnormalFirstId\" : 20000,\n" +
                    "\"abnormalFirstName\" : \"安全\",\n" +
                    "\"abnormalSecondId\" : 20012,\n" +
                    "\"abnormalSecondName\" : \"破损\",\n" +
                    "\"abnormalThirdId\" : 20013,\n" +
                    "\"abnormalThirdName\" : \"外包装破损\",\n" +
                    "\"createRegion\" : \"3\",\n" +
                    "\"createDept\" : \"00058910\",\n" +
                    "\"createDeptName\" : \"京东集团-京东物流-快递快运事业部-上海区-短途运力部-上海松江传站车队\",\n" +
                    "\"createTime\" : 1712896331000,\n" +
                    "\"createUser\" : \"wuyoude\",\n" +
                    "\"dealRegion\" : \"3\",\n" +
                    "\"dealDept\" : \"40240\",\n" +
                    "\"dealDeptName\" : \"北京通州分拣中心\",\n" +
                    "\"abnormalStatus\" : \"34\",\n" +
                    "\"endStatus\" : \"5\",\n" +
                    "\"reportSystem\" : \"dms\"\n" +
                    "}";
            Message message = new Message();
            message.setText(jsonStr);
            qcAbnormalReportReportConsumer.consume(message);
            Assert.assertTrue(true);
        } catch (Exception e) {
            logger.error("服务异常!", e);
            Assert.fail();
        }
    }



}