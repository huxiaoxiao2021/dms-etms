package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.integral.request.IntegralRankingRequest;
import com.jd.bluedragon.common.dto.integral.request.IntegralRequest;
import com.jd.bluedragon.common.dto.integral.request.IntegralSummaryRequest;
import com.jd.bluedragon.common.dto.integral.response.*;
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
        return null;
    }

    @Override
    @JProfiler(jKey = "IntegralGatewayService.integralSummary",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<IntegralSummaryDto>> integralSummary(IntegralSummaryRequest req) {
        return null;
    }

    @Override
    @JProfiler(jKey = "IntegralGatewayService.personalIntegralRanking",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<JyPersonalIntegralRankingDto> personalIntegralRanking(IntegralRankingRequest req) {
        return null;
    }
}
