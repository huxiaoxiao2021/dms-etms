package com.jd.bluedragon.distribution.consumer.print;

import com.google.common.collect.Lists;
import com.jd.bluedragon.sdk.modules.recyclematerial.dto.MaterialAbolishReq;
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
 * @date 2023/4/14 5:34 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class RecycleBasketAbolishConsumerTest {

    private static final Logger logger = LoggerFactory.getLogger(RecycleBasketAbolishConsumerTest.class);
    
    @Autowired
    private RecycleBasketAbolishConsumer consumer;
    
    @Test
    public void consume() {

        try {
            MaterialAbolishReq materialAbolishReq = new MaterialAbolishReq();
            materialAbolishReq.setOperateSiteCode(910);
            materialAbolishReq.setOperateUserErp("wuyoude");
            List<String> materialList = Lists.newArrayList();
            materialList.add("AK1001220323200000100210");
            materialList.add("AK1001220323250000112004");
//            materialList.add("123");
            materialAbolishReq.setMaterialList(materialList);

            Message message = new Message();
            message.setText(JsonHelper.toJson(materialAbolishReq));

            consumer.consume(message);
            Assert.assertTrue(true);
        }catch (Throwable e){
            logger.error("dws抽检mq处理异常!", e);
            Assert.fail();
        }
        
    }
}