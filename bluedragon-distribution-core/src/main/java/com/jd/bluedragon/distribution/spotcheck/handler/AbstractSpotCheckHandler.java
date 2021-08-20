package com.jd.bluedragon.distribution.spotcheck.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.base.*;
import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.*;
import com.jd.bluedragon.distribution.spotcheck.enums.*;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.MathUtils;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.Waybill;
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

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 抽检处理抽象层
 *
 * @author hujiping
 * @date 2021/8/10 9:50 上午
 */
public abstract class AbstractSpotCheckHandler implements ISpotCheckHandler {

    private static Logger logger = LoggerFactory.getLogger(AbstractSpotCheckHandler.class);

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private SpotCheckDealService spotCheckDealService;

    @Autowired
    private ReportExternalManager reportExternalManager;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Override
    public InvokeResult<Boolean> spotCheckHandlerTypeChoice(SpotCheckDto spotCheckDto) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        if(Objects.equals(spotCheckDto.getHandlerType(), SpotCheckHandlerTypeEnum.ONLY_DEAL_SPOT_CHECK.getCode())){
            return dealSpotCheckData(spotCheckDto);
        }
        if(Objects.equals(spotCheckDto.getHandlerType(), SpotCheckHandlerTypeEnum.CHECK_AND_DEAL.getCode())){
            return dealSpotCheck(spotCheckDto);
        }
        result.error("未知处理类型，不予处理!");
        return result;
    }

    @Override
    public InvokeResult<Integer> checkIsExcess(SpotCheckDto spotCheckDto) {
        InvokeResult<Integer> checkResult = new InvokeResult<Integer>();
        // 参数校验
        InvokeResult<Boolean> result = basicCheck(spotCheckDto);
        if(!result.codeSuccess()){
            checkResult.customMessage(result.getCode(), result.getMessage());
            return checkResult;
        }
        // 初始化抽检上下文
        SpotCheckContext spotCheckContext = initSpotCheckContext(spotCheckDto);
        // 抽检校验
        spotCheck(spotCheckContext, result);
        if(!result.codeSuccess()){
            checkResult.customMessage(result.getCode(), result.getMessage());
            return checkResult;
        }
        // 超标校验
        InvokeResult<CheckExcessResult> checkExcessResultInvokeResult = checkIsExcess(spotCheckContext);
        checkResult.setData(checkExcessResultInvokeResult.getData().getExcessCode());
        return checkResult;
    }

    @Override
    public InvokeResult<Boolean> dealSpotCheckData(SpotCheckDto spotCheckDto) {
        InvokeResult<Boolean> result = new InvokeResult<>();

        // 初始化抽检上下文
        SpotCheckContext spotCheckContext = initSpotCheckContext(spotCheckDto);

        dealAfterCheckSuc(spotCheckContext);

        return result;
    }

    @Override
    public InvokeResult<Boolean> dealSpotCheck(SpotCheckDto spotCheckDto) {
        // 参数校验
        InvokeResult<Boolean> result = basicCheck(spotCheckDto);
        if(!result.codeSuccess()){
            return result;
        }
        // 初始化抽检上下文
        SpotCheckContext spotCheckContext = initSpotCheckContext(spotCheckDto);
        // 抽检校验
        spotCheck(spotCheckContext, result);
        if(!result.codeSuccess()){
            return result;
        }
        // 超标校验
        InvokeResult<CheckExcessResult> checkExcessResult = checkIsExcess(spotCheckContext);
        CheckExcessResult data = checkExcessResult.getData();
        spotCheckContext.setExcessStatus(data.getExcessCode());
        spotCheckContext.setExcessReason(data.getExcessReason());
        spotCheckContext.setDiffStandard(data.getExcessStandard());
        // 校验后处理
        dealAfterCheckSuc(spotCheckContext);
        return result;
    }

    /**
     * 抽检校验
     *
     * @param spotCheckContext
     * @param result
     */
    protected abstract void spotCheck(SpotCheckContext spotCheckContext, InvokeResult<Boolean> result);

    /**
     * 超标校验
     *
     * @param spotCheckContext
     * @return
     */
    protected abstract InvokeResult<CheckExcessResult> checkIsExcess(SpotCheckContext spotCheckContext);

    /**
     * 校验成功后的处理
     *
     * @param spotCheckContext
     */
    protected abstract void dealAfterCheckSuc(SpotCheckContext spotCheckContext);

    /**
     * 基础参数校验
     *
     * @param spotCheckDto
     * @return
     */
    private InvokeResult<Boolean> basicCheck(SpotCheckDto spotCheckDto) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        if(spotCheckDto == null){
            result.parameterError(InvokeResult.PARAM_ERROR);
            return result;
        }
        if(!WaybillUtil.isWaybillCode(spotCheckDto.getBarCode()) && !WaybillUtil.isPackageCode(spotCheckDto.getBarCode())){
            result.parameterError("单号不符合规则!");
            return result;
        }
        if(spotCheckDto.getWeight() == null
                || (spotCheckDto.getVolume() == null
                && (spotCheckDto.getLength() == null || spotCheckDto.getWidth() == null || spotCheckDto.getHeight() == null))
                || spotCheckDto.getSiteCode() == null
                || StringUtils.isEmpty(spotCheckDto.getOperateUserErp())){
            result.parameterError(InvokeResult.PARAM_ERROR);
            return result;
        }
        return result;
    }

    /**
     * 初始化抽检上下文
     *
     * @param spotCheckDto
     * @return
     */
    private SpotCheckContext initSpotCheckContext(SpotCheckDto spotCheckDto) {
        String waybillCode = WaybillUtil.getWaybillCode(spotCheckDto.getBarCode());
        SpotCheckContext spotCheckContext = new SpotCheckContext();
        spotCheckContext.setSpotCheckSourceFrom(spotCheckDto.getSpotCheckSourceFrom());
        spotCheckContext.setSpotCheckDimensionType(spotCheckDto.getDimensionType());
        spotCheckContext.setOperateTime(new Date());
        spotCheckContext.setWaybillCode(waybillCode);
        spotCheckContext.setPackageCode(spotCheckDto.getBarCode());
        Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(waybillCode);
        if(waybill == null){
            logger.error("根据运单号:{}未查询到运单信息!", waybillCode);
            throw new RuntimeException(String.format("运单%s不存在!", waybillCode));
        }
        spotCheckContext.setWaybill(waybill);
        Integer packNum = waybill.getGoodNumber();
        if(packNum == null || Objects.equals(packNum, Constants.NUMBER_ZERO)){
            packNum = WaybillUtil.getPackNumByPackCode(spotCheckContext.getPackageCode());
        }
        spotCheckContext.setPackNum(packNum);
        spotCheckContext.setIsMultiPack(packNum > Constants.CONSTANT_NUMBER_ONE);
        spotCheckContext.setSpotCheckBusinessType(BusinessHelper.getSpotCheckTypeBorC(waybill.getWaybillSign()));
        // 信任商家
        if(BusinessUtil.isTrustBusi(waybill.getWaybillSign())){
            spotCheckContext.setIsTrustMerchant(true);
        }else {
            spotCheckContext.setIsTrustMerchant(false);
        }
        spotCheckContext.setMerchantCode(waybill.getBusiOrderCode());
        spotCheckContext.setMerchantName(waybill.getBusiName());

        // 站点
        spotCheckContext.setReviewSiteCode(spotCheckDto.getSiteCode());
        spotCheckContext.setReviewSite(baseMajorManager.getBaseSiteBySiteId(spotCheckDto.getSiteCode()));

        // 计泡比系数
        int volumeRate;
        if(SpotCheckSourceFromEnum.B_SPOT_CHECK_SOURCE.contains(spotCheckDto.getSpotCheckSourceFrom())){
            volumeRate = BusinessUtil.isTKZH(waybill.getWaybillSign()) ? SpotCheckConstants.B_VOLUME_RATIO_TKZH : SpotCheckConstants.B_VOLUME_RATIO_NOT_TKZH;
        }else {
            volumeRate = BusinessUtil.isExpress(waybill.getWaybillSign()) ? SpotCheckConstants.C_VOLUME_RATIO_KY : SpotCheckConstants.C_VOLUME_RATIO_DEFAULT;
        }
        spotCheckContext.setVolumeRate(volumeRate);

        // 产品标识
        DmsBaseDict dmsBaseDict = spotCheckDealService.getProductType(waybill.getWaybillSign());
        spotCheckContext.setProductTypeCode(dmsBaseDict == null ? null : dmsBaseDict.getTypeCode());
        spotCheckContext.setProductTypeName(dmsBaseDict == null ? null : dmsBaseDict.getMemo());

        // 超标图片链接
        spotCheckContext.setPictureAddress(spotCheckDto.getPictureUrls() == null ? null : StringUtils.join(spotCheckDto.getPictureUrls().values(), Constants.SEPARATOR_COMMA));

        // 复核明细
        SpotCheckReviewDetail spotCheckReviewDetail = new SpotCheckReviewDetail();
        spotCheckContext.setSpotCheckReviewDetail(spotCheckReviewDetail);
        double reviewWeight = spotCheckDto.getWeight();
        spotCheckReviewDetail.setReviewWeight(reviewWeight);
        spotCheckReviewDetail.setReviewLength(spotCheckDto.getLength());
        spotCheckReviewDetail.setReviewWidth(spotCheckDto.getWidth());
        spotCheckReviewDetail.setReviewHeight(spotCheckDto.getHeight());
        spotCheckReviewDetail.setReviewLWH(spotCheckDto.getLength() + Constants.SEPARATOR_ASTERISK + spotCheckDto.getWidth()
                + Constants.SEPARATOR_ASTERISK + spotCheckDto.getHeight());
        double volume = spotCheckDto.getVolume();
        if(spotCheckDto.getVolume() == null){
            volume = spotCheckDto.getLength() * spotCheckDto.getWidth() * spotCheckDto.getHeight();
        }
        spotCheckReviewDetail.setReviewVolume(volume);
        double reviewVolumeWeight = MathUtils.keepScale(volume / volumeRate, 2);
        spotCheckReviewDetail.setReviewVolumeWeight(reviewVolumeWeight);
        spotCheckReviewDetail.setReviewLarge(Math.max(reviewWeight, reviewVolumeWeight));
        spotCheckReviewDetail.setReviewOrgId(spotCheckDto.getOrgId());
        spotCheckReviewDetail.setReviewOrgName(spotCheckDto.getOrgName());
        spotCheckReviewDetail.setReviewSiteCode(spotCheckDto.getSiteCode());
        spotCheckReviewDetail.setReviewSiteName(spotCheckDto.getSiteName());
        spotCheckReviewDetail.setReviewUserId(spotCheckDto.getOperateUserId());
        spotCheckReviewDetail.setReviewUserErp(spotCheckDto.getOperateUserErp());
        spotCheckReviewDetail.setReviewUserName(spotCheckDto.getOperateUserName());

        return spotCheckContext;
    }

    /**
     * 组装抽检数据
     *
     * @param spotCheckContext
     */
    protected WeightVolumeCollectDto assembleWeightVolumeCollectDto(SpotCheckContext spotCheckContext) {
        WeightVolumeCollectDto weightVolumeCollectDto = new WeightVolumeCollectDto();
        // 基础数据
        weightVolumeCollectDto.setFromSource(spotCheckContext.getSpotCheckSourceFrom());
        weightVolumeCollectDto.setSpotCheckType(spotCheckContext.getSpotCheckBusinessType());
        weightVolumeCollectDto.setRecordType(SpotCheckRecordTypeEnum.WAYBILL.getCode()); // 默认记录为运单维度
        weightVolumeCollectDto.setReviewDate(spotCheckContext.getOperateTime());
        weightVolumeCollectDto.setWaybillCode(spotCheckContext.getWaybillCode());
        weightVolumeCollectDto.setPackageCode(spotCheckContext.getPackageCode());
        weightVolumeCollectDto.setIsTrustBusi(spotCheckContext.getIsTrustMerchant() ? Constants.CONSTANT_NUMBER_ONE : Constants.NUMBER_ZERO);
        weightVolumeCollectDto.setMerchantCode(spotCheckContext.getMerchantCode());
        weightVolumeCollectDto.setBusiName(spotCheckContext.getMerchantName());
        weightVolumeCollectDto.setProductTypeCode(spotCheckContext.getProductTypeCode());
        weightVolumeCollectDto.setProductTypeName(spotCheckContext.getProductTypeName());
        BaseStaffSiteOrgDto reviewSite = spotCheckContext.getReviewSite();
        weightVolumeCollectDto.setReviewSubType(reviewSite == null ? null : reviewSite.getSubType());
        weightVolumeCollectDto.setVolumeRate(spotCheckContext.getVolumeRate());
        // 复核数据
        SpotCheckReviewDetail spotCheckReviewDetail = spotCheckContext.getSpotCheckReviewDetail();
        weightVolumeCollectDto.setReviewOrgCode(spotCheckReviewDetail.getReviewOrgId());
        weightVolumeCollectDto.setReviewOrgName(spotCheckReviewDetail.getReviewOrgName());
        weightVolumeCollectDto.setReviewSiteCode(spotCheckReviewDetail.getReviewSiteCode());
        weightVolumeCollectDto.setReviewSiteName(spotCheckReviewDetail.getReviewSiteName());
        weightVolumeCollectDto.setReviewErp(spotCheckReviewDetail.getReviewUserErp());
        weightVolumeCollectDto.setReviewWeight(spotCheckReviewDetail.getReviewTotalWeight());
        weightVolumeCollectDto.setReviewVolume(spotCheckReviewDetail.getReviewTotalVolume());
        weightVolumeCollectDto.setReviewVolumeWeight(spotCheckReviewDetail.getReviewVolumeWeight());
        weightVolumeCollectDto.setReviewLWH(spotCheckReviewDetail.getReviewLWH());
        weightVolumeCollectDto.setMoreBigWeight(spotCheckReviewDetail.getReviewLarge());
        // 核对数据
        SpotCheckContrastDetail spotCheckContrastDetail = spotCheckContext.getSpotCheckContrastDetail();
        if(spotCheckContrastDetail != null){
            weightVolumeCollectDto.setContrastSourceFrom(spotCheckContrastDetail.getContrastSourceFrom());
            weightVolumeCollectDto.setBillingOrgCode(spotCheckContrastDetail.getContrastOrgId());
            weightVolumeCollectDto.setBillingOrgName(spotCheckContrastDetail.getContrastOrgName());
            weightVolumeCollectDto.setBillingDeptCodeStr(spotCheckContrastDetail.getContrastDeptCode());
            weightVolumeCollectDto.setBillingDeptName(spotCheckContrastDetail.getContrastDeptName());
            weightVolumeCollectDto.setBillingThreeLevelId(spotCheckContrastDetail.getDutyThirdId());
            weightVolumeCollectDto.setBillingThreeLevelName(spotCheckContrastDetail.getDutyThirdName());
            weightVolumeCollectDto.setBillingCompanyCode(spotCheckContrastDetail.getContrastSiteCode());
            weightVolumeCollectDto.setBillingCompany(spotCheckContrastDetail.getContrastSiteName());
            weightVolumeCollectDto.setBillingErp(spotCheckContrastDetail.getContrastOperateUserErp());
            weightVolumeCollectDto.setBillingWeight(spotCheckContrastDetail.getContrastWeight());
            weightVolumeCollectDto.setBillingVolume(spotCheckContrastDetail.getContrastVolume());
            weightVolumeCollectDto.setBillingVolumeWeight(spotCheckContrastDetail.getContrastVolumeWeight());
            weightVolumeCollectDto.setContrastLarge(spotCheckContrastDetail.getContrastLarge());
            weightVolumeCollectDto.setBillingCalcWeight(spotCheckContrastDetail.getBillingCalcWeight());
            weightVolumeCollectDto.setDutyType(spotCheckContrastDetail.getDutyType());
            // 比较差异数据
            double weightDiff = MathUtils.keepScale(Math.abs(spotCheckContrastDetail.getContrastWeight() - spotCheckReviewDetail.getReviewWeight()), 2);
            weightVolumeCollectDto.setWeightDiff(String.valueOf(weightDiff));
            double volumeWeightDiff = MathUtils.keepScale(Math.abs(spotCheckContrastDetail.getContrastVolumeWeight() - spotCheckReviewDetail.getReviewVolumeWeight()), 2);
            weightVolumeCollectDto.setVolumeWeightDiff(String.valueOf(volumeWeightDiff));
            weightVolumeCollectDto.setDiffStandard(spotCheckContext.getDiffStandard());
            weightVolumeCollectDto.setIsExcess(spotCheckContext.getExcessStatus());
            weightVolumeCollectDto.setExcessReason(spotCheckContext.getExcessReason());
            weightVolumeCollectDto.setIsHasPicture(spotCheckContext.getIsHasPicture());
            weightVolumeCollectDto.setPictureAddress(spotCheckContext.getPictureAddress());
            weightVolumeCollectDto.setIsWaybillSpotCheck(spotCheckContext.getSpotCheckDimensionType());
            double largeDiff = MathUtils.keepScale(Math.abs(spotCheckContrastDetail.getContrastLarge() - spotCheckReviewDetail.getReviewLarge()), 2);
            weightVolumeCollectDto.setLargeDiff(largeDiff);
        }
        return weightVolumeCollectDto;
    }

    /**
     * 汇总复核重量体积
     *
     * @param spotCheckContext
     */
    protected void summaryReviewWeightVolume(SpotCheckContext spotCheckContext) {
        SpotCheckReviewDetail spotCheckReviewDetail = spotCheckContext.getSpotCheckReviewDetail();
        if(spotCheckReviewDetail.getReviewTotalWeight() > Constants.DOUBLE_ZERO){
            // 表示已汇总
            return;
        }
        double totalWeight = Constants.DOUBLE_ZERO;
        double totalVolume = Constants.DOUBLE_ZERO;
        if(spotCheckContext.getIsMultiPack()){
            // 获取运单下已抽检包裹明细
            WeightVolumeQueryCondition condition = new WeightVolumeQueryCondition();
            condition.setReviewSiteCode(spotCheckContext.getReviewSiteCode());
            condition.setWaybillCode(spotCheckContext.getWaybillCode());
            condition.setRecordType(SpotCheckRecordTypeEnum.PACKAGE.getCode());
            List<WeightVolumeCollectDto> weightVolumeCollectList = reportExternalManager.queryByCondition(condition);
            if(CollectionUtils.isNotEmpty(weightVolumeCollectList)){
                for (WeightVolumeCollectDto weightVolumeCollectDto : weightVolumeCollectList) {
                    totalWeight += weightVolumeCollectDto.getReviewWeight() == null ? Constants.DOUBLE_ZERO : weightVolumeCollectDto.getReviewWeight();
                    totalVolume += weightVolumeCollectDto.getReviewVolume() == null ? Constants.DOUBLE_ZERO : weightVolumeCollectDto.getReviewVolume();
                }
            }
        }
        totalWeight += spotCheckReviewDetail.getReviewWeight();
        totalVolume += spotCheckReviewDetail.getReviewVolume();
        spotCheckReviewDetail.setReviewTotalWeight(totalWeight);
        spotCheckReviewDetail.setReviewTotalVolume(totalVolume);
        double reviewVolumeWeight = MathUtils.keepScale(totalVolume / spotCheckContext.getVolumeRate(), 2);
        spotCheckReviewDetail.setReviewVolumeWeight(reviewVolumeWeight);
        spotCheckReviewDetail.setReviewLarge(Math.max(totalWeight, reviewVolumeWeight));
    }

    /**
     * 一单多件处理
     *
     * @param spotCheckContext
     */
    protected void multiPackDeal(SpotCheckContext spotCheckContext) {
        if(StringUtils.isEmpty(spotCheckDealService.getSpotCheckPackCache(spotCheckContext.getWaybillCode(), spotCheckContext.getReviewSiteCode()))){
            // 初始化运单维度记录
            WeightVolumeCollectDto initialWaybillCollect = assembleWeightVolumeCollectDto(spotCheckContext);
            initialWaybillCollect.setPackageCode(spotCheckContext.getWaybillCode());
            initialWaybillCollect.setReviewWeight(null);
            initialWaybillCollect.setReviewVolume(null);
            initialWaybillCollect.setReviewLWH(null);
            initialWaybillCollect.setReviewVolumeWeight(null);
            initialWaybillCollect.setMoreBigWeight(null);
            initialWaybillCollect.setLargeDiff(null);
            reportExternalManager.insertOrUpdateForWeightVolume(initialWaybillCollect);
        }
        // 集齐
        if(spotCheckDealService.gatherTogether(spotCheckContext)){
            reportExternalManager.insertOrUpdateForWeightVolume(assembleWeightVolumeCollectDto(spotCheckContext));
            // 设置超标缓存
            setSpotCheckCache(spotCheckContext.getWaybillCode(), spotCheckContext.getExcessStatus());
        }
        // 新增包裹维度记录
        WeightVolumeCollectDto packCollect = assembleWeightVolumeCollectDto(spotCheckContext);
        packCollect.setRecordType(SpotCheckRecordTypeEnum.PACKAGE.getCode());
        reportExternalManager.insertOrUpdateForWeightVolume(packCollect);
        // 设置包裹维度缓存
        setSpotCheckPackCache(spotCheckContext.getPackageCode(), spotCheckContext.getReviewSiteCode());
        // 抽检全程跟踪
        spotCheckDealService.sendWaybillTrace(spotCheckContext);
        // 记录抽检操作日志
        spotCheckDealService.recordSpotCheckLog(spotCheckContext);
    }

    /**
     * 一单一件处理
     *
     * @param spotCheckContext
     */
    protected void onePackDeal(SpotCheckContext spotCheckContext) {
        // 设置超标缓存
        setSpotCheckCache(spotCheckContext.getWaybillCode(), spotCheckContext.getExcessStatus());
        // 数据落es
        reportExternalManager.insertOrUpdateForWeightVolume(assembleWeightVolumeCollectDto(spotCheckContext));
        // 抽检全程跟踪
        spotCheckDealService.sendWaybillTrace(spotCheckContext);
        // 记录抽检操作日志
        spotCheckDealService.recordSpotCheckLog(spotCheckContext);
    }

    /**
     * 设置已抽检缓存
     *
     * @param waybillCode
     * @param excessStatus
     */
    protected void setSpotCheckCache(String waybillCode, Integer excessStatus) {
        try {
            if(Objects.equals(excessStatus, ExcessStatusEnum.EXCESS_ENUM_YES.getCode()) || Objects.equals(excessStatus, ExcessStatusEnum.EXCESS_ENUM_NO.getCode())){
                String key = String.format(CacheKeyConstants.CACHE_SPOT_CHECK, waybillCode);
                jimdbCacheService.setEx(key, excessStatus,7, TimeUnit.DAYS);
            }
        }catch (Exception e){
            logger.error("设置运单号:{}的抽检缓存异常!", waybillCode);
        }
    }

    /**
     * 设置已抽检包裹缓存
     *
     * @param packageCode
     * @param siteCode
     */
    protected void setSpotCheckPackCache(String packageCode, Integer siteCode) {
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);
        try {
            String packListKey = String.format(CacheKeyConstants.CACHE_SPOT_CHECK_PACK_LIST, waybillCode, siteCode);
            Set<String> packSet = new HashSet<>();
            String packSetStr = spotCheckDealService.getSpotCheckPackCache(waybillCode, siteCode);
            if(StringUtils.isEmpty(packSetStr)){
                packSet.add(packageCode);
            }else {
                packSet = JsonHelper.fromJson(packSetStr, Set.class);
                packSet.add(packageCode);
            }
            jimdbCacheService.setEx(packListKey, JsonHelper.toJson(packSet), 30, TimeUnit.MINUTES);
        }catch (Exception e){
            logger.error("设置场地:{}运单号:{}下的包裹号:{}的抽检缓存异常!", siteCode, waybillCode, packageCode);
        }
    }
}
