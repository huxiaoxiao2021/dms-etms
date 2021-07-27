package com.jd.bluedragon.distribution.test;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weight.domain.PackWeightVO;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.SpotCheckSourceEnum;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckService;
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
 * @date 2021/5/25 9:37 下午
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class PackageWeightVolumeHandlerTest {

    private static final Logger logger = LoggerFactory.getLogger(PackageWeightVolumeHandlerTest.class);

    @Autowired
    private WeightAndVolumeCheckService weightAndVolumeCheckService;

    @Test
    public void dealSportCheck(){
        try {
            PackWeightVO packWeightVO = new PackWeightVO();
            packWeightVO.setHigh(11.3D);
            packWeightVO.setLength(20.3D);
            packWeightVO.setWidth(10.3D);
            packWeightVO.setErpCode("bjxings");
            packWeightVO.setOperatorSiteCode(39);
            packWeightVO.setOperatorSiteName("石景山营业部");
            packWeightVO.setOrganizationCode(6);
            packWeightVO.setOrganizationName("总公司");
            packWeightVO.setCodeStr("JDV000693632590-1-1-");
            weightAndVolumeCheckService.dealSportCheck(packWeightVO, SpotCheckSourceEnum.SPOT_CHECK_DWS);
            Assert.assertTrue(true);
        }catch (Exception e){
            logger.error("服务异常!", e);
            Assert.fail();
        }

    }

}
