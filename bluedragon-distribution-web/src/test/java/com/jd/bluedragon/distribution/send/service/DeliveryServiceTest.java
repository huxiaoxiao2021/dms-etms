package com.jd.bluedragon.distribution.send.service;
import java.lang.reflect.Method;
import java.util.Date;

import com.google.common.collect.Lists;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jim.cli.Cluster;
import com.jd.transboard.api.service.GroupBoardService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    @Qualifier("redisClientCache")
    private Cluster redisClientCache;

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

    @Test
    public void testUpdateScanActionByBatchCode() {
        SendM sendM = new SendM();
        sendM.setSendCode("910-364605-20201126223512345");
        sendM.setCreateSiteCode(910);
        sendM.setUpdaterUser("管理员");
        sendM.setUpdateUserCode(100001);
        deliveryService.updateScanActionByBatchCode(sendM);
    }

    @Test
    public void packageSendByWaybillTest() {
        String boxCode = "JDVA00119929460-1-200-";
        String sendCode = "910-39-20200916155103192";
        SendM sendM = initSendM(sendCode, boxCode);
        SendResult sendResult = deliveryService.packageSendByWaybill(sendM, false, false);
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
    @Test
    public void doWaybillSendDeliveryTest02() {
        String taskJson = "\t{\n" +
                "\t\t\"taskId\" : 152422,\n" +
                "\t\t\"createTime\" : \"2021-03-01 14:46:48\",\n" +
                "\t\t\"updateTime\" : \"2021-03-01 14:46:48\",\n" +
                "\t\t\"keyword1\" : \"1\",\n" +
                "\t\t\"keyword2\" : \"10\",\n" +
                "\t\t\"createSiteCode\" : 910,\n" +
                "\t\t\"receiveSiteCode\" : 39,\n" +
                "\t\t\"boxCode\" : \"JD0003358121191\",\n" +
                "\t\t\"body\" : \"{\\r\\n  \\\"sendMId\\\" : 1366275560185106432,\\r\\n  \\\"sendCode\\\" : \\\"910-39-20210301143251786\\\",\\r\\n  \\\"boxCode\\\" : \\\"JD0003358121191\\\",\\r\\n  \\\"turnoverBoxCode\\\" : \\\"\\\",\\r\\n  \\\"createSiteCode\\\" : 910,\\r\\n  \\\"receiveSiteCode\\\" : 39,\\r\\n  \\\"sendType\\\" : 10,\\r\\n  \\\"createUser\\\" : \\\"吴有德\\\",\\r\\n  \\\"createUserCode\\\" : 17331,\\r\\n  \\\"operateTime\\\" : 1614580452712,\\r\\n  \\\"createTime\\\" : 1614580452712,\\r\\n  \\\"yn\\\" : 1,\\r\\n  \\\"transporttype\\\" : 0,\\r\\n  \\\"bizSource\\\" : 2,\\r\\n  \\\"handleCategory\\\" : 4\\r\\n}\",\n" +
                "\t\t\"executeCount\" : 1,\n" +
                "\t\t\"taskType\" : 1300,\n" +
                "\t\t\"taskStatus\" : 2,\n" +
                "\t\t\"yn\" : 1,\n" +
                "\t\t\"ownSign\" : \"DMS\",\n" +
                "\t\t\"fingerprint\" : \"C15748B91942ABDB809EB0493CCB7187\",\n" +
                "\t\t\"executeTime\" : \"2021-03-01 15:01:53\",\n" +
                "\t\t\"queueId\" : 0\n" +
                "\t}";

        Task task = JsonHelper.fromJson(taskJson, Task.class);
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

    @Test
    public void cacheTest() {
        String key = "sendByWaybill-bitmap001";
        Boolean set = redisClientCache.set(key, "", 2, TimeUnit.HOURS, false);
        System.out.println("设置Key完成:set=" + set);

        Long bitCount = redisClientCache.bitCount(key);
        System.out.println("空字符bitCount=" + bitCount);

        Boolean setBit = redisClientCache.setBit(key, 1, true);
        System.out.println("第一次设置结果setBit=" + setBit);

        bitCount = redisClientCache.bitCount(key);
        System.out.println("第一次设置后bitCount=" + bitCount);

        setBit = redisClientCache.setBit(key, 1, true);
        System.out.println("第二次设置结果setBit=" + setBit);

    }
}
