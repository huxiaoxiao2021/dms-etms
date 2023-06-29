package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.print.domain.ExchangeWaybillRequest;

/**
 * 拦截换单
 */
public interface WaybillInterceptReverseService {

    /**
     * 根据原单号获取新单号
     * @param request 请求信息
     * @return 新单号
     */
    InvokeResult<String> exchangeNewWaybill(ExchangeWaybillRequest request);

}
