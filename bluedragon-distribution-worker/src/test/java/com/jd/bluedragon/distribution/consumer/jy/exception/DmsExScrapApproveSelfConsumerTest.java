package com.jd.bluedragon.distribution.consumer.jy.exception;

import com.jd.bluedragon.common.dto.jyexpection.request.ExpScrappedDetailReq;
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
 * @date 2023/3/21 8:00 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class DmsExScrapApproveSelfConsumerTest {

    private static final Logger logger = LoggerFactory.getLogger(DmsExScrapApproveSelfConsumerTest.class);
    
    @Autowired
    private DmsExScrapApproveSelfConsumer dmsExScrapApproveSelfConsumer;
    
    @Test
    public void consume() {
        try {
            Message message = new Message();
            ExpScrappedDetailReq req = new ExpScrappedDetailReq();
            req.setBizId("SCRAPPED_JDVA19408919512");
            req.setGoodsImageUrl("http://storage.jd.local/volumepicture/JDVA15097578817-1-1-_695114_20220724235616.jpg?Expires=3806162170&AccessKey=6KBoeA1WKY6qcw10&Signature=bKn98ICaVsgqR67tRAh2Gxofulo%3D,http://storage.jd.local/volumepicture/JDVA15097578817-1-1-_695114_20220724235616.jpg?Expires=3806162170&AccessKey=6KBoeA1WKY6qcw10&Signature=bKn98ICaVsgqR67tRAh2Gxofulo%3D,http://storage.jd.local/volumepicture/JDVA15097578817-1-1-_695114_20220724235616.jpg?Expires=3806162170&AccessKey=6KBoeA1WKY6qcw10&Signature=bKn98ICaVsgqR67tRAh2Gxofulo%3D");
            req.setCertifyImageUrl("http://storage.jd.local/volumepicture/JDVA15097578817-1-1-_695114_20220724235616.jpg?Expires=3806162170&AccessKey=6KBoeA1WKY6qcw10&Signature=bKn98ICaVsgqR67tRAh2Gxofulo%3D,http://storage.jd.local/volumepicture/JDVA15097578817-1-1-_695114_20220724235616.jpg?Expires=3806162170&AccessKey=6KBoeA1WKY6qcw10&Signature=bKn98ICaVsgqR67tRAh2Gxofulo%3D,http://storage.jd.local/volumepicture/JDVA15097578817-1-1-_695114_20220724235616.jpg?Expires=3806162170&AccessKey=6KBoeA1WKY6qcw10&Signature=bKn98ICaVsgqR67tRAh2Gxofulo%3D");
            message.setText(JsonHelper.toJson(req));
            dmsExScrapApproveSelfConsumer.consume(message);
        }catch (Exception e){
            logger.error("服务异常!", e);
            Assert.fail();
        }
    }
}