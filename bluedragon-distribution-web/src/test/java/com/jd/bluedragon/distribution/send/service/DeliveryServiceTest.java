package com.jd.bluedragon.distribution.send.service;

import com.google.common.collect.Lists;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by xumigen on 2019/4/12.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( {"classpath:distribution-web-context-test.xml"})
public class DeliveryServiceTest {

    @Autowired
    private DeliveryServiceImpl deliveryService;

    @Test
    public void testdeliveryService(){
        List<SendDetail> sdList = Lists.newArrayList();
        SendDetail sendDetail1 = new SendDetail();
        sendDetail1.setCreateSiteCode(1);
        sendDetail1.setReceiveSiteCode(1);
        sendDetail1.setBoxCode("121");

        SendDetail sendDetail2 = new SendDetail();
        sendDetail2.setCreateSiteCode(1);
        sendDetail2.setReceiveSiteCode(1);
        sendDetail2.setBoxCode("121");
        sdList.add(sendDetail1);
        sdList.add(sendDetail2);
        deliveryService.saveOrUpdateBatch(sdList);
        sendDetail1.setReceiveSiteCode(null);
        deliveryService.saveOrUpdateBatch(sdList);
        sendDetail1.setBoxCode(null);
        deliveryService.saveOrUpdateBatch(sdList);
    }

    @Test
    public void testbatchQuerySendMList(){
        List<SendM> sdList = Lists.newArrayList();
        SendM sendDetail1 = new SendM();
        sendDetail1.setCreateSiteCode(1);
        sendDetail1.setReceiveSiteCode(1);
        sendDetail1.setBoxCode("121");

        SendM sendDetail2 = new SendM();
        sendDetail2.setCreateSiteCode(1);
        sendDetail2.setReceiveSiteCode(1);
        sendDetail2.setBoxCode("121");
        sdList.add(sendDetail1);
        sdList.add(sendDetail2);
//        deliveryService.batchQuerySendMList(sdList);
    }

    @Test
    public void testcancelStatusReceipt(){
        List<SendM> sdList = Lists.newArrayList();
        SendM sendDetail1 = new SendM();
        sendDetail1.setCreateSiteCode(1);
        sendDetail1.setReceiveSiteCode(1);
        sendDetail1.setBoxCode("121");

        SendM sendDetail2 = new SendM();
        sendDetail2.setCreateSiteCode(1);
        sendDetail2.setReceiveSiteCode(1);
        sendDetail2.setBoxCode("121");
        sdList.add(sendDetail1);
        sdList.add(sendDetail2);
        List<String> list = Lists.newArrayList();
//        deliveryService.cancelStatusReceipt(sdList,list);
    }
}
