package com.jd.bluedragon.distribution.spotcheck.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.*;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.DmsBaseDictService;
import com.jd.bluedragon.distribution.jss.JssService;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.dto.SendDetailDto;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.spotcheck.domain.*;
import com.jd.bluedragon.distribution.spotcheck.enums.*;
import com.jd.bluedragon.distribution.spotcheck.enums.DutyTypeEnum;
import com.jd.bluedragon.distribution.spotcheck.exceptions.SpotCheckSysException;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.*;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.WeightAndVolumeCheckHandleMessage;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.MathUtils;
import com.jd.bluedragon.dms.utils.WaybillSignConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.etms.finance.dto.BizDutyDTO;
import com.jd.etms.finance.util.ResponseDTO;
import com.jd.etms.waybill.common.Page;
import com.jd.etms.waybill.domain.PackFlowDetail;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.PackageStateDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import com.jd.ql.dms.report.domain.WeightVolumeQueryCondition;
import com.jd.ql.dms.report.domain.spotcheck.SpotCheckQueryCondition;
import com.jd.ql.dms.report.domain.spotcheck.WeightVolumeSpotCheckDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jd.wlai.center.service.outter.domian.AutoReportingPicDto;
import com.jd.wlai.center.service.outter.domian.AutoReportingRequest;
import com.jd.wlai.center.service.outter.domian.AutoReportingResponse;
import com.jdl.express.weight.report.api.CommonDTO;
import com.jdl.express.weight.report.api.rule.queries.sorting.ReportInfoDTO;
import com.jdl.express.weight.report.api.rule.queries.sorting.ReportInfoQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 抽检处理实现
 *
 * @author hujiping
 * @date 2021/8/18 9:44 上午
 */
@Service("spotCheckDealService")
public class SpotCheckDealServiceImpl implements SpotCheckDealService {

    private static final Logger logger = LoggerFactory.getLogger(SpotCheckDealServiceImpl.class);

    /**外部 访问域名 */
    private static final String STORAGE_DOMAIN_COM = "storage.jd.com";
    /**内部 访问域名 */
    private static final String STORAGE_DOMAIN_LOCAL = "storage.jd.local";

    private static final int OUT_EXCESS_STATUS = 3; // 外部门定义的未超标值

    @Value("${jss.pda.image.bucket}")
    private String bucket;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private BusinessFinanceManager businessFinanceManager;

    @Autowired
    private DmsBaseDictService dmsBaseDictService;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private ReportExternalManager reportExternalManager;

    @Autowired
    private WaybillPackageManager waybillPackageManager;

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private LogEngine logEngine;

    @Autowired
    @Qualifier("dmsWeightVolumeExcess")
    private DefaultJMQProducer dmsWeightVolumeExcess;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    @Qualifier("weightAndVolumeCheckHandleProducer")
    private DefaultJMQProducer weightAndVolumeCheckHandleProducer;

    @Autowired
    private SendDetailService sendDetailService;

    @Autowired
    private AutoReportingManager autoReportingManager;

    @Autowired
    private WeightReportCommonRuleManager weightReportCommonRuleManager;

    @Autowired
    @Qualifier("spotCheckIssueProducer")
    private DefaultJMQProducer spotCheckIssueProducer;

    @Autowired
    @Qualifier("spotCheckIssueZDProducer")
    private DefaultJMQProducer spotCheckIssueZDProducer;

    @Autowired
    @Qualifier("dwsAIDistinguishSmallProducer")
    private DefaultJMQProducer dwsAIDistinguishSmallProducer;

    @Autowired
    @Qualifier("dwsIssueDealProducer")
    private DefaultJMQProducer dwsIssueDealProducer;

    @Autowired
    private JssService jssService;

    @Autowired
    private SpotCheckQueryManager spotCheckQueryManager;

    @Autowired
    private SpotCheckServiceProxy spotCheckServiceProxy;

    @Override
    public void assembleContrastDataFromFinance(SpotCheckContext spotCheckContext) {
        // 从计费获取核对数据
        SpotCheckContrastDetail spotCheckContrastDetail = new SpotCheckContrastDetail();
        spotCheckContext.setSpotCheckContrastDetail(spotCheckContrastDetail);
        // 核对来源：计费
        spotCheckContrastDetail.setContrastSourceFrom(ContrastSourceFromEnum.SOURCE_FROM_BILLING.getCode());
        String waybillCode = spotCheckContext.getWaybillCode();
        ResponseDTO<BizDutyDTO> responseDto = businessFinanceManager.queryDutyInfo(waybillCode);
        if(responseDto == null || responseDto.getData() == null){
            logger.warn("根据运单号:{}未获取到计费的称重量方数据!", waybillCode);
            return;
        }
        BizDutyDTO bizDutyDTO = responseDto.getData();
        spotCheckContrastDetail.setContrastOperateUserErp(bizDutyDTO.getDutyErp());
        spotCheckContrastDetail.setContrastOrgId(bizDutyDTO.getFirstLevelId() == null ? null : Integer.parseInt(bizDutyDTO.getFirstLevelId()));
        spotCheckContrastDetail.setContrastOrgName(bizDutyDTO.getFirstLevelName());
        spotCheckContrastDetail.setContrastDeptCode(bizDutyDTO.getSecondLevelId());
        spotCheckContrastDetail.setContrastDeptName(bizDutyDTO.getSecondLevelName());
        BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByErpNoCache(bizDutyDTO.getDutyErp());
        spotCheckContrastDetail.setContrastSiteCode(dto == null ? null : dto.getSiteCode());
        spotCheckContrastDetail.setContrastSiteName(dto == null ? null : dto.getSiteName());

        spotCheckContrastDetail.setDutyType(bizDutyDTO.getDutyType());
        spotCheckContrastDetail.setDutyFirstId(bizDutyDTO.getFirstLevelId());
        spotCheckContrastDetail.setDutyFirstName(bizDutyDTO.getFirstLevelName());
        spotCheckContrastDetail.setDutySecondId(bizDutyDTO.getSecondLevelId());
        spotCheckContrastDetail.setDutySecondName(bizDutyDTO.getSecondLevelName());
        spotCheckContrastDetail.setDutyThirdId(bizDutyDTO.getThreeLevelId());
        spotCheckContrastDetail.setDutyThirdName(bizDutyDTO.getThreeLevelName());

        double contrastWeight = bizDutyDTO.getWeight() == null ? Constants.DOUBLE_ZERO : bizDutyDTO.getWeight().doubleValue();
        spotCheckContrastDetail.setContrastWeight(contrastWeight);
        double contrastVolume = bizDutyDTO.getVolume() == null ? Constants.DOUBLE_ZERO : bizDutyDTO.getVolume().doubleValue();
        spotCheckContrastDetail.setContrastVolume(contrastVolume);
        // 计费结算重量
        spotCheckContrastDetail.setBillingCalcWeight(bizDutyDTO.getCalcWeight() == null ? Constants.DOUBLE_ZERO : bizDutyDTO.getCalcWeight().doubleValue());
        int volumeRate = spotCheckContext.getVolumeRate();
        double contrastVolumeWeight = MathUtils.keepScale(contrastVolume / volumeRate, 2);
        // 核对体积重量
        spotCheckContrastDetail.setContrastVolumeWeight(contrastVolumeWeight);
        // 核对较大值
        Double contrastLarge = Math.max(contrastWeight, contrastVolumeWeight);
        spotCheckContrastDetail.setContrastLarge(contrastLarge);
    }

    @Override
    public void assembleContrastDataFromWaybillFlow(SpotCheckContext spotCheckContext) {
        SpotCheckContrastDetail spotCheckContrastDetail = new SpotCheckContrastDetail();
        spotCheckContext.setSpotCheckContrastDetail(spotCheckContrastDetail);
        // 核对来源：运单称重流水
        spotCheckContrastDetail.setContrastSourceFrom(ContrastSourceFromEnum.SOURCE_FROM_WAYBILL.getCode());
        String waybillCode = spotCheckContext.getWaybillCode();
        WeightVolumeSummary firstWeightVolumeSummary = getWeightVolumeSummary(spotCheckContext);
        if(firstWeightVolumeSummary == null){
            logger.warn("根据运单号:{}未获取到运单称重流水!", waybillCode);
            return;
        }
        spotCheckContrastDetail.setContrastOperateUserErp(firstWeightVolumeSummary.getOperateErp());
        spotCheckContrastDetail.setContrastOrgId(firstWeightVolumeSummary.getOperateOrgId());
        spotCheckContrastDetail.setContrastOrgName(firstWeightVolumeSummary.getOperateOrgName());
        spotCheckContrastDetail.setContrastSiteCode(firstWeightVolumeSummary.getOperateSiteCode());
        spotCheckContrastDetail.setContrastSiteName(firstWeightVolumeSummary.getOperateSiteName());
        spotCheckContrastDetail.setContrastDeptCode(firstWeightVolumeSummary.getOperateAreaCode());
        spotCheckContrastDetail.setContrastDeptName(firstWeightVolumeSummary.getOperateAreaName());

        spotCheckContrastDetail.setDutyFirstId(String.valueOf(firstWeightVolumeSummary.getOperateOrgId()));
        spotCheckContrastDetail.setDutyFirstName(firstWeightVolumeSummary.getOperateOrgName());
        spotCheckContrastDetail.setDutySecondId(String.valueOf(firstWeightVolumeSummary.getOperateAreaCode()));
        spotCheckContrastDetail.setDutySecondName(firstWeightVolumeSummary.getOperateAreaName());
        if(Objects.equals(spotCheckContext.getSpotCheckBusinessType(), SpotCheckBusinessTypeEnum.SPOT_CHECK_TYPE_B.getCode())){
            // B网责任类型判断
            if(BusinessUtil.isDistrubutionCenter(firstWeightVolumeSummary.getSiteType())){
                // 站点类型为分拣
                spotCheckContrastDetail.setDutyType(DutyTypeEnum.DMS.getCode());
            }else  if(BusinessUtil.isSite(firstWeightVolumeSummary.getSiteType())){
                // 站点类型站点
                spotCheckContrastDetail.setDutyThirdId(String.valueOf(firstWeightVolumeSummary.getOperateSiteCode()));
                spotCheckContrastDetail.setDutyThirdName(firstWeightVolumeSummary.getOperateSiteName());
                spotCheckContrastDetail.setDutyType(DutyTypeEnum.SITE.getCode());
            }else if(BusinessUtil.isFleet(firstWeightVolumeSummary.getSiteType())){
                // 站点类型车队
                spotCheckContrastDetail.setDutyThirdId(String.valueOf(firstWeightVolumeSummary.getOperateSiteCode()));
                spotCheckContrastDetail.setDutyThirdName(firstWeightVolumeSummary.getOperateSiteName());
                spotCheckContrastDetail.setDutyType(DutyTypeEnum.FLEET.getCode());
            }else {
                spotCheckContrastDetail.setDutyType(DutyTypeEnum.OTHER.getCode());
            }
        }
        if(Objects.equals(spotCheckContext.getSpotCheckBusinessType(), SpotCheckBusinessTypeEnum.SPOT_CHECK_TYPE_C.getCode())){
            // C网责任类型判断
            if(BusinessUtil.isDistrubutionCenter(firstWeightVolumeSummary.getSiteType())){
                // 站点类型为分拣
                spotCheckContrastDetail.setDutyType(DutyTypeEnum.DMS.getCode());
            }else  if(BusinessUtil.isSite(firstWeightVolumeSummary.getSiteType())){
                // 站点类型站点
                spotCheckContrastDetail.setDutyThirdId(String.valueOf(firstWeightVolumeSummary.getOperateSiteCode()));
                spotCheckContrastDetail.setDutyThirdName(firstWeightVolumeSummary.getOperateSiteName());
                spotCheckContrastDetail.setDutyType(DutyTypeEnum.SITE.getCode());
            }else if(BusinessUtil.isWmsSite(firstWeightVolumeSummary.getSiteType())){
                // C网抽检暂无仓数据，则不记录二级，只三级
                spotCheckContrastDetail.setDutyThirdId(String.valueOf(firstWeightVolumeSummary.getOperateSiteCode()));
                spotCheckContrastDetail.setDutyThirdName(firstWeightVolumeSummary.getOperateSiteName());
                spotCheckContrastDetail.setDutyType(DutyTypeEnum.WMS.getCode());
            }else {
                spotCheckContrastDetail.setDutyType(DutyTypeEnum.OTHER.getCode());
            }
        }

        double contrastWeight = firstWeightVolumeSummary.getTotalWeight() == null ? Constants.DOUBLE_ZERO : firstWeightVolumeSummary.getTotalWeight();
        spotCheckContrastDetail.setContrastWeight(contrastWeight);
        double contrastVolume = firstWeightVolumeSummary.getTotalVolume() == null ? Constants.DOUBLE_ZERO : firstWeightVolumeSummary.getTotalVolume();
        spotCheckContrastDetail.setContrastVolume(contrastVolume);
        int volumeRate = spotCheckContext.getVolumeRate();
        double contrastVolumeWeight = MathUtils.keepScale(contrastVolume / volumeRate, 2);
        // 核对体积重量
        spotCheckContrastDetail.setContrastVolumeWeight(contrastVolumeWeight);
        // 核对较大值
        Double contrastLarge = Math.max(contrastWeight, contrastVolumeWeight);
        spotCheckContrastDetail.setContrastLarge(contrastLarge);
    }

    /**
     * 获取称重汇总数据
     *  1、B网 抽检
     *      0）、信任商家直接取运单waybill中的重量体积goodWeight、goodVolume（无操作站点先剔除此规则）
     *      1）、取运单号相关的所有称重量方记录（包裹和运单维度的都要）
     *      2）、剔除重量体积均为0（注意，只剔除都是0的）的无意义的称重量方记录（多为系统卡控需要，实际并未称重）（剔除后无称重数据则取揽收单位）
     *      3）、按时间先后顺序，找到最早称重量方的人ERP
     *      4）、筛选出该ERP操作的所有称重量方记录
     *      5）、若既有整单录入又有包裹录入 || 若是整单，以最后一次运单称重为对比对象
     *      6）、若是包裹，则筛选出所有包裹维度称重量方的记录，然后以包裹维度进行去重，仅保留时间靠后的那条，最后汇总得到的重量体积为对比对象
     *
     * @param spotCheckContext
     * @return
     */
    private WeightVolumeSummary getWeightVolumeSummary(SpotCheckContext spotCheckContext) {
        String waybillCode = spotCheckContext.getWaybillCode();
        Page<PackFlowDetail> result;
        try {
            Page<PackFlowDetail> page = new Page<>();
            page.setPageSize(1000);
            result = waybillPackageManager.getOpeDetailByCode(waybillCode, page);
        }catch (Exception e){
            throw new SpotCheckSysException(e.getMessage());
        }
        if(result == null || CollectionUtils.isEmpty(result.getResult())){
            logger.warn("根据单号{}获取称重流水为空!", waybillCode);
            return null;
        }
        List<PackFlowDetail> packageOpeList = result.getResult();
        // 汇总核对称重数据
        WeightVolumeSummary weightVolumeSummary = new WeightVolumeSummary();
        summaryWeightVolume(waybillCode, packageOpeList, weightVolumeSummary);

        Integer operateSiteCode = weightVolumeSummary.getOperateSiteCode();
        BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(operateSiteCode);
        if(baseSite == null){
            logger.warn("运单号：{}的首称重场地ID：{}不存在!", waybillCode, operateSiteCode);
            return null;
        }
        weightVolumeSummary.setOperateOrgId(baseSite.getOrgId());
        weightVolumeSummary.setOperateOrgName(baseSite.getOrgName());
        weightVolumeSummary.setOperateAreaCode(baseSite.getAreaCode());
        weightVolumeSummary.setOperateAreaName(baseSite.getAreaName());
        weightVolumeSummary.setOperateSiteName(baseSite.getSiteName());
        weightVolumeSummary.setSiteType(baseSite.getSiteType());
        return weightVolumeSummary;
    }

    private void summaryWeightVolume(String waybillCode, List<PackFlowDetail> packageOpeList, WeightVolumeSummary weightVolumeSummary) {

        List<PackFlowDetail> needComputeList = new ArrayList<>();
        // 排除重量体积均为0的情况(系统卡控)
        for (PackFlowDetail packFlowDetail : packageOpeList) {
            if((packFlowDetail.getpWeight() == null || Objects.equals(packFlowDetail.getpWeight(), Constants.DOUBLE_ZERO))
                    && (packFlowDetail.getpLength() == null || Objects.equals(packFlowDetail.getpLength(), Constants.DOUBLE_ZERO))
                    &&(packFlowDetail.getpWidth() == null || Objects.equals(packFlowDetail.getpWidth(), Constants.DOUBLE_ZERO))
                    && (packFlowDetail.getpHigh() == null || Objects.equals(packFlowDetail.getpHigh(), Constants.DOUBLE_ZERO))){
                continue;
            }
            needComputeList.add(packFlowDetail);
        }
        // 全流程无称重则取揽收单位
        if(CollectionUtils.isEmpty(needComputeList)){
            List<PackageStateDto> collectList = waybillTraceManager.getPkStateDtoByWCodeAndState(waybillCode, Constants.WAYBILL_TRACE_STATE_COLLECT_COMPLETE);
            if(CollectionUtils.isNotEmpty(collectList)){
                PackageStateDto packageState = collectList.get(0);
                weightVolumeSummary.setTotalWeight(Constants.DOUBLE_ZERO);
                weightVolumeSummary.setTotalVolume(Constants.DOUBLE_ZERO);
                weightVolumeSummary.setOperateId(packageState.getOperatorUserId());
                weightVolumeSummary.setOperateErp(packageState.getOperatorUserErp());
                weightVolumeSummary.setOperateSiteCode(packageState.getOperatorSiteId());
                return;
            }
        }
        // 按称重时间从小到大排序
        sortedByWeightTime(needComputeList);
        // 获取第一个操作人的所有称重记录
        List<PackFlowDetail> firstWeightList = new ArrayList<>();
        PackFlowDetail firstFlowDetail = needComputeList.get(0);
        String firstWeightUserErp = firstFlowDetail.getWeighUserErp();
        for (PackFlowDetail packFlowDetail : needComputeList) {
            if(Objects.equals(packFlowDetail.getWeighUserErp(), firstWeightUserErp)){
                firstWeightList.add(packFlowDetail);
            }
        }
        // 汇总称重量方数据
        summaryFirstWeight(firstWeightList, weightVolumeSummary);
    }

    private void summaryFirstWeight(List<PackFlowDetail> firstWeightList, WeightVolumeSummary weightVolumeSummary) {
        Map<String,PackFlowDetail> waybillMap = new LinkedHashMap<>();
        Map<String,PackFlowDetail> packageMap = new LinkedHashMap<>();
        for (PackFlowDetail packFlowDetail : firstWeightList) {
            if(WaybillUtil.isPackageCode(packFlowDetail.getPackageCode())){
                packageMap.put(packFlowDetail.getPackageCode(), packFlowDetail);
            }
            if(WaybillUtil.isWaybillCode(packFlowDetail.getPackageCode())){
                waybillMap.put(packFlowDetail.getPackageCode(), packFlowDetail);
            }
        }
        double totalWeight = Constants.DOUBLE_ZERO;
        double totalVolume = Constants.DOUBLE_ZERO;
        if(waybillMap.isEmpty()){
            // 全是包裹维度称重则汇总所有包裹记录
            for (PackFlowDetail packFlowDetail : packageMap.values()) {
                totalWeight += packFlowDetail.getpWeight() == null ? Constants.DOUBLE_ZERO : packFlowDetail.getpWeight();
                totalVolume += (packFlowDetail.getpLength() == null ? Constants.DOUBLE_ZERO : packFlowDetail.getpLength())
                        * (packFlowDetail.getpWidth() == null ? Constants.DOUBLE_ZERO : packFlowDetail.getpWidth())
                        * (packFlowDetail.getpHigh() == null ? Constants.DOUBLE_ZERO : packFlowDetail.getpHigh());
            }
        }else {
            // 存在运单维度称重则取运单维度记录
            for (PackFlowDetail packFlowDetail : waybillMap.values()) {
                totalWeight = packFlowDetail.getpWeight() == null ? Constants.DOUBLE_ZERO : packFlowDetail.getpWeight();
                totalVolume = (packFlowDetail.getpLength() == null ? Constants.DOUBLE_ZERO : packFlowDetail.getpLength())
                        * (packFlowDetail.getpWidth() == null ? Constants.DOUBLE_ZERO : packFlowDetail.getpWidth())
                        * (packFlowDetail.getpHigh() == null ? Constants.DOUBLE_ZERO : packFlowDetail.getpHigh());
            }
        }
        weightVolumeSummary.setTotalWeight(totalWeight);
        weightVolumeSummary.setTotalVolume(totalVolume);
        weightVolumeSummary.setOperateErp(firstWeightList.get(0).getWeighUserErp());
        weightVolumeSummary.setOperateSiteCode(firstWeightList.get(0).getOperatorSiteId());
    }

    private void summaryWeightVolumeOfC(List<PackFlowDetail> packageOpeList, WeightVolumeSummary weightVolumeSummary) {
        // 按称重时间升序
        sortedByWeightTime(packageOpeList);
        // 获取最早操作的记录
        PackFlowDetail packFlowDetail = packageOpeList.get(0);
        weightVolumeSummary.setTotalWeight(packFlowDetail.getpWeight());
        weightVolumeSummary.setTotalVolume((packFlowDetail.getpLength() == null ? Constants.DOUBLE_ZERO : packFlowDetail.getpLength())
                * (packFlowDetail.getpWidth() == null ? Constants.DOUBLE_ZERO : packFlowDetail.getpWidth())
                * (packFlowDetail.getpHigh() == null ? Constants.DOUBLE_ZERO : packFlowDetail.getpHigh()));
        weightVolumeSummary.setOperateErp(packFlowDetail.getWeighUserErp());
        weightVolumeSummary.setOperateSiteCode(packFlowDetail.getOperatorSiteId());
    }

    private void sortedByWeightTime(List<PackFlowDetail> needComputeList) {
        Collections.sort(needComputeList, new Comparator<PackFlowDetail>() {
            @Override
            public int compare(PackFlowDetail o1, PackFlowDetail o2) {
                if (o1.getWeighTime() == null && o2.getWeighTime() == null) {
                    return 1;
                }
                if (o1.getWeighTime() == null && o2.getWeighTime() != null) {
                    return 1;
                }
                if (o1.getWeighTime() != null && o2.getWeighTime() == null) {
                    return -1;
                }
                return o1.getWeighTime().compareTo(o2.getWeighTime());
            }
        });
    }

    @Override
    public DmsBaseDict getProductType(String waybillSign) {
        List<DmsBaseDict> list = dmsBaseDictService.queryListByParentId(Constants.PRODUCT_PARENT_ID);
        HashMap<String, DmsBaseDict> map = new HashMap<String, DmsBaseDict>();
        for (int i = 0; i < list.size(); i++) {
            map.put(list.get(i).getTypeName(), list.get(i));
        }
        DmsBaseDict dmsBaseDict = null;
        //当waybillSign的40位为0时，根据waybillSign的31位值判断产品类型
        if (BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_40, '0')) {
            dmsBaseDict = map.get("31" + "-" + waybillSign.charAt(30));
        } else if (BusinessUtil.isFastTrans(waybillSign)) {
            //当waybillSign的40位为1，2，3，4，5时，根据waybillSign的80位值判断产品类型
            dmsBaseDict = map.get("80" + "-" + waybillSign.charAt(79));
        }
        return dmsBaseDict;
    }

    @Override
    public boolean gatherTogether(SpotCheckContext spotCheckContext) {
        String spotCheckPackCache = spotCheckPackSetStr(spotCheckContext.getWaybillCode(), spotCheckContext.getReviewSiteCode());
        if(StringUtils.isEmpty(spotCheckPackCache)){
            return false;
        }
        Set spotCheckPackSet = JsonHelper.fromJson(spotCheckPackCache, Set.class);
        if(CollectionUtils.isEmpty(spotCheckPackSet) || spotCheckPackSet.contains(spotCheckContext.getPackageCode())){
            return false;
        }
        return spotCheckContext.getPackNum() - spotCheckPackSet.size() == Constants.CONSTANT_NUMBER_ONE;
    }

    @Override
    public void sendWaybillTrace(SpotCheckContext spotCheckContext) {
        Task tTask = new Task();
        tTask.setKeyword1(spotCheckContext.getWaybillCode());
        tTask.setKeyword2(String.valueOf(WaybillStatus.WAYBILL_STATUS_WEIGHT_VOLUME_SPOT_CHECK));
        tTask.setCreateSiteCode(spotCheckContext.getReviewSiteCode());
        tTask.setCreateTime(spotCheckContext.getOperateTime());
        tTask.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_WAYBILL_TRACK));
        tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_POP));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);

        WaybillStatus status=new WaybillStatus();
        status.setOperateType(WaybillStatus.WAYBILL_STATUS_WEIGHT_VOLUME_SPOT_CHECK);
        status.setWaybillCode(spotCheckContext.getWaybillCode());
        status.setPackageCode(spotCheckContext.getPackageCode());
        status.setOperateTime(spotCheckContext.getOperateTime());
        SpotCheckReviewDetail spotCheckReviewDetail = spotCheckContext.getSpotCheckReviewDetail();
        status.setOperator(spotCheckReviewDetail.getReviewUserErp());
        status.setRemark(String.format(SpotCheckConstants.SPOT_CHECK_TRACE_REMARK, spotCheckReviewDetail.getReviewWeight(), spotCheckReviewDetail.getReviewVolume()));
        status.setCreateSiteCode(spotCheckContext.getReviewSiteCode());
        tTask.setBody(JsonHelper.toJson(status));
        taskService.add(tTask);
    }

    @Override
    public void recordSpotCheckLog(SpotCheckContext spotCheckContext) {
        BusinessLogConstans.OperateTypeEnum operateTypeEnum;
        if(Objects.equals(spotCheckContext.getSpotCheckSourceFrom(), SpotCheckSourceFromEnum.SPOT_CHECK_CLIENT_PLATE.getName())){
            operateTypeEnum = BusinessLogConstans.OperateTypeEnum.SPOT_CHECK_CLIENT;
        }else if(Objects.equals(spotCheckContext.getSpotCheckSourceFrom(), SpotCheckSourceFromEnum.SPOT_CHECK_DWS.getName())){
            operateTypeEnum = BusinessLogConstans.OperateTypeEnum.SPOT_CHECK_DWS;
        }else if(Objects.equals(spotCheckContext.getSpotCheckSourceFrom(), SpotCheckSourceFromEnum.SPOT_CHECK_ARTIFICIAL.getName())){
            operateTypeEnum = BusinessLogConstans.OperateTypeEnum.SPOT_CHECK_ARTIFICIAL;
        }else if(Objects.equals(spotCheckContext.getSpotCheckSourceFrom(), SpotCheckSourceFromEnum.SPOT_CHECK_DMS_WEB.getName())){
            operateTypeEnum = BusinessLogConstans.OperateTypeEnum.SPOT_CHECK_WEB;
        }else if(Objects.equals(spotCheckContext.getSpotCheckSourceFrom(), SpotCheckSourceFromEnum.SPOT_CHECK_ANDROID.getName())){
            operateTypeEnum = BusinessLogConstans.OperateTypeEnum.SPOT_CHECK_ANDROID;
        }else {
            logger.warn("非法抽检类型不记录操作日志");
            return;
        }
        try {
            SpotCheckReviewDetail spotCheckReviewDetail = spotCheckContext.getSpotCheckReviewDetail();
            long startTime = System.currentTimeMillis();
            JSONObject request = new JSONObject();
            request.put("operatorCode", spotCheckReviewDetail.getReviewUserErp());
            request.put("siteCode", spotCheckContext.getReviewSiteCode());
            request.put("operateTime", spotCheckContext.getOperateTime().getTime());
            request.put("waybillCode", spotCheckContext.getWaybillCode());
            request.put("packageCode", spotCheckContext.getPackageCode());
            long endTime = System.currentTimeMillis();

            BusinessLogProfiler logProfiler = new BusinessLogProfilerBuilder()
                    .operateTypeEnum(operateTypeEnum)
                    .processTime(endTime, startTime)
                    .operateRequest(request)
                    .reMark(spotCheckContext.getExcessReason())
                    .methodName("SpotCheckDealServiceImpl.recordSpotCheckLog")
                    .build();

            logEngine.addLog(logProfiler);
        }catch (Exception e){
            logger.error("SpotCheckDealServiceImpl recordSpotCheckLog异常!", e);
        }
    }

    @Override
    public boolean checkIsHasSend(String packageCode, Integer siteCode) {
        boolean isHasSend = false;
        if(StringUtils.isBlank(packageCode)){
            throw new SpotCheckSysException("包裹号不能为空!");
        }
        try {
            String key = String.format(CacheKeyConstants.CACHE_KEY_WAYBILL_SEND_STATUS, siteCode, packageCode);
            if(!StringUtils.isEmpty(jimdbCacheService.get(key))){
                isHasSend = true;
            }else {
                List<SendDetail> sendList;
                if(WaybillUtil.isWaybillCode(packageCode)){
                    sendList = sendDetailService.findByWaybillCodeOrPackageCode(siteCode, WaybillUtil.getWaybillCode(packageCode), null);
                }else {
                    sendList = sendDetailService.findByWaybillCodeOrPackageCode(siteCode, WaybillUtil.getWaybillCode(packageCode), packageCode);
                }
                isHasSend = CollectionUtils.isNotEmpty(sendList);
            }
        }catch (Exception e){
            logger.error("根据包裹号:{}站点:{}查询是否操作过发货异常!", packageCode, siteCode, e);
        }
        return isHasSend;
    }

    @Override
    public boolean checkIsHasSpotCheck(String waybillCode) {
        boolean isHasSpotCheck = false;
        try {
            String key = String.format(CacheKeyConstants.CACHE_SPOT_CHECK, waybillCode);
            if(StringUtils.isNotEmpty(jimdbCacheService.get(key))){
                isHasSpotCheck = true;
            }else {
                WeightVolumeQueryCondition condition = new WeightVolumeQueryCondition();
                condition.setWaybillCode(waybillCode);
                condition.setExcessStatusList(Arrays.asList(ExcessStatusEnum.EXCESS_ENUM_NO.getCode(), ExcessStatusEnum.EXCESS_ENUM_YES.getCode()));
                isHasSpotCheck = reportExternalManager.countByParam(condition) > Constants.NUMBER_ZERO;
            }
        }catch (Exception e){
            logger.error("根据运单号{}查询是否操作过抽检异常!", waybillCode, e);
        }
        return isHasSpotCheck;
    }

    @Override
    public boolean checkIsExcessFromRedis(String waybillCode) {
        boolean isExcess = false;
        try {
            String key = String.format(CacheKeyConstants.CACHE_SPOT_CHECK, waybillCode);
            String executeStatus = jimdbCacheService.get(key);
            if(StringUtils.isNotEmpty(executeStatus)){
                return Objects.equals(executeStatus, String.valueOf(ExcessStatusEnum.EXCESS_ENUM_YES.getCode()));
            }
        }catch (Exception e){
            logger.error("根据运单号{}查询是否超标异常!", waybillCode, e);
        }
        return isExcess;
    }

    @Override
    public String spotCheckPackSetStr(String waybillCode, Integer siteCode) {
        String packListKey = String.format(CacheKeyConstants.CACHE_SPOT_CHECK_PACK_LIST, siteCode, waybillCode);
        String packSetStr;
        try {
            packSetStr = jimdbCacheService.get(packListKey);
            if(StringUtils.isNotEmpty(packSetStr)){
                return packSetStr;
            }else {
                WeightVolumeQueryCondition condition = new WeightVolumeQueryCondition();
                condition.setWaybillCode(waybillCode);
                condition.setReviewSiteCode(siteCode);
                condition.setRecordType(SpotCheckRecordTypeEnum.DETAIL_RECORD.getCode());
                List<String> spotCheckedPackList = reportExternalManager.getSpotCheckPackageCodesByCondition(condition);
                if(CollectionUtils.isEmpty(spotCheckedPackList)){
                    return null;
                }
                packSetStr = jimdbCacheService.get(packListKey);
                if(StringUtils.isNotEmpty(packSetStr)){
                    spotCheckedPackList.addAll(Objects.requireNonNull(JsonHelper.jsonToList(packSetStr, String.class)));
                }
                // 已抽检包裹集合
                String spotCheckPackSetStr = JsonHelper.toJson(new HashSet<>(spotCheckedPackList));
                jimdbCacheService.setEx(packListKey, spotCheckPackSetStr, 30, TimeUnit.MINUTES);
                return spotCheckPackSetStr;
            }
        }catch (Exception e){
            logger.error("获取场地:{}运单号:{}下的包裹抽检缓存异常!", siteCode, waybillCode);
        }
        return null;
    }

    @Override
    public boolean checkPackHasSpotCheck(String packageCode, Integer siteCode) {
        try {
            // 按包裹查询是否操作抽检
            String checkPackageRecordKey = CacheKeyConstants.CACHE_KEY_PACKAGE_OR_WAYBILL_CHECK_FLAG.concat(packageCode);
            if(StringUtils.isNotEmpty(jimdbCacheService.get(checkPackageRecordKey))){
                return true;
            }
            // 按运单查询是否操作抽检
            checkPackageRecordKey = String.format(CacheKeyConstants.CACHE_SPOT_CHECK, WaybillUtil.getWaybillCode(packageCode));
            if(StringUtils.isNotEmpty(jimdbCacheService.get(checkPackageRecordKey))){
                return true;
            }
        }catch (Exception e){
            logger.error("校验包裹号:{}站点:{}是否操作过抽检异常!", packageCode, siteCode ,e);
        }
        return false;
    }

    @Override
    public void issueSpotCheckDetail(WeightVolumeCollectDto weightVolumeCollectDto) {
        if(isExecuteSpotCheckReform(weightVolumeCollectDto.getReviewSiteCode())){
            // 抽检改造的场地，老的抽检不处理
            return;
        }
        // 校验运单是否已下发
        if(checkWaybillHasIssued(weightVolumeCollectDto.getWaybillCode())){
            logger.info("spotCheckWaybill has issued will not send {}", weightVolumeCollectDto.getWaybillCode());
            return;
        }
        if(!Objects.equals(weightVolumeCollectDto.getIsExcess(), ExcessStatusEnum.EXCESS_ENUM_YES.getCode())){
            return;
        }
        // 核对区域、核对操作站点、核对erp三者缺一则不下发
        if(weightVolumeCollectDto.getBillingOrgCode() == null || StringUtils.isEmpty(weightVolumeCollectDto.getBillingCompany())
                || StringUtils.isEmpty(weightVolumeCollectDto.getBillingErp())){
            return;
        }
        // 设置下发fxm缓存
        setIssueWaybillCache(weightVolumeCollectDto.getWaybillCode());

        AbnormalResultMq abnormalResultMq = buildCommonAttr(weightVolumeCollectDto);
        if(Objects.equals(weightVolumeCollectDto.getSpotCheckType(), SpotCheckBusinessTypeEnum.SPOT_CHECK_TYPE_B.getCode())){
            bAssembleIssueSpotCheckDetail(weightVolumeCollectDto, abnormalResultMq);
        }
        if(Objects.equals(weightVolumeCollectDto.getSpotCheckType(), SpotCheckBusinessTypeEnum.SPOT_CHECK_TYPE_C.getCode())){
            cAssembleIssueSpotCheckDetail(weightVolumeCollectDto, abnormalResultMq);
        }
        if(logger.isInfoEnabled()){
            logger.info("下发运单号:{}的抽检超标数据,明细如下:{}", weightVolumeCollectDto.getWaybillCode(), JsonHelper.toJson(abnormalResultMq));
        }
        dmsWeightVolumeExcess.sendOnFailPersistent(abnormalResultMq.getAbnormalId(), JsonHelper.toJson(abnormalResultMq));
        // 更新已下发字段
        WeightVolumeCollectDto updateCollect = new WeightVolumeCollectDto();
        updateCollect.setWaybillCode(weightVolumeCollectDto.getWaybillCode());
        updateCollect.setPackageCode(weightVolumeCollectDto.getPackageCode());
        updateCollect.setReviewSiteCode(weightVolumeCollectDto.getReviewSiteCode());
        updateCollect.setIssueDownstream(Constants.CONSTANT_NUMBER_ONE);
        spotCheckServiceProxy.insertOrUpdateProxyPrevious(updateCollect);
    }

    private void setIssueWaybillCache(String waybillCode) {
        try {
            String key = String.format(CacheKeyConstants.CACHE_FXM_SEND_WAYBILL, waybillCode);
            jimdbCacheService.setEx(key, Constants.CONSTANT_NUMBER_ONE, 15, TimeUnit.DAYS);
        }catch (Exception e){
            logger.error("设置运单号:{}下发fxm的缓存异常!", waybillCode, e);
        }
    }

    @Override
    public void dealPictureUrl(String packageCode, Integer siteCode, String pictureUrl) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.spotCheck.SpotCheckDealService.dealPictureUrl",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            // 设置图片缓存
            addPicUrlCache(packageCode, siteCode, pictureUrl);

            String waybillCode = WaybillUtil.getWaybillCode(packageCode);
            Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(waybillCode);
            boolean isMultiPack = isMultiPack(waybill, packageCode);

            // 原抽检数据处理
            executeOldPicDeal(packageCode, isMultiPack, siteCode, pictureUrl);

            // 执行抽检改造
            executeReformPicDeal(packageCode, isMultiPack, siteCode, pictureUrl);

        }catch (Exception e){
            logger.error("包裹:{}的图片处理异常!", packageCode, e);
            Profiler.functionError(callerInfo);
            throw e;
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
    }

    private void executeOldPicDeal(String packageCode, Boolean isMultiPack, Integer siteCode, String pictureUrl) {
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);
        if(isMultiPack){
            String spotCheckPackCache = spotCheckPackSetStr(waybillCode, siteCode);
            if(StringUtils.isEmpty(spotCheckPackCache) || !spotCheckPackCache.contains(packageCode)){
                // 一单多件包裹未抽检
                return;
            }
            // 1、更新包裹图片
            WeightVolumeCollectDto weightVolumeCollectDto = new WeightVolumeCollectDto();
            weightVolumeCollectDto.setPackageCode(packageCode);
            weightVolumeCollectDto.setReviewSiteCode(siteCode);
            weightVolumeCollectDto.setIsHasPicture(Constants.CONSTANT_NUMBER_ONE);
            weightVolumeCollectDto.setPictureAddress(pictureUrl);
            spotCheckServiceProxy.insertOrUpdateProxyPrevious(weightVolumeCollectDto);
            // 2、更新运单维度字段
            WeightVolumeCollectDto updateWaybillCollect = new WeightVolumeCollectDto();
            updateWaybillCollect.setPackageCode(waybillCode);// 一单多件运单维度记录包裹号是运单号
            updateWaybillCollect.setReviewSiteCode(siteCode);
            updateWaybillCollect.setIsHasPicture(Constants.CONSTANT_NUMBER_ONE);
            spotCheckServiceProxy.insertOrUpdateProxyPrevious(updateWaybillCollect);
        }else {
            if(!checkIsHasSpotCheck(waybillCode)){
                // 一单一件包裹未抽检
                return;
            }
            WeightVolumeCollectDto weightVolumeCollectDto = new WeightVolumeCollectDto();
            weightVolumeCollectDto.setPackageCode(waybillCode);// 一单一件包裹号字段存的是运单号
            weightVolumeCollectDto.setReviewSiteCode(siteCode);
            weightVolumeCollectDto.setIsHasPicture(Constants.CONSTANT_NUMBER_ONE);
            weightVolumeCollectDto.setPictureAddress(pictureUrl);
            spotCheckServiceProxy.insertOrUpdateProxyPrevious(weightVolumeCollectDto);
        }
        // 下发超标mq处理
        WeightAndVolumeCheckHandleMessage weightAndVolumeCheckHandleMessage = new WeightAndVolumeCheckHandleMessage();
        weightAndVolumeCheckHandleMessage.setOpNode(WeightAndVolumeCheckHandleMessage.UPLOAD_IMG);
        weightAndVolumeCheckHandleMessage.setPackageCode(packageCode);
        weightAndVolumeCheckHandleMessage.setWaybillCode(WaybillUtil.getWaybillCode(packageCode));
        weightAndVolumeCheckHandleMessage.setSiteCode(siteCode);
        weightAndVolumeCheckHandleProducer.sendOnFailPersistent(packageCode, JsonHelper.toJson(weightAndVolumeCheckHandleMessage));

    }

    private void executeReformPicDeal(String packageCode, Boolean isMultiPack, Integer siteCode, String pictureUrl) {
        if(!isExecuteSpotCheckReform(siteCode)){
            return;
        }
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);
        if(isMultiPack){
            // 一单多件
            // 1、更新包裹明细维度数据
            // 2、更新总记录维度数据
            // 3、一单多件不下发图片故不再此下发超标MQ
            WeightVolumeSpotCheckDto detailRecord = new WeightVolumeSpotCheckDto();
            detailRecord.setPackageCode(packageCode);
            detailRecord.setReviewSiteCode(siteCode);
            detailRecord.setIsHasPicture(Constants.CONSTANT_NUMBER_ONE);
            detailRecord.setPictureAddress(pictureUrl);
            detailRecord.setRecordType(SpotCheckRecordTypeEnum.DETAIL_RECORD.getCode());
            spotCheckServiceProxy.insertOrUpdateProxyReform(detailRecord);
            WeightVolumeSpotCheckDto totalRecord = new WeightVolumeSpotCheckDto();
            totalRecord.setPackageCode(waybillCode);
            totalRecord.setReviewSiteCode(siteCode);
            totalRecord.setIsHasPicture(Constants.CONSTANT_NUMBER_ONE);
            totalRecord.setRecordType(SpotCheckRecordTypeEnum.SUMMARY_RECORD.getCode());
            spotCheckServiceProxy.insertOrUpdateProxyReform(totalRecord);
            // 4.下发超标数据
            if(isExecuteDwsAIDistinguish(siteCode)){
                if(checkIsExcessFromRedis(waybillCode)){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        logger.error("阻塞1s异常!", e);
                    }
                    totalRecord.setWaybillCode(waybillCode);
                    totalRecord.setIsExcess(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
                    totalRecord.setIsGatherTogether(Constants.CONSTANT_NUMBER_ONE);
                    dwsIssueDealProducer.sendOnFailPersistent(waybillCode, JsonHelper.toJson(totalRecord));
                }
            }
        }else {
            // 一单一件
            // 1、更新总记录维度数据
            WeightVolumeSpotCheckDto summaryRecord = new WeightVolumeSpotCheckDto();
            summaryRecord.setPackageCode(waybillCode);
            summaryRecord.setReviewSiteCode(siteCode);
            summaryRecord.setIsHasPicture(Constants.CONSTANT_NUMBER_ONE);
            summaryRecord.setPictureAddress(pictureUrl);
            summaryRecord.setRecordType(SpotCheckRecordTypeEnum.SUMMARY_RECORD.getCode());
            spotCheckServiceProxy.insertOrUpdateProxyReform(summaryRecord);
            // 2、一单一件下发超标数据要有图片，故图片上传在抽检数据之后则需要下发超标MQ
            if(!checkIsExcessFromRedis(waybillCode)){
                // 未超标则直接返回
                return;
            }
            WeightVolumeSpotCheckDto spotCheckDto = searchSpotCheck(packageCode, waybillCode, siteCode);
            if(spotCheckDto == null){
                return;
            }
            spotCheckDto.setIsHasPicture(Constants.CONSTANT_NUMBER_ONE);
            spotCheckDto.setPictureAddress(pictureUrl); // 此处设置防止es未查询到上面刚插入的数据
            spotCheckIssue(spotCheckDto);
        }
    }

    /**
     * 获取抽检汇总记录
     *
     * @param packageCode
     * @param waybillCode
     * @param siteCode
     * @return
     */
    private WeightVolumeSpotCheckDto searchSpotCheck(String packageCode, String waybillCode, Integer siteCode) {
        SpotCheckQueryCondition condition = new SpotCheckQueryCondition();
        condition.setWaybillCode(waybillCode);
        condition.setPackageCode(waybillCode);
        condition.setReviewSiteCode(siteCode);
        condition.setIsExcess(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
        List<WeightVolumeSpotCheckDto> spotCheckList = spotCheckQueryManager.querySpotCheckByCondition(condition);
        if(CollectionUtils.isEmpty(spotCheckList)){
            // 已抽检的数据落es，在1s内立即查询会查询不到数据，故此处睡眠1s后在查询一次
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("阻塞1s异常!", e);
            }
            spotCheckList = spotCheckQueryManager.querySpotCheckByCondition(condition);
            if(CollectionUtils.isEmpty(spotCheckList)){
                logger.warn("未查询到包裹号:{}站点:{}的已超标抽检数据!", packageCode, siteCode);
                return null;
            }
        }
        return spotCheckList.get(0);
    }

    @Override
    public InvokeResult<Boolean> executeNewHandleProcess(WeightAndVolumeCheckHandleMessage message) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        result.setData(true);
        // 获取抽检记录是运单维度的数据
        WeightVolumeQueryCondition condition = new WeightVolumeQueryCondition();
        condition.setReviewSiteCode(message.getSiteCode());
        condition.setWaybillCode(message.getWaybillCode());
        List<WeightVolumeCollectDto> accordList = reportExternalManager.queryByCondition(condition);
        if(CollectionUtils.isEmpty(accordList)){
            logger.warn("根据运单号:{}站点:{}未获取到的抽检记录!", message.getWaybillCode(), message.getSiteCode());
            return result;
        }
        // 上传图片环节
        if(Objects.equals(message.getOpNode(), WeightAndVolumeCheckHandleMessage.UPLOAD_IMG)){
            Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(message.getWaybillCode());
            boolean isMultiplePackage = isMultiPack(waybill, message.getPackageCode());
            if(!isMultiplePackage){
                // 一单一件处理：有图片&已发货，则下发到FXM
                oncePackIssueDeal(message, accordList);
            } else {
                // 一单多件处理
                multiPackIssueDeal(message, accordList);
            }
            return result;
        }
        // 发货完成环节
        if(Objects.equals(message.getOpNode(), WeightAndVolumeCheckHandleMessage.SEND)){
            return spotCheckPackSendDeal(message, accordList);
        }
        result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "未知类型不予处理!");
        return result;
    }

    @Override
    public boolean isExecuteSpotCheckReform(Integer siteCode) {
        try {
            String newSpotCheckSiteCodes = uccPropertyConfiguration.getSpotCheckReformSiteCodes();
            // 全部关闭
            if(StringUtils.isEmpty(newSpotCheckSiteCodes)){
                return false;
            }
            // 全部开启
            if(Objects.equals(newSpotCheckSiteCodes, String.valueOf(true))){
                return true;
            }
            return Arrays.asList(newSpotCheckSiteCodes.split(Constants.SEPARATOR_COMMA)).contains(String.valueOf(siteCode));
        }catch (Exception e){
            logger.error("获取抽检改造站点ID异常!", e);
        }
        return false;
    }

    @Override
    public boolean isExecuteDwsAIDistinguish(Integer siteCode) {
        try {
            String dwsAIDistinguishSiteCodes = uccPropertyConfiguration.getDeviceAIDistinguishSwitch();
            // 全部关闭
            if(StringUtils.isEmpty(dwsAIDistinguishSiteCodes)){
                return false;
            }
            // 全部开启
            if(Objects.equals(dwsAIDistinguishSiteCodes, String.valueOf(true))){
                return true;
            }
            return Arrays.asList(dwsAIDistinguishSiteCodes.split(Constants.SEPARATOR_COMMA)).contains(String.valueOf(siteCode));
        }catch (Exception e){
            logger.error("获取开通AI图片识别场地ID异常!", e);
        }
        return false;
    }

    @Override
    public ImmutablePair<Integer, String> singlePicAutoDistinguish(String waybillCode, Double weight, String picUrl, Integer uploadPicType, Integer excessType) {
        if(!uccPropertyConfiguration.getAiDistinguishSwitch()){
            return ImmutablePair.of(InvokeResult.RESULT_SUCCESS_CODE, InvokeResult.RESULT_SUCCESS_MESSAGE);
        }
        boolean isDistinguish = false;
        // AI图片识别前提：1&&2
        // 1、重量超标：1 （只识别图片类型是：重量和面单）
        // 2、体积超标：2 （只识别图片类型是：面单）
        if(Objects.equals(excessType, SpotCheckConstants.EXCESS_TYPE_WEIGHT)
                && (Objects.equals(SpotCheckPicTypeEnum.PIC_WEIGHT.getCode(), uploadPicType) || Objects.equals(SpotCheckPicTypeEnum.PIC_FACE.getCode(), uploadPicType))){
            isDistinguish = true;
        }
        if(Objects.equals(excessType, SpotCheckConstants.EXCESS_TYPE_VOLUME)
                && Objects.equals(SpotCheckPicTypeEnum.PIC_FACE.getCode(), uploadPicType)){
            isDistinguish = true;
        }
        // 是否执行AI识别
        if(!isDistinguish){
            return ImmutablePair.of(InvokeResult.RESULT_SUCCESS_CODE, InvokeResult.RESULT_SUCCESS_MESSAGE);
        }
        AutoReportingRequest autoReportingRequest = new AutoReportingRequest();
        String currentTimeStamp = String.valueOf(System.currentTimeMillis());
        autoReportingRequest.setUuid(currentTimeStamp);
        autoReportingRequest.setExpressBillNo(waybillCode);
        autoReportingRequest.setWeight(weight == null ? String.valueOf(Constants.DOUBLE_ZERO) : String.valueOf(weight));
        List<AutoReportingPicDto> singleList = new ArrayList<>();
        AutoReportingPicDto autoReportingPicDto = new AutoReportingPicDto();
        autoReportingPicDto.setUuid(currentTimeStamp);
        autoReportingPicDto.setPicUrl(picUrl);
        int picType = Objects.equals(uploadPicType, SpotCheckPicTypeEnum.PIC_WEIGHT.getCode())
                ? SpotCheckConstants.SPOT_CHECK_AI_TYPE_WEIGHT : SpotCheckConstants.SPOT_CHECK_AI_TYPE_FACE;
        autoReportingPicDto.setPicType(String.valueOf(picType));
        singleList.add(autoReportingPicDto);
        autoReportingRequest.setPicList(singleList);

        int code = InvokeResult.RESULT_SUCCESS_CODE;
        String message = InvokeResult.RESULT_SUCCESS_MESSAGE;
        AutoReportingResponse autoReportingResponse = autoReportingManager.reportingCheck(autoReportingRequest);
        if(autoReportingResponse == null || !Objects.equals(autoReportingResponse.getStatus(), String.valueOf(InvokeResult.RESULT_SUCCESS_CODE))
                || CollectionUtils.isEmpty(autoReportingResponse.getPicList())){
            code = SpotCheckConstants.SPOT_CHECK_AI_EXC_CODE;
            message = "AI图片识别失败，请联系分拣小秘!";
            return ImmutablePair.of(code, message);
        }
        AutoReportingPicDto reportResult = autoReportingResponse.getPicList().get(0);
        if(reportResult == null || !Objects.equals(reportResult.getStatus(), String.valueOf(InvokeResult.RESULT_SUCCESS_CODE))){
            code = SpotCheckConstants.SPOT_CHECK_AI_EXC_CODE;
            message = reportResult == null ? "AI图片识别失败，请联系分拣小秘！" : reportResult.getMessage();
            return ImmutablePair.of(code, message);
        }
        // 识别成功后，继续比对
        // 1、重量图片：比对录入重量和图片重量是否一致
        // 2、面单图片：比对录入单号和图片单号是否一致
        AutoReportingPicDto autoReportResult = autoReportingResponse.getPicList().get(0);
        String recognizedInfo = autoReportResult.getRecognizedInfo();
        if(Objects.equals(picType, SpotCheckConstants.SPOT_CHECK_AI_TYPE_WEIGHT)
                && (StringUtils.isEmpty(recognizedInfo) || !Objects.equals(weight, Double.parseDouble(recognizedInfo)))){
            return ImmutablePair.of(SpotCheckConstants.SPOT_CHECK_AI_EXC_CODE,
                    String.format(SpotCheckConstants.SPOT_CHECK_AI_WEIGHT_HIT, weight, recognizedInfo == null ? Constants.EMPTY_FILL : recognizedInfo));
        }
        if(Objects.equals(picType, SpotCheckConstants.SPOT_CHECK_AI_TYPE_FACE)
                && (StringUtils.isEmpty(recognizedInfo) || !Objects.equals(waybillCode, recognizedInfo))){
            return ImmutablePair.of(SpotCheckConstants.SPOT_CHECK_AI_EXC_CODE,
                    String.format(SpotCheckConstants.SPOT_CHECK_AI_FACE_HIT, waybillCode, recognizedInfo == null ? Constants.EMPTY_FILL : recognizedInfo));
        }
        return ImmutablePair.of(code, message);
    }

    @Override
    public InvokeResult<SpotCheckResult> checkIsExcessReform(SpotCheckContext spotCheckContext) {
        InvokeResult<SpotCheckResult> result = new InvokeResult<SpotCheckResult>();
        SpotCheckResult spotCheckResult = new SpotCheckResult();
        result.success();
        result.setData(spotCheckResult);

        ReportInfoQuery reportInfoQuery = new ReportInfoQuery();
        reportInfoQuery.setWaybillCode(spotCheckContext.getWaybillCode());
        reportInfoQuery.setChannel(SpotCheckSourceFromEnum.ARTIFICIAL_SOURCE.contains(spotCheckContext.getSpotCheckSourceFrom())
                ? SpotCheckConstants.ARTIFICIAL_SPOT_CHECK : SpotCheckConstants.EQUIPMENT_SPOT_CHECK);
        SpotCheckReviewDetail spotCheckReviewDetail = spotCheckContext.getSpotCheckReviewDetail();
        reportInfoQuery.setMeasureWeight(NumberHelper.formatMoney(spotCheckReviewDetail.getReviewTotalWeight()));
        reportInfoQuery.setMeasureVolume(NumberHelper.formatMoney(spotCheckReviewDetail.getReviewTotalVolume()));
        CommonDTO<ReportInfoDTO> commonDTO = weightReportCommonRuleManager.getReportInfo(reportInfoQuery);
        if(commonDTO == null || !Objects.equals(commonDTO.getCode(), CommonDTO.CODE_SUCCESS) || commonDTO.getData() == null){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,
                    commonDTO == null ? InvokeResult.SERVER_ERROR_MESSAGE : commonDTO.getMessage());
            return result;
        }
        ReportInfoDTO reportInfo = commonDTO.getData();
        spotCheckResult.setExcessStatus(Objects.equals(reportInfo.getExceedType(), OUT_EXCESS_STATUS)
                ? ExcessStatusEnum.EXCESS_ENUM_NO.getCode() : ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
        spotCheckResult.setExcessType(reportInfo.getExceedType());
        spotCheckResult.setIsMultiPack(spotCheckContext.getIsMultiPack());
        return result;
    }

    @Override
    public void assembleContrastData(SpotCheckContext spotCheckContext) {
        String waybillCode = spotCheckContext.getWaybillCode();
        SpotCheckReviewDetail spotCheckReviewDetail = spotCheckContext.getSpotCheckReviewDetail();
        Double reviewWeight = spotCheckReviewDetail.getReviewTotalWeight();
        Double reviewVolume = spotCheckReviewDetail.getReviewTotalVolume();

        ReportInfoQuery reportInfoQuery = new ReportInfoQuery();
        reportInfoQuery.setWaybillCode(waybillCode);
        reportInfoQuery.setChannel(SpotCheckSourceFromEnum.ARTIFICIAL_SOURCE.contains(spotCheckContext.getSpotCheckSourceFrom())
                ? SpotCheckConstants.ARTIFICIAL_SPOT_CHECK : SpotCheckConstants.EQUIPMENT_SPOT_CHECK);
        reportInfoQuery.setMeasureWeight(NumberHelper.formatMoney(reviewWeight));
        reportInfoQuery.setMeasureVolume(NumberHelper.formatMoney(reviewVolume));
        CommonDTO<ReportInfoDTO> commonDTO = weightReportCommonRuleManager.getReportInfo(reportInfoQuery);
        ReportInfoDTO reportInfo = (commonDTO == null || !Objects.equals(commonDTO.getCode(), CommonDTO.CODE_SUCCESS)) ? null : commonDTO.getData();
        // 组装核对数据
        SpotCheckContrastDetail spotCheckContrastDetail = spotCheckContext.getSpotCheckContrastDetail();
        spotCheckContrastDetail.setContrastSourceFrom(reportInfo == null ? null : reportInfo.getRecheckSource());
        spotCheckContrastDetail.setContrastWeight((reportInfo == null || reportInfo.getRecheckWeight() == null)
                ? null : MathUtils.keepScale(Double.parseDouble(reportInfo.getRecheckWeight()), 2));
        spotCheckContrastDetail.setContrastVolume((reportInfo == null || reportInfo.getRecheckVolume() == null)
                ? null : MathUtils.keepScale(Double.parseDouble(reportInfo.getRecheckVolume()), 2));
        spotCheckContrastDetail.setContrastOrgId(reportInfo == null ? null : reportInfo.getDutyOrgId());
        spotCheckContrastDetail.setContrastOrgName(reportInfo == null ? null : reportInfo.getDutyOrgName());
        spotCheckContrastDetail.setContrastWarZoneCode(reportInfo == null ? null : reportInfo.getDutyProvinceCompanyCode());
        spotCheckContrastDetail.setContrastWarZoneName(reportInfo == null ? null : reportInfo.getDutyProvinceCompanyName());
        spotCheckContrastDetail.setContrastAreaCode(reportInfo == null ? null : reportInfo.getDutyAreaCode());
        spotCheckContrastDetail.setContrastAreaName(reportInfo == null ? null : reportInfo.getDutyAreaName());
        spotCheckContrastDetail.setContrastSiteCode(reportInfo == null ? null : reportInfo.getDutySiteId());
        spotCheckContrastDetail.setContrastSiteName(reportInfo == null ? null : reportInfo.getDutySiteName());
        spotCheckContrastDetail.setContrastOperateUserErp(reportInfo == null ? null : reportInfo.getDutyStaffAccount());
        spotCheckContrastDetail.setContrastOperateUserName(reportInfo == null ? null : reportInfo.getDutyStaffName());
        spotCheckContrastDetail.setContrastOperateUserAccountType(reportInfo == null ? null : reportInfo.getDutyStaffType());
        spotCheckContrastDetail.setDutyType(reportInfo == null ? null : reportInfo.getDutyType());
        // 超标数据
        int excessStatus = ExcessStatusEnum.EXCESS_ENUM_NO.getCode();
        if(reportInfo != null && reportInfo.getExceedType() != null){
            excessStatus = Objects.equals(reportInfo.getExceedType(), OUT_EXCESS_STATUS)
                    ? ExcessStatusEnum.EXCESS_ENUM_NO.getCode() : ExcessStatusEnum.EXCESS_ENUM_YES.getCode();
        }
        spotCheckContext.setExcessStatus(excessStatus);
        spotCheckContext.setExcessType(reportInfo == null ? null : reportInfo.getExceedType());
        spotCheckContext.setDiffWeight((reportInfo == null || reportInfo.getDiffWeight() == null) ? null : Double.parseDouble(reportInfo.getDiffWeight()));
        spotCheckContext.setDiffStandard(reportInfo == null ? null : reportInfo.getDiffStandard());
        spotCheckContext.setVolumeRate((reportInfo == null || reportInfo.getConvertCoefficient() == null)
                ? null : Integer.valueOf(reportInfo.getConvertCoefficient()));
    }

    @Override
    public void spotCheckIssue(WeightVolumeSpotCheckDto spotCheckDto) {
        // 下发前置条件：超标&&集齐
        if(!Objects.equals(spotCheckDto.getIsExcess(), ExcessStatusEnum.EXCESS_ENUM_YES.getCode())){
            return;
        }
        if(!Objects.equals(spotCheckDto.getIsGatherTogether(), Constants.CONSTANT_NUMBER_ONE)){
            return;
        }
        // 设备抽检需校验图片是否合规
        if(SpotCheckSourceFromEnum.EQUIPMENT_SOURCE_NUM.contains(spotCheckDto.getReviewSource())
                && isExecuteDwsAIDistinguish(spotCheckDto.getReviewSiteCode())){
            if(logger.isInfoEnabled()){
                logger.info("设备抽检图片AI识别下发运单:{}站点:{}的数据!", spotCheckDto.getWaybillCode(), spotCheckDto.getReviewSiteCode());
            }
            String picUrl = StringUtils.isEmpty(spotCheckDto.getPictureAddress())
                    ? getSpotCheckPackUrlFromCache(spotCheckDto.getPackageCode(), spotCheckDto.getReviewSiteCode()) : spotCheckDto.getPictureAddress();
            if(StringUtils.isEmpty(picUrl)){
                return;
            }
            List<DwsAIDistinguishMQ.Package> list = new ArrayList<>();
            DwsAIDistinguishMQ.Package packageUrl = new DwsAIDistinguishMQ.Package();
            packageUrl.setPackageCode(spotCheckDto.getPackageCode());
            packageUrl.setPicUrl(picUrl.replace(STORAGE_DOMAIN_COM, STORAGE_DOMAIN_LOCAL));
            list.add(packageUrl);
            DwsAIDistinguishMQ dwsAIDistinguishMQ = new DwsAIDistinguishMQ();
            dwsAIDistinguishMQ.setUuid(spotCheckDto.getWaybillCode().concat(Constants.SEPARATOR_HYPHEN).concat(String.valueOf(System.currentTimeMillis())));
            dwsAIDistinguishMQ.setWaybillCode(spotCheckDto.getWaybillCode());
            dwsAIDistinguishMQ.setSiteCode(spotCheckDto.getReviewSiteCode());
            dwsAIDistinguishMQ.setPackages(list);
            dwsAIDistinguishSmallProducer.sendOnFailPersistent(dwsAIDistinguishMQ.getWaybillCode(), JsonHelper.toJson(dwsAIDistinguishMQ));
            return;
        }
        // 执行下发
        executeIssue(spotCheckDto);
    }

    @Override
    public void executeIssue(WeightVolumeSpotCheckDto spotCheckDto) {
        // 校验运单是否已下发
        if(checkWaybillHasIssued(spotCheckDto.getWaybillCode())){
            logger.info("spotCheckWaybill has issued will not send {}", spotCheckDto.getWaybillCode());
            return;
        }
        if(logger.isInfoEnabled()){
            logger.info("称重流程再造：下发运单号:{}站点:{}的超标数据!", spotCheckDto.getWaybillCode(), spotCheckDto.getReviewSiteCode());
        }
        // 体积超标下发终端条件：体积超标&&设备抽检
        if(SpotCheckSourceFromEnum.EQUIPMENT_SOURCE_NUM.contains(spotCheckDto.getReviewSource())
                && Objects.equals(spotCheckDto.getExcessType(), SpotCheckConstants.EXCESS_TYPE_VOLUME)){
            SpotCheckIssueZDMQ spotCheckIssueZDMQ = new SpotCheckIssueZDMQ();
            spotCheckIssueZDMQ.setWaybillCode(spotCheckDto.getWaybillCode());
            spotCheckIssueZDMQ.setSiteCode(spotCheckDto.getReviewSiteCode());
            spotCheckIssueZDMQ.setOperateErp(spotCheckDto.getReviewUserErp());
            spotCheckIssueZDMQ.setOperateUserName(spotCheckDto.getReviewUserName());
            spotCheckIssueZDMQ.setReviewWeight(spotCheckDto.getReviewWeight());
            spotCheckIssueZDMQ.setReviewVolume(spotCheckDto.getReviewVolume());
            spotCheckIssueZDMQ.setReviewTime(spotCheckDto.getReviewDate());
            spotCheckIssueZDProducer.sendOnFailPersistent(spotCheckIssueZDMQ.getWaybillCode(), JsonHelper.toJson(spotCheckIssueZDMQ));
        }
        // 下发超标数据至称重再造系统条件
        // 1、图片
        // 1.1、人工抽检：图片肯定有（都是按运单维度操作，肯定有5张图片）
        // 1.2、设备抽检：一单一件下发一张则必须要有图片，一单多件不下发图片则不需要图片限制条件
        if(
                (SpotCheckSourceFromEnum.ARTIFICIAL_SOURCE_NUM.contains(spotCheckDto.getReviewSource())
                        || !Objects.equals(spotCheckDto.getIsMultiPack(), Constants.CONSTANT_NUMBER_ONE))
                && !Objects.equals(spotCheckDto.getIsHasPicture(), Constants.CONSTANT_NUMBER_ONE)
        ){
            return;
        }
        // 设置已下发缓存
        setIssueWaybillCache(spotCheckDto.getWaybillCode());
        // 构建超标消息并下发
        SpotCheckIssueMQ spotCheckIssueMQ = new SpotCheckIssueMQ();
        spotCheckIssueMQ.setFlowSystem(SpotCheckSourceFromEnum.ARTIFICIAL_SOURCE_NUM.contains(spotCheckDto.getReviewSource())
                ? SpotCheckConstants.ARTIFICIAL_SPOT_CHECK : SpotCheckConstants.EQUIPMENT_SPOT_CHECK);
        spotCheckIssueMQ.setInitiationLink(String.valueOf(SpotCheckConstants.DMS_SPOT_CHECK_ISSUE));
        spotCheckIssueMQ.setSysSource(SpotCheckSourceFromEnum.ARTIFICIAL_SOURCE_NUM.contains(spotCheckDto.getReviewSource())
                ? String.valueOf(2) : String.valueOf(1)); // 1：设备抽检 2：人工抽检
        spotCheckIssueMQ.setFlowId(spotCheckIssueMQ.getFlowSystem() + Constants.UNDERLINE_FILL + spotCheckDto.getWaybillCode()
                + Constants.UNDERLINE_FILL + spotCheckDto.getReviewSiteCode());
        spotCheckIssueMQ.setOperateType(Constants.CONSTANT_NUMBER_ONE);
        spotCheckIssueMQ.setWaybillCode(spotCheckDto.getWaybillCode());
        spotCheckIssueMQ.setDutyType(spotCheckDto.getContrastDutyType());
        spotCheckIssueMQ.setDutyRegionCode(String.valueOf(spotCheckDto.getContrastOrgCode()));
        spotCheckIssueMQ.setDutyRegion(spotCheckDto.getContrastOrgName());
        spotCheckIssueMQ.setDutyProvinceCompanyCode(spotCheckDto.getContrastWarZoneCode());
        spotCheckIssueMQ.setDutyProvinceCompanyName(spotCheckDto.getContrastWarZoneName());
        spotCheckIssueMQ.setDutyAreaCode(spotCheckDto.getContrastAreaCode());
        spotCheckIssueMQ.setDutyAreaName(spotCheckDto.getContrastAreaName());
        spotCheckIssueMQ.setDutyOrgCode(String.valueOf(spotCheckDto.getContrastSiteCode()));
        spotCheckIssueMQ.setDutyOrgName(spotCheckDto.getContrastSiteName());
        spotCheckIssueMQ.setDutyStaffAccount(spotCheckDto.getContrastStaffAccount());
        spotCheckIssueMQ.setDutyStaffName(spotCheckDto.getContrastStaffName());
        spotCheckIssueMQ.setDutyStaffType(spotCheckDto.getContrastStaffType());
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        spotCheckIssueMQ.setConfirmWeight(spotCheckDto.getContrastWeight() == null ? null : decimalFormat.format(spotCheckDto.getContrastWeight()));
        spotCheckIssueMQ.setConfirmVolume(spotCheckDto.getContrastVolume() == null ? null : decimalFormat.format(spotCheckDto.getContrastVolume()));
        spotCheckIssueMQ.setStartStaffAccount(spotCheckDto.getReviewUserErp());
        spotCheckIssueMQ.setStartStaffName(spotCheckDto.getReviewUserName());
        spotCheckIssueMQ.setStartStaffType(Constants.CONSTANT_NUMBER_ONE);
        spotCheckIssueMQ.setStartRegionCode(String.valueOf(spotCheckDto.getReviewOrgCode()));
        spotCheckIssueMQ.setStartRegion(spotCheckDto.getReviewOrgName());
        spotCheckIssueMQ.setOrgCode(String.valueOf(spotCheckDto.getReviewSiteCode()));
        spotCheckIssueMQ.setOrgName(spotCheckDto.getReviewSiteName());
        spotCheckIssueMQ.setReConfirmLong(spotCheckDto.getReviewLength() == null ? null : String.valueOf(spotCheckDto.getReviewLength()));
        spotCheckIssueMQ.setReConfirmWidth(spotCheckDto.getReviewWidth() == null ? null : String.valueOf(spotCheckDto.getReviewWidth()));
        spotCheckIssueMQ.setReConfirmHigh(spotCheckDto.getReviewHeight() == null ? null : String.valueOf(spotCheckDto.getReviewHeight()));
        spotCheckIssueMQ.setReConfirmWeight(spotCheckDto.getReviewWeight() == null ? String.valueOf(Constants.DOUBLE_ZERO) : String.valueOf(spotCheckDto.getReviewWeight()));
        spotCheckIssueMQ.setReConfirmVolume(spotCheckDto.getReviewVolume() == null ? String.valueOf(Constants.DOUBLE_ZERO) : String.valueOf(spotCheckDto.getReviewVolume()));
        spotCheckIssueMQ.setConvertCoefficient(spotCheckDto.getVolumeRate() == null ? null : String.valueOf(spotCheckDto.getVolumeRate()));
        spotCheckIssueMQ.setConfirmWeightSource(spotCheckDto.getContrastSource());
        spotCheckIssueMQ.setDiffWeight(spotCheckDto.getDiffWeight() == null ? null : decimalFormat.format(spotCheckDto.getDiffWeight()));
        spotCheckIssueMQ.setStanderDiff(spotCheckDto.getDiffStandard());
        spotCheckIssueMQ.setExceedType(spotCheckDto.getExcessType());
        spotCheckIssueMQ.setStatus(spotCheckDto.getSpotCheckStatus());
        spotCheckIssueMQ.setAppendix(Constants.CONSTANT_NUMBER_ONE);
        spotCheckIssueMQ.setUrl(picUrlDeal(spotCheckDto));
        spotCheckIssueMQ.setStartTime(new Date());
        spotCheckIssueProducer.sendOnFailPersistent(spotCheckIssueMQ.getWaybillCode(), JsonHelper.toJson(spotCheckIssueMQ));
        // 更新抽检主记录数据
        spotCheckDto.setIsIssueDownstream(Constants.CONSTANT_NUMBER_ONE);
        spotCheckServiceProxy.insertOrUpdateProxyReform(spotCheckDto);
    }

    private List<String> picUrlDeal(WeightVolumeSpotCheckDto spotCheckDto) {
        List<String> picList = new ArrayList<>();
        if(SpotCheckSourceFromEnum.ARTIFICIAL_SOURCE_NUM.contains(spotCheckDto.getReviewSource())){
            // 人工抽检（只有运单维度，无包裹维度）
            List<String> picUrlList = Arrays.asList(spotCheckDto.getPictureAddress().split(Constants.SEPARATOR_SEMICOLON));
            if(picUrlList.size() < 5){
                logger.warn("运单:{}的抽检图片未集齐!", spotCheckDto.getWaybillCode());
                return picList;
            }
            if(Objects.equals(spotCheckDto.getExcessType(), SpotCheckConstants.EXCESS_TYPE_WEIGHT)){
                // 1）、重量超标：2张，面单和重量
                picList.add(replaceInOut(picUrlList.get(0)));
                picList.add(replaceInOut(picUrlList.get(1)));
                return picList;
            }else {
                // 2）、体积超标：5张，面单、全景、长、宽、高
                for (String picUrl : picUrlList) {
                    picList.add(replaceInOut(picUrl));
                }
                return picList;
            }
        }
        if(SpotCheckSourceFromEnum.EQUIPMENT_SOURCE_NUM.contains(spotCheckDto.getReviewSource())){
            // 设备抽检：
            // 1）、一单一件下发一张
            // 2）、一单多件不下发图片
            if(!Objects.equals(spotCheckDto.getIsMultiPack(), Constants.CONSTANT_NUMBER_ONE)){
                picList.add(replaceInOut(spotCheckDto.getPictureAddress()));
            }
        }
        return picList;
    }

    private String replaceInOut(String inAddress) {
        if(StringUtils.isEmpty(inAddress)){
            return Constants.EMPTY_FILL;
        }
        return inAddress.replace(STORAGE_DOMAIN_LOCAL, STORAGE_DOMAIN_COM);
    }

    @Override
    public String uploadExcessPicture(String originalFileName, InputStream inStream){
        String extName = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        String url = null;
        ByteArrayOutputStream swapStream = null;
        try {
            swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int rc;
            while ((rc = inStream.read(buff, 0, 1024)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            byte[] in2b = swapStream.toByteArray();
            inStream.close();
            swapStream.close();
            url = jssService.uploadFile(bucket, in2b, extName);
            logger.info("图片上传成功 : url[{}]", url);
        } catch (Exception e) {
            logger.error("图片上传时发生异常", e);
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    logger.error("图片上传输入流关闭异常", e);
                }
            }
            if (swapStream != null) {
                try {
                    swapStream.close();
                } catch (IOException e) {
                    logger.error("图片上传输出流关闭异常", e);
                }
            }
        }
        return url;
    }

    private void oncePackIssueDeal(WeightAndVolumeCheckHandleMessage message, List<WeightVolumeCollectDto> accordList) {
        boolean packageSendStatus = checkIsHasSend(message.getPackageCode(), message.getSiteCode());
        if(!packageSendStatus){
            logger.warn("包裹号:{}站点:{}未操作发货!", message.getPackageCode(), message.getSiteCode());
            return;
        }
        WeightVolumeCollectDto weightVolumeCollectDto = accordList.get(0);
        if(StringUtils.isEmpty(weightVolumeCollectDto.getPictureAddress())){
            // 图片还未写入则从缓存中再获取一次
            weightVolumeCollectDto.setPictureAddress(getSpotCheckPackUrlFromCache(message.getPackageCode(), message.getSiteCode()));
        }
        issueSpotCheckDetail(weightVolumeCollectDto);
    }

    private void multiPackIssueDeal(WeightAndVolumeCheckHandleMessage message, List<WeightVolumeCollectDto> accordList){
        String waybillCode = message.getWaybillCode();
        String packageCode = message.getPackageCode();
        Integer siteCode = message.getSiteCode();
        // 是否超标 、 包裹是否都有图片、包裹是否发货
        WeightVolumeCollectDto waybillCollect = null;
        for (WeightVolumeCollectDto collectDto : accordList) {
            String loopPackageCode = collectDto.getPackageCode();
            if(Objects.equals(collectDto.getIsWaybillSpotCheck(), SpotCheckDimensionEnum.SPOT_CHECK_PACK.getCode())){
                // notes：包裹维度抽检(一条总记录多条包裹记录)
                // 1、校验总记录是否超标
                if(Objects.equals(collectDto.getRecordType(), SpotCheckRecordTypeEnum.SUMMARY_RECORD.getCode())){
                    if(Objects.equals(collectDto.getIsExcess(), ExcessStatusEnum.EXCESS_ENUM_NO.getCode())){
                        logger.warn("运单号:{}站点:{}抽检还未判定超标!", waybillCode, siteCode);
                        return;
                    }
                    waybillCollect = collectDto;
                }
                if(Objects.equals(collectDto.getRecordType(), SpotCheckRecordTypeEnum.DETAIL_RECORD.getCode())){
                    // 2、校验包裹维度记录是否有图片（es中没有则从缓存中获取）
                    if(StringUtils.isEmpty(collectDto.getPictureAddress()) && StringUtils.isEmpty(getSpotCheckPackUrlFromCache(loopPackageCode, siteCode))){
                        logger.warn("包裹号:{}站点:{}的图片还未上传!", loopPackageCode, siteCode);
                        return;
                    }
                    // 3、校验包裹维度记录是否发货（es中没有则从缓存中获取）
                    if(!Objects.equals(collectDto.getWaybillStatus(), 2) && StringUtils.isEmpty(getSpotCheckSendStatusFromCache(loopPackageCode, siteCode))){
                        logger.warn("包裹号:{}站点:{}未发货!", loopPackageCode, siteCode);
                        return;
                    }
                }
                if(collectDto.getRecordType() == null){
                    // 兼容上线期间的历史数据
                    waybillCollect = collectDto;
                    collectDto.setPictureAddress(getSpotCheckPackUrlFromCache(loopPackageCode, siteCode));
                    break;
                }
            }else {
                // notes：运单维度抽检（只有一条总记录）（只有发货动作才会进入，上传图片动作不会进入）
                // 1、校验是否超标和是否有图片
                if(!Objects.equals(collectDto.getIsExcess(), ExcessStatusEnum.EXCESS_ENUM_YES.getCode()) || StringUtils.isEmpty(collectDto.getPictureAddress())){
                    logger.warn("运单号:{}站点:{}的抽检记录未超标或无图片不下发!", waybillCode, siteCode);
                    return;
                }
                // 2、校验运单下所有包裹是否发货
                if(!checkWaybillHasAllSend(packageCode, siteCode)){
                    logger.warn("站点:{},运单号:{}下的包裹未全部发货!", waybillCode, siteCode);
                    return;
                }
                waybillCollect = collectDto;
                break;
            }
        }
        if(waybillCollect == null){
            logger.warn("不存在运单号:{}站点:{}的运单维度记录", waybillCode, siteCode);
            return;
        }
        // 下发超标数据
        issueSpotCheckDetail(waybillCollect);
    }

    /**
     * 校验运单是否已下发fxm
     *
     * @param waybillCode
     * @return
     */
    private boolean checkWaybillHasIssued(String waybillCode) {
        try {
            String cacheFxmSendWaybillKey = String.format(CacheKeyConstants.CACHE_FXM_SEND_WAYBILL, waybillCode);
            return jimdbCacheService.exists(cacheFxmSendWaybillKey);
        }catch (Exception e){
            logger.error("校验运单号:{}是否下发超标mq异常!", waybillCode);
        }
        return false;
    }

    /**
     * 已抽检包裹发货完成处理
     *
     * @param message
     * @return
     */
    private InvokeResult<Boolean> spotCheckPackSendDeal(WeightAndVolumeCheckHandleMessage message, List<WeightVolumeCollectDto> accordList){
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.setData(true);
        Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(message.getWaybillCode());
        boolean isMultiplePackage = isMultiPack(waybill, message.getPackageCode());
        // 更新发货状态
        updateSendStatus(message.getPackageCode(), message.getSiteCode(), isMultiplePackage, accordList.size());
        // 下发处理
        if(!isMultiplePackage){
            // notes：一单一件（已发货状态）
            // 判断是否有图片、判断是否超标
            WeightVolumeCollectDto issueCollect = accordList.get(0);
            String picUrl = StringUtils.isEmpty(issueCollect.getPictureAddress())
                    ? getSpotCheckPackUrlFromCache(message.getPackageCode(), message.getSiteCode()) : issueCollect.getPictureAddress();
            issueCollect.setPictureAddress(picUrl);
            if(StringUtils.isEmpty(picUrl)){
                logger.warn("根据运单号:{}站点:{}获取到的抽检记录无图片!", message.getWaybillCode(), message.getSiteCode());
                return result;
            }
            if(!Objects.equals(issueCollect.getIsExcess(), ExcessStatusEnum.EXCESS_ENUM_YES.getCode())){
                logger.warn("根据运单号:{}站点:{}获取到的抽检记录未超标!", message.getWaybillCode(), message.getSiteCode());
                return result;
            }
            // 下发fxm
            issueSpotCheckDetail(issueCollect);
        } else {
            // 多包裹设置运单下已发货包裹缓存
            setWaybillSendPackCache(message.getPackageCode(), message.getSiteCode());
            // 一单多件下发处理
            multiPackIssueDeal(message, accordList);
        }
        return result;
    }

    /**
     * 设置运单下已发货包裹缓存
     *
     * @param packageCode
     * @param siteCode
     */
    private void setWaybillSendPackCache(String packageCode, Integer siteCode) {
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);
        Set<String> waybillSendPacks = new HashSet<>();
        waybillSendPacks.add(packageCode);
        try {
            String key = String.format(CacheKeyConstants.CACHE_KEY_WAYBILL_SEND_PACKS_STATUS, siteCode, waybillCode);
            String waybillHasSendPackCache = jimdbCacheService.get(key);
            if(StringUtils.isEmpty(waybillHasSendPackCache)){
                List<String> sendPackList = getHasSendPacks(waybillCode, siteCode);
                if(CollectionUtils.isNotEmpty(getHasSendPacks(waybillCode, siteCode))){
                    waybillSendPacks.addAll(sendPackList);
                }
            }else {
                Set set = JsonHelper.fromJson(waybillHasSendPackCache, Set.class);
                waybillSendPacks.addAll(set);
            }
            jimdbCacheService.setEx(key, JsonHelper.toJson(waybillSendPacks), 30, TimeUnit.MINUTES);
        }catch (Exception e){
            logger.error("设置运单:{}下已发货缓存异常!", waybillCode, e);
        }
    }

    private List<String> getHasSendPacks(String waybillCode, Integer siteCode){
        SendDetailDto params = new SendDetailDto();
        params.setCreateSiteCode(siteCode);
        params.setWaybillCode(waybillCode);
        params.setIsCancel(Constants.NUMBER_ZERO);
        params.setStatus(Constants.CONSTANT_NUMBER_ONE);
        return sendDetailService.queryPackageByWaybillCode(params);
    }

    /**
     * 更新抽检记录发货状态
     *
     * @param packageCode
     * @param siteCode
     * @param isMultiplePackage
     * @param count
     */
    private void updateSendStatus(String packageCode, Integer siteCode, boolean isMultiplePackage, int count) {
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);
        WeightVolumeCollectDto updateWeightVolumeCollectDto = new WeightVolumeCollectDto();
        updateWeightVolumeCollectDto.setWaybillCode(waybillCode);
        // notes：
        //  1、一单一件该字段是运单号（es中只有一条记录）
        //  2、一单多件运单维度抽检该字段是运单号（es中只有一条记录）
        //  3、一单多件包裹维度抽检（es中一条总记录 和 多条包裹维度记录）
        String barCode = !isMultiplePackage ? waybillCode : (count == 1) ? waybillCode : packageCode;
        updateWeightVolumeCollectDto.setPackageCode(barCode);
        updateWeightVolumeCollectDto.setReviewSiteCode(siteCode);
        updateWeightVolumeCollectDto.setWaybillStatus(WaybillStatus.WAYBILL_STATUS_CODE_FORWORD_DELIVERY);
        spotCheckServiceProxy.insertOrUpdateProxyPrevious(updateWeightVolumeCollectDto);
    }

    @Override
    public String getSpotCheckPackUrlFromCache(String packageCode, Integer siteCode) {
        try {
            String key = String.format(CacheKeyConstants.CACHE_SPOT_CHECK_PICTURE, packageCode, siteCode);
            return jimdbCacheService.get(key);
        }catch (Exception e){
            logger.error("根据包裹号:{}站点:{}获取图片url异常!", packageCode, siteCode, e);
        }
        return null;
    }

    private String getSpotCheckSendStatusFromCache(String packageCode, Integer siteCode) {
        try {
            String key = String.format(CacheKeyConstants.CACHE_KEY_WAYBILL_SEND_STATUS, siteCode, packageCode);
            return jimdbCacheService.get(key);
        }catch (Exception e){
            logger.error("根据包裹号:{}站点:{}获取发货状态异常!", packageCode, siteCode, e);
        }
        return null;
    }

    private boolean checkWaybillHasAllSend(String packageCode, Integer siteCode) {
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);
        Set<String> finalSet = new HashSet<>();
        try {
            String key = String.format(CacheKeyConstants.CACHE_KEY_WAYBILL_SEND_PACKS_STATUS, siteCode, waybillCode);
            String waybillHasSendPackCache = jimdbCacheService.get(key);
            if(StringUtils.isEmpty(waybillHasSendPackCache)){
                finalSet.addAll(getHasSendPacks(waybillCode, siteCode));
            }else {
                Set<String> waybillHasSendPackSet = JsonHelper.fromJson(waybillHasSendPackCache, Set.class);
                finalSet.addAll(waybillHasSendPackSet);
            }
            return finalSet.size() == WaybillUtil.getPackNumByPackCode(packageCode);
        }catch (Exception e){
            logger.error("根据运单号:{}站点:{}获取已发货包裹缓存异常!", waybillCode, siteCode, e);
        }
        return false;
    }

    private void addPicUrlCache(String packageCode, Integer siteCode, String pictureUrl) {
        try {
            String key = String.format(CacheKeyConstants.CACHE_SPOT_CHECK_PICTURE, packageCode, siteCode);
            jimdbCacheService.setEx(key, pictureUrl, 30, TimeUnit.MINUTES);
        }catch (Exception e){
            logger.error("设置站点{}上传的包裹{}图片链接缓存异常!", siteCode, packageCode);
        }
    }

    private boolean isMultiPack(Waybill waybill, String packageCode) {
        int packNum;
        if(WaybillUtil.isPackageCode(packageCode)){
            packNum = WaybillUtil.getPackNumByPackCode(packageCode);
        }else {
            packNum = (waybill == null || waybill.getGoodNumber() ==  null) ? Constants.NUMBER_ZERO : waybill.getGoodNumber();
        }
        return packNum > Constants.CONSTANT_NUMBER_ONE;
    }

    private AbnormalResultMq buildCommonAttr(WeightVolumeCollectDto weightVolumeCollectDto) {
        AbnormalResultMq abnormalResultMq = new AbnormalResultMq();
        abnormalResultMq.setId(weightVolumeCollectDto.getWaybillCode() + Constants.UNDERLINE_FILL + weightVolumeCollectDto.getReviewDate().getTime());
        abnormalResultMq.setAbnormalId(weightVolumeCollectDto.getWaybillCode() + Constants.UNDERLINE_FILL + weightVolumeCollectDto.getReviewDate().getTime());
        abnormalResultMq.setSource(SpotCheckSystemEnum.DMS.getCode());
        abnormalResultMq.setFrom(String.valueOf(SpotCheckSystemEnum.DMS.getCode()));
        abnormalResultMq.setBusinessType(BusinessHelper.translateSpotCheckTypeToBusinessType(weightVolumeCollectDto.getSpotCheckType()));
        abnormalResultMq.setIsExcess(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
        // 复核数据
        abnormalResultMq.setBillCode(weightVolumeCollectDto.getWaybillCode());
        abnormalResultMq.setBusinessObjectId(weightVolumeCollectDto.getBusiCode());
        abnormalResultMq.setBusinessObject(weightVolumeCollectDto.getBusiName());
        abnormalResultMq.setOperateTime(weightVolumeCollectDto.getReviewDate());
        abnormalResultMq.setIsTrustMerchant(weightVolumeCollectDto.getIsTrustBusi());
        abnormalResultMq.setReviewWeight(weightVolumeCollectDto.getReviewWeight());
        abnormalResultMq.setReviewVolume(weightVolumeCollectDto.getReviewVolume());
        abnormalResultMq.setReviewErp(weightVolumeCollectDto.getReviewErp());
        abnormalResultMq.setReviewFirstLevelId(weightVolumeCollectDto.getReviewOrgCode());
        abnormalResultMq.setReviewFirstLevelName(weightVolumeCollectDto.getReviewOrgName());
        abnormalResultMq.setReviewSecondLevelId(weightVolumeCollectDto.getReviewSiteCode());
        abnormalResultMq.setReviewSecondLevelName(weightVolumeCollectDto.getReviewSiteName());
        abnormalResultMq.setReviewDate(weightVolumeCollectDto.getReviewDate());
        abnormalResultMq.setReviewDutyErp(weightVolumeCollectDto.getReviewErp());
        abnormalResultMq.setReviewDutyType(DutyTypeEnum.DMS.getCode());
        // 核对数据
        abnormalResultMq.setFirstLevelId(String.valueOf(weightVolumeCollectDto.getBillingOrgCode()));
        abnormalResultMq.setFirstLevelName(weightVolumeCollectDto.getBillingOrgName());
        abnormalResultMq.setSecondLevelId(weightVolumeCollectDto.getBillingDeptCodeStr());
        abnormalResultMq.setSecondLevelName(weightVolumeCollectDto.getBillingDeptName());
        abnormalResultMq.setThreeLevelId(weightVolumeCollectDto.getBillingThreeLevelId());
        abnormalResultMq.setThreeLevelName(weightVolumeCollectDto.getBillingThreeLevelName());
        abnormalResultMq.setWeight(weightVolumeCollectDto.getBillingWeight() == null
                ? new BigDecimal(Constants.DOUBLE_ZERO) : BigDecimal.valueOf(weightVolumeCollectDto.getBillingWeight()));
        abnormalResultMq.setVolume(weightVolumeCollectDto.getBillingVolume() == null
                ? new BigDecimal(Constants.DOUBLE_ZERO) : BigDecimal.valueOf(weightVolumeCollectDto.getBillingVolume()));
        abnormalResultMq.setDutyType(weightVolumeCollectDto.getDutyType());
        abnormalResultMq.setDutyErp(weightVolumeCollectDto.getBillingErp());
        // 差异数据
        abnormalResultMq.setDiffStandard(weightVolumeCollectDto.getDiffStandard());
        abnormalResultMq.setWeightDiff(StringUtils.isEmpty(weightVolumeCollectDto.getWeightDiff())
                ? null : Double.parseDouble(weightVolumeCollectDto.getWeightDiff()));
        abnormalResultMq.setVolumeDiff(StringUtils.isEmpty(weightVolumeCollectDto.getVolumeWeightDiff())
                ? null : Double.parseDouble(weightVolumeCollectDto.getVolumeWeightDiff()));
        return abnormalResultMq;
    }

    private void bAssembleIssueSpotCheckDetail(WeightVolumeCollectDto weightVolumeCollectDto, AbnormalResultMq abnormalResultMq) {
        abnormalResultMq.setInputMode(weightVolumeCollectDto.getIsWaybillSpotCheck());
        if(DutyTypeEnum.FLEET.getCode().equals(abnormalResultMq.getDutyType())){
            //责任类型为车队
            abnormalResultMq.setCarCaptionErp(weightVolumeCollectDto.getBillingErp());
        }
        abnormalResultMq.setDutyErp(weightVolumeCollectDto.getBillingErp());
        if(abnormalResultMq.getDutyType() != null){
            if(DutyTypeEnum.DMS.getCode().equals(abnormalResultMq.getDutyType())
                    || DutyTypeEnum.FLEET.getCode().equals(abnormalResultMq.getDutyType())
                    || (DutyTypeEnum.SITE.getCode().equals(abnormalResultMq.getDutyType())
                    &&StringUtils.isEmpty(abnormalResultMq.getDutyErp()))){
                //责任为分拣或车队或站点无erp
                abnormalResultMq.setTo(SpotCheckSystemEnum.ZHIKONG.getCode().toString());
            }else if(DutyTypeEnum.SITE.getCode().equals(abnormalResultMq.getDutyType())
                    && !StringUtils.isEmpty(abnormalResultMq.getDutyErp())){
                //责任为站点有erp
                StringBuilder to = new StringBuilder();
                to.append(SpotCheckSystemEnum.TMS.getCode()).append(Constants.SEPARATOR_COMMA).append(SpotCheckSystemEnum.ZHIKONG.getCode());
                abnormalResultMq.setTo(to.toString());
            }else if(DutyTypeEnum.BIZ.getCode().equals(abnormalResultMq.getDutyType())){
                //责任为信任商家
                abnormalResultMq.setTo(SpotCheckSystemEnum.PANZE.getCode().toString());
            }else {
                logger.warn("未知去向!");
            }
            // B网设备抽检都是to5
            if(SpotCheckSourceFromEnum.EQUIPMENT_SOURCE.contains(weightVolumeCollectDto.getFromSource())){
                abnormalResultMq.setTo(SpotCheckSystemEnum.PANZE.getCode().toString());
            }
        }
        // B网下发图片使用字段：detailList
        bIssueDownPicDeal(weightVolumeCollectDto, abnormalResultMq);
        if(SpotCheckSourceFromEnum.EQUIPMENT_SOURCE.contains(weightVolumeCollectDto.getFromSource())){
            // 设备抽检：B网默认认责不判责
            abnormalResultMq.setIsAccusation(0);
            abnormalResultMq.setIsNeedBlame(1);
        }else {
            // 默认不认责判责
            abnormalResultMq.setIsAccusation(1);
            abnormalResultMq.setIsNeedBlame(0);
        }
    }

    private void cAssembleIssueSpotCheckDetail(WeightVolumeCollectDto weightVolumeCollectDto, AbnormalResultMq abnormalResultMq) {
        if(Objects.equals(abnormalResultMq.getDutyType(), SpotCheckSystemEnum.DMS.getCode())
                || Objects.equals(abnormalResultMq.getDutyType(), SpotCheckSystemEnum.TMS.getCode())){
            //分拣、站点发给下游判责
            abnormalResultMq.setTo(SpotCheckSystemEnum.PANZE.getCode().toString());
        }else if(Objects.equals(abnormalResultMq.getDutyType(), SpotCheckSystemEnum.JIFEI.getCode())){
            //仓发给下游质控
            abnormalResultMq.setTo(SpotCheckSystemEnum.ZHIKONG.getCode().toString());
        }else {
            logger.warn("未知去向!");
        }
        // C网下发图片使用字段：pictureAddress
        cIssueDownPicDeal(weightVolumeCollectDto, abnormalResultMq);
        // 默认认责不判责
        abnormalResultMq.setIsAccusation(0);
        abnormalResultMq.setIsNeedBlame(1);
    }

    /**
     * B网下发图片处理
     *  hit：detailList字段存储（早期约定存储5张，现在不足5张的也存储进去）
     *      1、人工抽检：一单一件5张（es中只有一条记录也存储了图片链接），一单多件不下发图片
     *      2、客户端抽检：只有一单一件（es中只有一条记录也存储了图片链接）
     *      3、dws抽检：一单一件1张（es中只有一条记录也存储了图片链接），一单多件不下发图片
     *      4、页面抽检：只有运单维度，总共5张（es中只有一条记录也存储了图片链接）
     *      5、转运安卓抽检：只有运单维度，总共5张（es中只有一条记录也存储了图片链接）
     * @param weightVolumeCollectDto
     * @param abnormalResultMq
     */
    private void bIssueDownPicDeal(WeightVolumeCollectDto weightVolumeCollectDto, AbnormalResultMq abnormalResultMq) {
        List<SpotCheckOfPackageDetail> detailList = new ArrayList<>();
        abnormalResultMq.setDetailList(detailList);
        if(
            (
                (Objects.equals(weightVolumeCollectDto.getFromSource(), SpotCheckSourceFromEnum.SPOT_CHECK_ARTIFICIAL.getName())
                || Objects.equals(weightVolumeCollectDto.getFromSource(), SpotCheckSourceFromEnum.SPOT_CHECK_CLIENT_PLATE.getName())
                || Objects.equals(weightVolumeCollectDto.getFromSource(), SpotCheckSourceFromEnum.SPOT_CHECK_DWS.getName()))
                && Objects.equals(weightVolumeCollectDto.getMultiplePackage(), Constants.NUMBER_ZERO)
            )
            || (Objects.equals(weightVolumeCollectDto.getFromSource(), SpotCheckSourceFromEnum.SPOT_CHECK_ARTIFICIAL.getName())
                    && Objects.equals(weightVolumeCollectDto.getIsWaybillSpotCheck(), SpotCheckDimensionEnum.SPOT_CHECK_WAYBILL.getCode()))
            || Objects.equals(weightVolumeCollectDto.getFromSource(), SpotCheckSourceFromEnum.SPOT_CHECK_DMS_WEB.getName())
            || Objects.equals(weightVolumeCollectDto.getFromSource(), SpotCheckSourceFromEnum.SPOT_CHECK_ANDROID.getName())
        ){
            SpotCheckOfPackageDetail detail = new SpotCheckOfPackageDetail();
            detail.setBillCode(weightVolumeCollectDto.getWaybillCode());
            detail.setWeight(weightVolumeCollectDto.getReviewWeight());
            detail.setLength(weightVolumeCollectDto.getReviewVolume());
            String pictureAddress = weightVolumeCollectDto.getPictureAddress();
            if(StringUtils.isNotEmpty(pictureAddress)){
                List<Map<String,String>> imgList = new ArrayList<>();
                for (String pictureUrl : pictureAddress.split(Constants.SEPARATOR_SEMICOLON)) {
                    Map<String,String> imgMap = new LinkedHashMap<>();
                    imgMap.put("url", pictureUrl);
                    imgList.add(imgMap);
                }
                detail.setImgList(imgList);
            }
            detailList.add(detail);
        }
    }

    /**
     * C网下发图片处理
     *  hit：pictureAddress字段进行存储（早期约定只存储一张，故现在也只存储一张，防止下游解析异常）
     *      1、人工抽检：一单一件5张，运单维度抽检5张（es中只有一条记录也存储了图片链接），包裹维度一单多件不下发图片
     *      2、客户端抽检：只有一单一件（es中只有一条记录也存储了图片链接）
     *      3、dws抽检：一单一件1张（es中只有一条记录也存储了图片链接），一单多件不下发图片
     *      4、页面抽检：只有运单维度，总共5张（es中只有一条记录也存储了图片链接）
     *      5、转运安卓抽检：只有运单维度，总共5张（es中只有一条记录也存储了图片链接）
     * @param weightVolumeCollectDto
     * @param abnormalResultMq
     */
    private void cIssueDownPicDeal(WeightVolumeCollectDto weightVolumeCollectDto, AbnormalResultMq abnormalResultMq) {
        if(
                ((Objects.equals(weightVolumeCollectDto.getFromSource(), SpotCheckSourceFromEnum.SPOT_CHECK_ARTIFICIAL.getName())
                && (
                        Objects.equals(weightVolumeCollectDto.getMultiplePackage(), Constants.NUMBER_ZERO)
                        || Objects.equals(weightVolumeCollectDto.getIsWaybillSpotCheck(), SpotCheckDimensionEnum.SPOT_CHECK_WAYBILL.getCode())
                    )
                )
                || Objects.equals(weightVolumeCollectDto.getFromSource(), SpotCheckSourceFromEnum.SPOT_CHECK_DMS_WEB.getName())
                || Objects.equals(weightVolumeCollectDto.getFromSource(), SpotCheckSourceFromEnum.SPOT_CHECK_ANDROID.getName()))
        ){
            // 只获取其中一张
            String pictureAddress = weightVolumeCollectDto.getPictureAddress();
            if(StringUtils.isNotEmpty(pictureAddress)){
                String[] picArray = pictureAddress.split(Constants.SEPARATOR_SEMICOLON);
                abnormalResultMq.setPictureAddress(picArray[0]);
            }

        }
        if(
                (Objects.equals(weightVolumeCollectDto.getFromSource(), SpotCheckSourceFromEnum.SPOT_CHECK_CLIENT_PLATE.getName())
                || Objects.equals(weightVolumeCollectDto.getFromSource(), SpotCheckSourceFromEnum.SPOT_CHECK_DWS.getName()))
                && Objects.equals(weightVolumeCollectDto.getMultiplePackage(), Constants.NUMBER_ZERO)
        ){
            // 只有一张图片直接设置
            abnormalResultMq.setPictureAddress(weightVolumeCollectDto.getPictureAddress());
        }
    }
}
