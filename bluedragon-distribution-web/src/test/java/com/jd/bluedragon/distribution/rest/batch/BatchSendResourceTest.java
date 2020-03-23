package com.jd.bluedragon.distribution.rest.batch;

import com.jd.bluedragon.distribution.batch.domain.BatchSendRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by xumigen on 2019/4/11.
 */
@ContextConfiguration( {"classpath:distribution-web-context-test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class BatchSendResourceTest {

    @Autowired
    private BatchSendResource batchSendResource;

    @Test
    public void testfindBatchSend(){
//        BatchSendRequest request = new BatchSendRequest();
//        request.setSendCode("12312");
//        request.setCreateSiteCode(0);
//        request.setReceiveCodes("1,2,3");
//        request.setCreateTime(new Date());
//        request.setUpdateTime(new Date());
//
//        batchSendResource.findBatchSend(request);

        BatchSendRequest request2 = new BatchSendRequest();
        request2.setCreateSiteCode(0);

        batchSendResource.findBatchSend(request2);
    }
}
