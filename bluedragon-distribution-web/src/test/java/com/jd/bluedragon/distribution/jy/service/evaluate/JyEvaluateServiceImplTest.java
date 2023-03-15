package com.jd.bluedragon.distribution.jy.service.evaluate;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.request.EvaluateTargetReq;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.response.DimensionOption;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.response.EvaluateDimensionDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
@Slf4j
public class JyEvaluateServiceImplTest {

    @Autowired
    private JyEvaluateService jyEvaluateService;

    @Test
    public void testDimensionOptions() {
        List<DimensionOption> result = jyEvaluateService.dimensionOptions();
        Assert.assertNotNull(result);
    }

    @Test
    public void testCheckIsEvaluate() {
        EvaluateTargetReq evaluateTargetReq = new EvaluateTargetReq();
        evaluateTargetReq.setUser(createUser());
        evaluateTargetReq.setCurrentOperate(createCurrentOperate());
        evaluateTargetReq.setSourceBizId("SC22022347855110");

        Boolean result = jyEvaluateService.checkIsEvaluate(evaluateTargetReq);
        Assert.assertFalse(result);
    }

    @Test
    public void testFindTargetEvaluateInfo() {
        EvaluateTargetReq evaluateTargetReq = new EvaluateTargetReq();
        evaluateTargetReq.setUser(createUser());
        evaluateTargetReq.setCurrentOperate(createCurrentOperate());
        evaluateTargetReq.setSourceBizId("SC22022347855110");

        List<EvaluateDimensionDto> result = jyEvaluateService.findTargetEvaluateInfo(evaluateTargetReq);
        Assert.assertNull(result);
    }

    private User createUser() {
        User user = new User();
        user.setUserErp("xumigen");
        user.setUserName("徐迷根");
        user.setUserCode(18225);
        return user;
    }

    private CurrentOperate createCurrentOperate() {
        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(10186);
        currentOperate.setSiteName("北京凉水河快运中心");
        currentOperate.setDmsCode("010K001");
        currentOperate.setOrgId(6);
        currentOperate.setOrgName("总公司");
        return currentOperate;
    }

}
