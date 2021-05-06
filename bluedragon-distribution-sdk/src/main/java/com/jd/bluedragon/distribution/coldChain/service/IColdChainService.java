package com.jd.bluedragon.distribution.coldChain.service;


import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.coldChain.domain.*;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * 冷链对外服务（提供给gateway工程使用，不允许外部系统直接调用）
 * @Auther: 刘铎（liuduo8）
 * @Date: 2021/5/2
 * @Description:
 */
public interface IColdChainService {

    /**
     * 冷链验货校验
     * @param vo
     * @return
     */
    InvokeResult<InspectionCheckResult> inspectionCheck(InspectionCheckVO vo);

    /**
     * 冷链验货
     * @param vo
     * @return
     */
    InvokeResult<Boolean> inspection(InspectionVO vo);

    /**
     * 冷链发货校验
     * @param vo
     * @return
     */
    InvokeResult<ColdCheckCommonResult> sendCheck(SendCheckVO vo);

    /**
     * 冷链发货
     * @param vo
     * @return
     */
    InvokeResult<Boolean> send(SendVO vo);

    /**
     * 冷链-快运发货校验
     * @param vo
     * @return
     */
    InvokeResult<ColdCheckCommonResult> sendOfKYCheck(SendOfKYCheckVO vo);

    /**
     * 冷链-快运发货
     * @param vo
     * @return
     */
    InvokeResult<Boolean> sendOfKY(SendOfKYVO vo);

}
