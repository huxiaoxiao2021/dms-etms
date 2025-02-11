package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.collectpackage.request.*;
import com.jd.bluedragon.common.dto.collectpackage.response.*;
import com.jd.bluedragon.common.dto.comboard.request.ExcepScanDto;
import com.jd.bluedragon.distribution.jy.enums.CollectPackageExcepScanEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskCollectPackageStatusEnum;
import com.jd.bluedragon.external.gateway.service.JyCollectPackageGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class JyCollectPackageGatewayServiceImplTest {

    @Autowired
    private JyCollectPackageGatewayService jyCollectPackageGatewayService;


    //todo  @liwenji 补全
    @Test
    public void printBoxAndCreateCollectPackageTask() {
        //生成箱号-生成集包任务
    }


    @Test
    public void collectPackage() {
        CollectPackageReq collectPackageReq =new CollectPackageReq();
        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(910);
        User user =new User();
        user.setUserErp("wuyoude");
        user.setUserName("吴有德");
        user.setUserCode(123);

        collectPackageReq.setCurrentOperate(currentOperate);
        collectPackageReq.setUser(user);


        collectPackageReq.setBizId("JCP23111300000004");
        collectPackageReq.setBoxCode("LL1001240313260000300226");
        collectPackageReq.setBarCode("BC1001240312200000700126");



        JdCResponse<CollectPackageResp> collectPackageRespJdCResponse = jyCollectPackageGatewayService.collectScan(collectPackageReq);
        System.out.println(JsonHelper.toJson(collectPackageRespJdCResponse));
    }


    @Test
    public void collectPackage2() {
        CollectPackageReq collectPackageReq =new CollectPackageReq();
        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(40240);
        User user =new User();
        user.setUserErp("wuyoude");
        user.setUserName("吴有德");
        user.setUserCode(123);

        collectPackageReq.setCurrentOperate(currentOperate);
        collectPackageReq.setUser(user);


        collectPackageReq.setBizId("JCP23110900000124");
        collectPackageReq.setBoxCode("BC1001231109260001003219");
        collectPackageReq.setBarCode("JD0003422585742-1-30-");



        JdCResponse<CollectPackageResp> collectPackageRespJdCResponse = jyCollectPackageGatewayService.collectScan(collectPackageReq);
        System.out.println(JsonHelper.toJson(collectPackageRespJdCResponse));
    }
    @Test
    public void bindTags() {
        CollectPackageReq collectPackageReq =new CollectPackageReq();

        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(40240);

        User user =new User();
        user.setUserErp("wuyoude");
        user.setUserName("吴有德");
        user.setUserCode(123);

        collectPackageReq.setCurrentOperate(currentOperate);
        collectPackageReq.setUser(user);


        collectPackageReq.setBizId("JCP23103100000192");
        collectPackageReq.setBoxCode("BC1001231031220000210023");
        collectPackageReq.setBarCode("AD12345678901234");



        JdCResponse<CollectPackageResp> collectPackageRespJdCResponse = jyCollectPackageGatewayService.collectScan(collectPackageReq);
        System.out.println(JsonHelper.toJson(collectPackageRespJdCResponse));
    }

    @Test
    public void cancelCollectPackage() {
        CancelCollectPackageReq cancelCollectPackageReq =new CancelCollectPackageReq();
        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(40240);
        User user =new User();
        user.setUserErp("wuyoude");
        user.setUserName("吴有德");
        user.setUserCode(17331);


        cancelCollectPackageReq.setCurrentOperate(currentOperate);
        cancelCollectPackageReq.setUser(user);

        cancelCollectPackageReq.setBarCode("JD0003422555037-1-10-");
        cancelCollectPackageReq.setBoxCode("BC1001231108290000800103");
        cancelCollectPackageReq.setBizId("JCP23110800000040");




        JdCResponse<CancelCollectPackageResp> cancelCollectPackageRespJdCResponse = jyCollectPackageGatewayService.cancelCollectPackage(cancelCollectPackageReq);
        System.out.println(JsonHelper.toJson(cancelCollectPackageRespJdCResponse));
    }

    @Test
    public void listCollectPackageTaskTest() {
        CollectPackageTaskReq taskReq = new CollectPackageTaskReq();
        taskReq.setTaskStatus(JyBizTaskCollectPackageStatusEnum.TO_COLLECT.getCode());
        taskReq.setPageNo(1);
        taskReq.setPageSize(30);
        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(910);
        User user =new User();
        user.setUserErp("wuyoude");
        user.setUserName("吴有德");
        taskReq.setCurrentOperate(currentOperate);
        taskReq.setUser(user);
        JdCResponse<CollectPackageTaskResp> response = jyCollectPackageGatewayService.listCollectPackageTask(taskReq);
        System.out.println(JsonHelper.toJson(response));
    }

    @Test
    public void searchPackageTaskTest() {
        SearchPackageTaskReq taskReq = new SearchPackageTaskReq();
        taskReq.setTaskStatus(JyBizTaskCollectPackageStatusEnum.TO_COLLECT.getCode());
        taskReq.setPageNo(1);
        taskReq.setPageSize(30);
        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(910);
        User user =new User();
        user.setUserErp("wuyoude");
        user.setUserName("吴有德");
        taskReq.setCurrentOperate(currentOperate);
        taskReq.setUser(user);
        taskReq.setBarCode("BC1001231019260000413101");
        JdCResponse<CollectPackageTaskResp> response = jyCollectPackageGatewayService.searchPackageTask(taskReq);
        System.out.println(JsonHelper.toJson(response));
    }
    
    @Test
    public void queryTaskDetailTest() {
        TaskDetailReq taskDetailReq = new TaskDetailReq();
        taskDetailReq.setBizId("JB23102300000004");
        taskDetailReq.setBarCode("BC1001231019260000403101");
        JdCResponse<TaskDetailResp> response = jyCollectPackageGatewayService.queryTaskDetail(taskDetailReq);
        System.out.println(JsonHelper.toJson(response));
    }

    @Test
    public void queryTaskDetailStatisticsTest() {
        TaskDetailReq taskDetailReq = new TaskDetailReq();
        taskDetailReq.setBizId("JB23102300000004");
        taskDetailReq.setBarCode("BC1001231019260000403101");
        JdCResponse<TaskDetailStatisticsResp> response = jyCollectPackageGatewayService.queryTaskDetailStatistics(taskDetailReq);
        System.out.println(JsonHelper.toJson(response));
    }

    @Test
    public void sealingBoxTest() {
        SealingBoxReq boxReq = new SealingBoxReq();
        List<SealingBoxDto> list = new ArrayList<>();
        boxReq.setSealingBoxDtoList(list);
        SealingBoxDto sealingBoxDto = new SealingBoxDto();
        sealingBoxDto.setBoxCode("BC1001231019260000403101");
        sealingBoxDto.setBizId("JB23102300000004");
        list.add(sealingBoxDto);
        JdCResponse<SealingBoxResp> response = jyCollectPackageGatewayService.sealingBox(boxReq);
        System.out.println(JsonHelper.toJson(response));
    }


    @Test
    public void queryStatisticsUnderTask() {

        StatisticsUnderTaskQueryResp data =new StatisticsUnderTaskQueryResp();

        data.setBoxCode("箱号");
        data.setMaterialCode("集包袋号码");

        List<ExcepScanDto> excepScanDtoList =new ArrayList<>();
        ExcepScanDto intercepter =new ExcepScanDto();
        intercepter.setType(CollectPackageExcepScanEnum.INTERCEPTED.getCode());
        intercepter.setName(CollectPackageExcepScanEnum.INTERCEPTED.getName());
        intercepter.setCount(1);
        excepScanDtoList.add(intercepter);


        ExcepScanDto haveScan =new ExcepScanDto();
        haveScan.setType(CollectPackageExcepScanEnum.HAVE_SCAN.getCode());
        haveScan.setName(CollectPackageExcepScanEnum.HAVE_SCAN.getName());
        haveScan.setCount(2);
        excepScanDtoList.add(haveScan);


        ExcepScanDto force =new ExcepScanDto();
        force.setType(CollectPackageExcepScanEnum.FORCE_SEND.getCode());
        force.setName(CollectPackageExcepScanEnum.FORCE_SEND.getName());
        force.setCount(3);
        excepScanDtoList.add(force);

        data.setExcepScanDtoList(excepScanDtoList);


        List<CollectPackageFlowDto> collectPackageFlowDtoList =new ArrayList<>();

        CollectPackageFlowDto collectPackageFlowDto1 =new CollectPackageFlowDto();
        collectPackageFlowDto1.setEndSiteId(1L);
        collectPackageFlowDto1.setEndSiteName("场地1");
        collectPackageFlowDto1.setCount(1);
        collectPackageFlowDtoList.add(collectPackageFlowDto1);


        CollectPackageFlowDto collectPackageFlowDto2 =new CollectPackageFlowDto();
        collectPackageFlowDto2.setEndSiteId(2L);
        collectPackageFlowDto2.setEndSiteName("场地2");
        collectPackageFlowDto2.setCount(2);
        collectPackageFlowDtoList.add(collectPackageFlowDto2);


        data.setCollectPackageFlowDtoList(collectPackageFlowDtoList);

        JdCResponse jdCResponse =new JdCResponse(JdCResponse.CODE_SUCCESS,JdCResponse.MESSAGE_SUCCESS);
        jdCResponse.setData(data);
        System.out.println(JsonHelper.toJson(jdCResponse));
    }

    @Test
    public void queryStatisticsUnderFlow() {
        StatisticsUnderFlowQueryResp data =new StatisticsUnderFlowQueryResp();
        List<CollectPackageDto> collectPackageDtoList =new ArrayList<>();
        data.setCollectPackageDtoList(collectPackageDtoList);

        CollectPackageDto collectPackageDto =new CollectPackageDto();
        collectPackageDto.setPackageCode("包裹号1");
        collectPackageDtoList.add(collectPackageDto);

        CollectPackageDto collectPackageDto2 =new CollectPackageDto();
        collectPackageDto2.setPackageCode("包裹号2");
        collectPackageDtoList.add(collectPackageDto2);


        JdCResponse jdCResponse =new JdCResponse(JdCResponse.CODE_SUCCESS,JdCResponse.MESSAGE_SUCCESS);
        jdCResponse.setData(data);
        System.out.println(JsonHelper.toJson(jdCResponse));
    }

    @Test
    public void querySiteMixFlowListTest() {
        MixFlowListReq req = new MixFlowListReq();
        req.setBizId("JCP23110700000022");
        req.setBoxCode("BC1001231107260000600320");
        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(910);
        User user =new User();
        user.setUserErp("wuyoude");
        user.setUserName("吴有德");
        user.setUserCode(17331);
        req.setUser(user);
        req.setCurrentOperate(currentOperate);
        JdCResponse<MixFlowListResp> response = jyCollectPackageGatewayService.querySiteMixFlowList(req);
        System.out.println(JsonHelper.toJson(response));
    }

    @Test
    public void updateTaskFlowListTest() {
        UpdateMixFlowListReq req = new UpdateMixFlowListReq();
        req.setBizId("JCP23110700000022");
        req.setBoxCode("BC1001231107260000600320");
        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(910);
        User user =new User();
        user.setUserErp("wuyoude");
        user.setUserName("吴有德");
        user.setUserCode(17331);
        req.setUser(user);
        req.setCurrentOperate(currentOperate);
        List<CollectPackageFlowDto> collectPackageFlowDtoList = new ArrayList<>();
        CollectPackageFlowDto dto = new CollectPackageFlowDto();
        dto.setEndSiteId(2007L);
        dto.setEndSiteName("太原大货仓");
        collectPackageFlowDtoList.add(dto);
        CollectPackageFlowDto dto2 = new CollectPackageFlowDto();
        dto2.setEndSiteId(40240L);
        dto2.setEndSiteName("北京通州分拣中心");
        collectPackageFlowDtoList.add(dto2);
        req.setCollectPackageFlowDtoList(collectPackageFlowDtoList);
        JdCResponse<UpdateMixFlowListResp> response = jyCollectPackageGatewayService.updateTaskFlowList(req);
        System.out.println(JsonHelper.toJson(response));
    }
}
