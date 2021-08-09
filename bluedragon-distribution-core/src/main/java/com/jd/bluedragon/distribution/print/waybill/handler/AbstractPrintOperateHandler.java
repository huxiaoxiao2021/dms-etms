package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;

import java.util.List;

/**
 * @ClassName: PlatePrintOperateHandler
 * @Description: 平台打印操作
 * @author: wuyoude
 * @date: 2018年2月6日 上午9:10:00
 */
public abstract class AbstractPrintOperateHandler implements InterceptHandler<WaybillPrintContext, String> {

    /**
     * 处理逻辑列表
     */
    private List<Handler<WaybillPrintContext, JdResult<String>>> handlers;
    /**
     * 处理打印结果,返回json数据
     * @param context
     */
    public abstract String dealPrintResult(WaybillPrintContext context);
    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.AbstractPrintOperateHandler.handle", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            InterceptResult<String> interceptResult = new InterceptResult<String>();
            interceptResult.toSuccess();
            if (handlers != null && !handlers.isEmpty()) {
                for (Handler<WaybillPrintContext, JdResult<String>> handler : handlers) {
                    //拦截类型的处理
                    if (handler instanceof InterceptHandler) {
                        InterceptResult<String> InterceptResult = (InterceptResult<String>) ((InterceptHandler) handler).handle(context);
                        if (InterceptResult != null && InterceptResult.isPassed()) {
                            if (InterceptResult.isWeakPassed()) {
                                context.appendMessage(InterceptResult.getMessage());
                                context.setStatus(InterceptResult.getStatus());
                            }
                        } else {
                            return InterceptResult;
                        }
                    } else {
                        JdResult<String> jdResult = handler.handle(context);
                        if (!jdResult.isSucceed()) {
                            context.setStatus(InterceptResult.STATUS_NO_PASSED);
                            interceptResult.toFail(jdResult.getMessageCode(), jdResult.getMessage());
                            return interceptResult;
                        }
                    }
                }
            }
            interceptResult.setData(dealPrintResult(context));
            if (InterceptResult.STATUS_WEAK_PASSED.equals(context.getStatus())) {
                interceptResult.setStatus(context.getStatus());
                interceptResult.setMessage(context.getMessages().get(0));
                interceptResult.setCode(JdResult.CODE_SUC);
            }
            return interceptResult;
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }
    /**
     * @return the handlers
     */
    public List<Handler<WaybillPrintContext, JdResult<String>>> getHandlers() {
        return handlers;
    }

    /**
     * @param handlers the handlers to set
     */
    public void setHandlers(
            List<Handler<WaybillPrintContext, JdResult<String>>> handlers) {
        this.handlers = handlers;
    }
}
