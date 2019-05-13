package com.jd.bluedragon.distribution.wss.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author : xumigen
 * @date : 2019/5/13
 */
public class DistributionWssServiceTest {

    @Autowired
    private DistributionWssService distributionWssService;

    @Test
    public void testqueryPageSendInfoByBatchCode(){
        distributionWssService.queryPageSendInfoByBatchCode(null,null);
    }


}
