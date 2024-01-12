package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.basedata.response.StreamlinedBasicSite;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodTaskTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.SendFlowDisplayEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.*;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.*;
import com.jd.bluedragon.distribution.external.service.DmsTimingHandlerService;
import com.jd.bluedragon.distribution.jy.enums.JyFuncCodeEnum;
import com.jd.bluedragon.external.gateway.service.JyAviationRailwayPickingGoodsGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class JyAviationRailwayPickingGoodsGatewayServiceTest {

    private static final CurrentOperate SITE_910 = new CurrentOperate(910,"马驹桥分拣中心",new Date());
    public static final CurrentOperate SITE_40240 = new CurrentOperate(40240, "北京通州分拣中心", new Date());

    public static final User USER_wuyoude = new User(65396,"吴有德");

    public static final String GROUP_CODE = "G00000130001";
    public static final String POST = JyFuncCodeEnum.AVIATION_RAILWAY_SEND_SEAL_POSITION.getCode();

    static {
        USER_wuyoude.setUserErp("wuyoude");
    }

    private void setBaseReq(BaseReq req) {
        req.setUser(USER_wuyoude);
        req.setCurrentOperate(SITE_40240);
        req.setGroupCode(GROUP_CODE);
        req.setPost(POST);
    }

    @Autowired
    private JyAviationRailwayPickingGoodsGatewayService jyAviationRailwayPickingGoodsGatewayService;

    @Autowired
    private DmsTimingHandlerService dmsTimingHandlerService;
    @Test
    public void finishPickGoodsTest() {
        FinishPickGoodsReq req = new FinishPickGoodsReq();
        setBaseReq(req);
        req.setBizId("test_biz_id");
        JdCResponse<Void> response = jyAviationRailwayPickingGoodsGatewayService.finishPickGoods(req);
        log.info("finishPickGoodsTest response {}", JsonHelper.toJson(response));
    }

    @Test
    public void submitExceptionTest() {
        ExceptionSubmitReq req = new ExceptionSubmitReq();
        setBaseReq(req);
        req.setBizId("test_biz_id");
        JdCResponse<Void> response = jyAviationRailwayPickingGoodsGatewayService.submitException(req);
        log.info("submitExceptionTest response {}", JsonHelper.toJson(response));
    }

    @Test
    public void listSendFlowInfoTest() {
        SendFlowReq req = new SendFlowReq();
        setBaseReq(req);
        req.setDisplayType(SendFlowDisplayEnum.DEFAULT.getCode());
        JdCResponse<SendFlowRes> response1 = jyAviationRailwayPickingGoodsGatewayService.listSendFlowInfo(req);
        log.info("listSendFlowInfoTest response1 {}", JsonHelper.toJson(response1));

        req.setDisplayType(SendFlowDisplayEnum.COUNT.getCode());
        JdCResponse<SendFlowRes> response2 = jyAviationRailwayPickingGoodsGatewayService.listSendFlowInfo(req);
        log.info("listSendFlowInfoTest response2 {}", JsonHelper.toJson(response2));
    }

    @Test
    public void addSendFlowTest() {
        SendFlowAddReq req = new SendFlowAddReq();
        setBaseReq(req);
        List<StreamlinedBasicSite> siteList;
        StreamlinedBasicSite site = new StreamlinedBasicSite();
        site.setSiteCode(40240);
        site.setSiteName("北京通州分拣中心");
        siteList = Arrays.asList(site);
        req.setSiteList(siteList);
        JdCResponse<Void> response = jyAviationRailwayPickingGoodsGatewayService.addSendFlow(req);
        log.info("addSendFlowTest response {}", JsonHelper.toJson(response));
    }

    @Test
    public void deleteSendFlowTest() {
        SendFlowDeleteReq req = new SendFlowDeleteReq();
        setBaseReq(req);
        req.setTaskType(PickingGoodTaskTypeEnum.AVIATION.getCode());
        req.setNextSiteId(40240);
        JdCResponse<Void> response = jyAviationRailwayPickingGoodsGatewayService.deleteSendFlow(req);
        log.info("deleteSendFlowTest response {}", JsonHelper.toJson(response));
    }

    @Test
    public void finishSendTaskTest() {
        FinishSendTaskReq req = new FinishSendTaskReq();
        setBaseReq(req);
        req.setSendCode("send_code_01");
        req.setNextSiteId(40240);
        req.setTaskType(PickingGoodTaskTypeEnum.AVIATION.getCode());
        jyAviationRailwayPickingGoodsGatewayService.finishSendTask(req);
    }

    @Test
    public void listAirRailTaskSummaryTest() {
        while (true) {
            try{

                AirRailTaskSummaryReq req = new AirRailTaskSummaryReq();
                setBaseReq(req);
                req.setStatus(PickingGoodStatusEnum.TO_PICKING.getCode());
                req.setTaskType(PickingGoodTaskTypeEnum.AVIATION.getCode());
                req.setPageNum(1);
                req.setPageSize(10);
                JdCResponse<AirRailTaskRes> response = jyAviationRailwayPickingGoodsGatewayService.listAirRailTaskSummary(req);
                log.info("listAirRailTaskSummaryTest response {}", JsonHelper.toJson(response));
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Test
    public void listAirRailTaskAggTest() {
        AirRailTaskAggReq req = new AirRailTaskAggReq();
        setBaseReq(req);
        req.setStatus(PickingGoodStatusEnum.TO_PICKING.getCode());
        req.setTaskType(PickingGoodTaskTypeEnum.AVIATION.getCode());
//        req.setPickingNodeCode("bjtz");
        req.setPageNum(1);
        req.setPageSize(10);
        JdCResponse<AirRailTaskAggRes> response = jyAviationRailwayPickingGoodsGatewayService.listAirRailTaskAgg(req);
        log.info("listAirRailTaskSummaryTest response {}", JsonHelper.toJson(response));
    }

    @Test
    public void timingHandlerFinishAirRailTask() {
        dmsTimingHandlerService.timingHandlerFinishAirRailTask();
    }

    @Test
    public void timingHandlerFinishAirRailManualTask() {
        dmsTimingHandlerService.timingHandlerFinishAirRailManualTask();
    }



    @Test
    public void pickingGoodsScanTest(){

//        pickingGoodTest();
        pickingAndSendGoodTest();
    }

    private void pickingGoodTest() {
        while (true) {

            //首次提货
            PickingGoodsReq request = new PickingGoodsReq();
            this.setBaseReq(request);
//            request.setBarCode("JD0003423499306-5-50-");
            request.setBarCode("BC1001210816140000000505");
            request.setTaskType(PickingGoodTaskTypeEnum.AVIATION.getCode());
            request.setLastScanTaskBizId(null);
            request.setSendGoodFlag(false);
            request.setForceSendFlag(false);
            request.setNextSiteId(null);
            request.setNextSiteName(null);
            request.setBoxConfirmNextSiteKey(null);
            request.setSendNextSiteSwitch(false);
            request.setBeforeSwitchNextSiteId(null);
            request.setBeforeSwitchNextSiteName(null);
            JdCResponse<PickingGoodsRes> res = jyAviationRailwayPickingGoodsGatewayService.pickingGoodsScan(request);
            System.out.println("end");

            if (res.isSucceed()) {
                //非第一次提货、走上一次bizId
                PickingGoodsReq request2 = new PickingGoodsReq();
                this.setBaseReq(request2);
                request2.setBarCode("JD0003423499306-2-50-");
                request2.setTaskType(PickingGoodTaskTypeEnum.AVIATION.getCode());
                request2.setLastScanTaskBizId(res.getData().getAirRailTaskAggDto().getBizId());
                request2.setSendGoodFlag(false);
                request2.setForceSendFlag(false);
                request2.setNextSiteId(null);
                request2.setNextSiteName(null);
                request2.setBoxConfirmNextSiteKey(null);
                request2.setSendNextSiteSwitch(false);
                request2.setBeforeSwitchNextSiteId(null);
                request2.setBeforeSwitchNextSiteName(null);
                JdCResponse<PickingGoodsRes> res2 = jyAviationRailwayPickingGoodsGatewayService.pickingGoodsScan(request2);
                System.out.println("end");

                //非第一次提货、走已存在自建任务
                PickingGoodsReq request3 = new PickingGoodsReq();
                this.setBaseReq(request3);
                request3.setBarCode("JD0003423499306-3-50-");
                request3.setTaskType(PickingGoodTaskTypeEnum.AVIATION.getCode());
                request3.setLastScanTaskBizId(res.getData().getAirRailTaskAggDto().getBizId());
                request3.setSendGoodFlag(false);
                request3.setForceSendFlag(false);
                request3.setNextSiteId(null);
                request3.setNextSiteName(null);
                request3.setBoxConfirmNextSiteKey(null);
                request3.setSendNextSiteSwitch(false);
                request3.setBeforeSwitchNextSiteId(null);
                request3.setBeforeSwitchNextSiteName(null);
                JdCResponse<PickingGoodsRes> res3 = jyAviationRailwayPickingGoodsGatewayService.pickingGoodsScan(request3);
                System.out.println("end");

            }
        }
    }

    private void pickingAndSendGoodTest() {
        while (true) {
            String barCode = "BC1001210816140000000505";

            Long nextSiteId = 910l;
            String nextSiteName = "马驹桥分拣中心";
            //首次提发货
            PickingGoodsReq request = new PickingGoodsReq();
            this.setBaseReq(request);
            request.setBarCode(barCode);
            request.setTaskType(PickingGoodTaskTypeEnum.AVIATION.getCode());
            request.setLastScanTaskBizId(null);
            request.setSendGoodFlag(true);
            request.setForceSendFlag(false);
            request.setNextSiteId(nextSiteId);
            request.setNextSiteName(nextSiteName);
            request.setBoxConfirmNextSiteKey(null);
            request.setSendNextSiteSwitch(false);
            request.setBeforeSwitchNextSiteId(null);
            request.setBeforeSwitchNextSiteName(null);
            JdCResponse<PickingGoodsRes> res = jyAviationRailwayPickingGoodsGatewayService.pickingGoodsScan(request);
            System.out.println("end");

            if (PickingGoodsRes.CODE_30001.equals(res.getCode())) {
                //强发同流向场景
                PickingGoodsReq request2 = new PickingGoodsReq();
                this.setBaseReq(request2);
                request2.setBarCode("");
                request2.setTaskType(PickingGoodTaskTypeEnum.AVIATION.getCode());
                request2.setLastScanTaskBizId(null);
                request2.setSendGoodFlag(true);
                request2.setForceSendFlag(true);
                request2.setNextSiteId(nextSiteId);
                request2.setNextSiteName(nextSiteName);
                request2.setBoxConfirmNextSiteKey(null);
                request2.setSendNextSiteSwitch(false);
                request2.setBeforeSwitchNextSiteId(null);
                request2.setBeforeSwitchNextSiteName(null);
                JdCResponse<PickingGoodsRes> res2 = jyAviationRailwayPickingGoodsGatewayService.pickingGoodsScan(request2);
                System.out.println("end");


                //强发切换流向场景
                PickingGoodsReq request3 = new PickingGoodsReq();
                this.setBaseReq(request3);
                request3.setBarCode("");
                request3.setTaskType(PickingGoodTaskTypeEnum.AVIATION.getCode());
                request3.setLastScanTaskBizId(null);
                request3.setSendGoodFlag(true);
                request3.setForceSendFlag(true);
                request3.setNextSiteId(10186l);
                request3.setNextSiteName("凉水河测试");
                request3.setBoxConfirmNextSiteKey(null);
                request3.setSendNextSiteSwitch(true);
                request3.setBeforeSwitchNextSiteId(nextSiteId);
                request3.setBeforeSwitchNextSiteName(nextSiteName);
                JdCResponse<PickingGoodsRes> res3 = jyAviationRailwayPickingGoodsGatewayService.pickingGoodsScan(request3);
                System.out.println("end");
            }


            if (res.isSucceed()) {
                //非第一次提发货
                PickingGoodsReq request4 = new PickingGoodsReq();
                this.setBaseReq(request4);
                request4.setBarCode("");
                request4.setTaskType(PickingGoodTaskTypeEnum.AVIATION.getCode());
                request4.setLastScanTaskBizId(res.getData().getAirRailTaskAggDto().getBizId());
                request4.setSendGoodFlag(true);
                request4.setForceSendFlag(false);
                request4.setNextSiteId(nextSiteId);
                request4.setNextSiteName(nextSiteName);
                request4.setBoxConfirmNextSiteKey(null);
                request4.setSendNextSiteSwitch(false);
                request4.setBeforeSwitchNextSiteId(null);
                request4.setBeforeSwitchNextSiteName(null);
                JdCResponse<PickingGoodsRes> res4 = jyAviationRailwayPickingGoodsGatewayService.pickingGoodsScan(request4);
                System.out.println("end");
            }
        }
    }
}
