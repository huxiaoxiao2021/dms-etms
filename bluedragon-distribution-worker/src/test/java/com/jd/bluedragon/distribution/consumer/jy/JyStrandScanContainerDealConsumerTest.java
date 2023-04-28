package com.jd.bluedragon.distribution.consumer.jy;

import com.jd.bluedragon.distribution.consumer.jy.strand.JyStrandScanContainerDealConsumer;
import com.jd.bluedragon.distribution.jy.dao.strand.JyBizStrandReportDetailDao;
import com.jd.bluedragon.distribution.jy.strand.JyBizStrandReportDetailEntity;
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
 * @date 2023/4/6 2:24 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class JyStrandScanContainerDealConsumerTest {

    private static final Logger logger = LoggerFactory.getLogger(JyStrandScanContainerDealConsumerTest.class);

    @Autowired
    private JyStrandScanContainerDealConsumer consumer;

    @Autowired
    private JyBizStrandReportDetailDao jyBizStrandReportDetailDao;

    @Test
    public void consumer() {
        try {
            Message message = new Message();
            JyBizStrandReportDetailEntity condition = new JyBizStrandReportDetailEntity();
            condition.setBizId("");
            JyBizStrandReportDetailEntity jyBizStrandReportDetail = jyBizStrandReportDetailDao.queryOneByCondition(condition);
            message.setText(JsonHelper.toJson(jyBizStrandReportDetail));
            consumer.consume(message);
            Assert.assertTrue(true);
        }catch (Throwable e){
            logger.error("滞留容器内扫描件处理mq处理异常!", e);
            Assert.fail();
        }
    }
}
