package com.jd.bluedragon.distribution.jy;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.*;
import com.jd.bluedragon.common.dto.jyexpection.response.DmsBarCode;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.external.gateway.service.JyExceptionGatewayService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:bak/distribution-web-context-test.xml"})
public class JyExceptionServiceTest {
    @Autowired
    JyExceptionService jyExceptionService;

    @Autowired
    JyExceptionGatewayService jyExceptionGatewayService;


    @Test
    public void uploadScanTest() {
        ExpUploadScanReq req = new ExpUploadScanReq();

        req.setUserErp("wuyoude");
        req.setBarCode("sw000001");
        req.setSource(0);
        req.setPositionCode("GW00029026");

        jyExceptionService.uploadScan(req);
    }


    @Test
    public void getGridStatisticsPageList() {
        StatisticsByGridReq req = new StatisticsByGridReq();
        req.setUserErp("bjxings");
        req.setPositionCode("GW00007007");
        jyExceptionService.getGridStatisticsPageList(req);
    }


    @Test
    public void getExceptionTaskPageList() {

        ExpTaskPageReq req = new ExpTaskPageReq();
        req.setFloor(2);
        req.setStatus(0);
        req.setGridCode("CLLQ-07");
        req.setPositionCode("GW00003001");
        req.setUserErp("userErp");
        req.setOffSet(0);
        jyExceptionService.getExceptionTaskPageList(req);
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
        JdCResponse<List<DmsBarCode>> listJdCResponse = jyExceptionGatewayService.queryProductName("a,aa,4");
        System.out.println(JSONObject.toJSON(listJdCResponse));
    }

}
