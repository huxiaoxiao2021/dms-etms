package com.jd.bluedragon.distribution.consumer.jy;

import com.jd.bluedragon.distribution.consumer.jy.vehicle.TmsJdiTransWorkItemEstimateArriveTimeConsumer;
import com.jd.bluedragon.distribution.jy.dto.task.TransWorkItemEstimateArriveTimeMqDTO;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * @program: ql-dms-distribution
 * @description: 运输封车任务预计到达时间消费测试
 * @author: ext.tiyong1
 * @create: 2023/8/4 周五 10:40
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class TmsJdiTransWorkItemEstimateArriveTimeConsumerTest {

    private static final Logger logger = LoggerFactory.getLogger(TmsJdiTransWorkItemEstimateArriveTimeConsumerTest.class);

    @Autowired
    private TmsJdiTransWorkItemEstimateArriveTimeConsumer tmsJdiTransWorkItemEstimateArriveTimeConsumer;

    @Test
    public void consumer() {
        try {
            TransWorkItemEstimateArriveTimeMqDTO transWorkItemEstimateArriveTimeMqDTO = new TransWorkItemEstimateArriveTimeMqDTO();
            transWorkItemEstimateArriveTimeMqDTO.setSealCarCode("SC22081600020777");
            transWorkItemEstimateArriveTimeMqDTO.setCreateUserCode("23");
            transWorkItemEstimateArriveTimeMqDTO.setCreateTime(new Date());
            transWorkItemEstimateArriveTimeMqDTO.setEstimateArriveTime(new Date());

            Message message = new Message();
            message.setText(JsonHelper.toJson(transWorkItemEstimateArriveTimeMqDTO));

            tmsJdiTransWorkItemEstimateArriveTimeConsumer.consume(message);

            Assert.assertTrue(true);
        } catch (Throwable e) {
            logger.error("运输封车任务预计到达时间mq处理异常!", e);
            Assert.fail();
        }
    }
}
