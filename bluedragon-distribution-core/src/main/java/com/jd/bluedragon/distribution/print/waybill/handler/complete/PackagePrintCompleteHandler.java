package com.jd.bluedragon.distribution.print.waybill.handler.complete;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.command.JdCommand;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.request.PrintCompleteRequest;

/**
 * @author wyh
 * @className PackagePrintCompleteHandler
 * @description
 * @date 2021/12/2 14:23
 **/
public class PackagePrintCompleteHandler extends AbstractPrintCompleteCommandHandler<WaybillPrintCompleteContext, InvokeResult<Boolean>> {

    /**
     * @param target
     * @return
     */
    @Override
    public WaybillPrintCompleteContext initContext(JdCommand<PrintCompleteRequest> target) {
        WaybillPrintCompleteContext context = new WaybillPrintCompleteContext();
        context.setSystemCode(target.getSystemCode());
        context.setProgramType(target.getProgramType());
        context.setBusinessType(target.getBusinessType());
        context.setOperateType(target.getOperateType());

        context.setRequest(target.getData());

        //初始化请求信息和返回结果信息
        InterceptResult<Boolean> result = new InterceptResult<>();
        result.toSuccess();

        context.setResult(result);

        return context;
    }
}
