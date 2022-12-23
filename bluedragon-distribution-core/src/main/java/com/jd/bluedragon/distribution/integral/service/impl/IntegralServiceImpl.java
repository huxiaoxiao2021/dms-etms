package com.jd.bluedragon.distribution.integral.service.impl;

import com.jd.bluedragon.Constants;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.integral.response.*;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.integral.domain.IntegralProxy;
import com.jd.bluedragon.distribution.integral.service.IntegralService;
import com.jd.tp.common.utils.Objects;
import com.jdl.jy.flat.dto.personalIntegralStatistics.JyIntegralDTO;
import com.jdl.jy.flat.enums.JyIntegralQuotaEnum;
import com.jdl.jy.flat.enums.JyPositionTypeEnum;
import com.jdl.jy.flat.query.personalIntegralStatistics.JyIntegralQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jdl.jy.flat.enums.JyIntegralQuotaEnum.*;

/**
 * @author liuluntao1
 * @description
 * @date 2022/12/19
 */
@Service
public class IntegralServiceImpl implements IntegralService {

    @Autowired
    IntegralProxy integralProxy;

    /**
     * 获取今日积分和总积分
     * @param query
     * @return
     */
    @Override
    public JdCResponse<JyIntegralDetailDTO> getSimpleJyIntegralInfo(JyIntegralDetailQuery query) {
        JdCResponse<JyIntegralDetailDTO> response = new JdCResponse<>();
        JyIntegralDetailDTO result = new JyIntegralDetailDTO();
        if (Objects.isNull(query.getUserCode()) || Objects.isNull(query.getQueryDate())) {
            response.toError(Response.MESSAGE_WARN);
            return response;
        }
        try {
            // 处理成一天内的查询范围
            DateTime tmpTime = new DateTime(query.getQueryDate().getTime());
            DateTime startTime = new DateTime(tmpTime.getYear(),tmpTime.getMonthOfYear(), tmpTime.getDayOfMonth(),
                    Constants.Numbers.INTEGER_ZERO, Constants.Numbers.INTEGER_ZERO, Constants.Numbers.INTEGER_ZERO);
            DateTime endTime = startTime.plusDays(1).plusSeconds(-1);
            query.setStartDate(startTime.toDate());
            query.setEndDate(endTime.toDate());

            JyIntegralQuery jyIntegralQuery = new JyIntegralQuery();
            BeanUtils.copyProperties(query, jyIntegralQuery);

            List<JyIntegralDTO> dtos = integralProxy.queryIntegralPersonalByCondition(jyIntegralQuery);
            if (CollectionUtils.isEmpty(dtos)) {
                result.setIntegral(BigDecimal.ZERO);
            } else {
                result.setIntegral(dtos.get(Constants.Numbers.INTEGER_ZERO).getIntegral());
            }
            // 处理成总计的积分查询条件
            query.setQueryDate(null);
            query.setStartDate(null);
            query.setEndDate(null);
            BeanUtils.copyProperties(query, jyIntegralQuery);
            List<JyIntegralDTO> totalIntegral = integralProxy.queryIntegralPersonalByCondition(jyIntegralQuery);
            if (CollectionUtils.isEmpty(totalIntegral)) {
                result.setIntegral(BigDecimal.ZERO);
            } else {
                result.setTotalIntegral(totalIntegral.get(Constants.Numbers.INTEGER_ZERO).getIntegral());
            }
            response.toSucceed();
            response.setData(result);
            return response;
        } catch (Exception e) {
            response.toError(e.getMessage());
            return response;
        }
    }

    /**
     * 获取基础分详情
     * @param query
     * @return
     */
    @Override
    public JdCResponse<JyIntegralDetailDTO> getJyBaseScoreDetail(JyIntegralDetailQuery query) {
        JdCResponse<JyIntegralDetailDTO> jdCResponse = new JdCResponse<>();
        JyIntegralDetailDTO result = new JyIntegralDetailDTO();
        JyIntegralQuery integralQuery = new JyIntegralQuery();
        BeanUtils.copyProperties(query, integralQuery);
        // 查询出总览
        List<JyIntegralDTO> overviews = integralProxy.queryIntegralPersonalByCondition(integralQuery);
        if (CollectionUtils.isEmpty(overviews)) {
            result.setTotalIntegral(BigDecimal.ZERO);
            result.setTotalScore(BigDecimal.ZERO);
            result.setAverageCoefficient(BigDecimal.ZERO);
        } else {
            result.setTotalIntegral(overviews.get(Constants.Numbers.INTEGER_ZERO).getTotalIntegral());
            result.setTotalScore(overviews.get(Constants.Numbers.INTEGER_ZERO).getTotalScore());
            result.setAverageCoefficient(overviews.get(Constants.Numbers.INTEGER_ZERO).getAverageCoefficient());
        }
        // 查询明细
        List<JyIntegralDTO> details = integralProxy.queryIntegralPersonalQuotaByCondition(integralQuery);
        // 合并明细
        result = makeJyIntegralDetail(result, details);
        jdCResponse.setData(result);
        return jdCResponse;
    }

    private JyIntegralDetailDTO makeJyIntegralDetail(JyIntegralDetailDTO result, List<JyIntegralDTO> details) {
        // 基础分明细
        List<JyBaseScoreCalcuDetailDTO> baseScores = new ArrayList<>();
        // 系数明细
        List<JyIntegralCoefficientDetailDTO> coefficients = new ArrayList<>();
        // 指标Map 岗位，具体指标
        Map<Integer, List<JyQuotaCoefficientDetailDTO>> coefficientMap = new HashMap<>();
        for (JyIntegralDTO detail : details) {
            JyIntegralQuotaEnum quotaEnum = getFromCode(detail.getQuotaNo());
            if (PF_LIST.contains(quotaEnum)) {
                List<JyQuotaCoefficientDetailDTO> quotas = new ArrayList<>();
                if (coefficientMap.containsKey(detail.getPositionType())) {
                    quotas = coefficientMap.get(detail.getPositionType());
                } else {
                    coefficientMap.put(detail.getPositionType(), quotas);
                }
                JyQuotaCoefficientDetailDTO quotaCoefficient = new JyQuotaCoefficientDetailDTO();
                quotaCoefficient.setQuotaNo(detail.getQuotaNo());
                quotaCoefficient.setQuotaName(getName(detail.getQuotaNo()));
                quotaCoefficient.setCoefficient(detail.getCoefficient());
                quotas.add(quotaCoefficient);
                coefficientMap.put(detail.getPositionType(), quotas);
            }
            if (JC_LIST.contains(quotaEnum)) {
                // 岗位分别的基础分 业务确认按照指标来
                JyBaseScoreCalcuDetailDTO calcuDetailDTO = new JyBaseScoreCalcuDetailDTO();
                calcuDetailDTO.setQuantity(detail.getQuantity());
                calcuDetailDTO.setPositionType(detail.getPositionType());
                calcuDetailDTO.setPositionTypeName(JyPositionTypeEnum.getName(detail.getPositionType()));
                calcuDetailDTO.setQuotaNo(detail.getQuotaNo());
                calcuDetailDTO.setQuotaName(getName(detail.getQuotaNo()));
                baseScores.add(calcuDetailDTO);
            }
        }
        // 扁平化指标系数map
        for (Map.Entry<Integer, List<JyQuotaCoefficientDetailDTO>> entry : coefficientMap.entrySet()) {
            JyIntegralCoefficientDetailDTO detailDTO = new JyIntegralCoefficientDetailDTO();
            detailDTO.setPositionType(entry.getKey());
            detailDTO.setPositionTypeName(JyPositionTypeEnum.getName(entry.getKey()));
            detailDTO.setQuotaCoefficientDetailDTOS(entry.getValue());
            coefficients.add(detailDTO);
        }
        // 基础分规则装入
        baseScores = handleBaseScoreRule(result, baseScores);
        // 装入结果
        result.setCalcuDetailDTOList(baseScores);
        result.setIntegralCoefficientDetailDTOList(coefficients);
        return result;
    }


    private List<JyBaseScoreCalcuDetailDTO> handleBaseScoreRule(JyIntegralDetailDTO personal, List<JyBaseScoreCalcuDetailDTO> baseScores) {
        List<JyBaseScoreCalcuDetailDTO> ruleDTOList = new ArrayList<>();
        // 构造查询条件
        JyIntegralQuery query = new JyIntegralQuery();
        // 全国统一一套则只能使用quotaNo来查，分场地等使用其他查询条件
        for (JyBaseScoreCalcuDetailDTO baseScore : baseScores) {
            query.setQuotaNo(baseScore.getQuotaNo());
            // 操作量转分规则列表
            List<JyIntegralDTO> rules = integralProxy.queryFlatIntegralScoreRuleByCondition(query);
            List<JyBaseScoreRuleDTO> ruleDTOS = new ArrayList<>();
            // 最小的最大值
            BigDecimal upValue = BigDecimal.valueOf(Long.MAX_VALUE/2);
            BigDecimal upScore = BigDecimal.ZERO;
            for (JyIntegralDTO rule : rules) {
                JyBaseScoreRuleDTO dto = new JyBaseScoreRuleDTO();
                BeanUtils.copyProperties(rule, dto);
                // 差值计算
                // 如果是最大值
                if (rule.getLtValue().compareTo(BigDecimal.valueOf(Integer.MAX_VALUE / 2)) > 0
                        && baseScore.getQuantity().compareTo(dto.getGtValue()) > 0) {
                    baseScore.setToNextQuantity(BigDecimal.ZERO);
                    baseScore.setNextScore(rule.getScore());
                    continue;
                }
                // 如果在区间内算出差值
                if (baseScore.getQuantity().compareTo(dto.getLtValue()) <= 0
                        && baseScore.getQuantity().compareTo(dto.getGtValue()) > 0) {
                    baseScore.setToNextQuantity(dto.getLtValue().subtract(baseScore.getQuantity()));
                }
                // 下一阶段分数
                if (baseScore.getQuantity().compareTo(dto.getGtValue()) <= 0) {
                    if (dto.getGtValue().compareTo(upValue) < 0){
                        upValue = dto.getGtValue();
                        upScore = rule.getScore();
                        baseScore.setNextScore(upScore);
                    }
                }
                ruleDTOS.add(dto);
            }
            baseScore.setRuleDTOList(ruleDTOS);
            ruleDTOList.add(baseScore);
        }
        ruleDTOList = handleBaseScoreUnitName(ruleDTOList);
        return ruleDTOList;
    }

    private List<JyBaseScoreCalcuDetailDTO> handleBaseScoreUnitName(List<JyBaseScoreCalcuDetailDTO> ruleDTOList) {
        List<JyBaseScoreCalcuDetailDTO> result = new ArrayList<>();
        Map<String, String> cacheMap = new HashMap<>();
        for (JyBaseScoreCalcuDetailDTO dto : ruleDTOList) {
            if (cacheMap.containsKey(dto.getQuotaNo())) {
                dto.setUnitName(cacheMap.get(dto.getQuotaNo()));
            } else {
                JyIntegralQuery query = new JyIntegralQuery();
                query.setQuotaNo(dto.getQuotaNo());
                List<JyIntegralDTO> dtos = integralProxy.queryFlatIntegralQuotaByCondition(query);
                JyIntegralDTO rootQuota = new JyIntegralDTO();
                if (CollectionUtils.isEmpty(dtos)) {
                    dto.setUnitName(" ");
                    continue;
                }
                dto.setUnitName(rootQuota.getUnitName());
                cacheMap.put(dto.getQuotaNo(), dto.getUnitName());
            }
            result.add(dto);
        }
        return result;
    }

    @Override
    public JdCResponse<JyIntegralDetailDTO> getJyIntegralCoefficientDetail(JyIntegralDetailQuery query) {
        return null;
    }

    @Override
    public JdCResponse<JyIntroductionDTO> getJyIntegralIntroduction() {
        JdCResponse<JyIntroductionDTO> jdCResponse = new JdCResponse<>();
        JyIntroductionDTO dto = new JyIntroductionDTO();
        dto.setTitle("规则");
        dto.setContent("等待硕哥文案");
        jdCResponse.setData(dto);
        return jdCResponse;
    }

}
