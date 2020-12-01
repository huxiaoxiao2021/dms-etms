package com.jd.bluedragon.distribution.send.service;
import java.util.Date;

import com.google.common.collect.Lists;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;
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
    public void packageSendByWaybillTest() {
        String boxCode = "JDVA00119929460-1-200-";
        String sendCode = "910-39-20200916155103192";
        SendM sendM = initSendM(sendCode, boxCode);
        SendResult sendResult = deliveryService.packageSendByWaybill(sendM);
        System.out.println(JsonHelper.toJson(sendResult));
    }

    @Test
    public void doWaybillSendDeliveryTest() {
        String boxCode = "JDVA00119929280-1-1-";
        String sendCode = "910-39-20200916155103192";
        SendM sendM = initSendM(sendCode, boxCode);
        Task task = new Task();
        task.setBoxCode(boxCode);
        task.setReceiveSiteCode(39);
        task.setExecuteCount(0);
        task.setExecuteTime(new Date());
        task.setType(0);
        task.setKeyword1("1");
        task.setKeyword2("");
        task.setBody(JsonHelper.toJson(sendM));
        task.setCreateSiteCode(910);
        task.setTableName("");
        task.setFingerprint("");
        task.setParsedObject(new Object());
        task.setSequenceName("");
        task.setOwnSign("");
        task.setBusinessType(0);
        task.setOperateType(0);
        task.setOperateTime(new Date());
        task.setQueueId(0);
        task.setSubType(0);

        deliveryService.doWaybillSendDelivery(task);
    }

    private SendM initSendM(String sendCode,String boxCode) {
        SendM sendM = new SendM();
        sendM.setSendMId(0L);
        sendM.setSendCode(sendCode);
        sendM.setThirdWaybillCode("");
        sendM.setSendUser("bjxings");
        sendM.setSendUserCode(0);
        sendM.setCreateSiteCode(910);
        sendM.setReceiveSiteCode(39);
        sendM.setCarCode("");
        sendM.setSendType(0);
        sendM.setCreateUser("bjxings");
        sendM.setCreateUserCode(0);
        sendM.setUpdateUserCode(0);
        sendM.setUpdaterUser("");
        sendM.setUpdateTime(new Date());
        sendM.setYn(1);
        sendM.setShieldsCarId(0L);
        sendM.setSendmStatus(0);
        sendM.setBoxCode(boxCode);
        sendM.setExcuteCount(0);
        sendM.setExcuteTime(new Date());
        sendM.setOperateTime(new Date());
        sendM.setCreateTime(new Date());
        sendM.setTurnoverBoxCode("");
        sendM.setTransporttype(0);
        sendM.setBoardCode("");
        sendM.setBizSource(SendBizSourceEnum.WAYBILL_SEND.getCode());

        return sendM;
    }
}
