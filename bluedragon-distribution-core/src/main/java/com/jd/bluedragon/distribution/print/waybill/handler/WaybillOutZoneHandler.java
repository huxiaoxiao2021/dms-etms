package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.IotServiceWSManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 超区拦截
 * tips：超区预分拣给的解释是 疫情超区或者春节禁售。
 */
@Service("waybillOutZoneHandler")
public class WaybillOutZoneHandler implements Handler<WaybillPrintContext,JdResult<String>> {
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
