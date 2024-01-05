package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.basedata.response.StreamlinedBasicSite;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.SendFlowDisplayEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.*;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.AirRailTaskAggReq;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.SendFlowRes;
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
        req.setCurrentOperate(SITE_910);
        req.setGroupCode(GROUP_CODE);
        req.setPost(POST);
    }

    @Autowired
    private JyAviationRailwayPickingGoodsGatewayService jyAviationRailwayPickingGoodsGatewayService;

    @Test
    public void finishPickGoodsTestTest() {
        FinishPickGoodsReq req;
    }

    @Test
    public void submitExceptionTest() {
        ExceptionSubmitReq req;
    }

    @Test
    public void listSendFlowInfoTest() {
        SendFlowReq req = new SendFlowReq();
        setBaseReq(req);
        req.setDisplayType(SendFlowDisplayEnum.DEFAULT.getCode());
        JdCResponse<SendFlowRes> response = jyAviationRailwayPickingGoodsGatewayService.listSendFlowInfo(req);
        log.info("addSendFlowTest response {}", JsonHelper.toJson(response));
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
        SendFlowDeleteReq req;
    }

    @Test
    public void finishSendTaskTest() {
        FinishSendTaskReq req;
    }

    @Test
    public void listAirRailTaskSummaryTest() {
        AirRailTaskSummaryReq req;
    }

    @Test
    public void listAirRailTaskAggTest() {
        AirRailTaskAggReq req;
    }
}
