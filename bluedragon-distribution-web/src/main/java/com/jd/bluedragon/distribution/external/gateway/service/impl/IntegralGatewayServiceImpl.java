package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.integral.request.IntegralRankingRequest;
import com.jd.bluedragon.common.dto.integral.request.IntegralRequest;
import com.jd.bluedragon.common.dto.integral.request.IntegralSummaryRequest;
import com.jd.bluedragon.common.dto.integral.response.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.integral.service.IntegralService;
import com.jd.bluedragon.external.gateway.service.IntegralGatewayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liuluntao1
 * @description
 * @date 2022/12/23
 */
@Service("integralGatewayServiceImpl")
public class IntegralGatewayServiceImpl implements IntegralGatewayService {

    @Autowired
    IntegralService integralService;


    @JProfiler(jKey = "IntegralGatewayService.getSimpleJyIntegralInfo",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public JdCResponse<JyIntegralDetailDTO> getSimpleJyIntegralInfo(IntegralRequest request) {
        JyIntegralDetailQuery query = new JyIntegralDetailQuery();
        query.setUserCode(request.getUser().getUserErp());
        query.setSiteCode((long) request.getCurrentOperate().getSiteCode());
        query.setQueryDate(request.getQueryDate());
        return integralService.getSimpleJyIntegralInfo(query);
    }

    @JProfiler(jKey = "IntegralGatewayService.getJyBaseScoreDetail",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public JdCResponse<JyIntegralDetailDTO> getJyBaseScoreDetail(IntegralRequest request) {
        JyIntegralDetailQuery query = new JyIntegralDetailQuery();
        query.setUserCode(request.getUser().getUserErp());
        query.setQueryDate(request.getQueryDate());
        return integralService.getJyBaseScoreDetail(query);
    }

    @JProfiler(jKey = "IntegralGatewayService.getJyIntegralCoefficientDetail",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public JdCResponse<JyIntegralDetailDTO> getJyIntegralCoefficientDetail(IntegralRequest request) {
        JyIntegralDetailQuery query = new JyIntegralDetailQuery();
        query.setUserCode(request.getUser().getUserErp());
        query.setQueryDate(request.getQueryDate());
        return integralService.getJyBaseScoreDetail(query);
    }


    @JProfiler(jKey = "IntegralGatewayService.getJyIntegralIntroduction",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public JdCResponse<JyIntroductionDTO> getJyIntegralIntroduction(IntegralRequest request) {
        return integralService.getJyIntegralIntroduction();
    }

    @JProfiler(jKey = "IntegralGatewayService.queryQuotaDescriptionByCondition",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public JdCResponse<List<JyRuleDescriptionDTO>> queryQuotaDescriptionByCondition(IntegralRequest request) {
        JyIntegralDetailQuery query = new JyIntegralDetailQuery();
        BeanUtils.copyProperties(request, query);
        return integralService.queryQuotaDescriptionByCondition(query);
    }

    @Override
    @JProfiler(jKey = "IntegralGatewayService.integralRankingList",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<JyIntegralRankingDTO>> integralRankingList(IntegralRankingRequest req) {
        return retJdCResponse(integralService.integralRankingList(req));
    }

    @Override
    @JProfiler(jKey = "IntegralGatewayService.integralMonthlySummary",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<JyIntegralMonthlySummaryDto>> integralMonthlySummary(IntegralSummaryRequest req) {
        return retJdCResponse(integralService.integralMonthlySummary(req));
    }

    @Override
    @JProfiler(jKey = "IntegralGatewayService.integralDailySummary",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<JyIntegralDailySummaryDto>> integralDailySummary(IntegralSummaryRequest req) {
        return retJdCResponse(integralService.integralDailySummary(req));
    }

    @Override
    @JProfiler(jKey = "IntegralGatewayService.personalIntegralRanking",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<JyPersonalIntegralRankingDto> personalIntegralRanking(IntegralRankingRequest req) {
        return retJdCResponse(integralService.personalIntegralRanking(req));
    }

    private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
    }
}
