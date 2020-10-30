package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptResult;

public abstract class NeedPrepareDataInterceptHandler <T,R>  implements Handler<T, InterceptResult<R>> {

    /**
     * 准备需要的数据,
     * 建议：把处理链中公用数据在此处处理
     * @param param
     */
    abstract void prepareData(T param);

    /**
     * 实际处理逻辑
     * @param param
     * @return
     */
    abstract InterceptResult<R> doHandler(T param);

    @Override
    public InterceptResult<R> handle(T param) {
        this.prepareData(param);
        return this.doHandler(param);
    }
}
