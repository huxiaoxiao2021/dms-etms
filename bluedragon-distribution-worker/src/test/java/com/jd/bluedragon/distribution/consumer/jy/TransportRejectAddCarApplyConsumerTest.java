package com.jd.bluedragon.distribution.consumer.jy;

import com.google.common.collect.Lists;
import com.jd.bluedragon.distribution.consumer.jy.strand.TransportRejectAddCarApplyConsumer;
import com.jd.bluedragon.distribution.jy.dto.strand.TransportRejectAddCarApplyMQ;
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
import java.util.List;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2023/4/6 2:22 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class TransportRejectAddCarApplyConsumerTest {

    private static final Logger logger = LoggerFactory.getLogger(TransportRejectAddCarApplyConsumerTest.class);
    
    @Autowired
    private TransportRejectAddCarApplyConsumer consumer;

    @Test
    public void consumer() {
        try {
            Message message = new Message();
            TransportRejectAddCarApplyMQ rejectAddCarApplyMQ = new TransportRejectAddCarApplyMQ();
            rejectAddCarApplyMQ.setTransPlanCode("transPlanCode");
            rejectAddCarApplyMQ.setBeginNodeCode("910");
            rejectAddCarApplyMQ.setBeginNodeName("北京通州分拣中心");
            rejectAddCarApplyMQ.setEndNodeCode("39");
            rejectAddCarApplyMQ.setEndNodeName("石景山营业部");
            rejectAddCarApplyMQ.setBusinessSource(170);
            rejectAddCarApplyMQ.setReasonType("100");
            rejectAddCarApplyMQ.setReasonTypeName("作废");
            rejectAddCarApplyMQ.setOperateTime(new Date());
            rejectAddCarApplyMQ.setCreateUserCode("wuyoude");
            rejectAddCarApplyMQ.setPlanDepartTime(new Date());
            List<String> proveUrlList = Lists.newArrayList();
            proveUrlList.add("http://test.storage.jd.com/dms-feedback/13c72d01-7e8a-4e11-8941-c3fef5e77ad5.jpg?Expires=1946465841&AccessKey=a7ogJNbj3Ee9YM1O&Signature=8Z0AG4t%2F5cx3YhpzR%2B661oLj2Ko%3D");
            proveUrlList.add("http://test.storage.jd.com/dms-feedback/13c72d01-7e8a-4e11-8941-c3fef5e77ad5.jpg?Expires=1946465841&AccessKey=a7ogJNbj3Ee9YM1O&Signature=8Z0AG4t%2F5cx3YhpzR%2B661oLj2Ko%3D");
            rejectAddCarApplyMQ.setPhotoUrlList(proveUrlList);
            message.setText(JsonHelper.toJson(rejectAddCarApplyMQ));
            consumer.consume(message);
            Assert.assertTrue(true);
        }catch (Throwable e){
            logger.error("运输驳回mq处理异常!", e);
            Assert.fail();
        }
    }
}
