package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.etms.waybill.dto.PackageStateDto;
import com.jd.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
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
import java.util.Date;
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



    @Test
    public void handlerTest() throws Exception{
        //运单
        Waybill waybill = new Waybill();
        waybill.setWaybillSign("00000000000000000000000000000000000000000000" + "2");
        //请求体
        WaybillPrintRequest request = new WaybillPrintRequest();
        request.setOperateType(WaybillPrintOperateTypeEnum.PLATE_PRINT.getType());
        //上下文
        WaybillPrintContext context = new WaybillPrintContext();
        context.setWaybill(waybill);
        context.setRequest(request);



        List<PackageStateDto> collectCompleteResult = mock(ArrayList.class);
        when(collectCompleteResult.size()).thenReturn(1);
        when(waybillTraceManager.getPkStateDtoByWCodeAndState(any(String.class),any(String.class))).thenReturn(collectCompleteResult);


        InterceptResult<String> testResult = needCollectionCompletedInterceptHandler.handle(context);

        log.info(JSON.toJSONString(testResult));
        Assert.assertSame(testResult.getStatus(),InterceptResult.STATUS_NO_PASSED);
    }

}
