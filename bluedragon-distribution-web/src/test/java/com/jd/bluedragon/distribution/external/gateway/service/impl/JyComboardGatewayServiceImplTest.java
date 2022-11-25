package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.comboard.request.*;
import com.jd.bluedragon.common.dto.comboard.response.*;
import com.jd.bluedragon.external.gateway.service.JyComboardGatewayService;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.avro.data.Json;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liwenji
 * @date 2022-11-16 17:23
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class JyComboardGatewayServiceImplTest {

    @Autowired
    private JyComboardGatewayService jyComboardGatewayService;

    @Test
    public void listCrossDataTest() {
        CrossDataReq crossDataReq = new CrossDataReq();
        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(910);
        crossDataReq.setPageNo(1);
        crossDataReq.setPageSize(4);
        crossDataReq.setCurrentOperate(currentOperate);
        JdCResponse<CrossDataResp> crossDataRespJdCResponse = jyComboardGatewayService.listCrossData(crossDataReq);
        System.out.println(JsonHelper.toJson(crossDataRespJdCResponse));
    }

    @Test
    public void listTableTrolleyUnderCrossTest() {
        TableTrolleyReq query = new TableTrolleyReq();
        query.setPageSize(4);
        query.setPageNo(2);
        query.setCrossCode("2002");
        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(910);
        query.setCurrentOperate(currentOperate);
        JdCResponse<TableTrolleyResp> re = jyComboardGatewayService.listTableTrolleyUnderCross(query);
        System.out.println("根据滑道获取结果：" + JsonHelper.toJson(re));
        TableTrolleyReq tableTrolleyReq = new TableTrolleyReq();
        tableTrolleyReq.setPageSize(100);
        tableTrolleyReq.setPageNo(1);
        CurrentOperate operate = new CurrentOperate();
        operate.setSiteCode(910);
        tableTrolleyReq.setCurrentOperate(operate);
        JdCResponse<TableTrolleyResp> response = jyComboardGatewayService.listTableTrolleyUnderCross(tableTrolleyReq);
        System.out.println("根据场地获取结果：" + JsonHelper.toJson(response));
    }

    @Test
    public void getDefaultGroupCTTNameTest() {
        BaseReq request =new BaseReq();
        request.setGroupCode("group111");
        User user = new User();
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        request.setUser(user);
        CurrentOperate operate = new CurrentOperate();
        operate.setSiteCode(910);
        request.setCurrentOperate(operate);
        JdCResponse<CreateGroupCTTResp> response = jyComboardGatewayService.getDefaultGroupCTTName(request);
        System.out.println(JsonHelper.toJson(response));
    }

    @Test
    public void listCTTGroupDataTest() {
        CTTGroupDataReq request = new CTTGroupDataReq();
        request.setGroupQueryFlag(true);
        request.setGroupCode("group111");
        User user = new User();
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        request.setUser(user);
        CurrentOperate operate = new CurrentOperate();
        operate.setSiteCode(910);
        request.setCurrentOperate(operate);
        JdCResponse<CTTGroupDataResp> response = jyComboardGatewayService.listCTTGroupData(request);
        System.out.println(JsonHelper.toJson(response));
        CTTGroupDataReq request2 = new CTTGroupDataReq();
        request2.setGroupQueryFlag(false);
        CurrentOperate operate2 = new CurrentOperate();
        operate2.setSiteCode(910);
        User user2 = new User();
        user2.setUserName("李文吉");
        user2.setUserErp("liwenji3");
        request2.setUser(user);
        request2.setCurrentOperate(operate2);
        JdCResponse<CTTGroupDataResp> response2 = jyComboardGatewayService.listCTTGroupData(request2);
        System.out.println(JsonHelper.toJson(response2));
    }

    @Test
    public void queryCTTGroupByBarCodeTest() {
        QueryCTTGroupReq resp = new QueryCTTGroupReq();
        CurrentOperate operate = new CurrentOperate();
        resp.setGroupCode("group111");
        operate.setSiteCode(910);
        operate.setSiteName("北京马驹桥分拣中心");
        resp.setCurrentOperate(operate);
        User user = new User();
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        resp.setUser(user);
        resp.setBarCode("2002-106");
        JdCResponse<CTTGroupDataResp> response = jyComboardGatewayService.queryCTTGroupByBarCode(resp);
        System.out.println(JsonHelper.toJson(response));
    }
    @Test
    public void queryCTTGroupByBarCodeWayBillTest() {
        QueryCTTGroupReq resp = new QueryCTTGroupReq();
        CurrentOperate operate = new CurrentOperate();
        resp.setGroupCode("group2");
        operate.setSiteCode(910);
        operate.setSiteName("北京马驹桥分拣中心");
        resp.setCurrentOperate(operate);
        User user = new User();
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        resp.setUser(user);
        resp.setBarCode("JD0003403783896-1-1-");
        JdCResponse<CTTGroupDataResp> response = jyComboardGatewayService.queryCTTGroupByBarCode(resp);
        System.out.println(JsonHelper.toJson(response));
    }

    @Test
    public void queryCTTGroupByBarCodeByBoxTest() {
        QueryCTTGroupReq resp = new QueryCTTGroupReq();
        CurrentOperate operate = new CurrentOperate();
        resp.setGroupCode("group2");
        operate.setSiteCode(910);
        operate.setSiteName("北京马驹桥分拣中心");
        resp.setCurrentOperate(operate);
        User user = new User();
        user.setUserName("魏晓峰");
        user.setUserErp("weixiaofeng12");
        resp.setUser(user);
        resp.setBarCode("BC1001221122210000107925");
        JdCResponse<CTTGroupDataResp> response = jyComboardGatewayService.queryCTTGroupByBarCode(resp);
        System.out.println(JsonHelper.toJson(response));
    }
    @Test
    public void createGroupCTTDataTest() {
        CreateGroupCTTReq resp = new CreateGroupCTTReq();
        CurrentOperate operate = new CurrentOperate();
        resp.setGroupCode("G00000047004");
        operate.setSiteCode(910);
        operate.setSiteName("北京马驹桥分拣中心");
        resp.setCurrentOperate(operate);
        User user = new User();
        user.setUserName("魏晓峰");
        user.setUserErp("weixiaofeng12");
        resp.setUser(user);
        resp.setTemplateName("混扫01");
        List<TableTrolleyDto> dtos = new ArrayList<>();
        resp.setTableTrolleyDtoList(dtos);

        TableTrolleyDto tableTrolleyDto = new TableTrolleyDto();
        tableTrolleyDto.setEndSiteId(39);
        tableTrolleyDto.setCrossCode("2002");
        tableTrolleyDto.setEndSiteName("石景山营业部");
        tableTrolleyDto.setTableTrolleyCode("106");
        dtos.add(tableTrolleyDto);

        /*TableTrolleyDto tableTrolleyDto1 = new TableTrolleyDto();
        tableTrolleyDto1.setEndSiteId(630171);
        tableTrolleyDto1.setCrossCode("2002");
        tableTrolleyDto1.setEndSiteName("京东便民站-测试wyy");
        tableTrolleyDto1.setTableTrolleyCode("11-笼车");
        dtos.add(tableTrolleyDto1);

        TableTrolleyDto tableTrolleyDto2 = new TableTrolleyDto();
        tableTrolleyDto2.setEndSiteId(11398);
        tableTrolleyDto2.setCrossCode("89");
        tableTrolleyDto2.setEndSiteName("1栋15秦希深专用");
        tableTrolleyDto2.setTableTrolleyCode("106");
        dtos.add(tableTrolleyDto2);*/
        JdCResponse<CreateGroupCTTResp> groupCTTData = jyComboardGatewayService.createGroupCTTData(resp);
        System.out.println(JsonHelper.toJson(groupCTTData));
    }

    @Test
    public void querySendFlowByBarCodeTest() {
        QuerySendFlowReq resp = new QuerySendFlowReq();
        CurrentOperate operate = new CurrentOperate();
        resp.setGroupCode("group111");
        operate.setSiteCode(696);
        operate.setSiteName("北京马驹桥分拣中心");
        resp.setCurrentOperate(operate);
        User user = new User();
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        resp.setUser(user);
        resp.setBarCode("102-102");
        JdCResponse<TableTrolleyResp> response = jyComboardGatewayService.querySendFlowByBarCode(resp);
        System.out.println(JsonHelper.toJson(response));
    }

    @Test
    public void querySendFlowByBarCodeByWayBillTest() {
        QuerySendFlowReq resp = new QuerySendFlowReq();
        CurrentOperate operate = new CurrentOperate();
        resp.setGroupCode("group111");
        operate.setSiteCode(910);
        operate.setSiteName("北京马驹桥分拣中心");
        resp.setCurrentOperate(operate);
        User user = new User();
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        resp.setUser(user);
        resp.setBarCode("JD0003365196531-1-1-");
        JdCResponse<TableTrolleyResp> response = jyComboardGatewayService.querySendFlowByBarCode(resp);
        System.out.println(JsonHelper.toJson(response));
    }

    @Test
    public void querySendFlowByBarCodeByBoxTest() {
        QuerySendFlowReq resp = new QuerySendFlowReq();
        CurrentOperate operate = new CurrentOperate();
        resp.setGroupCode("group111");
        operate.setSiteCode(910);
        operate.setSiteName("北京马驹桥分拣中心");
        resp.setCurrentOperate(operate);
        User user = new User();
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        resp.setUser(user);
        resp.setBarCode("BC1001221122220000108026");
        JdCResponse<TableTrolleyResp> response = jyComboardGatewayService.querySendFlowByBarCode(resp);
        System.out.println(JsonHelper.toJson(response));
    }

    @Test
    public void addOrDeleteGroupCTTTest(){
        AddCTTReq resp = new AddCTTReq();
        CurrentOperate operate = new CurrentOperate();
        resp.setGroupCode("group111");
        operate.setSiteCode(910);
        resp.setTemplateCode("CTT22111700000002");
        operate.setSiteName("北京马驹桥分拣中心");
        resp.setCurrentOperate(operate);
        User user = new User();
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        resp.setUser(user);
        List<TableTrolleyDto> dtos = new ArrayList<>();
        resp.setTableTrolleyDtoList(dtos);
        TableTrolleyDto tableTrolleyDto = new TableTrolleyDto();
        tableTrolleyDto.setEndSiteId(6301);
        tableTrolleyDto.setCrossCode("202");
        tableTrolleyDto.setEndSiteName("添加北京海淀区言语站点001");
        tableTrolleyDto.setTableTrolleyCode("106");
        dtos.add(tableTrolleyDto);
        TableTrolleyDto tableTrolleyDto1 = new TableTrolleyDto();
        tableTrolleyDto1.setEndSiteId(630);
        tableTrolleyDto1.setCrossCode("202");
        tableTrolleyDto1.setEndSiteName("添加京东便民站-测试wyy");
        tableTrolleyDto1.setTableTrolleyCode("11-笼车");
        dtos.add(tableTrolleyDto1);
        JdCResponse jdCResponse = jyComboardGatewayService.addCTT2Group(resp);
        System.out.println(JsonHelper.toJson(jdCResponse));
        RemoveCTTReq req = new RemoveCTTReq();
        CurrentOperate operate2 = new CurrentOperate();
        req.setGroupCode("group2");
        operate2.setSiteCode(910);
        req.setTemplateCode("CTT22111700000001");
        operate2.setSiteName("北京马驹桥分拣中心");
        req.setCurrentOperate(operate2);
        User user2 = new User();
        user2.setUserName("李文吉");
        user2.setUserErp("liwenji3");
        req.setUser(user2);
        List<TableTrolleyDto> dtos2 = new ArrayList<>();
        req.setTableTrolleyDtoList(dtos2);
        TableTrolleyDto tableTrolleyDto2 = new TableTrolleyDto();
        tableTrolleyDto2.setEndSiteId(630171);
        dtos2.add(tableTrolleyDto2);
        TableTrolleyDto tableTrolleyDto3 = new TableTrolleyDto();
        tableTrolleyDto3.setEndSiteId(630109);
        dtos2.add(tableTrolleyDto3);
        JdCResponse jdCResponse1 = jyComboardGatewayService.removeCTTFromGroup(req);
        System.out.println(jdCResponse1);
    }

    @Test
    public void listSendFlowUnderCTTGroupTest() {
        SendFlowDataReq resp = new SendFlowDataReq();
        CurrentOperate operate = new CurrentOperate();
        resp.setGroupCode("group111");
        operate.setSiteCode(910);
        resp.setTemplateCode("CTT22111700000002");
        operate.setSiteName("北京马驹桥分拣中心");
        resp.setCurrentOperate(operate);
        User user = new User();
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        resp.setUser(user);
        JdCResponse<SendFlowDataResp> response = jyComboardGatewayService.listSendFlowUnderCTTGroup(resp);
        System.out.println(JsonHelper.toJson(response));
    }

    @Test
    public void queryBoardDetailTest() {
        BoardReq resp = new BoardReq();
        CurrentOperate operate = new CurrentOperate();
        resp.setGroupCode("group111");
        resp.setBoardCode("Y76364756");
        operate.setSiteCode(910);
        operate.setSiteName("北京马驹桥分拣中心");
        resp.setCurrentOperate(operate);
        User user = new User();
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        resp.setUser(user);
        JdCResponse<BoardResp> boardRespJdCResponse = jyComboardGatewayService.queryBoardDetail(resp);
        System.out.println(JsonHelper.toJson(boardRespJdCResponse));
    }

    @Test
    public void listPackageOrBoxUnderBoardUnBulkFlagTest() {
        BoardReq resp = new BoardReq();
        CurrentOperate operate = new CurrentOperate();
        resp.setGroupCode("group111");
        resp.setBoardCode("B21112200000035");
        operate.setSiteCode(910);
        operate.setSiteName("北京马驹桥分拣中心");
        resp.setCurrentOperate(operate);
        User user = new User();
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        resp.setUser(user);
        resp.setBulkFlag(false);
        JdCResponse<ComboardDetailResp> re = jyComboardGatewayService.listPackageOrBoxUnderBoard(resp);
        System.out.println(JsonHelper.toJson(re));
    }

    @Test
    public void comboardScanTest(){
        ComboardScanReq scanReq =new ComboardScanReq();

    }

    @Test
    public void listPackageOrBoxUnderBoardIsBulkFlagTest() {
        BoardReq resp = new BoardReq();
        CurrentOperate operate = new CurrentOperate();
        resp.setGroupCode("group111");
        resp.setBoardCode("B21112200000025");
        operate.setSiteCode(910);
        operate.setSiteName("北京马驹桥分拣中心");
        resp.setCurrentOperate(operate);
        User user = new User();
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        resp.setUser(user);
        resp.setBulkFlag(true);
        JdCResponse<ComboardDetailResp> re = jyComboardGatewayService.listPackageOrBoxUnderBoard(resp);
        System.out.println(JsonHelper.toJson(re));
    }

    @Test
    public void cancelComboardIsBulkFlagTest() {
        CancelBoardReq resp = new CancelBoardReq();
        CurrentOperate operate = new CurrentOperate();
        resp.setGroupCode("group111");
        resp.setBoardCode("B21112200000035");
        operate.setSiteCode(910);
        operate.setSiteName("北京马驹桥分拣中心");
        resp.setCurrentOperate(operate);
        User user = new User();
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        resp.setUser(user);
        resp.setBulkFlag(true);
        List<ComboardDetailDto>  cancelList = new ArrayList<>();
        ComboardDetailDto comboardDetailDto = new ComboardDetailDto();
        comboardDetailDto.setBarCode("JDV000707553583");
        cancelList.add(comboardDetailDto);
        resp.setCancelList(cancelList);
        jyComboardGatewayService.cancelComboard(resp);
    }

    @Test
    public void cancelComboardUnBulkFlagTest() {
        CancelBoardReq resp = new CancelBoardReq();
        CurrentOperate operate = new CurrentOperate();
        resp.setGroupCode("group111");
        resp.setBoardCode("B21112200000035");
        operate.setSiteCode(910);
        operate.setSiteName("北京马驹桥分拣中心");
        resp.setCurrentOperate(operate);
        User user = new User();
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        resp.setUser(user);
        resp.setBulkFlag(false);
        List<ComboardDetailDto>  cancelList = new ArrayList<>();
        ComboardDetailDto comboardDetailDto = new ComboardDetailDto();
        comboardDetailDto.setBarCode("JDV000707553583-3-5-");
        cancelList.add(comboardDetailDto);
        resp.setCancelList(cancelList);
        jyComboardGatewayService.cancelComboard(resp);
    }
}
