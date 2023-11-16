package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.send.request.CreateAviationTaskReq;
import com.jd.bluedragon.common.dto.send.response.CreateAviationTaskResp;
import com.jd.bluedragon.distribution.jy.enums.JyFuncCodeEnum;
import com.jd.bluedragon.external.gateway.service.JyNoTaskSendGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class JyNoTaskSendGatewayServiceTest {

    @Autowired
    private JyNoTaskSendGatewayService jyNoTaskSendGatewayService;

    public static final CurrentOperate SITE_40240 = new CurrentOperate(40240, "北京通州分拣中心", new Date());
    public static final String POST = "GW00184002";
    public static final User USER_wuyoude = new User(17331,"吴有德");
    public static final String GROUP_CODE = "G00000059567";
    static {
        USER_wuyoude.setUserErp("wuyoude");
    }




    @Test
    public void listVehicleTaskTest(){

        CreateAviationTaskReq param = new CreateAviationTaskReq();
        param.setUser(USER_wuyoude);
        param.setCurrentOperate(SITE_40240);
        param.setPost(JyFuncCodeEnum.AVIATION_RAILWAY_SEND_SEAL_POSITION.getCode());
        param.setPositionCode(POST);
        param.setGroupCode(GROUP_CODE);
//        param.setAirType();
        param.setBookingWeight(100.0);
//        param.setConfirmCreate();
        param.setFlightNumber("FN001");
        param.setNextSiteId(910);
        param.setNextSiteName("马驹桥分拣测试");
        param.setTakeOffTimeStamp(System.currentTimeMillis());
        param.setTouchDownTimeStamp(System.currentTimeMillis() + 3600 * 1000 * 24);
        while (true) {

            JdCResponse<CreateAviationTaskResp>  response = jyNoTaskSendGatewayService.createAviationTask(param);
            JsonHelper.toJson(response);
            if(CreateAviationTaskResp.EXIST_SAME_DESTINATION_TASK_CODE.equals(response.getCode())) {
                param.setConfirmCreate(true);
                JdCResponse<CreateAviationTaskResp> res2 = jyNoTaskSendGatewayService.createAviationTask(param);
                JsonHelper.toJson(res2);
            }
            System.out.println("end");
        }
    }


}
