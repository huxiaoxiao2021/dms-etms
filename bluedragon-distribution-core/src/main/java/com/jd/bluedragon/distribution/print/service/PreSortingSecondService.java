package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.PrintWaybill;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;

/**
 * 纯外单中小件一单一件触发二次预分拣服务
 * Created by shipeilin on 2018/1/31.
 */
public interface PreSortingSecondService {

    /**
     * 一单一件且重新称重或量方的触发二次预分拣
     * @param context 请求上下文
     * @param commonWaybill 运单实体
     * @return 处理结果，处理是否通过
     */
    public InterceptResult<String> preSortingAgain(WaybillPrintContext context, PrintWaybill commonWaybill);

}
