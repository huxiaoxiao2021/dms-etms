package com.jd.bluedragon.distribution.integral.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.integral.request.IntegralRankingRequest;
import com.jd.bluedragon.common.dto.integral.request.IntegralSummaryRequest;
import com.jd.bluedragon.common.dto.integral.response.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

import java.util.List;

/**
 * @author liuluntao1
 * @description
 * @date 2022/12/19
 */
public interface IntegralService {

    /*
     * 查询个人积分总览
     */
    JdCResponse<JyIntegralDetailDTO> getSimpleJyIntegralInfo(JyIntegralDetailQuery query);

    /*
     * 查询基本分明细
     */
    JdCResponse<JyIntegralDetailDTO> getJyBaseScoreDetail(JyIntegralDetailQuery query);

    /*
     * 查询系数明细
     */
    JdCResponse<JyIntegralDetailDTO> getJyIntegralCoefficientDetail(JyIntegralDetailQuery query);

    /*
     * 获取规则介绍
     */
    JdCResponse<JyIntroductionDTO> getJyIntegralIntroduction();

    /*
     * 获取规则描述
     */
    JdCResponse<List<JyRuleDescriptionDTO>> queryQuotaDescriptionByCondition(JyIntegralDetailQuery query);

    /**
     * 积分排行榜
     * @param req
     * @return
     */
    InvokeResult<List<JyIntegralRankingDTO>> integralRankingList(IntegralRankingRequest req);

    /**
     * 每月积分汇总
     * @param req
     * @return
     */
    InvokeResult<List<JyIntegralMonthlySummaryDto>> integralMonthlySummary(IntegralSummaryRequest req);


    /**
     * 每日积分汇总
     * @param req
     * @return
     */
    InvokeResult<List<JyIntegralDailySummaryDto>> integralDailySummary(IntegralSummaryRequest req);

    /**
     * 个人积分排名
     * @param req
     * @return
     */
    InvokeResult<JyPersonalIntegralRankingDto> personalIntegralRanking(IntegralRankingRequest req);
}
