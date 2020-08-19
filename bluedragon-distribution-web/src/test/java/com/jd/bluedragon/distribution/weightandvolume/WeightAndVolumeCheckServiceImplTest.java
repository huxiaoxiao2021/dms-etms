package com.jd.bluedragon.distribution.weightandvolume;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weight.domain.PackWeightVO;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.SpotCheckSourceEnum;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckService;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author lijie
 * @date 2019/11/18 11:19
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( {"classpath:distribution-web-context-test.xml"})
public class WeightAndVolumeCheckServiceImplTest {

    @Autowired
    private WeightAndVolumeCheckService weightAndVolumeCheckService;

    @Test
    public void testInsertAndSendMq(){
        PackWeightVO packWeightVO = new PackWeightVO();
        packWeightVO.setHigh(19.2);
        packWeightVO.setLength(28.9);
        packWeightVO.setWidth(20.3);
        packWeightVO.setWeight(1.13);
        packWeightVO.setCodeStr("JDVB01759364382-1-1-");
        packWeightVO.setOperatorSiteCode(11111);
        packWeightVO.setOperatorSiteName("北京亚一分拣中心");
        packWeightVO.setOrganizationCode(22222);
        packWeightVO.setOrganizationName("武汉亚一分拣中心");
        packWeightVO.setErpCode("lijie123");
        WeightVolumeCollectDto weightVolumeCollectDto = new WeightVolumeCollectDto();
        InvokeResult<Boolean> result = new InvokeResult<>();
        weightAndVolumeCheckService.dealSportCheck(packWeightVO, SpotCheckSourceEnum.SPOT_CHECK_DWS,result);
    }
}
