package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.distribution.api.request.ReversePrintRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.message.OwnReverseTransferDomain;


/**
 * 逆向换单打印
 * Created by wangtingwei on 14-8-7.
 */
public interface ReversePrintService {
    /**
     * 处理打印数据
     * @param domain 打印提交数据
     * @return 处理是否成功
     */
    boolean handlePrint(ReversePrintRequest domain);

    /**
     * 获取换单后对应的新运单号
     * @param oldWaybillCode 原单号
     * @return
     */
    InvokeResult<String> getNewWaybillCode(String oldWaybillCode);

    /**
     * 执行逆向换单
     * @param domain
     * @return
     */
    InvokeResult<Boolean> exchangeOwnWaybill(OwnReverseTransferDomain domain);

    /**
     * 执行逆向换单前校验（拒收订单或异常订单才可以执行逆向换单）
     * @param wayBillCode
     * @param siteCode
     * @return
     */
    InvokeResult<Boolean> checkWayBillForExchange(String wayBillCode, Integer siteCode);
}
