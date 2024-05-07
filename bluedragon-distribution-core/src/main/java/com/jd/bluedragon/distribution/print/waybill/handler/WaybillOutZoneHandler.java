package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.AbstractHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 超区拦截
 * tips：超区预分拣给的解释是 疫情超区或者春节禁售。
 */
@Service("waybillOutZoneHandler")
public class WaybillOutZoneHandler extends AbstractHandler<WaybillPrintContext,JdResult<String>> {
    private static final Log logger = LogFactory.getLog(WaybillOutZoneHandler.class);

    @Autowired
    private WaybillService waybillService;

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> result = new InterceptResult<String>();
        result.toSuccess();

        if(waybillService.isOutZoneControl(context.getBigWaybillDto().getWaybill())){
            result.toFail(JdResponse.CODE_OUT_ZONE_ERROR, JdResponse.MESSAGE_OUT_ZONE_ERROR);
            return result;
        }
        return result;
    }


}
