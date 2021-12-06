package com.jd.bluedragon.distribution.print.waybill.handler.complete;

import com.jd.bluedragon.distribution.command.JdCommand;
import com.jd.bluedragon.distribution.command.handler.AbstractCommandHandlerMapping;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.request.PrintCompleteRequest;

/**
 * @author wyh
 * @className PrintCompleteOperateHandlerMapping
 * @description
 * @date 2021/12/2 14:00
 **/
public class PrintCompleteOperateHandlerMapping<T, R> extends AbstractCommandHandlerMapping<PrintCompleteRequest, T, R> {

    /**
     * 处理逻辑单元
     *
     * @param k
     */
    @Override
    public Handler<T, R> getHandler(JdCommand<PrintCompleteRequest> k) {
        return this.handlerMap.get(k.getOperateType());
    }
}
