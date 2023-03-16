package com.jd.bluedragon.distribution.consumer.jy.exception;

import com.google.common.collect.Maps;
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionDao;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;
import com.jd.bluedragon.distribution.jy.exception.JyExScrapNoticeMQ;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionAgg;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.utils.DateHelper;
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
import java.util.Map;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2023/3/13 5:41 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class DmsExScrapNoticeConsumerTest {

    private static final Logger logger = LoggerFactory.getLogger(DmsExScrapNoticeConsumerTest.class);
    
    @Autowired
    private DmsExScrapNoticeConsumer dmsExScrapNoticeConsumer;
    
    @Autowired
    private JyExceptionService jyExceptionService;

    @Autowired
    private JyBizTaskExceptionDao jyBizTaskExceptionDao;
    
    @Test
    public void consume() {
        try {

            
            String handlerErp = "wuyoude";
            Date queryStartTime = DateHelper.parseDateTime("2022-08-01 00:00:00");
            Date queryEndTime = DateHelper.parseDateTime("2022-09-01 00:00:00");
            List<JyBizTaskExceptionEntity> list = jyExceptionService.queryScrapDetailByCondition(handlerErp, queryStartTime, queryEndTime);
            Assert.assertTrue(true);

            List<String> handlerErpList = jyExceptionService.queryScrapHandlerErp(queryStartTime, queryEndTime);
            Assert.assertTrue(true);
            
            JyBizTaskExceptionEntity entity = new JyBizTaskExceptionEntity();
            entity.setSiteCode(910L);
            entity.setProcessBeginTime(DateHelper.parseDateTime("2022-08-01 00:00:00"));
            Integer count = jyBizTaskExceptionDao.queryScrapCountByCondition(entity);
            Assert.assertTrue(true);

            Map<String, Object> params = Maps.newHashMap();
            params.put("queryStartTime", queryStartTime);
            params.put("queryEndTime", queryEndTime);
            List<JyExceptionAgg> jyExceptionAggs = jyBizTaskExceptionDao.queryUnCollectAndOverTimeAgg(params);
            Assert.assertTrue(true);
            
            long time = DateHelper.getFirstDateOfMonth().getTime();
            Message message = new Message();
            JyExScrapNoticeMQ jyExScrapNoticeMQ = new JyExScrapNoticeMQ();
            jyExScrapNoticeMQ.setHandlerErp("bjxings");
            jyExScrapNoticeMQ.setQueryStartTime(new Date(1678701600000L));
            jyExScrapNoticeMQ.setQueryEndTime(new Date(1678636800000L));
            message.setText(JsonHelper.toJson(jyExScrapNoticeMQ));
            dmsExScrapNoticeConsumer.consume(message);
        }catch (Exception e){
            logger.error("服务异常!", e);
            Assert.fail();
        }
    }
}
