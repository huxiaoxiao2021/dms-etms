package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.common.domain.RepeatPrint;
import com.jd.bluedragon.distribution.api.request.ReversePrintRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.message.OwnReverseTransferDomain;
import com.jd.bluedragon.distribution.reverse.domain.ExchangeWaybillDto;
import com.jd.bluedragon.distribution.reverse.domain.TwiceExchangeRequest;
import com.jd.bluedragon.distribution.reverse.domain.TwiceExchangeResponse;


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
     * @param isPickUpFinished 是否限制取件完成
     * @return
     */
    InvokeResult<String> getNewWaybillCode(String oldWaybillCode, boolean isPickUpFinished);

    /**
     * 获取换单后对应的新运单号和取件单的创建时间是否超过15天
     * @param oldWaybillCode 原单号
     * @param isPickUpFinished 是否限制取件完成
     * @return
     */
    InvokeResult<RepeatPrint> getNewWaybillCode1(String oldWaybillCode, boolean isPickUpFinished);

    /**
     * 执行逆向换单(自营异步接口)
     * @param domain
     * @return
     */
    InvokeResult<Boolean> exchangeOwnWaybill(OwnReverseTransferDomain domain);

    /**
     * 执行逆向换单(自营同步接口,直接返回新单)
     * @param domain 换单请求参数
     * @return 新单号
     */
    InvokeResult<String> exchangeOwnWaybillSync(OwnReverseTransferDomain domain);

    /**
     * 执行逆向换单前校验（拒收订单或异常订单才可以执行逆向换单）
     * @param wayBillCode
     * @param siteCode
     * @return
     */
    InvokeResult<Boolean> checkWayBillForExchange(String wayBillCode, Integer siteCode);

    /**
     * 执行逆向换单前校验（专为德邦提供,不校验异常处理）
     * @param wayBillCode
     * @param siteCode
     * @return
     */
    InvokeResult<Boolean> checkWayBillForDpkExchange(String wayBillCode, Integer siteCode);

    /**
     * 获取二次换单信息
     * @param twiceExchangeRequest
     * @return
     */
    JdResult<TwiceExchangeResponse> getTwiceExchangeInfo(TwiceExchangeRequest twiceExchangeRequest);

    /**
     * 换单成功后操作
     * @param request
     * @return
     */
    InvokeResult exchangeSuccessAfter(ExchangeWaybillDto request);

    /**
     * 保存二次换单地址信息
     * @param request
     */
    void saveReturnAddressInfo(ExchangeWaybillDto request);
}
