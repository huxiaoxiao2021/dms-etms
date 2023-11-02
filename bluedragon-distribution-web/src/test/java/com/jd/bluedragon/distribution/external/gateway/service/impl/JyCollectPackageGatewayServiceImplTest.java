package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.collectpackage.request.*;
import com.jd.bluedragon.common.dto.collectpackage.response.*;
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
        currentOperate.setSiteCode(40240);
        User user =new User();
        user.setUserErp("wuyoude");
        user.setUserName("吴有德");
        user.setUserCode(123);

        collectPackageReq.setCurrentOperate(currentOperate);
        collectPackageReq.setUser(user);


        collectPackageReq.setBizId("JCP23103100000192");
        collectPackageReq.setBoxCode("BC1001231031220000210023");
        collectPackageReq.setBarCode("JD0003422420020-5-100-");



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
        user.setUserCode(123);


        cancelCollectPackageReq.setCurrentOperate(currentOperate);
        cancelCollectPackageReq.setUser(user);

        cancelCollectPackageReq.setBarCode("JD0003422420020-1-100-");
        cancelCollectPackageReq.setBoxCode("BC1001231031250000208306");
        cancelCollectPackageReq.setBizId("JCP23103100000165");




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
}
