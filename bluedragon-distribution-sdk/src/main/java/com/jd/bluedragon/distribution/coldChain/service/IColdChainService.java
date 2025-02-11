package com.jd.bluedragon.distribution.coldChain.service;


import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.coldChain.domain.*;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;

import java.util.List;

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
    @Deprecated
    InvokeResult<ColdCheckCommonResult> sendCheck(SendCheckVO vo);

    /**
     * 冷链发货
     * @param vo
     * @return
     */
    @Deprecated
    InvokeResult<Boolean> send(SendVO vo);

    /**
     * 冷链-快运发货校验
     * @param vo
     * @return
     */
    @Deprecated
    InvokeResult<ColdCheckCommonResult> sendOfKYCheck(SendOfKYCheckVO vo);

    /**
     * 冷链-快运发货
     * @param vo
     * @return
     */
    @Deprecated
    InvokeResult<Boolean> sendOfKY(SendOfKYVO vo);

    /**
     * 检查批次号是否已封车
     * @param vo
     * @return
     */
    @Deprecated
    InvokeResult<Boolean> checkSendCodeOfSeal(SendVO vo);

    /**
     * 冷链-发货并验货接口
     * @param vo
     * @return
     */
    InvokeResult<Boolean> sendAndInspectionOfPack(SendInspectionVO vo);
    /**
     * 冷链验货
     * @param vo
     * @return
     */
    InvokeResult<String> inspectionOfColdNew(ColdInspectionVo vo);

    /**
     * 冷链新发货
     * 一单单
     * @param request
     * @return
     */
    InvokeResult<ColdSendResult> sendOfColdBusinessNew(ColdSendVo request);
    /**
     * 冷链新发货
     * 一单单
     * @param request
     * @return
     */
    InvokeResult<ColdSendResult> sendOfColdKYNew(ColdSendVo request);

    /**
     * 检查批次号是否发车
     * @param request 发送请求
     * @return 发送结果
     */
    InvokeResult<ColdSendResult> checkSendCodeStatus(SendCheckVO request);

    /**
     * 获取运输计划
     * @param request 请求对象
     * @return 返回运输计划结果列表
     */
    InvokeResult<List<TransPlanResult>> getTransPlan(SendCheckVO request);
}
