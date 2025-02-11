package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.comboard.request.*;
import com.jd.bluedragon.common.dto.comboard.response.*;
import com.jd.bluedragon.distribution.board.SortBoardJsfService;
import com.jd.bluedragon.distribution.jy.service.send.JyBizTaskComboardService;
import com.jd.bluedragon.distribution.jy.service.send.JyComBoardSendService;
import com.jd.bluedragon.external.gateway.service.JyComboardGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import java.util.Date;

//import org.apache.avro.data.Json;
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

    @Autowired
    JyComBoardSendService jyComBoardSendService;

    @Autowired
    SortBoardJsfService sortBoardJsfService;
    @Autowired
    private JyBizTaskComboardService jyBizTaskComboardService;

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
        query.setNeedSendFlowStatistics(true);
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
        resp.setTemplateCode("CTT22120100000004");
        resp.setTemplateName("混扫4");
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
    }

    @Test
    public void listSendFlowUnderCTTGroupTest() {
        SendFlowDataReq resp = new SendFlowDataReq();
        CurrentOperate operate = new CurrentOperate();
        resp.setGroupCode("G00000059001");
        operate.setSiteCode(910);
        resp.setTemplateCode("CTT23010400000002");
        operate.setSiteName("北京马驹桥分拣中心");
        resp.setCurrentOperate(operate);
        User user = new User();
        user.setUserName("吴有德");
        user.setUserErp("wuyoude");
        resp.setUser(user);
        JdCResponse<SendFlowDataResp> response = jyComboardGatewayService.listSendFlowUnderCTTGroup(resp);
        System.out.println(JsonHelper.toJson(response));
    }

    @Test
    public void querySendFlowDetailTest() {
        SendFlowDetailReq resp = new SendFlowDetailReq();
        CurrentOperate operate = new CurrentOperate();
        resp.setGroupCode("group111");
        operate.setSiteCode(910);
        operate.setSiteName("北京马驹桥分拣中心");
        resp.setCurrentOperate(operate);
        User user = new User();
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        resp.setUser(user);
        resp.setEndSiteId(630105);
        resp.setBoardCode("B22120200000067");
        JdCResponse<SendFlowDetailResp> r = jyComboardGatewayService.querySendFlowDetail(resp);
        System.out.println(JsonHelper.toJson(r));
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
        scanReq.setTemplateCode("CTT22120500000007");
        scanReq.setGroupCode("G00000047004");
        scanReq.setBarCode("JD0003403913723-1-1-");
        scanReq.setScanType(0);
        scanReq.setEndSiteId(38);
        scanReq.setSupportMutilSendFlow(true);
        //scanReq.setNeedSkipSendFlowCheck(true);
        CurrentOperate operate = new CurrentOperate();
        operate.setSiteCode(910);
        operate.setSiteName("北京马驹桥分拣中心");
        operate.setOperateTime(new Date());
        scanReq.setCurrentOperate(operate);
        User user = new User();
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        user.setUserCode(123456);
        scanReq.setUser(user);
        final JdVerifyResponse<ComboardScanResp> re = jyComboardGatewayService.comboardScan(scanReq);
        System.out.println(JsonHelper.toJson(re));
    }

    @Test
    public void comboardScanAutoMachineTest(){

//
//        BindBoardRequest req = new BindBoardRequest();
//
//        sortBoardJsfService.addToBoard(req);
//
//
//
//        BindBoardRequest scanReq = new BindBoardRequest();
//
//        scanReq.setBarcode("JDV000707422015-1-5-");
//        Board board = new Board();
//
//        scanReq.setBoard(board);
//
//        Response<BoardSendDto> re = sortBoardJsfService.sortMachineComboard(scanReq);
//
//        System.out.println(JsonHelper.toJson(re));
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
        user.setUserCode(123456);
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
        resp.setBoardCode("B21112200000025");
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
        comboardDetailDto.setBarCode("JD0003403868515-1-1-");
        cancelList.add(comboardDetailDto);
        resp.setCancelList(cancelList);
        jyComboardGatewayService.cancelComboard(resp);
    }

    @Test
    public void cancelComboardUnBulkFlagTest() {
        CancelBoardReq resp = new CancelBoardReq();
        CurrentOperate operate = new CurrentOperate();
        resp.setGroupCode("group111");
        resp.setBoardCode("B22120100000055");
        operate.setSiteCode(910);
        operate.setSiteName("北京马驹桥分拣中心");
        resp.setCurrentOperate(operate);
        User user = new User();
        user.setUserCode(11111);
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        resp.setUser(user);
        resp.setBulkFlag(false);
        List<ComboardDetailDto>  cancelList = new ArrayList<>();
        ComboardDetailDto comboardDetailDto = new ComboardDetailDto();
        comboardDetailDto.setBarCode("JD0003365286683-1-1-");
        cancelList.add(comboardDetailDto);
        resp.setCancelList(cancelList);
        resp.setEndSiteName("石景山营业部");
        jyComboardGatewayService.cancelComboard(resp);
    }

    @Test
    public void finishBoardTest() {
        BoardReq resp = new BoardReq();
        CurrentOperate operate = new CurrentOperate();
        resp.setGroupCode("group111");
        resp.setBoardCode("B22112600000025");
        operate.setSiteCode(910);
        operate.setSiteName("北京马驹桥分拣中心");
        resp.setCurrentOperate(operate);
        User user = new User();
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        resp.setUser(user);
        JdCResponse jdCResponse = jyComboardGatewayService.finishBoard(resp);
        System.out.println(JsonHelper.toJson(jdCResponse));
        BoardReq resp2 = new BoardReq();
        CurrentOperate operate2 = new CurrentOperate();
        resp2.setGroupCode("group111");
        resp2.setBoardCode("B22112600000025");
        operate2.setSiteCode(910);
        operate2.setSiteName("北京马驹桥分拣中心");
        resp2.setCurrentOperate(operate2);
        User user2 = new User();
        user2.setUserName("李文吉");
        user2.setUserErp("liwenji3");
        resp2.setUser(user2);
        JdCResponse jdCResponse1 = jyComboardGatewayService.finishBoard(resp2);
        System.out.println(JsonHelper.toJson(jdCResponse1));
    }

    @Test
    public void finishBoardsUnderCTTGroupTest() {
        CTTGroupReq resp = new CTTGroupReq();
        CurrentOperate operate = new CurrentOperate();
        resp.setGroupCode("G00000047004");
        operate.setSiteCode(910);
        operate.setSiteName("北京马驹桥分拣中心");
        resp.setCurrentOperate(operate);
        User user = new User();
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        resp.setUser(user);
        resp.setTemplateCode("CTT22112500000001");
        JdCResponse jdCResponse = jyComboardGatewayService.finishBoardsUnderCTTGroup(resp);
        System.out.println(JsonHelper.toJson(jdCResponse));
    }

    @Test
    public void queryBelongBoardByBarCodeTest() {
        QueryBelongBoardReq resp = new QueryBelongBoardReq();
        CurrentOperate operate = new CurrentOperate();
        resp.setGroupCode("G00000047004");
        operate.setSiteCode(910);
        operate.setSiteName("北京马驹桥分拣中心");
        resp.setCurrentOperate(operate);
        User user = new User();
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        resp.setUser(user);
        resp.setBarCode("JDV000707553583-2-5-");
        JdCResponse<QueryBelongBoardResp> re = jyComboardGatewayService.queryBelongBoardByBarCode(resp);
        System.out.println(JsonHelper.toJson(re));
    }

    @Test
    public void queryBoardStatisticsUnderSendFlowTest() {
        BoardStatisticsReq boardStatisticsReq = new BoardStatisticsReq();
        CurrentOperate operate = new CurrentOperate();
        boardStatisticsReq.setGroupCode("G00000047004");
        operate.setSiteCode(910);
        operate.setSiteName("北京马驹桥分拣中心");
        boardStatisticsReq.setCurrentOperate(operate);
        User user = new User();
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        boardStatisticsReq.setUser(user);
        boardStatisticsReq.setPageNo(1);
        boardStatisticsReq.setPageSize(50);
        boardStatisticsReq.setEndSiteId(39);
        JdCResponse<BoardStatisticsResp> boardStatisticsRespJdCResponse = jyComboardGatewayService.queryBoardStatisticsUnderSendFlow(boardStatisticsReq);

        System.out.println(JsonHelper.toJson(boardStatisticsRespJdCResponse));
    }

    @Test
    public void listPackageDetailUnderBoxTest() {
        BoxQueryReq resp = new BoxQueryReq();
        CurrentOperate operate = new CurrentOperate();
        resp.setGroupCode("G00000047004");
        operate.setSiteCode(910);
        operate.setSiteName("北京马驹桥分拣中心");
        resp.setCurrentOperate(operate);
        User user = new User();
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        resp.setUser(user);
        resp.setBoxCode("BC1001190626170000000101");
        resp.setPageNo(1);
        resp.setPageSize(1000);
        JdCResponse<PackageDetailResp> respJdCResponse = jyComboardGatewayService.listPackageDetailUnderBox(resp);
        System.out.println(JsonHelper.toJson(respJdCResponse));
    }

    @Test
    public void queryExcepScanStatisticsUnderBoardTest() {
        BoardExcepStatisticsReq req = new BoardExcepStatisticsReq();
        CurrentOperate operate = new CurrentOperate();
        req.setGroupCode("G00000047004");
        operate.setSiteCode(910);
        operate.setSiteName("北京马驹桥分拣中心");
        req.setCurrentOperate(operate);
        User user = new User();
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        req.setUser(user);
        req.setBoardCode("B22120700000039");
        req.setPageNo(1);
        req.setPageSize(30);
        req.setExcepType(1);
        JdCResponse<BoardExcepStatisticsResp> s = jyComboardGatewayService.queryExcepScanStatisticsUnderBoard(req);
        System.out.println(JsonHelper.toJson(s));
    }

    @Test
    public void updateBoardStatusBySendCodeListTest() {
        List<String> batchCodes = new ArrayList<>();
        batchCodes.add("910-39-20221205212254643");
        batchCodes.add("910-39-20221205212254654");
        jyBizTaskComboardService.updateBoardStatusBySendCode("dehudheu","liwenji3","李文吉");
    }

    @Test
    public void deleteCTTGroupTest() {
        DeleteCTTGroupReq deleteCTTGroupReq = new DeleteCTTGroupReq();
        CurrentOperate operate = new CurrentOperate();
        operate.setSiteCode(910);
        operate.setSiteName("北京马驹桥分拣中心");
        deleteCTTGroupReq.setCurrentOperate(operate);
        deleteCTTGroupReq.setGroupCode("G00000059001");
        deleteCTTGroupReq.setTemplateCode("CTT22121400000010");
        User user = new User();
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        deleteCTTGroupReq.setUser(user);
        JdCResponse<String> jdCResponse = jyComboardGatewayService.deleteCTTGroup(deleteCTTGroupReq);
        System.out.println(JsonHelper.toJson(jdCResponse));
    }
    
    @Test
    public void  queryUserByStartSiteCodeTest() {
        SendFlowQueryReq boardReq = new SendFlowQueryReq();
        CurrentOperate operate = new CurrentOperate();
        operate.setSiteCode(910);
        operate.setSiteName("北京马驹桥分拣中心");
        boardReq.setCurrentOperate(operate);
        boardReq.setGroupCode("G00000059001");
        User user = new User();
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        boardReq.setUser(user);
        jyComboardGatewayService.queryScanUser(boardReq);
    }
}
