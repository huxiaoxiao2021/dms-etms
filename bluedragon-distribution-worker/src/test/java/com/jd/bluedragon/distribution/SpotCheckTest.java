package com.jd.bluedragon.distribution;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.consumer.spotCheck.SpotCheckNotifyConsumer;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckDto;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckNotifyMQ;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckCurrencyService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
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

    @Autowired
    private SpotCheckNotifyConsumer spotCheckNotifyConsumer;

    @Test
    public void spotCheckDeal() {
        try {
            String text = "{\n" +
                    "\"barCode\":\"JDVF00001501950-1-3-\",\n" +
                    "\"spotCheckSourceFrom\":\"SPOT_CHECK_DWS\",\n" +
                    "\"weight\":50,\n" +
                    "\"length\":30,\n" +
                    "\"width\":10,\n" +
                    "\"height\":200,\n" +
                    "\n" +
                    "\"orgId\":6,\n" +
                    "\"orgName\":\"总公司\",\n" +
                    "\"siteCode\":10098,\n" +
                    "\"siteName\":\"北京双树直送第一车队-测试\",\n" +
                    "\"operateUserId\":10053,\n" +
                    "\"operateUserErp\":\"bjxings\",\n" +
                    "\"operateUserName\":\"邢松\",\n" +
                    "\"machineCode\":\"machineCode\",\n" +
                    "\"dimensionType\":0\n" +
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
//            InvokeResult<Boolean> result1 = spotCheckCurrencyService.spotCheckDeal(spotCheckDto);
//            spotCheckDto.setBarCode("JDV000706650345-2-3-");
//            InvokeResult<Boolean> result2 = spotCheckCurrencyService.spotCheckDeal(spotCheckDto);
//            spotCheckDto.setBarCode("JDV000706650345-3-3-");
//            InvokeResult<Boolean> result3 = spotCheckCurrencyService.spotCheckDeal(spotCheckDto);


            text = "{\n" +
                    "    \"confirmVolume\":\"10000.00\",\n" +
                    "    \"confirmWeight\":\"20.00\",\n" +
                    "    \"confirmWeightSource\":2,\n" +
                    "    \"convertCoefficient\":\"4800\",\n" +
                    "    \"customerCode\":\"10K10044\",\n" +
                    "    \"diffWeight\":\"30.00\",\n" +
                    "    \"dutyOrgCode\":\"39\",\n" +
                    "    \"dutyOrgName\":\"石景山营业部\",\n" +
                    "    \"dutyProvinceCompanyCode\":\"010S002\",\n" +
                    "    \"dutyRegion\":\"总公司\",\n" +
                    "    \"dutyRegionCode\":\"6\",\n" +
                    "    \"dutyStaffAccount\":\"bjych\",\n" +
                    "    \"dutyStaffName\":\"杨超Test\",\n" +
                    "    \"dutyStaffType\":1,\n" +
                    "    \"dutyType\":15,\n" +
                    "    \"exceedType\":1,\n" +
                    "    \"flowId\":\"DMS-MSI_JDVF00001511122_10098\",\n" +
                    "    \"flowSystem\":\"DMS-MSI\",\n" +
                    "    \"initiationLink\":\"1\",\n" +
                    "    \"orgCode\":\"10098\",\n" +
                    "    \"orgName\":\"北京双树直送第一车队-测试\",\n" +
                    "    \"packageNumber\":1,\n" +
                    "    \"reConfirmHigh\":\"90.0\",\n" +
                    "    \"reConfirmLong\":\"10.0\",\n" +
                    "    \"reConfirmVolume\":\"9000.0\",\n" +
                    "    \"reConfirmWeight\":\"50.0\",\n" +
                    "    \"reConfirmWidth\":\"10.0\",\n" +
                    "    \"standerDiff\":\"±5%\",\n" +
                    "    \"startRegion\":\"总公司\",\n" +
                    "    \"startRegionCode\":\"6\",\n" +
                    "    \"startStaffAccount\":\"bjxings\",\n" +
                    "    \"startStaffName\":\"刑松\",\n" +
                    "    \"startStaffType\":1,\n" +
                    "    \"startTime\":\"2022-01-18 10:28:31\",\n" +
                    "    \"status\":2,\n" +
                    "    \"statusUpdateTime\":\"2022-01-18 10:38:43\",\n" +
                    "    \"url\":[\n" +
                    "        \"http://test.storage.jd.com/dms-feedback/ce499e73-471c-491a-b07c-ee69b311712a.png?Expires=1957832658&AccessKey=a7ogJNbj3Ee9YM1O&Signature=XsaOGMnMPs8a2M2bwgdaunLvvFQ%3D\",\n" +
                    "        \"http://test.storage.jd.com/dms-feedback/6c424b2c-5816-4f61-b555-536543c0d125.jpg?Expires=1957832673&AccessKey=a7ogJNbj3Ee9YM1O&Signature=Wb7enskgeu5qGbThaXlva8gSE%2Fw%3D\"\n" +
                    "    ],\n" +
                    "    \"waybillCode\":\"JDVF00001511122\",\n" +
                    "    \"waybillSign\":\"30001000110909000000000000000000000030020002005000002000010000000000001000000015000100003010100000300000000010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\"\n" +
                    "}";
            Message message = new Message();
            message.setText(text);
            spotCheckNotifyConsumer.consume(message);
            Assert.assertTrue(true);
        }catch (Throwable e){
            logger.error("监听流程结果处理异常!", e);
            Assert.fail();
        }
    }
}
