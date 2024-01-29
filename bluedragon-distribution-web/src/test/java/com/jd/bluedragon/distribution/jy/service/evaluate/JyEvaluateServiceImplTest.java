package com.jd.bluedragon.distribution.jy.service.evaluate;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.request.EvaluateTargetReq;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.response.DimensionOption;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.response.EvaluateDimensionDto;
import com.jd.bluedragon.utils.JsonHelper;
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

    @Test
    public void testSendMQ() {
        String str = "{\n" +
                "\"sourceBizId\" : \"SC23112800038979\",\n" +
                "\"targetBizId\" : \"\",\n" +
                "\"status\" : 0,\n" +
                "\"dimensionList\" : [ {\n" +
                "\"dimensionCode\" : 800,\n" +
                "\"imgUrlList\" : [ \"https://s3-internal.cn-north-1.jdcloud-oss.com/dmsweb/dms-feedback/5b89106d-d582-4337-95ad-633204f41586.jpg?AWSAccessKeyId=JDC_D7F39DBD95C716540108BD9333F4&Expires=1735356204&Signature=tegpfm4ipBAfxXgL4LIRenPO3wo%3D\", \"https://s3-internal.cn-north-1.jdcloud-oss.com/dmsweb/dms-feedback/8fdc557e-bc9d-4001-9054-8ac445aeb953.jpg?AWSAccessKeyId=JDC_D7F39DBD95C716540108BD9333F4&Expires=1735356211&Signature=zKE3SY38k2QTkwtaXf80iV3KxzE%3D\" ]\n" +
                "}, {\n" +
                "\"dimensionCode\" : 600,\n" +
                "\"imgUrlList\" : [ \"https://s3-internal.cn-north-1.jdcloud-oss.com/dmsweb/dms-feedback/af54de26-3018-42c2-a309-9b9f644e514d.jpg?AWSAccessKeyId=JDC_D7F39DBD95C716540108BD9333F4&Expires=1735356183&Signature=tsGf9uHit78MttMVYV9sewom08k%3D\", \"https://s3-internal.cn-north-1.jdcloud-oss.com/dmsweb/dms-feedback/4f9e1164-e559-4a53-96c6-37cda3ac61b8.jpg?AWSAccessKeyId=JDC_D7F39DBD95C716540108BD9333F4&Expires=1735356190&Signature=WwYzty6nrg3cDzge8ERGo23cm6A%3D\" ]\n" +
                "}, {\n" +
                "\"dimensionCode\" : 700,\n" +
                "\"imgUrlList\" : [ \"https://s3-internal.cn-north-1.jdcloud-oss.com/dmsweb/dms-feedback/893515c0-c163-4d4d-b8a6-3bff856815d8.jpg?AWSAccessKeyId=JDC_D7F39DBD95C716540108BD9333F4&Expires=1735356197&Signature=lJ7M36dHIUoaXf4mbFNmiRyPJnc%3D\" ]\n" +
                "} ],\n" +
                "\"user\" : {\n" +
                "\"userCode\" : 17849,\n" +
                "\"userName\" : \"刘铎\",\n" +
                "\"userErp\" : \"liuduo8\"\n" +
                "},\n" +
                "\"currentOperate\" : {\n" +
                "\"siteCode\" : 910,\n" +
                "\"siteName\" : \"北京马驹桥分拣中心\",\n" +
                "\"operateTime\" : 1703820212110,\n" +
                "\"orgId\" : 6,\n" +
                "\"orgName\" : \"华北\",\n" +
                "\"dmsCode\" : \"020F002\",\n" +
                "\"operatorTypeCode\" : 1,\n" +
                "\"operatorId\" : \"65033\",\n" +
                "\"operatorData\" : {\n" +
                "\"operatorTypeCode\" : 1,\n" +
                "\"operatorId\" : \"65033\",\n" +
                "\"workGridKey\" : \"CDWG00000079001\",\n" +
                "\"workStationGridKey\" : \"CDGX00000178001\"\n" +
                "}\n" +
                "}\n" +
                "}";
        EvaluateTargetReq req = JsonHelper.fromJson(str, EvaluateTargetReq.class);
        jyEvaluateService.saveTargetEvaluate(req);
    }

}
