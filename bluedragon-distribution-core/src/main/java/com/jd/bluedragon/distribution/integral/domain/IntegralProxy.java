package com.jd.bluedragon.distribution.integral.domain;


import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.jy.flat.api.PersonalIntegralStatistics.IJyPersonalIntegralStatisticsJSFService;
import com.jdl.jy.flat.api.integral.IJyFlatIntegralRankingJSFService;
import com.jdl.jy.flat.base.Pager;
import com.jdl.jy.flat.base.ServiceResult;
import com.jdl.jy.flat.dto.integral.JyIntegralDailyDTO;
import com.jdl.jy.flat.dto.integral.JyIntegralMonthlySummaryDTO;
import com.jdl.jy.flat.dto.integral.JyIntegralRankingDTO;
import com.jdl.jy.flat.dto.integral.JyPersonalIntegralRankingDto;
import com.jdl.jy.flat.dto.personalIntegralStatistics.JyIntegralDTO;
import com.jdl.jy.flat.dto.personalIntegralStatistics.JyRuleDescriptionDTO;
import com.jdl.jy.flat.query.integral.IntegralRankingParam;
import com.jdl.jy.flat.query.integral.JyFlatIntegralRankingQuery;
import com.jdl.jy.flat.query.personalIntegralStatistics.JyIntegralQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liuluntao1
 * @description
 * @date 2022/12/19
 */
@Service
public class IntegralProxy {

    private static final Logger log = LoggerFactory.getLogger(IntegralProxy.class);

    @Autowired
    IJyPersonalIntegralStatisticsJSFService personalIntegralJSFService;

    @Autowired
    private IJyFlatIntegralRankingJSFService rankingJSFService;

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IntegralProxy.querySumByUserCode",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<JyIntegralDTO> querySumByUserCode(JyIntegralQuery query){
        ServiceResult<List<JyIntegralDTO>> serviceResult = personalIntegralJSFService.querySumByUserCode(query);
        if (serviceResult.getSuccess()) {
            return serviceResult.getData();
        } else {
            throw new RuntimeException();
        }
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IntegralProxy.queryIntegralPersonalByCondition",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<JyIntegralDTO> queryIntegralPersonalByCondition(JyIntegralQuery query){
        ServiceResult<List<JyIntegralDTO>> serviceResult = personalIntegralJSFService.queryIntegralPersonalByCondition(query);
        if (serviceResult.getSuccess()) {
            return serviceResult.getData();
        } else {
            throw new RuntimeException();
        }
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IntegralProxy.queryIntegralPersonalQuotaByCondition",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<JyIntegralDTO> queryIntegralPersonalQuotaByCondition(JyIntegralQuery query){
        ServiceResult<List<JyIntegralDTO>> serviceResult = personalIntegralJSFService.queryIntegralPersonalQuotaByCondition(query);
        if (serviceResult.getSuccess()) {
            return serviceResult.getData();
        } else {
            throw new RuntimeException();
        }
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IntegralProxy.queryFlatIntegralScoreRuleByCondition",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<JyIntegralDTO> queryFlatIntegralScoreRuleByCondition(JyIntegralQuery query){
        ServiceResult<List<JyIntegralDTO>> serviceResult = personalIntegralJSFService.queryFlatIntegralScoreRuleByCondition(query);
        if (serviceResult.getSuccess()) {
            return serviceResult.getData();
        } else {
            throw new RuntimeException();
        }
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IntegralProxy.queryFlatIntegralQuotaByCondition",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<JyIntegralDTO> queryFlatIntegralQuotaByCondition(JyIntegralQuery query){
        ServiceResult<List<JyIntegralDTO>> serviceResult = personalIntegralJSFService.queryFlatIntegralQuotaByCondition(query);
        if (serviceResult.getSuccess()) {
            return serviceResult.getData();
        } else {
            throw new RuntimeException();
        }
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IntegralProxy.queryQuotaDescriptionByCondition",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<JyRuleDescriptionDTO> queryQuotaDescriptionByCondition(JyIntegralQuery query){
        ServiceResult<List<JyRuleDescriptionDTO>> serviceResult = personalIntegralJSFService.queryQuotaDescriptionByCondition(query);
        if (serviceResult.getSuccess()) {
            return serviceResult.getData();
        } else {
            throw new RuntimeException();
        }
    }


    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IntegralProxy.getJyIntegralIntroduction",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public String getJyIntegralIntroduction(){
        ServiceResult<String> serviceResult = personalIntegralJSFService.getJyIntegralIntroduction();
        if (serviceResult.getSuccess()) {
            return serviceResult.getData();
        } else {
            throw new RuntimeException();
        }
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IntegralProxy.queryIntegralRanking",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<List<JyIntegralRankingDTO>> queryIntegralRanking(Pager<JyFlatIntegralRankingQuery> pager) {
        InvokeResult<List<JyIntegralRankingDTO>> result = new InvokeResult<>();

        try {
            ServiceResult<List<JyIntegralRankingDTO>> serviceResult = rankingJSFService.queryIntegralRanking(pager);
            if (serviceResult.retFail()) {
                result.customMessage(serviceResult.getCode(), serviceResult.getMessage());
                return result;
            }
            result.setData(serviceResult.getData());
        }
        catch (Exception e) {
            log.error("查询积分排名异常. {}", JsonHelper.toJson(pager), e);
            result.error("查询积分排名异常！");
        }

        return result;
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IntegralProxy.queryMonthlyIntegral",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<List<JyIntegralMonthlySummaryDTO>> queryMonthlyIntegral(IntegralRankingParam param) {
        InvokeResult<List<JyIntegralMonthlySummaryDTO>> result = new InvokeResult<>();

        try {
            ServiceResult<List<JyIntegralMonthlySummaryDTO>> serviceResult = rankingJSFService.queryMonthlyIntegral(param);
            if (serviceResult.retFail()) {
                result.customMessage(serviceResult.getCode(), serviceResult.getMessage());
                return result;
            }
            result.setData(serviceResult.getData());
        }
        catch (Exception e) {
            log.error("查询月度积分汇总数据异常. {}", JsonHelper.toJson(param), e);
            result.error("查询月度积分汇总数据异常！");
        }

        return result;
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IntegralProxy.queryDailyIntegral",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<List<JyIntegralDailyDTO>> queryDailyIntegral(IntegralRankingParam param) {
        InvokeResult<List<JyIntegralDailyDTO>> result = new InvokeResult<>();

        try {
            ServiceResult<List<JyIntegralDailyDTO>> serviceResult = rankingJSFService.queryDailyIntegral(param);
            if (serviceResult.retFail()) {
                result.customMessage(serviceResult.getCode(), serviceResult.getMessage());
                return result;
            }
            result.setData(serviceResult.getData());
        }
        catch (Exception e) {
            log.error("查询日维度积分汇总数据异常. {}", JsonHelper.toJson(param), e);
            result.error("查询日维度积分汇总数据异常！");
        }

        return result;
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IntegralProxy.integralGapBetweenPre",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<JyPersonalIntegralRankingDto> integralGapBetweenPre(JyFlatIntegralRankingQuery query) {
        InvokeResult<JyPersonalIntegralRankingDto> result = new InvokeResult<>();

        try {
            ServiceResult<JyPersonalIntegralRankingDto> serviceResult = rankingJSFService.integralGapBetweenPre(query);
            if (serviceResult.retFail()) {
                result.customMessage(serviceResult.getCode(), serviceResult.getMessage());
                return result;
            }
            result.setData(serviceResult.getData());
        }
        catch (Exception e) {
            log.error("查询个人积分和上一名的差距异常. {}", JsonHelper.toJson(query), e);
            result.error("查询个人积分和上一名的差距异常！");
        }

        return result;
    }

}
