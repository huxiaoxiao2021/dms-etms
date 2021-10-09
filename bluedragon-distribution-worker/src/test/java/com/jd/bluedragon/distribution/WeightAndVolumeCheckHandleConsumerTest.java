package com.jd.bluedragon.distribution;

import com.jd.bluedragon.distribution.consumer.weightAndVolumeCheck.WeightAndVolumeCheckHandleConsumer;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.WeightAndVolumeCheckHandleMessage;
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

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2021/9/7 6:18 下午
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class WeightAndVolumeCheckHandleConsumerTest {

    private static final Logger logger = LoggerFactory.getLogger(WeightAndVolumeCheckHandleConsumerTest.class);

    @Autowired
    private WeightAndVolumeCheckHandleConsumer weightAndVolumeCheckHandleConsumer;

    @Test
    public void consumer() {
        try {
            WeightAndVolumeCheckHandleMessage weightAndVolumeCheckHandleMessage = new WeightAndVolumeCheckHandleMessage();
            weightAndVolumeCheckHandleMessage.setSiteCode(910);
            weightAndVolumeCheckHandleMessage.setWaybillCode("JDV000705034485");
            weightAndVolumeCheckHandleMessage.setPackageCode("JDV000705034485-1-3-");
//            weightAndVolumeCheckHandleMessage.setOpNode(WeightAndVolumeCheckHandleMessage.UPLOAD_IMG);
            weightAndVolumeCheckHandleMessage.setOpNode(WeightAndVolumeCheckHandleMessage.SEND);
            Message message = new Message();
            message.setText(JsonHelper.toJson(weightAndVolumeCheckHandleMessage));

            weightAndVolumeCheckHandleConsumer.consume(message);
            Assert.assertTrue(true);
        }catch (Throwable e){
            logger.error("抽检包裹发货处理异常!", e);
            Assert.fail();
        }
    }
}
