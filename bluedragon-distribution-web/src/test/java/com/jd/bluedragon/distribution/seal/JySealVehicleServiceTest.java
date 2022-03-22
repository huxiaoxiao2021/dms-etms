package com.jd.bluedragon.distribution.seal;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealVehicleTaskResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.seal.service.IJySealVehicleService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jdl.jy.realtime.model.query.seal.SealVehicleTaskQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @ClassName JySealVehicleServiceTest
 * @Description
 * @Author wyh
 * @Date 2022/3/16 14:15
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:bak/distribution-web-context-test.xml"})
public class JySealVehicleServiceTest {

    @Autowired
    private IJySealVehicleService jySealVehicleService;

    private static User user;

    private static CurrentOperate currentOperate;

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
    public void fetchSealTaskTest() {
        SealVehicleTaskRequest request = new SealVehicleTaskRequest();
        request.setPageNumber(1);
        request.setPageSize(10);
        request.setUser(user);
        request.setCurrentOperate(currentOperate);
        request.setEndSiteCode(10186);
        request.setVehicleStatus(1);  // 待解封
        request.setBarCode("");
        request.setLineType(0);
        request.setFetchType(SealVehicleTaskQuery.FETCH_TYPE_REFRESH);
        request.setSealCarCode("");

        InvokeResult<SealVehicleTaskResponse> result = jySealVehicleService.fetchSealTask(request);
        System.out.println(JsonHelper.toJson(result));
    }

}
