package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;

/**
 * 面单打印冗余的服务
 * Created by shipeilin on 2018/1/31.
 */
public interface WayBillPrintRedundanceService {

    /**
     * 获取打印数据
     * @param context 打印请求上下文
     * @return
     */
    InterceptResult<String> getWaybillPack(WaybillPrintContext context);
}
