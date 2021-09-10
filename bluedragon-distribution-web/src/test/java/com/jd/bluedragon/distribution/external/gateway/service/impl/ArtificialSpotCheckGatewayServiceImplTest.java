package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.spotcheck.ArtificialSpotCheckRequest;
import com.jd.bluedragon.external.gateway.service.ArtificialSpotCheckGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * 类的描述
 *
 * @author hujiping
 * @date 2021/9/8 10:55 上午
 */
@ContextConfiguration( {"classpath:bak/distribution-web-context-test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ArtificialSpotCheckGatewayServiceImplTest {

    private static final Logger logger = LoggerFactory.getLogger(ArtificialSpotCheckGatewayServiceImplTest.class);

    @Autowired
    private ArtificialSpotCheckGatewayService artificialSpotCheckGatewayService;

    @Test
    public void artificialCheckIsExcess() {
        try {
            String text = "{\n" +
                    "    \"barCode\":\"JDV000706598422-1-3-\",\n" +
                    "    \"excessStatus\":-1,\n" +
                    "    \"height\":10,\n" +
                    "    \"isWaybillSpotCheck\":false,\n" +
                    "    \"length\":10,\n" +
                    "    \"operateOrgId\":6,\n" +
                    "    \"operateOrgName\":\"总公司\",\n" +
                    "    \"operateSiteCode\":39,\n" +
                    "    \"operateSiteName\":\"石景山营业部\",\n" +
                    "    \"operateUserErp\":\"bjxings\",\n" +
                    "    \"operateUserId\":10053,\n" +
                    "    \"operateUserName\":\"刑松\",\n" +
                    "    \"pictureUrlsMap\":{\n" +
                    "\n" +
                    "    },\n" +
                    "    \"weight\":1.3,\n" +
                    "    \"width\":10\n" +
                    "}";
            ArtificialSpotCheckRequest artificialSpotCheckRequest = JsonHelper.fromJson(text, ArtificialSpotCheckRequest.class);
            // 超标校验
            JdCResponse<Integer> jdCResponse1 = artificialSpotCheckGatewayService.artificialCheckIsExcess(artificialSpotCheckRequest);
            Assert.assertTrue(true);
            JdCResponse<Integer> jdCResponse2 = artificialSpotCheckGatewayService.artificialCheckIsExcess(artificialSpotCheckRequest);
            Assert.assertTrue(true);
        }catch (Throwable e){
            logger.error("人工抽检校验异常!", e);
            Assert.fail();
        }
    }

    @Test
    public void artificialSubmitSpotCheckInfo() {
        try {
            String text = "{\n" +
                    "    \"barCode\":\"JDV000706607477-1-1-\",\n" +
                    "    \"excessStatus\":-1,\n" +
                    "    \"height\":10,\n" +
                    "    \"isWaybillSpotCheck\":false,\n" +
                    "    \"length\":10,\n" +
                    "    \"operateOrgId\":6,\n" +
                    "    \"operateOrgName\":\"总公司\",\n" +
                    "    \"operateSiteCode\":39,\n" +
                    "    \"operateSiteName\":\"石景山营业部\",\n" +
                    "    \"operateUserErp\":\"bjxings\",\n" +
                    "    \"operateUserId\":10053,\n" +
                    "    \"operateUserName\":\"刑松\",\n" +
                    "    \"pictureUrlsMap\":{\n" +
                    "\n" +
                    "    },\n" +
                    "    \"weight\":1.3,\n" +
                    "    \"width\":10\n" +
                    "}";
            ArtificialSpotCheckRequest artificialSpotCheckRequest = JsonHelper.fromJson(text, ArtificialSpotCheckRequest.class);
            // 超标提交
            JdCResponse<Void> jdCResponse = artificialSpotCheckGatewayService.artificialSubmitSpotCheckInfo(artificialSpotCheckRequest);
            Assert.assertTrue(true);
        }catch (Throwable e){
            logger.error("人工抽检提交超标数据异常!", e);
            Assert.fail();
        }
    }

}