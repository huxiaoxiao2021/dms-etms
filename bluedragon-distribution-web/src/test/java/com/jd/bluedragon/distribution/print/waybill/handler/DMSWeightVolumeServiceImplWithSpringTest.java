package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeCondition;
import com.jd.bluedragon.distribution.weightVolume.service.DMSWeightVolumeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2021/1/26 20:16
 * @Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( {"classpath:distribution-web-context.xml"})
public class DMSWeightVolumeServiceImplWithSpringTest {
    @Autowired
    private DMSWeightVolumeService dmsWeightVolumeService;
    @Test
    public void testCheckBeforeUpload() throws Exception{
    	WeightVolumeCondition condition = new WeightVolumeCondition();
    	condition.setBarCode("JDVA00000000001");
        condition.setBusinessType("BY_PACKAGE");
        dmsWeightVolumeService.checkBeforeUpload(condition);
    }	
}