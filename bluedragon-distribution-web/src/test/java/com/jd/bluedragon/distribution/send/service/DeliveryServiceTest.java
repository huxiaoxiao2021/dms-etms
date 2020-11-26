package com.jd.bluedragon.distribution.send.service;

import com.google.common.collect.Lists;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.transboard.api.service.GroupBoardService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xumigen on 2019/4/12.
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( {"classpath:distribution-web-context.xml"})
public class DeliveryServiceTest {

    @Autowired
    private DeliveryServiceImpl deliveryService;

    @Autowired
    private GroupBoardService groupBoardService;

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

    @Test
    public void changeBoardStatusTest(){
        SendM sendM = new SendM();
        sendM.setUpdaterUser("jubingtao");
        sendM.setCreateSiteCode(100);
        List<String> list = new ArrayList<>();
        list.add("B19103000000034");
        deliveryService.changeBoardStatus(sendM,list);
        Assert.assertEquals(new Integer(1),groupBoardService.getBoardByCode("B19103000000034").getData().getStatus());
    }

    @Test
    public void testUpdateScanActionByPackageCodes() {
        List<SendDetail> sendDetails = new ArrayList<>();
        SendDetail sendDetail1 = new SendDetail();
        sendDetail1.setPackageBarcode("JDV000488250208-1-5-");
        sendDetail1.setCreateSiteCode(910);
        sendDetails.add(sendDetail1);
        SendDetail sendDetail2 = new SendDetail();
        sendDetail2.setPackageBarcode("JDV000488250208-2-5-");
        sendDetail2.setCreateSiteCode(910);
        sendDetails.add(sendDetail2);

        SendM sendM = new SendM();
        sendM.setCreateSiteCode(910);
        sendM.setUpdaterUser("管理员");
        sendM.setUpdateUserCode(100001);
        deliveryService.updateScanActionByPackageCodes(sendDetails, sendM);
    }

    @Test
    public void testUpdateScanActionByBoardCode() {
        SendM sendM = new SendM();
        sendM.setBoardCode("B20102400000016");
        sendM.setCreateSiteCode(910);
        sendM.setUpdaterUser("管理员");
        sendM.setUpdateUserCode(100001);
        deliveryService.updateScanActionByBoardCode(sendM);
    }
}
