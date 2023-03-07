package com.jd.bluedragon.distribution.test.print;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.waybill.handler.ClientUncollectedPackageInterceptHandler;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.distribution.testCore.base.EntityUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientUncollectedPackageInterceptHandlerTest {

    @Mock
    private BaseMajorManager baseMajorManager;
    @Mock
    private WaybillTraceManager waybillTraceManager;

    @InjectMocks
    private ClientUncollectedPackageInterceptHandler handler;

    @Test
    public void handleTest() {
        WaybillPrintContext context = EntityUtil.getInstance(WaybillPrintContext.class);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 60; i++) {
            if (i == 52) {
                sb.append(2);
                continue;
            }
            sb.append(0);
        }
        context.getRequest().setOperateType(100310);
        context.getBigWaybillDto().getWaybill().setWaybillSign(sb.toString());
        Set<Integer> set = new HashSet<>();
        set.add(-640);
        set.add(-1300);
        BaseStaffSiteOrgDto dto = new BaseStaffSiteOrgDto();
        dto.setSiteType(Constants.BASE_SITE_TYPE_THIRD);
        when(baseMajorManager.getBaseStaffByStaffId(context.getRequest().getUserCode())).thenReturn(dto);
        when(waybillTraceManager.getAllOperationsByOpeCodeAndState(context.getRequest().getBarCode(), set)).thenReturn(new ArrayList<>());
        InterceptResult result = handler.handle(context);
        System.out.println(JsonHelper.toJson(result));
    }
}
