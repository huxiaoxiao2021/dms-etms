package com.jd.bluedragon.distribution.jy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.*;
import com.jd.bluedragon.common.dto.jyexpection.response.*;
import com.jd.bluedragon.distribution.external.service.DmsTimingHandlerService;
import com.jd.bluedragon.distribution.jy.exception.JyAssignExpTaskDto;
import com.jd.bluedragon.distribution.jy.service.exception.JyDamageExceptionService;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionContrabandDto;
import com.jd.bluedragon.distribution.jy.service.exception.JyContrabandExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.JySanwuExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.impl.JyScrappedExceptionServiceImpl;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.JyBizTaskAutoCloseHelperService;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseTaskMq;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.qualityControl.dto.QcReportJmqDto;
import com.jd.bluedragon.distribution.qualityControl.dto.QcReportOutCallJmqDto;
import com.jd.bluedragon.utils.DateHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:distribution-web-context.xml"})
public class JyExceptionServiceTest {
    @Autowired
    JyExceptionService jyExceptionService;

//    @Autowired
//    JySanwuExceptionService jySanwuExceptionService;
//
//    @Autowired
//    private DmsTimingHandlerService dmsTimingHandlerService;
//
//    @Autowired
//    private JyScrappedExceptionServiceImpl jyScrappedExceptionService;
//
//    @Autowired
//    private JyDamageExceptionService jyDamageExceptionService;
//
//    @Test
//    public void uploadScanTest() {
//        ExpUploadScanReq req = new ExpUploadScanReq();
//
//        req.setUserErp("wuyoude");
//        req.setSiteId(910);
//        req.setPositionCode("GW00003001");
//
//       //req.setBarCode("SW2111111111");
//        req.setSource(0);
//
//        req.setBarCode("JD0003421266039");
//        //req.setType(1);
//
//        JdCResponse<Object> response = jyExceptionService.uploadScan(req);
//        System.out.println(JSON.toJSONString(response));
//    }
//
//    @Test
//    public void statisticsByStatusTest(){
//
//        ExpBaseReq req = new ExpBaseReq();
//        req.setUserErp("wuyoude");
//        req.setSiteId(910);
//        req.setPositionCode("GW00003001");
//        JdCResponse<List<StatisticsByStatusDto>> response = jyExceptionService.statisticsByStatus(req);
//
//        System.out.println(JSON.toJSONString(response));
//
//
//    }
//
//    @Test
//    public void getGridStatisticsPageList() {
//        StatisticsByGridReq req = new StatisticsByGridReq();
//        req.setUserErp("wuyoude");
//        req.setPositionCode("GW00003001");
//        JdCResponse<List<StatisticsByGridDto>> response = jyExceptionService.getGridStatisticsPageList(req);
//        System.out.println(JSON.toJSONString(response));
//    }
//
//
//    @Test
//    public void getExceptionTaskPageList() {
//
//        ExpTaskPageReq req = new ExpTaskPageReq();
//        req.setStatus(2);
//        req.setPositionCode("GW00003001");
//        req.setUserErp("wuyoude");
//        JdCResponse<List<ExpTaskDto>> response = jyExceptionService.getExceptionTaskPageList(req);
//        System.out.println(JSON.toJSONString(response));
//    }
//
//
//    @Test
//    public void receiveTest() {
//
//        ExpReceiveReq req = new ExpReceiveReq();
//        req.setUserErp("wuyoude");
//        req.setBarCode("SANWU_111111");
//        req.setPositionCode("GW00003001");
//        //req.setType(0);
//        JdCResponse<Object> receive = jyExceptionService.receive(req);
//        System.out.println(JSON.toJSONString(receive));
//    }
//
//    @Test
//    public void processTaskTest() {
//
//        ExpTaskDetailReq req = new ExpTaskDetailReq();
//        req.setUserErp("wuyoude");
//        req.setPositionCode("GW00003001");
//        req.setBatchNo("xxx");
//        req.setFrom("364605");
//        req.setTo("364605,38");
//        req.setBizId("SANWU_sw000001");
//        req.setGoodsNo("1234567890");
//        req.setGoodsNum("1");
//        req.setInnerDesc("inner");
//        req.setOuterDesc("1");
//        req.setPrice("11.11");
//        req.setSaveType("1");
//        req.setWeight("11.11");
//
//        JdCResponse<Object> objectJdCResponse = jyExceptionService.processTask(req);
//        System.out.println(JSONObject.toJSON(objectJdCResponse));
//    }
//
//
//    @Test
//    public void queryProductNameTest() {
//        //JdCResponse<List<DmsBarCode>> listJdCResponse = jyExceptionGatewayService.queryProductName("a,aa,4");
//        //System.out.println(JSONObject.toJSON(listJdCResponse));
//    }
//
//    @Test
//    public void getJyExceptionScrappedTypeListTest(){
//        JdCResponse<List<JyExceptionScrappedTypeDto>> list = jyScrappedExceptionService.getJyExceptionScrappedTypeList();
//        Assert.assertEquals(list.isSucceed(),true);
//    }
//
//    @Test
//    public void getJyExceptionPackageTypeListTest(){
//        JdCResponse<List<JyExceptionPackageTypeDto>> list = jyDamageExceptionService.getJyExceptionPackageTypeList(null);
//        Assert.assertEquals(list.isSucceed(),true);
//    }
//
//    @Test
//    public void processTaskOfscrappedTest(){
//        ExpScrappedDetailReq req = new ExpScrappedDetailReq();
//        req.setUserErp("wuyoude");
//        req.setPositionCode("GW00003001");
//        req.setBarCode("JDVA00255154794");
//        req.setBizId("JDVA00255154794_910");
//        req.setSiteId(910);
//        req.setScrappedTypCode(1);
//        req.setCertifyImageUrl("123.jpg,1456.jpg");
//        req.setGoodsImageUrl("123.jpg,1456.jpg");
//        req.setSaveType(1);
//
//        JdCResponse<Boolean> response = jyScrappedExceptionService.processTaskOfscrapped(req);
//        System.out.println(JSON.toJSONString(response));
//    }
//
//    @Test
//    public void getTaskDetailOfscrappedTest(){
//        ExpTaskByIdReq req=new ExpTaskByIdReq();
//        req.setBizId("SANWU_sw000001");
//        JdCResponse<ExpScrappedDetailDto> response = jyScrappedExceptionService.getTaskDetailOfscrapped(req);
//        Assert.assertEquals(response.isSucceed(),true);
//
//    }
//
//    @Test
//    public void timingTaskHandlerTest() {
//        dmsTimingHandlerService.timingHandlerFreshScrapNotice();
//        Assert.assertTrue(true);
//        dmsTimingHandlerService.timingHandlerOverTimeException();
//        Assert.assertTrue(true);
//    }
//
//    @Test
//    public void checkExceptionPrincipalTest(){
//
//        ExpBaseReq req = new ExpBaseReq();
//        req.setSiteId(65388);
//        req.setUserErp("liuduo");
//        req.setPositionCode("GW00145001");
//        JdCResponse<Boolean> response = jyExceptionService.checkExceptionPrincipal(req);
//        System.out.println(JSON.toJSONString(response));
//    }
//
//    @Test
//    public void getWaitReceiveSanwuExceptionByPageTest(){
//
//        ExpTaskStatisticsReq req = new ExpTaskStatisticsReq();
//        req.setSiteId(910);
//        req.setPositionCode("GW00003001");
//        req.setPageSize(10);
//        req.setPageNumber(1);
//        JdCResponse<List<ExpTaskStatisticsOfWaitReceiveDto>> response = jySanwuExceptionService.getExpTaskStatisticsOfWaitReceiveByPage(req);
//        System.out.println(JSON.toJSONString(response));
//
//    }
//
//    @Test
//    public void getWaitReceiveSanwuExpTaskByPageTest(){
//        ExpTaskStatisticsDetailReq req = new ExpTaskStatisticsDetailReq();
//        req.setSiteId(910);
//        req.setPositionCode("GW00003001");
//        req.setPageSize(10);
//        req.setPageNumber(1);
//        req.setFloor(1);
//        req.setGridCode("BDXC-01");
//        req.setGridNo("01");
//        req.setAreaCode("BDXC");
//        JdCResponse<List<ExpTaskDto>> response = jySanwuExceptionService.getWaitReceiveSanwuExpTaskByPage(req);
//        System.out.println(JSON.toJSONString(response));
//
//    }
//
//    @Test
//    public void getExpSignInUserByPageTest(){
//
//        ExpSignUserReq req = new ExpSignUserReq();
//        req.setPositionCode("GW00019001");
//        req.setExpUserErp("wu");
//        req.setPageSize(10);
//        req.setPageNumber(1);
//        JdCResponse<List<ExpSignUserResp>> response = jySanwuExceptionService.getExpSignInUserByPage(req);
//        System.out.println(JSON.toJSONString(response));
//
//    }
//
//    @Test
//    public void assignExpTaskTest(){
//        ExpTaskAssignRequest req = new ExpTaskAssignRequest();
//        req.setSiteId(910);
//        req.setPositionCode("GW00003001");
//        req.setUserErp("wuyoude");
//        req.setAssignHandlerErp("wuyoude");
//        req.setBizIds(Arrays.asList("SANWU_SW1234567892","SANWU_SWNEW67890"));
//
//
//        List<ExpTaskStatisticsOfWaitReceiveDto> expTaskStatistics =  new ArrayList<>();
//        ExpTaskStatisticsOfWaitReceiveDto detailReq = new ExpTaskStatisticsOfWaitReceiveDto();
//        detailReq.setFloor(1);
//        detailReq.setGridCode("BGQ-01");
//        detailReq.setGridNo("01");
//        detailReq.setAreaCode("BGQ");
//        expTaskStatistics.add(detailReq);
//        req.setExpTaskStatistics(expTaskStatistics);
//
//        JdCResponse<Boolean> response = jySanwuExceptionService.assignExpTask(req);
//        System.out.println(JSON.toJSONString(response));
//    }
//
//    @Test
//    public void dealAssignTaskDataTest(){
//        JyAssignExpTaskDto mq = new JyAssignExpTaskDto();
//        mq.setPrincipalErp("wuyoude");
//        mq.setBizId("SANWU_SWNEW67890");
//        mq.setAssignHandlerErp("wuyoude");
//        //jySanwuExceptionService.dealAssignTaskData(mq);
//    }
//
//    @Test
//    public void getAssignExpTaskCountTest(){
//        ExpBaseReq req = new ExpBaseReq();
//        req.setSiteId(910);
//        req.setPositionCode("GW00003001");
//        req.setUserErp("wuyoude");
//
//
//        JdCResponse<Integer> response = jySanwuExceptionService.getAssignExpTaskCount(req);
//        System.out.println(JSON.toJSONString(response));
//    }
//
//    @Test
//    public void exceptionTaskCheckByExceptionTypeTest(){
//
//        ExpTypeCheckReq req = new ExpTypeCheckReq();
//
//        req.setBarCode("JDX000253957820");
//        req.setType(2);
//
//        JdCResponse<Boolean> response = jyExceptionService.exceptionTaskCheckByExceptionType(req);
//        System.out.println(JSON.toJSONString(response));
//    }
//
//    @Test
//    public void dealExpDamageInfoByAbnormalReportOutCallTest(){
//        QcReportJmqDto dto = new QcReportJmqDto();
//        //dto.setAbnormalDocumentNum("JDVA00255154794");
//        dto.setAbnormalDocumentNum("JDX000201044026");
//        dto.setAbnormalFirstId(20009l);
//        dto.setAbnormalFirstName("外呼类");
//        dto.setAbnormalSecondId(20051L);
//        dto.setAbnormalSecondName("揽收派送异常");
//        dto.setAbnormalThirdId(230502L);
//        dto.setAbnormalThirdName("派送联系不上客户");
//        dto.setCreateDept("40240");
//        dto.setCreateDeptName("资阳娇子营业部");
//        dto.setCreateRegion("4");
//        dto.setCreateTime(1691137481000L);
//        dto.setCreateUser("jiangchengjie5");
//        dto.setEndStatus("1");
//        dto.setId(404341744L);
//        dto.setPackageNumber("JDVA21647328198-1-1-");
//        dto.setRemark("空号");
//        dto.setReportSystem("xiaoge");
//
//        jyDamageExceptionService.dealExpDamageInfoByAbnormalReportOutCall(dto);
//    }
//
//    @Test
//    public void dealDamageExpTaskStatusTest(){
//
//        jyDamageExceptionService.dealDamageExpTaskStatus("JDX000201044026",null);
//    }
//
//    @Test
//    public void dealDamageExpTaskOverTwoDagsTest(){
//        jyDamageExceptionService.dealDamageExpTaskOverTwoDags();
//    }
//
//
//
//    @Autowired
//    private JyContrabandExceptionService jyContrabandExceptionService;
//
//    @Test
//    public void processTaskOfContrabandTest(){
//
//        ExpContrabandReq req = new ExpContrabandReq();
//        req.setUserErp("wuyoude");
//        req.setSiteId(910);
//        req.setPositionCode("GW00003001");
//        req.setBarCode("JDVE00088304206-1-1-");
//        req.setContrabandType(3);
//        req.setDescription("hahahahahahaha");
//
//
//        List<String> imageUrlList = new ArrayList<>();
//        imageUrlList.add("1.jpg");
//        imageUrlList.add("2.jpg");
//        req.setImageUrlList(imageUrlList);
//
//        JdCResponse<Boolean> response = jyContrabandExceptionService.processTaskOfContraband(req);
//
//        System.out.println(JSON.toJSONString(response));
//
//    }
//
//    @Test
//    public void dealContrabandUploadDataTest(){
//
//        JyExceptionContrabandDto dto = new JyExceptionContrabandDto();
//        dto.setBarCode("JD0003421542886-1-1-");
//        dto.setBizId("JD0003421542886-1-1-_40240");
//        dto.setContrabandType(3);
//        dto.setCreateStaffName("wuyoude");
//        dto.setCreateStaffName("吴有德");
//        dto.setCreateUserId(17331);
//        dto.setDescription("hhhhhhhh");
//        dto.setSiteCode(40240);
//        dto.setSiteName("北京通州分拣中心");
//
//
//        try {
//            jyContrabandExceptionService.dealContrabandUploadData(dto);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//
//    }

    @Autowired
    private JyBizTaskAutoCloseHelperService jyBizTaskAutoCloseHelperService;

    @Test
    public void processTaskOfContrabandTest(){

        ExpContrabandReq req = new ExpContrabandReq();
        req.setUserErp("wuyoude");
        req.setSiteId(910);
        req.setPositionCode("GW00003001");
        req.setBarCode("JDVE00088304206-1-1-");
        req.setContrabandType(3);
        req.setDescription("hahahahahahaha");


        List<String> imageUrlList = new ArrayList<>();
        imageUrlList.add("1.jpg");
        imageUrlList.add("2.jpg");
        req.setImageUrlList(imageUrlList);

        String dateStr ="2023-10-09 06:00:00";
        for (int i = 0; i < 12; i++) {
            AutoCloseTaskMq autoCloseTaskMq = new AutoCloseTaskMq();
            autoCloseTaskMq.setOperateTime(new Date().getTime());
            autoCloseTaskMq.setChangeStatus(1);
            JyBizTaskSendVehicleDetailEntity sendVehicleDetail = new JyBizTaskSendVehicleDetailEntity();
            sendVehicleDetail.setSendVehicleBizId("SendVehicleBizId");
            sendVehicleDetail.setEndSiteId(40240L);
            sendVehicleDetail.setBizId("BizId");
            sendVehicleDetail.setPlanDepartTime(DateHelper.parse(dateStr,DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2));
            jyBizTaskAutoCloseHelperService.pushBizTaskAutoCloseTask4SendVehicleTask(autoCloseTaskMq,sendVehicleDetail);
        }


    }






}
