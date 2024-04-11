package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.consumable.request.WaybillConsumablePackConfirmReq;
import com.jd.bluedragon.common.dto.weight.request.AppWeightVolumeCondition;
import com.jd.bluedragon.common.dto.weight.request.AppWeightVolumeRuleCheckDto;
import com.jd.bluedragon.common.dto.weight.request.AppWeightVolumeUploadResult;
import com.jd.bluedragon.external.gateway.service.WeightAndVolumeGatewayService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @program: ql-dms-distribution
 * @description:
 * @author: caozhixing3
 * @date: 2024-03-26 21:00
 **/
@ContextConfiguration(locations = {"classpath:distribution-web-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class WeightAndVolumeGatewayServiceImplTest {

    @Autowired
    private WeightAndVolumeGatewayService weightAndVolumeGatewayService;

    @Test
    public void testWeightVolumeRuleCheck() {
        for (int i = 0; i<10; i++) {
            try{
                AppWeightVolumeRuleCheckDto request = new AppWeightVolumeRuleCheckDto();
                request.setBarCode("");
                request.setCheckWeight(true);
                request.setCheckVolume(true);
                JdCResponse<Boolean> res = weightAndVolumeGatewayService.weightVolumeRuleCheck(request);
                System.out.println(res.getCode() + res.getMessage());
            }catch (Exception e) {
                System.out.println("--error-----------------");
            }
        }
    }

    @Test
    public void testCheckAndUpload() {
        for (int i = 0; i<10; i++) {
            try{
                AppWeightVolumeCondition request = new AppWeightVolumeCondition();
                JdCResponse<AppWeightVolumeUploadResult> res = weightAndVolumeGatewayService.checkAndUpload(request);
                System.out.println(res.getCode() + res.getMessage());
            }catch (Exception e) {
                System.out.println("--error-----------------");
            }
        }
    }
}
