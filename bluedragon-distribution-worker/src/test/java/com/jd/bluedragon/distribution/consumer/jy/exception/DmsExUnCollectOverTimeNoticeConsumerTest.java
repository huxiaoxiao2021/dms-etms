package com.jd.bluedragon.distribution.consumer.jy.exception;

import com.google.common.collect.Lists;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionAgg;
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

import java.util.List;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2023/3/13 5:41 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class DmsExUnCollectOverTimeNoticeConsumerTest {

    private static final Logger logger = LoggerFactory.getLogger(DmsExUnCollectOverTimeNoticeConsumerTest.class);
    
    @Autowired
    private DmsExUnCollectOverTimeNoticeConsumer dmsExUnCollectOverTimeNoticeConsumer;
    
    @Test
    public void consume() {
        try {
            Message message = new Message();
            List<JyExceptionAgg> jyExceptionAggs = Lists.newArrayList();
            JyExceptionAgg jyExceptionAgg = new JyExceptionAgg();
            jyExceptionAgg.setSiteCode(910);
            jyExceptionAgg.setGridCode("gridCode1");
            jyExceptionAgg.setQuantity(2);
            jyExceptionAgg.setCreateUserErp("bjxings");
            JyExceptionAgg jyExceptionAgg1 = new JyExceptionAgg();
            jyExceptionAgg1.setSiteCode(910);
            jyExceptionAgg1.setGridCode("gridCode2");
            jyExceptionAgg1.setQuantity(3);
            jyExceptionAgg1.setCreateUserErp("bjxings");
            jyExceptionAggs.add(jyExceptionAgg);
            jyExceptionAggs.add(jyExceptionAgg1);
            message.setText(JsonHelper.toJson(jyExceptionAggs));
            dmsExUnCollectOverTimeNoticeConsumer.consume(message);
        }catch (Exception e){
            logger.error("服务异常!", e);
            Assert.fail();
        }
    }
}
