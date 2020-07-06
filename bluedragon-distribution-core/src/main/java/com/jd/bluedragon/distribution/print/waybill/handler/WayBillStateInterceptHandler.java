package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.WayBillFinishedEnum;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.etms.waybill.domain.PackageState;
import com.jd.etms.waybill.dto.PackageStateDto;
import com.jd.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ClassName: C2cInterceptHandler
 * @Description: C2C运单打印面单校验揽收完成
 * @author: tangcq
 * @date: 2018年12月25日17:38:01
 */
@Service("wayBillStateInterceptHandler")
public class WayBillStateInterceptHandler implements Handler<WaybillPrintContext, JdResult<String>> {
    private static final Logger log = LoggerFactory.getLogger(WayBillStateInterceptHandler.class);

    @Autowired
    WaybillTraceManager waybillTraceManager;

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        log.info("WayBillStateInterceptHandler-获取运单是否是终节点状态", JSON.toJSONString(context));
        InterceptResult<String> interceptResult = new InterceptResult<String>();
        interceptResult.toSuccess();
        if (WaybillPrintOperateTypeEnum.PACKAGE_AGAIN_PRINT.getType().equals(context.getRequest().getOperateType()))
                 {
            List<PackageState> collectCompleteResult = waybillTraceManager.getAllOperationsByOpeCodeAndState(context.getWaybill().getWaybillCode(),WayBillFinishedEnum.WAYBILLSTATES);
            //判断该运单是否是终结点
            if (CollectionUtils.isNotEmpty(collectCompleteResult)) {
                String  message=String.format(WaybillPrintMessages.MESSAGE_WAYBILL_FINISHED.getMsgFormat(),collectCompleteResult.get(0).getStateName());
                interceptResult.toWeakSuccess(WaybillPrintMessages.MESSAGE_WAYBILL_FINISHED.getMsgCode(),message);
                return interceptResult;
            }
        }
        return interceptResult;
    }
}
