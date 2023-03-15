package com.jd.bluedragon.distribution.jy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.*;
import com.jd.bluedragon.common.dto.jyexpection.response.DmsBarCode;
import com.jd.bluedragon.common.dto.jyexpection.response.ExpTaskDto;
import com.jd.bluedragon.common.dto.jyexpection.response.StatisticsByGridDto;
import com.jd.bluedragon.distribution.external.service.DmsTimingHandlerService;
import com.jd.bluedragon.common.dto.jyexpection.response.StatisticsByStatusDto;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
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


    @Test
    public void uploadScanTest() {
        ExpUploadScanReq req = new ExpUploadScanReq();

        req.setUserErp("wuyoude");
        req.setSiteId(910);
        //req.setBarCode("sw000001");
        //req.setBarCode("SW1111112223");
        req.setBarCode("JDVA19408919504");
        req.setSource(0);
        req.setPositionCode("GW00003001");
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
        req.setStatus(1);
        req.setPositionCode("GW00003001");
        req.setUserErp("wuyoude");
        JdCResponse<List<ExpTaskDto>> response = jyExceptionService.getExceptionTaskPageList(req);
        System.out.println(JSON.toJSONString(response));
    }


    @Test
    public void receiveTest() {

        ExpReceiveReq req = new ExpReceiveReq();
        req.setUserErp("wuyoude");
        req.setBarCode("sw000001");
        req.setPositionCode("GW00003001");
        jyExceptionService.receive(req);
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
    public void timingTaskHandlerTest() {
        dmsTimingHandlerService.timingHandlerFreshScrapNotice();
        Assert.assertTrue(true);
        dmsTimingHandlerService.timingHandlerOverTimeException();
        Assert.assertTrue(true);
    }

}
