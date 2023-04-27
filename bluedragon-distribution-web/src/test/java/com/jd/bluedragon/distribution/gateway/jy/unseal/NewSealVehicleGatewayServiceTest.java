package com.jd.bluedragon.distribution.gateway.jy.unseal;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealTaskInfoRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealTaskInfo;
import com.jd.bluedragon.external.gateway.service.JySealVehicleGatewayService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 解封车单测
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-03-07 10:19:35 周二
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:bak/distribution-web-context-test.xml"})
public class NewSealVehicleGatewayServiceTest {

    private static User user;

    private static CurrentOperate currentOperate;

    @Autowired
    private JySealVehicleGatewayService jySealVehicleGatewayService;

    static {
        user = new User();
        user.setUserCode(1);
        user.setUserName("徐迷根");
        user.setUserErp("xumigen");

        currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(10186);
        currentOperate.setSiteName("北京凉水河快运中心");
        currentOperate.setOrgId(6);
        currentOperate.setOrgName("总公司");
    }

    @Test
    public void getUnSealTaskInfoTest(){
        SealTaskInfoRequest request = new SealTaskInfoRequest();
        request.setBizId("SC23022800028276");
        request.setQueryRankOrder(true);
        request.setSealCarCode("SC23022800028276");
        request.setUser(user);
        request.setCurrentOperate(currentOperate);
        final JdCResponse<SealTaskInfo> result = jySealVehicleGatewayService.getUnSealTaskInfo(request);
        System.out.println(result);
    }
}
