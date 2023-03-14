package com.jd.bluedragon.distribution.consumer.jy.exception;

import com.jd.bluedragon.common.dto.jyexpection.request.ExpScrappedDetailReq;
import com.jd.bluedragon.distribution.jy.exception.JyExScrapNoticeMQ;
import com.jd.bluedragon.distribution.jy.service.exception.JyScrappedExceptionService;
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
    private JyScrappedExceptionService jyScrappedExceptionService;
    
    @Test
    public void consume() {
        try {
            ExpScrappedDetailReq req = new ExpScrappedDetailReq();
            req.setBizId("SANWU_SW0000326");
            req.setGoodsImageUrl("http://storage.jd.local/volumepicture/JDVA15097578817-1-1-_695114_20220724235616.jpg?Expires=3806162170&AccessKey=6KBoeA1WKY6qcw10&Signature=bKn98ICaVsgqR67tRAh2Gxofulo%3D,http://storage.jd.local/volumepicture/JDVA15097578817-1-1-_695114_20220724235616.jpg?Expires=3806162170&AccessKey=6KBoeA1WKY6qcw10&Signature=bKn98ICaVsgqR67tRAh2Gxofulo%3D,http://storage.jd.local/volumepicture/JDVA15097578817-1-1-_695114_20220724235616.jpg?Expires=3806162170&AccessKey=6KBoeA1WKY6qcw10&Signature=bKn98ICaVsgqR67tRAh2Gxofulo%3D");
            req.setCertifyImageUrl("http://storage.jd.local/volumepicture/JDVA15097578817-1-1-_695114_20220724235616.jpg?Expires=3806162170&AccessKey=6KBoeA1WKY6qcw10&Signature=bKn98ICaVsgqR67tRAh2Gxofulo%3D,http://storage.jd.local/volumepicture/JDVA15097578817-1-1-_695114_20220724235616.jpg?Expires=3806162170&AccessKey=6KBoeA1WKY6qcw10&Signature=bKn98ICaVsgqR67tRAh2Gxofulo%3D,http://storage.jd.local/volumepicture/JDVA15097578817-1-1-_695114_20220724235616.jpg?Expires=3806162170&AccessKey=6KBoeA1WKY6qcw10&Signature=bKn98ICaVsgqR67tRAh2Gxofulo%3D");
            jyScrappedExceptionService.dealApproveTest(req);
            
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
