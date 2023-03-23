package com.jd.bluedragon.distribution.integral.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.integral.constants.RankingRegionScopeEnum;
import com.jd.bluedragon.common.dto.integral.constants.RankingTimeRangeScopeEnum;
import com.jd.bluedragon.common.dto.integral.request.IntegralRankingRequest;
import com.jd.bluedragon.common.dto.integral.request.IntegralSummaryRequest;
import com.jd.bluedragon.common.dto.integral.response.*;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.integral.domain.IntegralProxy;
import com.jd.bluedragon.distribution.integral.service.IntegralService;
import com.jd.bluedragon.distribution.station.dao.UserSignRecordDao;
import com.jd.bluedragon.distribution.station.domain.UserSignRecord;
import com.jd.bluedragon.distribution.station.query.UserSignRecordQuery;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.etms.sdk.util.DateUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.tp.common.utils.Objects;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.jy.flat.base.Pager;
import com.jdl.jy.flat.dto.integral.JyIntegralDailyDTO;
import com.jdl.jy.flat.dto.integral.JyIntegralMonthlySummaryDTO;
import com.jdl.jy.flat.dto.personalIntegralStatistics.JyIntegralDTO;
import com.jdl.jy.flat.enums.JyIntegralQuotaEnum;
import com.jdl.jy.flat.enums.JyPositionTypeEnum;
import com.jdl.jy.flat.query.integral.IntegralRankingParam;
import com.jdl.jy.flat.query.integral.JyFlatIntegralRankingQuery;
import com.jdl.jy.flat.query.personalIntegralStatistics.JyIntegralQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.jdl.jy.flat.enums.JyIntegralQuotaEnum.*;

/**
 * @author liuluntao1
 * @description
 * @date 2022/12/19
 */
@Service
public class IntegralServiceImpl implements IntegralService {

    private static final Logger log = LoggerFactory.getLogger(IntegralServiceImpl.class);

    @Autowired
    IntegralProxy integralProxy;

    @Autowired
    UserSignRecordDao userSignRecordDao;

    @Autowired
    private BaseMajorManager baseMajorManager;

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
            List<JyIntegralDTO> totalIntegral = integralProxy.querySumByUserCode(jyIntegralQuery);
            if (CollectionUtils.isEmpty(totalIntegral)) {
                result.setIntegral(BigDecimal.ZERO);
            } else {
                result.setTotalIntegral(totalIntegral.get(Constants.Numbers.INTEGER_ZERO).getIntegral());
            }
            handlerLastSignTime(result, query.getUserCode(), query.getSiteCode());
            response.toSucceed();
            response.setData(result);
            return response;
        } catch (Exception e) {
            response.toError(e.getMessage());
            return response;
        }
    }

    private void handlerLastSignTime(JyIntegralDetailDTO result, String userErp, Long siteCode) {
        UserSignRecordQuery query = new UserSignRecordQuery();
        query.setUserCode(userErp);
        query.setSiteCode(siteCode.intValue());
        try {
            UserSignRecord userSignRecord = userSignRecordDao.queryLastSignRecord(query);
            if (Objects.nonNull(userSignRecord)) {
                result.setAttendanceTime(userSignRecord.getSignInTime().getTime());
            } else {
                throw new RuntimeException("未查询到最近签到记录:" + userErp + ":" + System.currentTimeMillis());
            }
        } catch (Exception e) {
            log.warn("IntegralServiceImpl.handlerLastSignTime:{}", e.getMessage(), e);
            result.setAttendanceTime(System.currentTimeMillis());
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
            result.setIntegral(BigDecimal.ZERO);
            result.setTotalScore(BigDecimal.ZERO);
            result.setAverageCoefficient(BigDecimal.ZERO);
        } else {
            result.setIntegral(overviews.get(Constants.Numbers.INTEGER_ZERO).getIntegral());
            result.setTotalScore(overviews.get(Constants.Numbers.INTEGER_ZERO).getTotalScore());
            result.setAverageCoefficient(overviews.get(Constants.Numbers.INTEGER_ZERO).getAverageCoefficient());
        }
        // 查询明细
        List<JyIntegralDTO> details = integralProxy.queryIntegralPersonalQuotaByCondition(integralQuery);
        // 合并明细
        makeJyIntegralDetail(result, details);
        handleRank(result);
        jdCResponse.setData(result);
        jdCResponse.toSucceed();
        return jdCResponse;
    }

    private void handleRank(JyIntegralDetailDTO result) {
        // 对BaseScore指标排序
        int j , k;
        // flag来记录最后交换的位置，也就是排序的尾边界
        int flag = result.getCalcuDetailDTOList().size();
        // 排序未结束标志
        while (flag > 0){
            // k 来记录遍历的尾边界
            k = flag;
            flag = 0;
            for(j = 1; j < k; j++){
                // 前面的大于后面的就交换
                if(result.getCalcuDetailDTOList().get(j-1).getQuotaNo().compareTo(result.getCalcuDetailDTOList().get(j).getQuotaNo()) > 0 ){
                    // 交换
                    JyBaseScoreCalcuDetailDTO baseTmp = new JyBaseScoreCalcuDetailDTO();
                    baseTmp = result.getCalcuDetailDTOList().get(j-1);
                    result.getCalcuDetailDTOList().set(j - 1, result.getCalcuDetailDTOList().get(j));
                    result.getCalcuDetailDTOList().set(j, baseTmp);
                    // 表示交换过数据;
                    // 记录最新的尾边界.
                    flag = j;
                }
            }
        }
    }

    private void makeJyIntegralDetail(JyIntegralDetailDTO result, List<JyIntegralDTO> details) {
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
                calcuDetailDTO.setScore(detail.getScore());
                calcuDetailDTO.setQuantity(detail.getQuantity());
                calcuDetailDTO.setPositionType(detail.getPositionType());
                calcuDetailDTO.setPositionTypeName(JyPositionTypeEnum.getName(detail.getPositionType()));
                calcuDetailDTO.setQuotaNo(detail.getQuotaNo());
                calcuDetailDTO.setQuotaName(getName(detail.getQuotaNo()));
                calcuDetailDTO.setAbbreviation(abbreviationMap.get(quotaEnum));
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
                // 如果是最大值上限
                if (rule.getLtValue().compareTo(BigDecimal.valueOf(Integer.MAX_VALUE / 2)) > 0
                        && baseScore.getQuantity().compareTo(dto.getLtValue()) > 0) {
                    baseScore.setToNextQuantity(BigDecimal.ZERO);
                    baseScore.setNextScore(rule.getScore());
                }
                // 如果在区间内算出差值
                if (baseScore.getQuantity().compareTo(dto.getLtValue()) <= 0
                        && baseScore.getQuantity().compareTo(dto.getGtValue()) > 0) {
                    baseScore.setToNextQuantity(dto.getLtValue().subtract(baseScore.getQuantity()));
                    if (dto.getLteSign().equals(Constants.CONSTANT_NUMBER_ONE) ||
                            baseScore.getQuantity().compareTo(dto.getLtValue()) == 0) {
                        baseScore.setToNextQuantity(BigDecimal.ONE);
                    }
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
                } else {
                    rootQuota = dtos.get(Constants.NUMBER_ZERO);
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
        String introduction = integralProxy.getJyIntegralIntroduction();
        dto.setContent(introduction);
        jdCResponse.setData(dto);
        jdCResponse.toSucceed();
        return jdCResponse;
    }

    @Override
    public JdCResponse<List<JyRuleDescriptionDTO>> queryQuotaDescriptionByCondition(JyIntegralDetailQuery request) {
        JdCResponse<List<JyRuleDescriptionDTO>> jdCResponse = new JdCResponse<>();
        List<JyRuleDescriptionDTO> result =new ArrayList<>();
        // 参数检验
        if (Objects.isNull(request.getQuotaNo())) {
            jdCResponse.toFail();
            jdCResponse.setMessage("参数错误");
        }
        // 构造查询条件
        JyIntegralQuery query = new JyIntegralQuery();
        try {
            BeanUtils.copyProperties(request, query);
            List<com.jdl.jy.flat.dto.personalIntegralStatistics.JyRuleDescriptionDTO> list = integralProxy.queryQuotaDescriptionByCondition(query);
            if (CollectionUtils.isNotEmpty(list)) {
                for (com.jdl.jy.flat.dto.personalIntegralStatistics.JyRuleDescriptionDTO descriptionDTO : list) {
                    JyRuleDescriptionDTO jyRuleDescriptionDTO = new JyRuleDescriptionDTO();
                    BeanUtils.copyProperties(descriptionDTO, jyRuleDescriptionDTO);
                    result.add(jyRuleDescriptionDTO);
                }
            }
            jdCResponse.setData(result);
            jdCResponse.toSucceed();
            return jdCResponse;
        } catch (Exception e) {
            log.error("IntegralService.queryQuotaDescriptionByCondition失败{}", JsonHelper.toJson(request), e);
        }
        jdCResponse.toFail();
        return jdCResponse;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IntegralService.integralRankingList",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<List<JyIntegralRankingDTO>> integralRankingList(IntegralRankingRequest req) {
        InvokeResult<List<JyIntegralRankingDTO>> invokeResult = new InvokeResult<>();
        if (req.getTimeRangeScope() == null || req.getRegionScope() == null) {
            invokeResult.parameterError("请选择查询范围！");
            return invokeResult;
        }
        if (!NumberHelper.gt0(req.getPageNumber())) {
            invokeResult.parameterError("缺少页码参数！");
            return invokeResult;
        }
        if (!NumberHelper.gt0(req.getPageSize())) {
            req.setPageSize(100);
        }
        // 设置日期、月份、年份默认参数等查询条件
        Pager<JyFlatIntegralRankingQuery> pager = setFlatQueryCondition(req);
        try {
            InvokeResult<List<com.jdl.jy.flat.dto.integral.JyIntegralRankingDTO>> flatResult = integralProxy.queryIntegralRanking(pager);
            if (!flatResult.codeSuccess()) {
                invokeResult.customMessage(flatResult.getCode(), flatResult.getMessage());
                return invokeResult;
            }
            if (CollectionUtils.isEmpty(flatResult.getData())) {
                return invokeResult;
            }
            List<JyIntegralRankingDTO> dtoList = makeRankingList(flatResult);
            invokeResult.setData(dtoList);
        }
        catch (Exception e) {
            log.error("查询积分排名异常. {}", JsonHelper.toJson(pager), e);
            invokeResult.error("查询积分排名异常！");
        }
        return invokeResult;
    }

    private Pager<JyFlatIntegralRankingQuery> setFlatQueryCondition(IntegralRankingRequest req) {
        Pager<JyFlatIntegralRankingQuery> pager = new Pager<>(req.getPageNumber(), req.getPageSize());
        pager.setSearchVo(setRankingQueryParam(req));

        return pager;
    }

    private JyFlatIntegralRankingQuery setRankingQueryParam(IntegralRankingRequest req) {
        JyFlatIntegralRankingQuery query = new JyFlatIntegralRankingQuery();
        query.setDimensionType(req.getTimeRangeScope().getType());
        query.setScopeType(req.getRegionScope().getType());
        if (RankingTimeRangeScopeEnum.RANGE_TODAY.equals(req.getTimeRangeScope())) {
            if (StringUtils.isBlank(req.getDate())) {
                req.setDate(DateHelper.formatDate(new Date()));
            }
            query.setDate(DateHelper.parseDate(req.getDate()));
        }
        if (RankingTimeRangeScopeEnum.RANGE_MONTH.equals(req.getTimeRangeScope())) {
            if (StringUtils.isBlank(req.getMonth())) {
                req.setMonth(DateHelper.formatDate(new Date(), DateHelper.DATE_FORMAT_MONTH));
            }
            query.setMonth(req.getMonth());
        }
        if (RankingTimeRangeScopeEnum.RANGE_YEAR.equals(req.getTimeRangeScope())) {
            if (StringUtils.isBlank(req.getYear())) {
                req.setYear(DateHelper.formatDate(new Date(), DateHelper.DATE_FORMAT_YEAR));
            }
            query.setYear(req.getYear());
        }
        if (RankingRegionScopeEnum.SCOPE_SITE.equals(req.getRegionScope())) {
            query.setSiteCode(req.getCurrentOperate().getSiteCode());
        }
        if (RankingRegionScopeEnum.SCOPE_ORG.equals(req.getRegionScope())) {
            query.setOrgCode(req.getCurrentOperate().getOrgId());
        }
        return query;
    }

    private List<JyIntegralRankingDTO> makeRankingList(InvokeResult<List<com.jdl.jy.flat.dto.integral.JyIntegralRankingDTO>> flatResult) {
        List<JyIntegralRankingDTO> dtoList = new ArrayList<>();
        for (com.jdl.jy.flat.dto.integral.JyIntegralRankingDTO flatDto : flatResult.getData()) {
            JyIntegralRankingDTO rankingDTO = new JyIntegralRankingDTO();
            rankingDTO.setUserCode(flatDto.getUserCode());
            rankingDTO.setUserName(flatDto.getUserName());
            rankingDTO.setSiteCode(flatDto.getSiteCode().intValue());
            BaseStaffSiteOrgDto operateSite = baseMajorManager.getBaseSiteBySiteId(rankingDTO.getSiteCode());
            rankingDTO.setSiteName(operateSite.getSiteName());
            rankingDTO.setRanking(flatDto.getRanking());
            rankingDTO.setIntegral(flatDto.getIntegral());
            rankingDTO.setIntegralUpdateTime(flatDto.getRankingUpdateTime());
            dtoList.add(rankingDTO);
        }
        return dtoList;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IntegralService.integralMonthlySummary",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<List<JyIntegralMonthlySummaryDto>> integralMonthlySummary(IntegralSummaryRequest req) {
        InvokeResult<List<JyIntegralMonthlySummaryDto>> invokeResult = new InvokeResult<>();
        if (req.getUser() == null || StringUtils.isBlank(req.getUser().getUserErp())) {
            invokeResult.parameterError("缺少参数：用户标识！");
            return invokeResult;
        }
        IntegralRankingParam param = new IntegralRankingParam();
        param.setUserCode(req.getUser().getUserErp());
        param.setPageNo(1);
        param.setPageSize(100);
        // 显示本月数据
        String curMonth = DateHelper.formatDate(new Date(), DateHelper.DATE_FORMAT_MONTH);
        param.setMonthSt("2023-01");
        param.setMonthEt(curMonth); // FIXME 查询最近几个月的积分
        InvokeResult<List<JyIntegralMonthlySummaryDTO>> flatResult = integralProxy.queryMonthlyIntegral(param);
        if (!flatResult.codeSuccess()) {
            invokeResult.customMessage(flatResult.getCode(), flatResult.getMessage());
            return invokeResult;
        }
        if (CollectionUtils.isEmpty(flatResult.getData())) {
            return invokeResult;
        }
        try {
            List<JyIntegralMonthlySummaryDto> summaryDtoList = setMonthlySummaryList(flatResult);
            invokeResult.setData(summaryDtoList);
        }
        catch (Exception e) {
            log.error("查询月度积分异常. {}", JsonHelper.toJson(req), e);
            invokeResult.error("查询月度积分异常！");
        }
        return invokeResult;
    }

    private static List<JyIntegralMonthlySummaryDto> setMonthlySummaryList(InvokeResult<List<JyIntegralMonthlySummaryDTO>> flatResult) {
        Map<String, List<JyIntegralMonthlySummaryDTO>> yearMap = flatResult.getData().stream().collect(Collectors.groupingBy(JyIntegralMonthlySummaryDTO::getYear));
        List<JyIntegralMonthlySummaryDto> summaryDtoList = new ArrayList<>();
        for (String year : yearMap.keySet()) {
            JyIntegralMonthlySummaryDto summaryDto = new JyIntegralMonthlySummaryDto();
            summaryDto.setYear(year);
            List<JyIntegralMonthlySummaryDto.MonthlyIntegral> monthlyIntegralList = new ArrayList<>();
            summaryDto.setMonthlyIntegralList(monthlyIntegralList);
            for (JyIntegralMonthlySummaryDTO innerDto : yearMap.get(year)) {
                JyIntegralMonthlySummaryDto.MonthlyIntegral integral = new JyIntegralMonthlySummaryDto.MonthlyIntegral();
                integral.setMonth(innerDto.getMonth());
                integral.setTotalIntegral(innerDto.getIntegral());
                monthlyIntegralList.add(integral);
            }
            summaryDtoList.add(summaryDto);
        }
        return summaryDtoList;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IntegralService.integralDailySummary",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<List<JyIntegralDailySummaryDto>> integralDailySummary(IntegralSummaryRequest req) {
        InvokeResult<List<JyIntegralDailySummaryDto>> invokeResult = new InvokeResult<>();
        if (req.getUser() == null || StringUtils.isBlank(req.getUser().getUserErp())) {
            invokeResult.parameterError("缺少参数：用户标识！");
            return invokeResult;
        }
        IntegralRankingParam param = new IntegralRankingParam();
        param.setUserCode(req.getUser().getUserErp());
        param.setPageNo(1);
        param.setPageSize(100);
        // 显示最近两个月数据
        param.setDateSt(DateHelper.preMonthFirstDay());
        param.setDateEt(DateHelper.getLastMonthDay());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.get(Calendar.MONTH);

        try {
            InvokeResult<List<JyIntegralDailyDTO>> flatResult = integralProxy.queryDailyIntegral(param);
            if (!flatResult.codeSuccess()) {
                invokeResult.customMessage(flatResult.getCode(), flatResult.getMessage());
                return invokeResult;
            }
            if (CollectionUtils.isEmpty(flatResult.getData())) {
                return invokeResult;
            }
            List<JyIntegralDailySummaryDto> summaryDtoList = getJyIntegralDailySummaryDtos(flatResult);
            invokeResult.setData(summaryDtoList);
        }
        catch (Exception e) {
            log.error("查询月度积分异常. {}", JsonHelper.toJson(req), e);
            invokeResult.error("查询月度积分异常！");
        }
        return invokeResult;
    }

    private static List<JyIntegralDailySummaryDto> getJyIntegralDailySummaryDtos(InvokeResult<List<JyIntegralDailyDTO>> flatResult) {
        List<JyIntegralDailySummaryDto> summaryDtoList = new ArrayList<>();
        Map<String, List<JyIntegralDailyDTO>> monthIntegralMap = flatResult.getData().stream()
                .collect(Collectors.groupingBy(item -> DateHelper.formatDate(item.getDate(), DateHelper.DATE_FORMAT_MONTH)));

        for (String month : monthIntegralMap.keySet()) {
            JyIntegralDailySummaryDto summaryDto = new JyIntegralDailySummaryDto();
            summaryDto.setMonth(month);
            summaryDto.setYear(StringUtils.split(summaryDto.getMonth(), DmsConstants.PACKAGE_SEPARATOR)[0]);
            List<JyIntegralDailySummaryDto.DailyIntegral> dailyIntegralList = new ArrayList<>();
            summaryDto.setDailyIntegralList(dailyIntegralList);
            for (JyIntegralDailyDTO dailyDTO : monthIntegralMap.get(month)) {
                JyIntegralDailySummaryDto.DailyIntegral dailyIntegral = new JyIntegralDailySummaryDto.DailyIntegral();
                dailyIntegral.setDate(DateHelper.formatDate(dailyDTO.getDate(), DateUtil.FORMAT_DATE_MM_DD));
                dailyIntegral.setTotalIntegral(dailyDTO.getIntegral());
                dailyIntegralList.add(dailyIntegral);
            }
            summaryDtoList.add(summaryDto);
        }
        return summaryDtoList;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IntegralService.personalIntegralRanking",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<JyPersonalIntegralRankingDto> personalIntegralRanking(IntegralRankingRequest req) {
        InvokeResult<JyPersonalIntegralRankingDto> invokeResult = new InvokeResult<>();
        if (req.getUser() == null || StringUtils.isBlank(req.getUser().getUserErp())) {
            invokeResult.parameterError("缺少参数：用户标识！");
            return invokeResult;
        }
        if (req.getRegionScope() == null || req.getTimeRangeScope() == null) {
            invokeResult.parameterError("缺少必要参数！");
            return invokeResult;
        }
        JyFlatIntegralRankingQuery query = setRankingQueryParam(req);
        query.setUserCode(req.getUser().getUserErp());

        try {
            InvokeResult<com.jdl.jy.flat.dto.integral.JyPersonalIntegralRankingDto> flatResult = integralProxy.integralGapBetweenPre(query);
            if (!flatResult.codeSuccess()) {
                invokeResult.customMessage(flatResult.getCode(), flatResult.getMessage());
                return invokeResult;
            }
            com.jdl.jy.flat.dto.integral.JyPersonalIntegralRankingDto rankingDto = flatResult.getData();
            if (rankingDto == null) {
                return invokeResult;
            }
            JyPersonalIntegralRankingDto resultDto = new JyPersonalIntegralRankingDto();
            resultDto.setRanking(rankingDto.getRanking());
            resultDto.setGapBetweenPre(rankingDto.getGapBetweenPre());
            resultDto.setTotalIntegral(rankingDto.getTotalIntegral());
            invokeResult.setData(resultDto);
        }
        catch (Exception e) {
            log.error("查询个人积分异常. {}", JsonHelper.toJson(req), e);
            invokeResult.error("查询个人积分异常！");
        }

        return invokeResult;
    }

}
