package com.jd.bluedragon.distribution.jy;

import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.waybill.handler.InterceptSiteTypePrinrHandler;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/11/29 14:32
 * @Description:
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:distribution-web-context.xml"})
public class InterceptSiteTypeprintHandlerTest {

    @Autowired
    private InterceptSiteTypePrinrHandler interceptSiteTypePrinrHandler;

    @Test
    public void handlerTest(){

        WaybillPrintContext context = new WaybillPrintContext();

        InterceptResult<String> result = new InterceptResult<>();
        context.setResult(result);

        WaybillPrintRequest request = new WaybillPrintRequest();
        request.setOperateType(100103);
        request.setSiteCode(910);
        request.setUserERP("wuyoude");
        context.setRequest(request);

        interceptSiteTypePrinrHandler.handle(context);
    }
}
