package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.etms.waybill.dto.PackageStateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: NeedCollectionCompletedInterceptHandler
 * @Description: 对 揽收时采集商品信息运单 未揽收完成禁止打印限制
 * @author: zhangzhongkai
 * @date: 2018年12月25日17:38:01
 */
@Service("needCollectionCompletedInterceptHandler")
public class NeedCollectionCompletedInterceptHandler implements InterceptHandler<WaybillPrintContext, String> {
    private static final Logger log = LoggerFactory.getLogger(NeedCollectionCompletedInterceptHandler.class);

    @Autowired
    WaybillTraceManager waybillTraceManager;

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> interceptResult = new InterceptResult<String>();
        interceptResult.toSuccess();
        String waybillSign = context.getWaybill().getWaybillSign();
        if (BusinessHelper.isNeedCollectingWaybill(waybillSign)) {
            //查询揽收完成（-640）全程跟踪结果
            List<PackageStateDto> collectCompleteResult = waybillTraceManager.getPkStateDtoByWCodeAndState(
                    context.getWaybill().getWaybillCode(), Constants.WAYBILL_TRACE_STATE_COLLECT_COMPLETE);
            // 没有揽收完成 禁止打印
            if (collectCompleteResult.size() == 0) {
                interceptResult.toFail(InterceptResult.STATUS_NO_PASSED, WaybillPrintMessages.MESSAGE_NEED_COLLECT_FINISHED);
                return interceptResult;
            }
        }
        return interceptResult;
    }
}
