package com.jd.bluedragon.distribution.weightVolume.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeCondition;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2021/1/26 20:16
 * @Description:
 */
@RunWith(MockitoJUnitRunner.class)
public class DMSWeightVolumeServiceImplTest {
    @Mock
    private DMSWeightVolumeService dmsWeightVolumeService;
    @Test
    public void testCheckBeforeUpload() throws Exception{
    	WeightVolumeCondition condition = new WeightVolumeCondition();
    	condition.setBarCode("JDVA00000000001");
        dmsWeightVolumeService.checkBeforeUpload(condition);
    }	
}