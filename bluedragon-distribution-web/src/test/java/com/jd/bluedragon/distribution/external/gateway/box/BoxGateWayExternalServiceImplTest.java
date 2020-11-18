package com.jd.bluedragon.distribution.external.gateway.box;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.external.gateway.base.GateWayBaseResponse;
import com.jd.bluedragon.external.gateway.box.BoxGateWayExternalService;
import com.jd.bluedragon.external.gateway.dto.request.BoxGenerateRequest;
import com.jd.bluedragon.external.gateway.dto.response.BoxDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2020/4/20 18:59
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:distribution-web-context.xml" })
public class BoxGateWayExternalServiceImplTest {

    @Autowired
    private BoxGateWayExternalService boxGateWayExternalService;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void generateBoxCodes() {
        try {
            BoxGenerateRequest request = new BoxGenerateRequest();
            request.setBoxType("BC");
            request.setStartSiteCode("910");
            request.setEndSiteCode("39");
            request.setNum(3);
            request.setOperatorId(910);
            request.setOperatorName("刑松");
            request.setOperatorUnitName("北京通州分拣中心");
            request.setTenantCode(Constants.TENANT_CODE_ECONOMIC);
            String pin = "";
            GateWayBaseResponse<BoxDto> result = boxGateWayExternalService.generateBoxCodes(request, pin);
            String boxCode = result.getData().getBoxCodes().get(0);
        }catch (Exception e){
            Assert.assertTrue(Boolean.FALSE);
        }
    }

    @Test
    public void pushBoxCodeTest() throws Exception {
        Method pushBoxCode = boxGateWayExternalService.getClass().getDeclaredMethod("pushBoxCode", BoxDto.class, BoxGenerateRequest.class);
        pushBoxCode.setAccessible(true);

        BoxDto boxDto = new BoxDto();
        boxDto.setBoxCodes(Arrays.asList("BC202011040001","BC202011040002","BC202011040003","BC202011040004"));

        BoxGenerateRequest boxResponse = new BoxGenerateRequest();

        boxResponse.setStartSiteCode("1001");
        boxResponse.setEndSiteCode("1002");

        pushBoxCode.invoke(boxGateWayExternalService, boxDto, boxResponse);


    }
}