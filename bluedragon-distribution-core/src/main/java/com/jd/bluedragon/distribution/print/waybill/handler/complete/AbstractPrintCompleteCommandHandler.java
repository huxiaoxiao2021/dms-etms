package com.jd.bluedragon.distribution.print.waybill.handler.complete;

import com.jd.bluedragon.distribution.command.JdCommand;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.command.JdResults;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.request.PrintCompleteRequest;

/**
 * @author wyh
 * @className AbstractPrintCompleteCommandHandler
 * @description
 * @date 2021/12/2 13:55
 **/
public abstract class AbstractPrintCompleteCommandHandler<T, R> implements Handler<JdCommand<PrintCompleteRequest>, JdResult<Boolean>> {

    /**
     * 逻辑单元映射关系
     */
    private PrintCompleteOperateHandlerMapping<T, JdResult<Boolean>> operateHandlerMapping;

    /**
     *
     * @param target
     * @return
     */
    public abstract T initContext(JdCommand<PrintCompleteRequest> target);

    /**
     * 执行处理，返回处理结果
     *
     * @param target
     * @return
     */
    @Override
    public JdResult<Boolean> handle(JdCommand<PrintCompleteRequest> target) {
        Handler<T, JdResult<Boolean>> handler = operateHandlerMapping.getHandler(target);

        // 返回无服务信息
        if (handler == null) {
            return JdResults.REST_FAIL_SERVER_NOT_FIND;
        }

        T target0 = initContext(target);

        // 返回参数错误信息
        if (target0 == null) {
            return JdResults.REST_FAIL_PARAM_ERROR;
        }

        return handler.handle(target0);
    }

    public PrintCompleteOperateHandlerMapping<T, JdResult<Boolean>> getOperateHandlerMapping() {
        return operateHandlerMapping;
    }

    public void setOperateHandlerMapping(PrintCompleteOperateHandlerMapping<T, JdResult<Boolean>> operateHandlerMapping) {
        this.operateHandlerMapping = operateHandlerMapping;
    }
}
