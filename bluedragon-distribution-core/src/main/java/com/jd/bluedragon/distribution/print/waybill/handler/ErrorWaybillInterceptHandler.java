package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.waybill.service.WaybillCancelService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName:
 * @Description: 异常订单拦截不让操作换单打印【背景：事故带来的二十万订单不让操作换单打印】【waybillCancel type == 99】
 * @author:
 * @date: 2023-02-06
 */
@Service("errorWaybillInterceptHandler")
public class ErrorWaybillInterceptHandler extends NeedPrepareDataInterceptHandler<WaybillPrintContext,String> {
    private static final Logger log = LoggerFactory.getLogger(ErrorWaybillInterceptHandler.class);

    @Autowired
    private WaybillCancelService waybillCancelService;

    @Override
    void prepareData(WaybillPrintContext param) {

    }

    @Override
    InterceptResult<String> doHandler(WaybillPrintContext context) {
        CallerInfo callerInfo = Profiler.registerInfo("dms.web.errorWaybillInterceptHandler.doHandler",
                Constants.UMP_APP_NAME_DMSWEB, false, true);
        try {
            InterceptResult<String> interceptResult = context.getResult();

            String oldBarCode = context.getRequest().getOldBarCode();/* 获取输入旧单号 */
            String oldWaybillCode = WaybillUtil.getWaybillCode(oldBarCode);/* 获取旧运单号 */

            boolean bool = waybillCancelService.checkWaybillCancelInterceptType99(oldWaybillCode);
            if (bool) {
                interceptResult.toFail(InterceptResult.STATUS_NO_PASSED, HintService.getHint(HintCodeConstants.WAYBILL_ERROR_RE_PRINT));
                return interceptResult;
            }
            return interceptResult;
        } finally {
            Profiler.registerInfoEnd(callerInfo);
        }
    }
}
