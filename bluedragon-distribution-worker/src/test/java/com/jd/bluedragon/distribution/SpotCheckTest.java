package com.jd.bluedragon.distribution;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckDto;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckCurrencyService;
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
 * @date 2021/9/7 6:18 下午
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class SpotCheckTest {

    private static final Logger logger = LoggerFactory.getLogger(SpotCheckTest.class);

    @Autowired
    private SpotCheckCurrencyService spotCheckCurrencyService;

    @Test
    public void spotCheckDeal() {
        try {
            String text = "{\n" +
                    "    \"barCode\":\"JDV000706650345-1-3-\",\n" +
                    "    \"spotCheckSourceFrom\":\"SPOT_CHECK_DWS\",\n" +
                    "    \"spotCheckBusinessType\":1,\n" +
                    "    \"weight\":1.3,\n" +
                    "    \"length\":10.1,\n" +
                    "    \"width\":10.1,\n" +
                    "    \"height\":10.1,\n" +
                    "    \"orgId\":6,\n" +
                    "    \"orgName\":\"总公司\",\n" +
                    "    \"siteCode\":39,\n" +
                    "    \"siteName\":\"石景山营业部\",\n" +
                    "    \"operateUserId\":10053,\n" +
                    "    \"operateUserErp\":\"bjxings\",\n" +
                    "    \"operateUserName\":\"邢松\",\n" +
                    "    \"dimensionType\":0\n" +
                    "}";
            SpotCheckDto spotCheckDto = JsonHelper.fromJson(text, SpotCheckDto.class);

            // 超标校验
//            InvokeResult<Integer> checkResult1 = spotCheckCurrencyService.checkIsExcess(spotCheckDto);
//            spotCheckDto.setBarCode("JDV000705034549-2-3-");
//            InvokeResult<Integer> checkResult2 = spotCheckCurrencyService.checkIsExcess(spotCheckDto);
//            spotCheckDto.setBarCode("JDV000705034549-3-3-");
//            InvokeResult<Integer> checkResult3 = spotCheckCurrencyService.checkIsExcess(spotCheckDto);
//            Assert.assertTrue(true);
//
//
//            // 超标处理
            InvokeResult<Boolean> result1 = spotCheckCurrencyService.spotCheckDeal(spotCheckDto);
            spotCheckDto.setBarCode("JDV000706650345-2-3-");
            InvokeResult<Boolean> result2 = spotCheckCurrencyService.spotCheckDeal(spotCheckDto);
            spotCheckDto.setBarCode("JDV000706650345-3-3-");
            InvokeResult<Boolean> result3 = spotCheckCurrencyService.spotCheckDeal(spotCheckDto);
            Assert.assertTrue(true);

        }catch (Throwable e){
            logger.error("监听流程结果处理异常!", e);
            Assert.fail();
        }
    }
}
