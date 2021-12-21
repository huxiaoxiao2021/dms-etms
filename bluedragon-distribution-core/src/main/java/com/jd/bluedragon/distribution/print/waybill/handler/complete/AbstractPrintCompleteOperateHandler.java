package com.jd.bluedragon.distribution.print.waybill.handler.complete;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @author wyh
 * @className AbstractPrintCompleteOperateHandler
 * @description
 * @date 2021/12/2 14:56
 **/
public abstract class AbstractPrintCompleteOperateHandler implements InterceptHandler<WaybillPrintCompleteContext, Boolean> {

    /**
     * 处理逻辑列表
     */
    private List<Handler<WaybillPrintCompleteContext, JdResult<Boolean>>> handlers;

    /**
     * 执行处理，返回处理结果
     *
     * @param context
     * @return
     */
    @Override
    public InterceptResult<Boolean> handle(WaybillPrintCompleteContext context) {
        InterceptResult<Boolean> result = new InterceptResult<>();
        result.toSuccess();

        if (CollectionUtils.isEmpty(handlers)) {
            return result;
        }

        for (Handler<WaybillPrintCompleteContext, JdResult<Boolean>> handler : handlers) {

            JdResult<Boolean> jdResult = handler.handle(context);
            if (jdResult!= null && !jdResult.isSucceed()) {
                result.toFail(jdResult.getMessageCode(), jdResult.getMessage());
                return result;
            }
        }

        return result;
    }

    public List<Handler<WaybillPrintCompleteContext, JdResult<Boolean>>> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<Handler<WaybillPrintCompleteContext, JdResult<Boolean>>> handlers) {
        this.handlers = handlers;
    }
}
