package com.jd.bluedragon.distribution.jy;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.send.request.CancelSendTaskReq;
import com.jd.bluedragon.common.dto.send.request.CreateVehicleTaskReq;
import com.jd.bluedragon.common.dto.send.request.DeleteVehicleTaskReq;
import com.jd.bluedragon.common.dto.send.response.CancelSendTaskResp;
import com.jd.bluedragon.common.dto.send.response.CreateVehicleTaskResp;
import com.jd.bluedragon.common.dto.send.response.VehicleSpecResp;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.service.send.JyNoTaskSendService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-core-context-dev.xml")
@Slf4j
public class NoTaskSendTest {
    @Autowired
    JyNoTaskSendService jyNoTaskSendService;


    @Test
    public void test() {

       String str ="1350";
        log.info("code",str.length()==4);
        log.info("code",str.substring(0,3));
    }

    @Test
    public void listVehicleTypeTest() {

        InvokeResult<List<VehicleSpecResp>> invokeResult =jyNoTaskSendService.listVehicleType();
        log.info("code",invokeResult.getCode());
        log.info("date",invokeResult.getData().size());
    }

    @Test
    public void createVehicleTaskTest() {

        CreateVehicleTaskReq createVehicleTaskReq =new CreateVehicleTaskReq();
        createVehicleTaskReq.setVehicleType(1);
        createVehicleTaskReq.setVehicleTypeName("测试类型");
        CurrentOperate currentOperate =new CurrentOperate();
        currentOperate.setSiteCode(912);
        currentOperate.setSiteName("北京马驹桥分拣中心6");
        createVehicleTaskReq.setCurrentOperate(currentOperate);
        User user =new User();
        user.setUserErp("wuyoude");
        user.setUserName("吴有德");
        createVehicleTaskReq.setUser(user);

        InvokeResult<CreateVehicleTaskResp> invokeResult =jyNoTaskSendService.createVehicleTask(createVehicleTaskReq);
        log.info("==========result code===========:{}", invokeResult.getCode());
        log.info("==========result msg===========:{}", invokeResult.getMessage());
    }

    @Test
    public void deleteVehicleTaskTest() {

        DeleteVehicleTaskReq deleteVehicleTaskReq =new DeleteVehicleTaskReq();
        deleteVehicleTaskReq.setBizId("NSST22060700000005");
        CurrentOperate currentOperate =new CurrentOperate();
        currentOperate.setSiteCode(910);
        currentOperate.setSiteName("北京马驹桥分拣中心6");
        deleteVehicleTaskReq.setCurrentOperate(currentOperate);
        User user =new User();
        user.setUserErp("wuyoude");
        user.setUserName("吴有德");
        deleteVehicleTaskReq.setUser(user);

        InvokeResult<CreateVehicleTaskResp> invokeResult =jyNoTaskSendService.deleteVehicleTask(deleteVehicleTaskReq);
        log.info("==========result code===========:{}", invokeResult.getCode());
        log.info("==========result msg===========:{}", invokeResult.getMessage());
    }

    @Test
    public void listVehicleTaskTest() {

        CreateVehicleTaskReq createVehicleTaskReq =new CreateVehicleTaskReq();
        createVehicleTaskReq.setVehicleType(1);
        createVehicleTaskReq.setVehicleTypeName("测试类型");
        CurrentOperate currentOperate =new CurrentOperate();
        currentOperate.setSiteCode(910);
        currentOperate.setSiteName("北京马驹桥分拣中心6");
        createVehicleTaskReq.setCurrentOperate(currentOperate);
        User user =new User();
        user.setUserErp("wuyoude");
        user.setUserName("吴有德");
        createVehicleTaskReq.setUser(user);

        InvokeResult<CreateVehicleTaskResp> invokeResult =jyNoTaskSendService.createVehicleTask(createVehicleTaskReq);
        log.info("==========result code===========:{}", invokeResult.getCode());
        log.info("==========result msg===========:{}", invokeResult.getMessage());
        log.info("==========result Data===========:{}", invokeResult.getData().getBizNo());
    }

    @Test
    public void cancelSendTaskTest() {

        CancelSendTaskReq cancelSendTaskReq =new CancelSendTaskReq();
        cancelSendTaskReq.setCode("JDX000200462956-1-3-");
        cancelSendTaskReq.setType(1);
        CurrentOperate currentOperate =new CurrentOperate();
        currentOperate.setSiteCode(910);
        currentOperate.setSiteName("北京马驹桥分拣中心6");
        cancelSendTaskReq.setCurrentOperate(currentOperate);
        User user =new User();
        user.setUserErp("wuyoude");
        user.setUserName("吴有德");
        cancelSendTaskReq.setUser(user);

        InvokeResult<CancelSendTaskResp> invokeResult =jyNoTaskSendService.cancelSendTask(cancelSendTaskReq);
        log.info("==========result code===========:{}", invokeResult.getCode());
        log.info("==========result msg===========:{}", invokeResult.getMessage());
        log.info("==========result msg===========:{}", invokeResult.getData().toString());
    }
}
