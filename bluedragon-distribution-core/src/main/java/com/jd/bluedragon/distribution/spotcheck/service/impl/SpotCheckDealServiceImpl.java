package com.jd.bluedragon.distribution.spotcheck.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.*;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.bluedragon.distribution.base.service.DmsBaseDictService;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    private static Logger logger = LoggerFactory.getLogger(SpotCheckDealServiceImpl.class);

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

    @Override
    public void assembleContrastDataFromFinance(SpotCheckContext spotCheckContext) {
        // 从计费获取核对数据
        SpotCheckContrastDetail spotCheckContrastDetail = new SpotCheckContrastDetail();
        spotCheckContext.setSpotCheckContrastDetail(spotCheckContrastDetail);
        String waybillCode = spotCheckContext.getWaybillCode();
        ResponseDTO<BizDutyDTO> responseDto = businessFinanceManager.queryDutyInfo(waybillCode);
        if(responseDto == null || responseDto.getData() == null){
            logger.warn("根据运单号:{}未获取到计费的称重量方数据!", waybillCode);
            return;
        }
        BizDutyDTO bizDutyDTO = responseDto.getData();
        // 核对来源：计费
        spotCheckContrastDetail.setContrastSourceFrom(ContrastSourceFromEnum.SOURCE_FROM_BILLING.getCode());
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
        double contrastVolumeWeight = MathUtils.keepScale(contrastVolume / volumeRate, 3);
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
        double contrastVolumeWeight = MathUtils.keepScale(contrastVolume / volumeRate, 3);
        // 核对体积重量
        spotCheckContrastDetail.setContrastVolumeWeight(contrastVolumeWeight);
        // 核对较大值
        Double contrastLarge = Math.max(contrastWeight, contrastVolumeWeight);
        spotCheckContrastDetail.setContrastLarge(contrastLarge);
    }

    /**
     * 获取称重汇总数据
     *  1、SpotCheckSourceFromEnum.B_SPOT_CHECK_SOURCE 抽检
     *      0）、信任商家直接取运单waybill中的重量体积goodWeight、goodVolume（无操作站点先剔除此规则）
     *      1）、取运单号相关的所有称重量方记录（包裹和运单维度的都要）
     *      2）、剔除重量体积均为0（注意，只剔除都是0的）的无意义的称重量方记录（多为系统卡控需要，实际并未称重）（剔除后无称重数据则取揽收单位）
     *      3）、按时间先后顺序，找到最早称重量方的人ERP
     *      4）、筛选出该ERP操作的所有称重量方记录
     *      5）、若既有整单录入又有包裹录入 || 若是整单，以最后一次运单称重为对比对象
     *      6）、若是包裹，则筛选出所有包裹维度称重量方的记录，然后以包裹维度进行去重，仅保留时间靠后的那条，最后汇总得到的重量体积为对比对象
     *  2、SpotCheckSourceFromEnum.C_SPOT_CHECK_SOURCE 抽检
     *      1）、一单一件：取运单称重流水第一个操作人的称重量方记录
     *      2）、一单多件：无
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
        WeightVolumeSummary weightVolumeSummary = new WeightVolumeSummary();
        if(SpotCheckSourceFromEnum.B_SPOT_CHECK_SOURCE.contains(spotCheckContext.getSpotCheckSourceFrom())){
            summaryWeightVolumeOfB(waybillCode, packageOpeList, weightVolumeSummary);
        }else {
            summaryWeightVolumeOfC(packageOpeList, weightVolumeSummary);
        }

        Integer operateSiteCode = weightVolumeSummary.getOperateSiteCode();
        BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(operateSiteCode);
        weightVolumeSummary.setOperateOrgId(baseSite.getOrgId());
        weightVolumeSummary.setOperateOrgName(baseSite.getOrgName());
        weightVolumeSummary.setOperateAreaCode(baseSite.getAreaCode());
        weightVolumeSummary.setOperateAreaName(baseSite.getAreaName());
        weightVolumeSummary.setOperateSiteName(baseSite.getSiteName());
        weightVolumeSummary.setSiteType(baseSite.getSiteType());
        return weightVolumeSummary;
    }

    private void summaryWeightVolumeOfB(String waybillCode, List<PackFlowDetail> packageOpeList, WeightVolumeSummary weightVolumeSummary) {

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
            PackFlowDetail packFlowDetail = ((List<PackFlowDetail>) waybillMap.values()).get(0);
            totalWeight = packFlowDetail.getpWeight() == null ? Constants.DOUBLE_ZERO : packFlowDetail.getpWeight();
            totalVolume = (packFlowDetail.getpLength() == null ? Constants.DOUBLE_ZERO : packFlowDetail.getpLength())
                    * (packFlowDetail.getpWidth() == null ? Constants.DOUBLE_ZERO : packFlowDetail.getpWidth())
                    * (packFlowDetail.getpHigh() == null ? Constants.DOUBLE_ZERO : packFlowDetail.getpHigh());
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
    public boolean isExecuteNewSpotCheck(Integer siteCode) {
        try {
            String newSpotCheckSiteCodes = uccPropertyConfiguration.getNewSpotCheckSiteCodes();
            if(StringUtils.isEmpty(newSpotCheckSiteCodes)){
                return false;
            }
            return Arrays.asList(newSpotCheckSiteCodes.split(Constants.SEPARATOR_COMMA)).contains(String.valueOf(siteCode));
        }catch (Exception e){
            logger.error("获取新抽检站点ID异常!", e);
        }
        return false;
    }

    @Override
    public boolean gatherTogether(SpotCheckContext spotCheckContext) {
        String spotCheckPackCache = getSpotCheckPackCache(spotCheckContext.getWaybillCode(), spotCheckContext.getReviewSiteCode());
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
    public String getSpotCheckPackCache(String waybillCode, Integer siteCode) {
        String packListKey = String.format(CacheKeyConstants.CACHE_SPOT_CHECK_PACK_LIST, waybillCode, siteCode);
        try {
            String packSetStr = jimdbCacheService.get(packListKey);
            if(StringUtils.isNotEmpty(packSetStr)){
                return packSetStr;
            }else {
                WeightVolumeQueryCondition condition = new WeightVolumeQueryCondition();
                condition.setWaybillCode(waybillCode);
                condition.setReviewSiteCode(siteCode);
                condition.setRecordType(SpotCheckRecordTypeEnum.PACKAGE.getCode());
                List<String> spotCheckedPackList = reportExternalManager.getSpotCheckPackageCodesByCondition(condition);
                if(CollectionUtils.isEmpty(spotCheckedPackList)){
                    return null;
                }
                return JsonHelper.toJson(new HashSet<>(spotCheckedPackList));
            }
        }catch (Exception e){
            logger.error("获取场地:{}运单号:{}下的包裹抽检缓存异常!", siteCode, waybillCode);
        }
        return null;
    }

    @Override
    public boolean checkPackHasSpotCheck(String packageCode, Integer siteCode) {
        try {
            String spotCheckPackCache = getSpotCheckPackCache(packageCode, siteCode);
            if(StringUtils.isEmpty(spotCheckPackCache)){
                return false;
            }
            Set packSet = JsonHelper.fromJson(spotCheckPackCache, Set.class);
            if(CollectionUtils.isNotEmpty(packSet) && packSet.contains(packageCode)){
                return true;
            }
        }catch (Exception e){
            logger.error("校验包裹号:{}站点:{}是否操作过抽检异常!", packageCode, siteCode ,e);
        }
        return false;
    }

    @Override
    public void issueSpotCheckDetail(WeightVolumeCollectDto weightVolumeCollectDto) {
        if(!Objects.equals(weightVolumeCollectDto.getIsExcess(), ExcessStatusEnum.EXCESS_ENUM_YES.getCode())){
            return;
        }
        // 核对区域、核对操作站点、核对erp三者缺一则不下发
        if(weightVolumeCollectDto.getBillingOrgCode() == null || StringUtils.isEmpty(weightVolumeCollectDto.getBillingCompany())
                || StringUtils.isEmpty(weightVolumeCollectDto.getBillingErp())){
            return;
        }
        AbnormalResultMq abnormalResultMq = buildCommonAttr(weightVolumeCollectDto);
        if(Objects.equals(weightVolumeCollectDto.getSpotCheckType(), SpotCheckBusinessTypeEnum.SPOT_CHECK_TYPE_B.getCode())){
            assembleIssueSpotCheckDetailOfB(weightVolumeCollectDto, abnormalResultMq);
        }
        if(Objects.equals(weightVolumeCollectDto.getSpotCheckType(), SpotCheckBusinessTypeEnum.SPOT_CHECK_TYPE_C.getCode())){
            assembleIssueSpotCheckDetailOfC(weightVolumeCollectDto, abnormalResultMq);
        }
        if(logger.isInfoEnabled()){
            logger.info("下发运单号:{}的抽检超标数据,明细如下:{}", weightVolumeCollectDto.getWaybillCode(), JsonHelper.toJson(abnormalResultMq));
        }
        dmsWeightVolumeExcess.sendOnFailPersistent(abnormalResultMq.getAbnormalId(),JsonHelper.toJson(abnormalResultMq));
    }

    @Override
    public void dealPictureUrl(String packageCode, Integer siteCode, String pictureUrl) {
        // 设置图片缓存
        addPicUrlCache(packageCode, siteCode, pictureUrl);
        // 图片url落库
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);
        Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(waybillCode);
        boolean isMultiPack = isMultiPack(waybill, packageCode);
        boolean packHasSpotCheck;
        if(isMultiPack){
            String spotCheckPackCache = getSpotCheckPackCache(waybillCode, siteCode);
            packHasSpotCheck = StringUtils.isNotEmpty(spotCheckPackCache) && spotCheckPackCache.contains(packageCode);
        }else {
            packHasSpotCheck = checkIsHasSpotCheck(waybillCode);
        }
        if(packHasSpotCheck){
            WeightVolumeCollectDto weightVolumeCollectDto = new WeightVolumeCollectDto();
            weightVolumeCollectDto.setWaybillCode(WaybillUtil.getWaybillCode(packageCode));
            weightVolumeCollectDto.setPackageCode(packageCode);
            weightVolumeCollectDto.setReviewSiteCode(siteCode);
            weightVolumeCollectDto.setIsHasPicture(Constants.CONSTANT_NUMBER_ONE);
            weightVolumeCollectDto.setPictureAddress(pictureUrl);
            reportExternalManager.insertOrUpdateForWeightVolume(weightVolumeCollectDto);
        }
        // 下发超标mq处理
        WeightAndVolumeCheckHandleMessage weightAndVolumeCheckHandleMessage = new WeightAndVolumeCheckHandleMessage();
        weightAndVolumeCheckHandleMessage.setOpNode(WeightAndVolumeCheckHandleMessage.UPLOAD_IMG);
        weightAndVolumeCheckHandleMessage.setPackageCode(packageCode);
        weightAndVolumeCheckHandleMessage.setWaybillCode(WaybillUtil.getWaybillCode(packageCode));
        weightAndVolumeCheckHandleMessage.setSiteCode(siteCode);
        weightAndVolumeCheckHandleProducer.sendOnFailPersistent(packageCode, JsonHelper.toJson(weightAndVolumeCheckHandleMessage));
    }

    private void addPicUrlCache(String packageCode, Integer siteCode, String pictureUrl) {
        try {
            String key = String.format(CacheKeyConstants.CACHE_SPOT_CHECK_PICTURE, packageCode, siteCode);
            jimdbCacheService.setEx(key, pictureUrl, 5, TimeUnit.MINUTES);
        }catch (Exception e){
            logger.error("设置站点{}上传的包裹{}图片链接缓存异常!", siteCode, packageCode);
        }
    }

    private boolean isMultiPack(Waybill waybill, String packageCode) {
        int packNum;
        if(WaybillUtil.isPackageCode(packageCode)){
            packNum = WaybillUtil.getPackNumByPackCode(packageCode);
        }else {
            packNum = waybill == null ? Constants.NUMBER_ZERO : waybill.getGoodNumber();
        }
        return packNum > Constants.CONSTANT_NUMBER_ONE;
    }

    private AbnormalResultMq buildCommonAttr(WeightVolumeCollectDto weightVolumeCollectDto) {
        AbnormalResultMq abnormalResultMq = new AbnormalResultMq();
        abnormalResultMq.setId(weightVolumeCollectDto.getPackageCode() + Constants.UNDERLINE_FILL + weightVolumeCollectDto.getReviewDate().getTime());
        abnormalResultMq.setAbnormalId(weightVolumeCollectDto.getPackageCode() + Constants.UNDERLINE_FILL + weightVolumeCollectDto.getReviewDate().getTime());
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

    private void assembleIssueSpotCheckDetailOfB(WeightVolumeCollectDto weightVolumeCollectDto, AbnormalResultMq abnormalResultMq) {
        abnormalResultMq.setInputMode(weightVolumeCollectDto.getIsWaybillSpotCheck());
        // 图片
        if(Objects.equals(weightVolumeCollectDto.getFromSource(), SpotCheckSourceFromEnum.SPOT_CHECK_DMS_WEB.getName())
                || Objects.equals(weightVolumeCollectDto.getFromSource(), SpotCheckSourceFromEnum.SPOT_CHECK_ANDROID.getName())){
            List<SpotCheckOfPackageDetail> detailList = new ArrayList<>();
            abnormalResultMq.setDetailList(detailList);
            if(Objects.equals(weightVolumeCollectDto.getIsWaybillSpotCheck(), SpotCheckDimensionEnum.SPOT_CHECK_WAYBILL.getCode())){
                //运单维度
                SpotCheckOfPackageDetail detail = new SpotCheckOfPackageDetail();
                detail.setBillCode(weightVolumeCollectDto.getWaybillCode());
                detail.setWeight(weightVolumeCollectDto.getReviewWeight());
                detail.setLength(weightVolumeCollectDto.getReviewVolume());
                String pictureAddress = weightVolumeCollectDto.getPictureAddress();
                if(StringUtils.isNotEmpty(pictureAddress)){
                    List<Map<String,String>> imgList = new ArrayList<>();
                    for (String pictureUrl : pictureAddress.split(Constants.SEPARATOR_COMMA)) {
                        Map<String,String> imgMap = new LinkedHashMap<>();
                        imgMap.put("url", pictureUrl);
                        imgList.add(imgMap);
                    }
                    detail.setImgList(imgList);
                }
                detailList.add(detail);
            }else{
                //包裹维度（前台已屏蔽）todo 待规划好后在补充
            }
        }
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
        }
        // 默认不认责判责
        abnormalResultMq.setIsAccusation(1);
        abnormalResultMq.setIsNeedBlame(0);
    }

    private void assembleIssueSpotCheckDetailOfC(WeightVolumeCollectDto weightVolumeCollectDto, AbnormalResultMq abnormalResultMq) {
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
        // 图片
        if(Objects.equals(weightVolumeCollectDto.getFromSource(), SpotCheckSourceFromEnum.SPOT_CHECK_CLIENT_PLATE.getName())
                || (Objects.equals(weightVolumeCollectDto.getFromSource(), SpotCheckSourceFromEnum.SPOT_CHECK_ANDROID.getName())
                && Objects.equals(weightVolumeCollectDto.getMultiplePackage(), Constants.CONSTANT_NUMBER_ONE))){
            abnormalResultMq.setPictureAddress(weightVolumeCollectDto.getPictureAddress());
        }
        // 默认认责不判责
        abnormalResultMq.setIsAccusation(0);
        abnormalResultMq.setIsNeedBlame(1);
    }
}
