package com.jd.bluedragon.distribution.weight;


import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.weight.request.WeightVolumeRequest;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.external.gateway.service.JyWeightVolumeGatewayService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author zs
 * @date 2023/2/14 16:18
 * @description 称重量方单元测试
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-web-context.xml")
public class JyWeightVolumeGatewayServiceTest {

    @Autowired
    private JyWeightVolumeGatewayService jyWeightVolumeGatewayService;

    @Test
    public void weightVolumeCheckAndDealTest() {
        WeightVolumeRequest request = new WeightVolumeRequest();
        request.setBarCode("JDL123456");
        request.setBusinessType(WeightVolumeBusinessTypeEnum.BY_WAYBILL.name());
        request.setSourceCode(FromSourceEnum.DMS_CLIENT_WEIGHT_VOLUME.name());
        request.setOperateSiteCode(1234);
        request.setOperateSiteName("宁德企配中心");
        request.setOperatorCode("zhangshuo147");
        request.setOperatorName("张硕");

        //运单
        request.setWeight(100D);
        request.setVolume(12D);

        //包裹
        request.setLength(100D);
        request.setWidth(100D);
        request.setHeight(100D);

        JdCResponse<Boolean> invokeResult = jyWeightVolumeGatewayService.weightVolumeCheckAndDeal(request);
        System.out.println("invokeResult=" + JSON.toJSONString(invokeResult));

    }
}
