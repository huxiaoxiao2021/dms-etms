package com.jd.bluedragon.distribution.jy.service.evaluate;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.request.EvaluateTargetReq;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.response.DimensionOption;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.response.EvaluateDimensionDto;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateRecordEntity;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateTargetInfoEntity;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateTargetInfoQuery;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
@Slf4j
public class JyEvaluateServiceImplTest {

    @Autowired
    private JyEvaluateService jyEvaluateService;

    @Test
    public void queryPageListTest() {
        JyEvaluateTargetInfoQuery query = new JyEvaluateTargetInfoQuery();
        query.setTargetStartTime(new Date(1677229837000L));
        query.setTargetEndTime(new Date(1677834637000L));
        query.setTargetAreaCode(6);
        query.setTargetSiteCode(910);
        query.setVehicleNumber("京·A8888");
        query.setEvaluateStartTime(new Date(1677229837000L));
        query.setEvaluateEndTime(new Date(1677834637000L));
        query.setSourceAreaCode(6);
        query.setSourceSiteCode(910);
        query.setHelperErp("wuyoude");
        query.setPageNumber(1);
        query.setPageSize(10);
        Result<List<JyEvaluateTargetInfoEntity>> result = jyEvaluateService.queryPageList(query);
        log.info("queryPageListTest result: {}", JsonHelper.toJson(result));
    }

    @Test
    public void queryCountTest() {
        JyEvaluateTargetInfoQuery query = new JyEvaluateTargetInfoQuery();
        query.setTargetStartTime(new Date(1677229837000L));
        query.setTargetEndTime(new Date(1677834637000L));
        query.setTargetAreaCode(6);
        query.setTargetSiteCode(910);
        query.setVehicleNumber("京·A8888");
        query.setEvaluateStartTime(new Date(1677229837000L));
        query.setEvaluateEndTime(new Date(1677834637000L));
        query.setSourceAreaCode(6);
        query.setSourceSiteCode(910);
        query.setHelperErp("wuyoude");
        query.setPageNumber(1);
        query.setPageSize(10);
        Result<Long> result = jyEvaluateService.queryCount(query);
        log.info("queryCountTest result: {}", JsonHelper.toJson(result));
    }

    @Test
    public void queryInfoByTargetBizIdTest() {
        String businessId = "SST23022400000075";
        Result<JyEvaluateTargetInfoEntity> result = jyEvaluateService.queryInfoByTargetBizId(businessId);
        log.info("queryInfoByTargetBizIdTest result: {}", JsonHelper.toJson(result));
    }

    @Test
    public void queryRecordByTargetBizIdTest() {
        String businessId = "SST23022400000075";
        Result<List<JyEvaluateRecordEntity>> result = jyEvaluateService.queryRecordByTargetBizId(businessId);
        log.info("queryRecordByTargetBizIdTest result: {}", JsonHelper.toJson(result));
    }

    @Test
    public void testDimensionOptions() {
        JdCResponse<List<DimensionOption>> result = jyEvaluateService.dimensionOptions();
        Assert.assertNotNull(result.getData());
    }

    @Test
    public void testCheckIsEvaluate() {
        EvaluateTargetReq evaluateTargetReq = new EvaluateTargetReq();
        evaluateTargetReq.setUser(createUser());
        evaluateTargetReq.setCurrentOperate(createCurrentOperate());
        evaluateTargetReq.setSourceBizId("SC22022347855110");

        JdCResponse<Boolean> result = jyEvaluateService.checkIsEvaluate(evaluateTargetReq);
        Assert.assertFalse(result.getData());
    }

    @Test
    public void testFindTargetEvaluateInfo() {
        EvaluateTargetReq evaluateTargetReq = new EvaluateTargetReq();
        evaluateTargetReq.setUser(createUser());
        evaluateTargetReq.setCurrentOperate(createCurrentOperate());
        evaluateTargetReq.setSourceBizId("SC22022347855110");

        JdCResponse<List<EvaluateDimensionDto>> result = jyEvaluateService.findTargetEvaluateInfo(evaluateTargetReq);
        Assert.assertNull(result.getData());
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
