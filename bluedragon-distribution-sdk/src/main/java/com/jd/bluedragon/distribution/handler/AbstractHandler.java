package com.jd.bluedragon.distribution.handler;

import com.jd.bluedragon.distribution.handler.Handler;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @author: 刘铎（liuduo8）
 * @date: 2024/4/17
 * @description:
 */
public abstract class AbstractHandler<T,R> implements Handler<T,R> {

    /**
     * 是否跳过执行，当返回true时跳过此handler
     *
     * @param target
     * @return
     */
    @Override
    public Boolean isSkip(T target) {
        return Boolean.FALSE;
    }
}
