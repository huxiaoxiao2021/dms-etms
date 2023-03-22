package com.jd.bluedragon.distribution.jy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.*;
import com.jd.bluedragon.common.dto.jyexpection.response.*;
import com.jd.bluedragon.distribution.external.service.DmsTimingHandlerService;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.impl.JyScrappedExceptionServiceImpl;
import com.jd.bluedragon.external.gateway.service.JyExceptionGatewayService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:distribution-web-context.xml"})
public class JyExceptionServiceTest {
    @Autowired
    JyExceptionService jyExceptionService;



    @Autowired
    private DmsTimingHandlerService dmsTimingHandlerService;

    @Autowired
    private JyScrappedExceptionServiceImpl jyScrappedExceptionService;

    @Test
    public void uploadScanTest() {
        ExpUploadScanReq req = new ExpUploadScanReq();

        req.setUserErp("wuyoude");
        req.setSiteId(910);
        req.setSource(0);
        req.setPositionCode("GW00003001");

//        req.setBarCode("SW1111112225");
//        req.setType(0);


        req.setBarCode("JDVA00255154794");
        req.setType(1);

        JdCResponse<Object> response = jyExceptionService.uploadScan(req);
        System.out.println(JSON.toJSONString(response));
    }

    @Test
    public void statisticsByStatusTest(){

        ExpBaseReq req = new ExpBaseReq();
        req.setUserErp("wuyoude");
        req.setSiteId(910);
        req.setPositionCode("GW00003001");
        JdCResponse<List<StatisticsByStatusDto>> response = jyExceptionService.statisticsByStatus(req);

        System.out.println(JSON.toJSONString(response));


    }

    @Test
    public void getGridStatisticsPageList() {
        StatisticsByGridReq req = new StatisticsByGridReq();
        req.setUserErp("wuyoude");
        req.setPositionCode("GW00003001");
        JdCResponse<List<StatisticsByGridDto>> response = jyExceptionService.getGridStatisticsPageList(req);
        System.out.println(JSON.toJSONString(response));
    }


    @Test
    public void getExceptionTaskPageList() {

        ExpTaskPageReq req = new ExpTaskPageReq();
        req.setStatus(3);
        req.setPositionCode("GW00003001");
        req.setUserErp("wuyoude");
        JdCResponse<List<ExpTaskDto>> response = jyExceptionService.getExceptionTaskPageList(req);
        System.out.println(JSON.toJSONString(response));
    }


    @Test
    public void receiveTest() {

        ExpReceiveReq req = new ExpReceiveReq();
        req.setUserErp("wuyoude");
        req.setBarCode("SANWU_111111");
        req.setPositionCode("GW00003001");
        //req.setType(0);
        JdCResponse<Object> receive = jyExceptionService.receive(req);
        System.out.println(JSON.toJSONString(receive));
    }

    @Test
    public void processTaskTest() {

        ExpTaskDetailReq req = new ExpTaskDetailReq();
        req.setUserErp("wuyoude");
        req.setPositionCode("GW00003001");
        req.setBatchNo("xxx");
        req.setFrom("364605");
        req.setTo("364605,38");
        req.setBizId("SANWU_sw000001");
        req.setGoodsNo("1234567890");
        req.setGoodsNum("1");
        req.setInnerDesc("inner");
        req.setOuterDesc("1");
        req.setPrice("11.11");
        req.setSaveType("1");
        req.setWeight("11.11");

        JdCResponse<Object> objectJdCResponse = jyExceptionService.processTask(req);
        System.out.println(JSONObject.toJSON(objectJdCResponse));
    }


    @Test
    public void queryProductNameTest() {
        //JdCResponse<List<DmsBarCode>> listJdCResponse = jyExceptionGatewayService.queryProductName("a,aa,4");
        //System.out.println(JSONObject.toJSON(listJdCResponse));
    }

    @Test
    public void getJyExceptionScrappedTypeListTest(){
        JdCResponse<List<JyExceptionScrappedTypeDto>> list = jyScrappedExceptionService.getJyExceptionScrappedTypeList();
        Assert.assertEquals(list.isSucceed(),true);
    }

    @Test
    public void processTaskOfscrappedTest(){
        ExpScrappedDetailReq req = new ExpScrappedDetailReq();
        req.setUserErp("wuyoude");
        req.setPositionCode("GW00003001");
        req.setBizId("SANWU_sw000001");
        req.setSaveType(1);

        JdCResponse<Boolean> response = jyScrappedExceptionService.processTaskOfscrapped(req);
        Assert.assertEquals(response.isSucceed(),true);
    }

    @Test
    public void getTaskDetailOfscrappedTest(){
        ExpTaskByIdReq req=new ExpTaskByIdReq();
        req.setBizId("SANWU_sw000001");
        JdCResponse<ExpScrappedDetailDto> response = jyScrappedExceptionService.getTaskDetailOfscrapped(req);
        Assert.assertEquals(response.isSucceed(),true);

    }

    @Test
    public void timingTaskHandlerTest() {
        dmsTimingHandlerService.timingHandlerFreshScrapNotice();
        Assert.assertTrue(true);
        dmsTimingHandlerService.timingHandlerOverTimeException();
        Assert.assertTrue(true);
    }

}
