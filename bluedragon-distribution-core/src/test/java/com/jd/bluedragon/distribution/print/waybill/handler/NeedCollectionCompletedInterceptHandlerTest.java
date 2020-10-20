package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.weight.domain.PackWeightVO;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.SpotCheckSourceEnum;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.handler.BoxWeightVolumeHandler;
import com.jd.bluedragon.external.crossbow.economicNet.domain.EconomicNetErrorRes;
import com.jd.bluedragon.external.crossbow.economicNet.domain.EconomicNetResult;
import com.jd.etms.waybill.dto.PackageStateDto;
import com.jd.fastjson.JSON;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NeedCollectionCompletedInterceptHandlerTest {

    private static final Logger log = LoggerFactory.getLogger(NeedCollectionCompletedInterceptHandlerTest.class);
    @InjectMocks
    private NeedCollectionCompletedInterceptHandler needCollectionCompletedInterceptHandler;

    @Mock
    private WaybillTraceManager waybillTraceManager;

    @Before
    public void before(){

    }


    @Test
    public void handlerTest() throws Exception{
        //运单
        Waybill waybill = new Waybill();
        waybill.setWaybillSign("00000000000000000000000000000000000000000000" + "2");
        //上下文
        WaybillPrintContext context = new WaybillPrintContext();
        context.setWaybill(waybill);

        NeedCollectionCompletedInterceptHandler handler = needCollectionCompletedInterceptHandler;

        List<PackageStateDto> collectCompleteResult = mock(ArrayList.class);
        when(collectCompleteResult.size()).thenReturn(0);
        when(waybillTraceManager.getPkStateDtoByWCodeAndState(any(String.class),any(String.class))).thenReturn(collectCompleteResult);


        InterceptResult<String> testResult = needCollectionCompletedInterceptHandler.handle(context);

        log.info(JSON.toJSONString(testResult));
        Assert.assertSame(testResult.getStatus(),InterceptResult.STATUS_NO_PASSED);
    }

}
