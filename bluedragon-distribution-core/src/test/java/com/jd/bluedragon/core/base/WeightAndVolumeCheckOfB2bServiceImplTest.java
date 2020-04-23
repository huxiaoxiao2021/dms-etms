package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.weightAndVolumeCheck.WaybillFlowDetail;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.impl.WeightAndVolumeCheckOfB2bServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2019/10/31 12:13
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-core-context-test.xml")
public class WeightAndVolumeCheckOfB2bServiceImplTest {

    @Autowired
    private WeightAndVolumeCheckOfB2bServiceImpl service;

    @Test
    public void getFirstWeightAndVolumeDetail(){
        try {
            String waybillCode = "JDVA00049150879";
            WaybillFlowDetail weightAndVolumeDetail = service.getFirstWeightAndVolumeDetail(waybillCode);

        }catch (Exception e){
            Assert.assertTrue(e.getMessage(),false);
        }
    }
}
