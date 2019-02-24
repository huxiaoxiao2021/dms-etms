package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.etms.waybill.domain.PackageState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: C2cInterceptHandler
 * @Description: C2C运单打印面单校验揽收完成
 * @author: tangcq
 * @date: 2018年12月25日17:38:01
 */
@Service("c2cInterceptHandler")
public class C2cInterceptHandler implements Handler<WaybillPrintContext, JdResult<String>> {
    private static final Log logger = LogFactory.getLog(C2cInterceptHandler.class);

    @Autowired
    WaybillTraceManager waybillTraceManager;

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        logger.info("C2cInterceptHandler-C2C运单打印面单校验揽收完成");
        InterceptResult<String> interceptResult = new InterceptResult<String>();
        interceptResult.toSuccess();
        if ((WaybillPrintOperateTypeEnum.PLATE_PRINT.getType().equals(context.getRequest().getOperateType())
                || WaybillPrintOperateTypeEnum.PACKAGE_AGAIN_PRINT.getType().equals(context.getRequest().getOperateType())
                || WaybillPrintOperateTypeEnum.SITE_PLATE_PRINT.getType().equals(context.getRequest().getOperateType()))
                && BusinessHelper.isC2c(context.getWaybill().getWaybillSign())) {
            //查询揽收完成（-640）全程跟踪结果
            List<PackageState> collectCompleteResult = waybillTraceManager.getPkStateByWCodeAndState(context.getWaybill().getWaybillCode(), Constants.WAYBILL_TRACE_STATE_COLLECT_COMPLETE);
            //揽收交接完成（-1300）全程跟踪结果
            List<PackageState> collectHandoverCompleteResult = waybillTraceManager.getPkStateByWCodeAndState(context.getWaybill().getWaybillCode(), Constants.WAYBILL_TRACE_STATE_BMZT_COLLECT_HANDOVER_COMPLETE);
            //没有揽收完成 或 没有交接完成 的全程跟踪，进行拦截提示，禁止打印
            if (collectCompleteResult.size() == 0 || collectHandoverCompleteResult.size() == 0) {
                interceptResult.toFail(InterceptResult.STATUS_NO_PASSED, WaybillPrintMessages.MESSAGE_NEED_RECEIVE);
                return interceptResult;
            }
        }
        return interceptResult;
    }
}
