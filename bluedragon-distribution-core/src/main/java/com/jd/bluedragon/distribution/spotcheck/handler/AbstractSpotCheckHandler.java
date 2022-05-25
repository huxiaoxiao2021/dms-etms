package com.jd.bluedragon.distribution.spotcheck.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.base.*;
import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.spotcheck.domain.*;
import com.jd.bluedragon.distribution.spotcheck.enums.*;
import com.jd.bluedragon.distribution.spotcheck.exceptions.SpotCheckBusinessException;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.MathUtils;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import com.jd.ql.dms.report.domain.WeightVolumeQueryCondition;
import com.jd.ql.dms.report.domain.spotcheck.SpotCheckQueryCondition;
import com.jd.ql.dms.report.domain.spotcheck.WeightVolumeSpotCheckDto;
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

    private static final Logger logger = LoggerFactory.getLogger(AbstractSpotCheckHandler.class);

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

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    private SendDetailService sendDetailService;

    @Autowired
    private SpotCheckQueryManager spotCheckQueryManager;

    @Autowired
    private SpotCheckServiceProxy spotCheckServiceProxy;

    @Override
    public InvokeResult<SpotCheckResult> checkExcess(SpotCheckDto spotCheckDto) {
        InvokeResult<SpotCheckResult> checkResult = new InvokeResult<SpotCheckResult>();
        SpotCheckResult spotCheckResult = new SpotCheckResult();
        checkResult.setData(spotCheckResult);
        // 参数校验
        InvokeResult<Boolean> result = basicCheck(spotCheckDto);
        if(!result.codeSuccess()){
            checkResult.customMessage(result.getCode(), result.getMessage());
            return checkResult;
        }
        // 初始化抽检上下文
        SpotCheckContext spotCheckContext = initSpotCheckContext(spotCheckDto);

        // 执行新的抽检改造模式
        if(spotCheckDto.getIsReformedSpotCheck()
                || Objects.equals(spotCheckContext.getSpotCheckSourceFrom(), SpotCheckSourceFromEnum.SPOT_CHECK_DMS_WEB.getName())
                || (SpotCheckSourceFromEnum.EQUIPMENT_SOURCE.contains(spotCheckContext.getSpotCheckSourceFrom())
                && spotCheckDealService.isExecuteSpotCheckReform(spotCheckDto.getSiteCode()))){
            // 抽检校验
            reformCheck(spotCheckContext, result);
            if(!result.codeSuccess()){
                checkResult.customMessage(result.getCode(), result.getMessage());
                return checkResult;
            }
            // 超标校验
            return checkIsExcessReform(spotCheckContext);
        }
        // 抽检校验
        spotCheck(spotCheckContext, result);
        if(!result.codeSuccess()){
            checkResult.customMessage(result.getCode(), result.getMessage());
            return checkResult;
        }
        // 超标校验
        InvokeResult<CheckExcessResult> checkExcessResultInvokeResult = checkIsExcess(spotCheckContext);
        checkResult.customMessage(checkExcessResultInvokeResult.getCode(), checkExcessResultInvokeResult.getMessage());
        spotCheckResult.setExcessStatus(checkExcessResultInvokeResult.getData() == null
                ? ExcessStatusEnum.EXCESS_ENUM_NO_KNOW.getCode() : checkExcessResultInvokeResult.getData().getExcessCode());
        return checkResult;
    }

    protected void reformCheck(SpotCheckContext spotCheckContext, InvokeResult<Boolean> result) {
        String packageCode = spotCheckContext.getPackageCode();
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);
        // 纯配外单校验
        String waybillSign = spotCheckContext.getWaybill().getWaybillSign();
        if(!BusinessUtil.isPurematch(waybillSign)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_ONLY_SUPPORT_PURE_MATCH);
            return;
        }
        // BC网校验
        if(!BusinessUtil.isBInternet(waybillSign) && !BusinessUtil.isCInternet(waybillSign)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_BC);
            return;
        }
        // 泡重比校验
        if(Objects.equals(spotCheckContext.getSpotCheckBusinessType(), SpotCheckBusinessTypeEnum.SPOT_CHECK_TYPE_B.getCode())
                && weightVolumeRatioCheck(spotCheckContext, result)){
            return;
        }
        // 是否妥投
        if(waybillTraceManager.isWaybillFinished(waybillCode)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_FORBID_FINISHED_PACK);
            return;
        }
        // 是否发货校验
        if(reformSendCheck(spotCheckContext, result)){
            return;
        }
        // 是否已抽检
        reformSpotCheck(spotCheckContext, result);
    }

    private boolean reformSendCheck(SpotCheckContext spotCheckContext, InvokeResult<Boolean> result) {
        SendDetail sendDetail = sendDetailService.findOneByWaybillCode(spotCheckContext.getReviewSiteCode(), spotCheckContext.getWaybillCode());
        if(sendDetail != null){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, String.format(SpotCheckConstants.SPOT_CHECK_PACK_SEND_REFORM, sendDetail.getPackageBarcode()));
            return true;
        }
        return false;
    }

    private boolean reformSpotCheck(SpotCheckContext spotCheckContext, InvokeResult<Boolean> result) {
        if(spotCheckDealService.checkIsHasSpotCheck(spotCheckContext.getWaybillCode())){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_HAS_SPOT_CHECK);
            return true;
        }
        SpotCheckQueryCondition condition = new SpotCheckQueryCondition();
        condition.setWaybillCode(spotCheckContext.getWaybillCode());
        condition.setReviewSiteCode(spotCheckContext.getReviewSiteCode());
        List<WeightVolumeSpotCheckDto> spotCheckList = spotCheckQueryManager.querySpotCheckByCondition(condition);
        if(CollectionUtils.isNotEmpty(spotCheckList)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_PACK_SPOT_CHECK_REFORM);
            return true;
        }
        return false;
    }

    protected InvokeResult<SpotCheckResult> checkIsExcessReform(SpotCheckContext spotCheckContext) {
        return spotCheckDealService.checkIsExcessReform(spotCheckContext);
    }

    @Override
    public InvokeResult<Integer> checkIsExcessWithOutOtherCheck(SpotCheckDto spotCheckDto) {
        InvokeResult<Integer> checkResult = new InvokeResult<Integer>();
        // 初始化抽检上下文
        SpotCheckContext spotCheckContext = initSpotCheckContext(spotCheckDto);
        if(spotCheckDealService.isExecuteSpotCheckReform(spotCheckDto.getSiteCode())){
            InvokeResult<SpotCheckResult> checkExcessResultInvokeResult = checkIsExcessReform(spotCheckContext);
            checkResult.customMessage(checkExcessResultInvokeResult.getCode(), checkExcessResultInvokeResult.getMessage());
            checkResult.setData(checkExcessResultInvokeResult.getData().getExcessStatus());
            return checkResult;
        }
        // 超标校验
        InvokeResult<CheckExcessResult> checkExcessResultInvokeResult = checkIsExcess(spotCheckContext);
        checkResult.customMessage(checkExcessResultInvokeResult.getCode(), checkExcessResultInvokeResult.getMessage());
        checkResult.setData(checkExcessResultInvokeResult.getData() == null
                ? ExcessStatusEnum.EXCESS_ENUM_NO_KNOW.getCode() : checkExcessResultInvokeResult.getData().getExcessCode());
        return checkResult;
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

        // 执行新的抽检改造模式
        if(spotCheckDto.getIsReformedSpotCheck()
                || Objects.equals(spotCheckContext.getSpotCheckSourceFrom(), SpotCheckSourceFromEnum.SPOT_CHECK_DMS_WEB.getName())
                || (SpotCheckSourceFromEnum.EQUIPMENT_SOURCE.contains(spotCheckContext.getSpotCheckSourceFrom())
                && spotCheckDealService.isExecuteSpotCheckReform(spotCheckDto.getSiteCode()))){
            // 抽检校验
            reformCheck(spotCheckContext, result);
            if(!result.codeSuccess()){
                return result;
            }
            // 数据处理
            afterCheckDealReform(spotCheckDto, spotCheckContext, result);
            return result;
        }

        // 抽检校验
        spotCheck(spotCheckContext, result);
        if(!result.codeSuccess()){
            return result;
        }
        // 超标校验
        InvokeResult<CheckExcessResult> checkExcessResult = checkIsExcess(spotCheckContext);
        if(!checkExcessResult.codeSuccess()){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, checkExcessResult.getMessage());
            return result;
        }
        supplementSpotCheckContext(spotCheckContext, checkExcessResult.getData());
        // 校验后处理
        dealAfterCheckSuc(spotCheckContext);
        result.setMessage(checkExcessResult.getData().getExcessReason());
        return result;
    }

    protected void uniformityCheck(SpotCheckDto spotCheckDto, SpotCheckContext spotCheckContext, InvokeResult<Boolean> result) {}

    protected void afterCheckDealReform(SpotCheckDto spotCheckDto, SpotCheckContext spotCheckContext, InvokeResult<Boolean> result) {
        // 获取核对数据
        spotCheckDealService.assembleContrastData(spotCheckContext);
        // 数据一致性校验
        uniformityCheck(spotCheckDto, spotCheckContext, result);
        if(!result.codeSuccess()){
            return;
        }
        // 设置已抽检缓存
        setSpotCheckCache(spotCheckContext.getWaybillCode(), spotCheckContext.getExcessStatus());
        // 数据落库
        WeightVolumeSpotCheckDto summaryDto = assembleSummaryReform(spotCheckContext);
        spotCheckServiceProxy.insertOrUpdateProxyReform(summaryDto);
        // 下发超标数据
        spotCheckDealService.spotCheckIssue(summaryDto);
        // 双写老的抽检数据
        assembledSummaryOldDto(spotCheckContext);
        // 抽检全程跟踪
        spotCheckDealService.sendWaybillTrace(spotCheckContext);
        // 记录抽检操作日志
        spotCheckDealService.recordSpotCheckLog(spotCheckContext);
    }

    protected WeightVolumeSpotCheckDto assembleSummaryReform(SpotCheckContext spotCheckContext) {
        WeightVolumeSpotCheckDto dto = new WeightVolumeSpotCheckDto();
        // 复核数据
        SpotCheckReviewDetail spotCheckReviewDetail = spotCheckContext.getSpotCheckReviewDetail();
        dto.setReviewSource(SpotCheckSourceFromEnum.analysisCodeFromName(spotCheckContext.getSpotCheckSourceFrom()));
        dto.setReviewOrgCode(spotCheckReviewDetail.getReviewOrgId());
        dto.setReviewOrgName(spotCheckReviewDetail.getReviewOrgName());
        dto.setReviewSiteCode(spotCheckReviewDetail.getReviewSiteCode());
        dto.setReviewSiteName(spotCheckReviewDetail.getReviewSiteName());
        dto.setReviewUserErp(spotCheckReviewDetail.getReviewUserErp());
        dto.setReviewUserName(spotCheckReviewDetail.getReviewUserName());
        dto.setReviewWeight(spotCheckReviewDetail.getReviewTotalWeight());
        dto.setReviewLength(spotCheckReviewDetail.getReviewLength());
        dto.setReviewWidth(spotCheckReviewDetail.getReviewWidth());
        dto.setReviewHeight(spotCheckReviewDetail.getReviewHeight());
        dto.setReviewVolume(spotCheckReviewDetail.getReviewTotalVolume());
        dto.setReviewLWH(spotCheckReviewDetail.getReviewLWH());
        dto.setMachineCode(spotCheckReviewDetail.getMachineCode());
        // 核对数据
        SpotCheckContrastDetail spotCheckContrastDetail = spotCheckContext.getSpotCheckContrastDetail();
        dto.setContrastSource(spotCheckContrastDetail.getContrastSourceFrom());
        dto.setContrastOrgCode(spotCheckContrastDetail.getContrastOrgId());
        dto.setContrastOrgName(spotCheckContrastDetail.getContrastOrgName());
        dto.setContrastWarZoneCode(spotCheckContrastDetail.getContrastWarZoneCode());
        dto.setContrastWarZoneName(spotCheckContrastDetail.getContrastWarZoneName());
        dto.setContrastAreaCode(spotCheckContrastDetail.getContrastAreaCode());
        dto.setContrastAreaName(spotCheckContrastDetail.getContrastAreaName());
        dto.setContrastSiteCode(spotCheckContrastDetail.getContrastSiteCode());
        dto.setContrastSiteName(spotCheckContrastDetail.getContrastSiteName());
        dto.setContrastStaffAccount(spotCheckContrastDetail.getContrastOperateUserErp());
        dto.setContrastStaffName(spotCheckContrastDetail.getContrastOperateUserName());
        dto.setContrastStaffType(spotCheckContrastDetail.getContrastOperateUserAccountType());
        dto.setContrastDutyType(spotCheckContrastDetail.getDutyType());
        dto.setContrastWeight(spotCheckContrastDetail.getContrastWeight());
        dto.setContrastVolume(spotCheckContrastDetail.getContrastVolume());
        // 通用数据
        dto.setReviewDate(System.currentTimeMillis());
        dto.setWaybillCode(spotCheckContext.getWaybillCode());
        dto.setPackageCode(spotCheckContext.getWaybillCode());
        dto.setMerchantCode(spotCheckContext.getMerchantCode());
        dto.setMerchantName(spotCheckContext.getMerchantName());
        dto.setIsTrustMerchant(spotCheckContext.getIsTrustMerchant() ? Constants.CONSTANT_NUMBER_ONE : Constants.NUMBER_ZERO);
        dto.setVolumeRate(spotCheckContext.getVolumeRate());
        dto.setProductTypeName(spotCheckContext.getProductTypeName());
        BaseStaffSiteOrgDto reviewSite = spotCheckContext.getReviewSite();
        dto.setSiteTypeName(reviewSite == null
                ? null : Objects.equals(reviewSite.getSiteType(), Constants.DMS_SITE_TYPE) ? Constants.CONSTANT_NUMBER_ONE : Constants.NUMBER_ZERO);
        dto.setDiffWeight(spotCheckContext.getDiffWeight());
        dto.setDiffStandard(spotCheckContext.getDiffStandard());
        dto.setIsMultiPack(spotCheckContext.getIsMultiPack() ? Constants.CONSTANT_NUMBER_ONE : Constants.NUMBER_ZERO);

        String pictureAddress = spotCheckContext.getPictureAddress();
        if(SpotCheckSourceFromEnum.EQUIPMENT_SOURCE_NUM.contains(dto.getReviewSource())){
            // 设备图片上传可能在前，故从缓存中在获取一次
            pictureAddress = spotCheckDealService.getSpotCheckPackUrlFromCache(spotCheckContext.getPackageCode(), spotCheckContext.getReviewSiteCode());
        }
        // dws一单多件无需设置图片字段（异步图片上传会更新总记录'是否有图片'和'图片链接'字段）
        if(!(Objects.equals(SpotCheckSourceFromEnum.SPOT_CHECK_DWS.getCode(), dto.getReviewSource()) && spotCheckContext.getIsMultiPack())){
            dto.setIsHasPicture(StringUtils.isNotEmpty(pictureAddress) ? Constants.CONSTANT_NUMBER_ONE : Constants.NUMBER_ZERO);
            dto.setPictureAddress(pictureAddress);
        }
        dto.setBusinessType(spotCheckContext.getSpotCheckBusinessType());
        dto.setDimensionType(spotCheckContext.getSpotCheckDimensionType());
        dto.setRecordType(SpotCheckRecordTypeEnum.SUMMARY_RECORD.getCode());
        dto.setIsGatherTogether(Constants.CONSTANT_NUMBER_ONE);
        dto.setIsExcess(spotCheckContext.getExcessStatus());
        dto.setExcessType(spotCheckContext.getExcessType());
        dto.setSpotCheckStatus(Objects.equals(spotCheckContext.getExcessStatus(), ExcessStatusEnum.EXCESS_ENUM_YES.getCode())
                ? SpotCheckStatusEnum.SPOT_CHECK_STATUS_VERIFY.getCode() : SpotCheckStatusEnum.SPOT_CHECK_STATUS_INVALID_UN_EXCESS.getCode());
        dto.setYn(Constants.CONSTANT_NUMBER_ONE);
        return dto;
    }

    protected void assembledSummaryOldDto(SpotCheckContext spotCheckContext) {
        WeightVolumeCollectDto oldSummaryDto = new WeightVolumeCollectDto();
        oldSummaryDto.setReviewDate(new Date());
        oldSummaryDto.setWaybillCode(spotCheckContext.getWaybillCode());
        oldSummaryDto.setPackageCode(spotCheckContext.getWaybillCode());
        oldSummaryDto.setIsTrustBusi(spotCheckContext.getIsTrustMerchant() ? Constants.CONSTANT_NUMBER_ONE : Constants.NUMBER_ZERO);
        oldSummaryDto.setBusiName(spotCheckContext.getMerchantName());
        oldSummaryDto.setBusiCode(spotCheckContext.getMerchantId());
        SpotCheckReviewDetail spotCheckReviewDetail = spotCheckContext.getSpotCheckReviewDetail();
        oldSummaryDto.setReviewOrgCode(spotCheckReviewDetail.getReviewOrgId());
        oldSummaryDto.setReviewOrgName(spotCheckReviewDetail.getReviewOrgName());
        oldSummaryDto.setReviewSiteCode(spotCheckReviewDetail.getReviewSiteCode());
        oldSummaryDto.setReviewSiteName(spotCheckReviewDetail.getReviewSiteName());
        BaseStaffSiteOrgDto reviewSite = spotCheckContext.getReviewSite();
        oldSummaryDto.setReviewSubType(reviewSite == null
                ? null : Objects.equals(reviewSite.getSiteType(), Constants.DMS_SITE_TYPE) ? Constants.CONSTANT_NUMBER_ONE : Constants.NUMBER_ZERO);
        double reviewWeight = spotCheckReviewDetail.getReviewTotalWeight();
        double reviewVolume = spotCheckReviewDetail.getReviewTotalVolume();
        oldSummaryDto.setReviewErp(spotCheckReviewDetail.getReviewUserErp());
        oldSummaryDto.setReviewWeight(reviewWeight);
        oldSummaryDto.setReviewVolume(reviewVolume);
        oldSummaryDto.setReviewLWH(spotCheckReviewDetail.getReviewLWH());
        SpotCheckContrastDetail spotCheckContrastDetail = spotCheckContext.getSpotCheckContrastDetail();
        oldSummaryDto.setBillingOrgCode(spotCheckContrastDetail.getContrastOrgId());
        oldSummaryDto.setBillingOrgName(spotCheckContrastDetail.getContrastOrgName());
        oldSummaryDto.setBillingDeptCodeStr(spotCheckContrastDetail.getContrastAreaCode());
        oldSummaryDto.setBillingDeptName(spotCheckContrastDetail.getContrastAreaName());
        oldSummaryDto.setBillingCompanyCode(spotCheckContrastDetail.getContrastSiteCode());
        oldSummaryDto.setBillingCompany(spotCheckContrastDetail.getContrastSiteName());
        oldSummaryDto.setBillingErp(spotCheckContrastDetail.getContrastOperateUserErp());
        double contrastWeight = spotCheckContrastDetail.getContrastWeight() == null ? Constants.DOUBLE_ZERO : spotCheckContrastDetail.getContrastWeight();
        double contrastVolume = spotCheckContrastDetail.getContrastVolume() == null ? Constants.DOUBLE_ZERO : spotCheckContrastDetail.getContrastVolume();
        oldSummaryDto.setBillingWeight(contrastWeight);
        oldSummaryDto.setBillingVolume(contrastVolume);
        oldSummaryDto.setDutyType(spotCheckContrastDetail.getDutyType());
        Integer contrastSource = Objects.equals(spotCheckContrastDetail.getContrastSourceFrom(), 1)
                ? ContrastSourceFromEnum.SOURCE_FROM_BILLING.getCode() : Objects.equals(spotCheckContrastDetail.getContrastSourceFrom(), 2)
                ? ContrastSourceFromEnum.SOURCE_FROM_WAYBILL.getCode() : null;
        oldSummaryDto.setContrastSourceFrom(contrastSource);
        Integer volumeRate = spotCheckContext.getVolumeRate();
        if(volumeRate != null && volumeRate != 0){
            double reviewVolumeWeight = MathUtils.keepScale(reviewVolume / volumeRate, 2);
            double contrastVolumeWeight = MathUtils.keepScale(contrastVolume / volumeRate, 2);
            double reviewLarge = Math.max(reviewWeight, reviewVolumeWeight);
            double contrastLarge = Math.max(contrastWeight, contrastVolumeWeight);
            oldSummaryDto.setReviewVolumeWeight(reviewVolumeWeight);
            oldSummaryDto.setBillingVolumeWeight(contrastVolumeWeight);
            oldSummaryDto.setMoreBigWeight(Math.max(reviewWeight, reviewVolumeWeight));
            oldSummaryDto.setContrastLarge(Math.max(spotCheckContrastDetail.getContrastWeight(), contrastVolumeWeight));
            oldSummaryDto.setVolumeWeightDiff(String.valueOf(Math.abs(reviewVolumeWeight - contrastVolumeWeight)));
            oldSummaryDto.setLargeDiff(Math.abs(reviewLarge - contrastLarge));
        }
        oldSummaryDto.setWeightDiff(String.valueOf(Math.abs(reviewWeight - contrastWeight)));
        oldSummaryDto.setDiffStandard(spotCheckContext.getDiffStandard());
        oldSummaryDto.setIsExcess(spotCheckContext.getExcessStatus());
        oldSummaryDto.setExcessReason(Objects.equals(spotCheckContext.getExcessType(), 1)
                ? "重量超标" : Objects.equals(spotCheckContext.getExcessType(), 2) ? "体积超标" : null);
        String picUrl = spotCheckContext.getPictureAddress();
        oldSummaryDto.setIsHasPicture(StringUtils.isEmpty(picUrl) ? Constants.NUMBER_ZERO : Constants.CONSTANT_NUMBER_ONE);
        oldSummaryDto.setPictureAddress(picUrl);
        oldSummaryDto.setProductTypeCode(spotCheckContext.getProductTypeCode());
        oldSummaryDto.setProductTypeName(spotCheckContext.getProductTypeName());
        oldSummaryDto.setSpotCheckType(spotCheckContext.getSpotCheckBusinessType());
        oldSummaryDto.setIsWaybillSpotCheck(spotCheckContext.getSpotCheckDimensionType());
        oldSummaryDto.setFromSource(spotCheckContext.getSpotCheckSourceFrom());
        oldSummaryDto.setMerchantCode(spotCheckContext.getMerchantCode());
        oldSummaryDto.setVolumeRate(spotCheckContext.getVolumeRate());
        oldSummaryDto.setRecordType(SpotCheckRecordTypeEnum.SUMMARY_RECORD.getCode());
        oldSummaryDto.setFullCollect(Constants.CONSTANT_NUMBER_ONE);
        oldSummaryDto.setMultiplePackage(spotCheckContext.getIsMultiPack() ? Constants.CONSTANT_NUMBER_ONE : Constants.NUMBER_ZERO);
        reportExternalManager.insertOrUpdateForWeightVolume(oldSummaryDto);
    }

    protected void assembledDetailOldDto(SpotCheckContext spotCheckContext) {
        WeightVolumeCollectDto oldDetailDto = new WeightVolumeCollectDto();
        oldDetailDto.setReviewDate(new Date());
        oldDetailDto.setWaybillCode(spotCheckContext.getWaybillCode());
        oldDetailDto.setPackageCode(spotCheckContext.getPackageCode());
        oldDetailDto.setIsTrustBusi(spotCheckContext.getIsTrustMerchant() ? Constants.CONSTANT_NUMBER_ONE : Constants.NUMBER_ZERO);
        oldDetailDto.setBusiName(spotCheckContext.getMerchantName());
        oldDetailDto.setBusiCode(spotCheckContext.getMerchantId());
        SpotCheckReviewDetail spotCheckReviewDetail = spotCheckContext.getSpotCheckReviewDetail();
        oldDetailDto.setReviewOrgCode(spotCheckReviewDetail.getReviewOrgId());
        oldDetailDto.setReviewOrgName(spotCheckReviewDetail.getReviewOrgName());
        oldDetailDto.setReviewSiteCode(spotCheckReviewDetail.getReviewSiteCode());
        oldDetailDto.setReviewSiteName(spotCheckReviewDetail.getReviewSiteName());
        BaseStaffSiteOrgDto reviewSite = spotCheckContext.getReviewSite();
        oldDetailDto.setReviewSubType(reviewSite == null
                ? null : Objects.equals(reviewSite.getSiteType(), Constants.DMS_SITE_TYPE) ? Constants.CONSTANT_NUMBER_ONE : Constants.NUMBER_ZERO);
        double reviewWeight = spotCheckReviewDetail.getReviewWeight();
        double reviewVolume = spotCheckReviewDetail.getReviewVolume();
        oldDetailDto.setReviewErp(spotCheckReviewDetail.getReviewUserErp());
        oldDetailDto.setReviewWeight(reviewWeight);
        oldDetailDto.setReviewVolume(reviewVolume);
        oldDetailDto.setReviewLWH(spotCheckReviewDetail.getReviewLWH());
        oldDetailDto.setIsExcess(ExcessStatusEnum.EXCESS_ENUM_COMPUTE.getCode());
        oldDetailDto.setProductTypeCode(spotCheckContext.getProductTypeCode());
        oldDetailDto.setProductTypeName(spotCheckContext.getProductTypeName());
        oldDetailDto.setSpotCheckType(spotCheckContext.getSpotCheckBusinessType());
        oldDetailDto.setIsWaybillSpotCheck(spotCheckContext.getSpotCheckDimensionType());
        oldDetailDto.setFromSource(spotCheckContext.getSpotCheckSourceFrom());
        oldDetailDto.setMerchantCode(spotCheckContext.getMerchantCode());
        oldDetailDto.setRecordType(SpotCheckRecordTypeEnum.DETAIL_RECORD.getCode());
        oldDetailDto.setFullCollect(Constants.NUMBER_ZERO);
        oldDetailDto.setMultiplePackage(spotCheckContext.getIsMultiPack() ? Constants.CONSTANT_NUMBER_ONE : Constants.NUMBER_ZERO);
        reportExternalManager.insertOrUpdateForWeightVolume(oldDetailDto);
    }

    private void supplementSpotCheckContext(SpotCheckContext spotCheckContext, CheckExcessResult checkExcessResult) {
        spotCheckContext.setExcessStatus(checkExcessResult.getExcessCode());
        spotCheckContext.setExcessReason(checkExcessResult.getExcessReason());
        spotCheckContext.setDiffStandard(checkExcessResult.getExcessStandard());
    }

    /**
     * 抽检校验
     *
     * @param spotCheckContext
     * @param result
     */
    protected void spotCheck(SpotCheckContext spotCheckContext, InvokeResult<Boolean> result) {
        if(Objects.equals(spotCheckContext.getSpotCheckBusinessType(), SpotCheckBusinessTypeEnum.SPOT_CHECK_TYPE_B.getCode())){
            spotCheckOfB(spotCheckContext, result);
            return;
        }
        if(Objects.equals(spotCheckContext.getSpotCheckBusinessType(), SpotCheckBusinessTypeEnum.SPOT_CHECK_TYPE_C.getCode())){
            spotCheckOfC(spotCheckContext, result);
            return;
        }
        result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "未知业务类型暂不支持!");
    }

    private void spotCheckOfC(SpotCheckContext spotCheckContext, InvokeResult<Boolean> result) {
        Waybill waybill = spotCheckContext.getWaybill();
        String packageCode = spotCheckContext.getPackageCode();
        // 纯配外单校验
        if(!BusinessUtil.isPurematch(waybill.getWaybillSign())){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_ONLY_SUPPORT_PURE_MATCH);
            return;
        }
        // 是否发货校验
        if(isHasSendCheck(spotCheckContext, result)){
            return;
        }
        // 是否已抽检
        if(spotCheckDealService.checkIsHasSpotCheck(WaybillUtil.getWaybillCode(packageCode))){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_HAS_SPOT_CHECK);
        }
    }

    private void spotCheckOfB(SpotCheckContext spotCheckContext, InvokeResult<Boolean> result) {
        String packageCode = spotCheckContext.getPackageCode();
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);
        // 纯配外单校验
        if(!BusinessUtil.isPurematch(spotCheckContext.getWaybill().getWaybillSign())){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_ONLY_SUPPORT_PURE_MATCH);
            return;
        }
        // 泡重比校验
        if(weightVolumeRatioCheck(spotCheckContext, result)){
            return;
        }
        // 是否妥投
        if(waybillTraceManager.isWaybillFinished(waybillCode)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_FORBID_FINISHED_PACK);
            return;
        }
        // 是否发货校验
        if(isHasSendCheck(spotCheckContext, result)){
            return;
        }
        // 是否已抽检
        if(spotCheckDealService.checkIsHasSpotCheck(waybillCode)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_HAS_SPOT_CHECK);
        }
    }

    /**
     * 泡重比校验
     *
     * @param spotCheckContext
     * @param result
     * @return
     */
    protected boolean weightVolumeRatioCheck(SpotCheckContext spotCheckContext, InvokeResult<Boolean> result) {
        Waybill waybill = spotCheckContext.getWaybill();
        String packageCode = spotCheckContext.getPackageCode();
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);
        SpotCheckReviewDetail spotCheckReviewDetail = spotCheckContext.getSpotCheckReviewDetail();
        double weight = spotCheckReviewDetail.getReviewWeight();
        double volume = spotCheckReviewDetail.getReviewVolume();
        // 包裹维度泡重比校验
        if(Objects.equals(spotCheckContext.getSpotCheckDimensionType(), SpotCheckDimensionEnum.SPOT_CHECK_PACK.getCode())){
            if(weight / volume * SpotCheckConstants.CM3_M3_MAGNIFICATION > SpotCheckConstants.WEIGHT_VOLUME_RATIO){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, String.format(SpotCheckConstants.SPOT_CHECK_VOLUME_RATE_LIMIT_B_PACK, packageCode, SpotCheckConstants.WEIGHT_VOLUME_RATIO));
                return true;
            }
            if(weight > SpotCheckConstants.WEIGHT_MAX_RATIO){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, String.format(SpotCheckConstants.SPOT_CHECK_WEIGHT_LIMIT_B_PACK, SpotCheckConstants.WEIGHT_MAX_RATIO));
                return true;
            }
            if(volume > SpotCheckConstants.VOLUME_MAX_RATIO * SpotCheckConstants.CM3_M3_MAGNIFICATION){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, String.format(SpotCheckConstants.SPOT_CHECK_VOLUME_LIMIT_B_PACK, SpotCheckConstants.VOLUME_MAX_RATIO));
                return true;
            }
            return false;
        }
        // 运单维度泡重比校验
        if(weight / volume * SpotCheckConstants.CM3_M3_MAGNIFICATION > SpotCheckConstants.WEIGHT_VOLUME_RATIO){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, String.format(SpotCheckConstants.SPOT_CHECK_VOLUME_RATE_LIMIT_B, waybillCode, SpotCheckConstants.WEIGHT_VOLUME_RATIO));
            return true;
        }
        int packNum = waybill.getGoodNumber();
        if(weight / packNum > SpotCheckConstants.WEIGHT_MAX_RATIO){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, String.format(SpotCheckConstants.SPOT_CHECK_WEIGHT_LIMIT_B, SpotCheckConstants.WEIGHT_MAX_RATIO));
            return true;
        }
        if(volume / packNum > SpotCheckConstants.VOLUME_MAX_RATIO * SpotCheckConstants.CM3_M3_MAGNIFICATION){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, String.format(SpotCheckConstants.SPOT_CHECK_VOLUME_LIMIT_B, SpotCheckConstants.VOLUME_MAX_RATIO));
            return true;
        }
        return false;
    }

    /**
     * 是否发货校验
     *
     * @param spotCheckContext
     * @return
     */
    private boolean isHasSendCheck(SpotCheckContext spotCheckContext, InvokeResult<Boolean> result){
        String packageCode = spotCheckContext.getPackageCode();
        Integer siteCode = spotCheckContext.getReviewSiteCode();
        Integer spotCheckDimensionType = spotCheckContext.getSpotCheckDimensionType();
        boolean isMultiPack = spotCheckContext.getIsMultiPack();
        // 是否发货校验
        // 1、运单维度抽检：只要其中某一包裹已经发货则提示按包裹维度操作抽检
        // 2、包裹维度抽检：只要操作了发货则提示禁止抽检
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);
        if(Objects.equals(SpotCheckDimensionEnum.SPOT_CHECK_WAYBILL.getCode(), spotCheckDimensionType)){
            if(!WaybillUtil.isWaybillCode(waybillCode)){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_ONLY_WAYBILL);
                return true;
            }
            SendDetail sendDetail = sendDetailService.findOneByWaybillCode(siteCode, waybillCode);
            if(sendDetail != null){
                if(!isMultiPack){
                    result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, String.format(SpotCheckConstants.SPOT_CHECK_PACK_SEND, sendDetail.getPackageBarcode()));
                    return true;
                }
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, String.format(SpotCheckConstants.SPOT_CHECK_PACK_SEND_TRANSFER, sendDetail.getPackageBarcode()));
                return true;
            }
        }else {
            if(!WaybillUtil.isPackageCode(packageCode)){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_ONLY_PACK);
                return true;
            }
            List<SendDetail> sendList = sendDetailService.findByWaybillCodeOrPackageCode(siteCode, waybillCode, packageCode);
            if(CollectionUtils.isNotEmpty(sendList)){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, String.format(SpotCheckConstants.SPOT_CHECK_PACK_SEND, packageCode));
                return true;
            }
        }
        return false;
    }

    /**
     * 超标校验
     *
     * @param spotCheckContext
     * @return
     */
    private InvokeResult<CheckExcessResult> checkIsExcess(SpotCheckContext spotCheckContext) {
        InvokeResult<CheckExcessResult> result = new InvokeResult<CheckExcessResult>();
        if(Objects.equals(spotCheckContext.getSpotCheckBusinessType(), SpotCheckBusinessTypeEnum.SPOT_CHECK_TYPE_B.getCode())){
            return checkIsExcessB(spotCheckContext);
        }
        if(Objects.equals(spotCheckContext.getSpotCheckBusinessType(), SpotCheckBusinessTypeEnum.SPOT_CHECK_TYPE_C.getCode())){
            return checkIsExcessC(spotCheckContext);
        }
        result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "未知业务类型暂不支持!");
        return result;
    }

    protected InvokeResult<CheckExcessResult> checkIsExcessB(SpotCheckContext spotCheckContext){
        return null;
    }

    protected InvokeResult<CheckExcessResult> checkIsExcessC(SpotCheckContext spotCheckContext){
        return null;
    }

    /**
     * 获取核对数据
     *
     * @param spotCheckContext
     */
    protected void obtainContrast(SpotCheckContext spotCheckContext){
        // B网从运单获取
        if(Objects.equals(spotCheckContext.getSpotCheckBusinessType(), SpotCheckBusinessTypeEnum.SPOT_CHECK_TYPE_B.getCode())){
            spotCheckDealService.assembleContrastDataFromWaybillFlow(spotCheckContext);
            return;
        }
        // C网:
        // 一单多件从计费获取
        // 一单一件从计费获取后无从运单获取
        if(spotCheckContext.getIsMultiPack()){
            spotCheckDealService.assembleContrastDataFromFinance(spotCheckContext);
            return;
        }
        spotCheckDealService.assembleContrastDataFromFinance(spotCheckContext);
        SpotCheckContrastDetail spotCheckContrastDetail = spotCheckContext.getSpotCheckContrastDetail();
        if(spotCheckContrastDetail.getContrastWeight() == null
                || Objects.equals(spotCheckContrastDetail.getContrastWeight(), Constants.DOUBLE_ZERO)){
            spotCheckDealService.assembleContrastDataFromWaybillFlow(spotCheckContext);
        }
    }

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
        if(spotCheckDto.getSiteCode() == null || Objects.equals(spotCheckDto.getSiteCode(), Constants.NUMBER_ZERO)
                || StringUtils.isEmpty(spotCheckDto.getOperateUserErp())){
            result.parameterError("操作人信息不存在!");
            return result;
        }
        if(!WaybillUtil.isWaybillCode(spotCheckDto.getBarCode()) && !WaybillUtil.isPackageCode(spotCheckDto.getBarCode())){
            result.parameterError("单号不符合规则!");
            return result;
        }
        if(spotCheckDto.getWeight() == null || spotCheckDto.getWeight() <= Constants.DOUBLE_ZERO){
            result.parameterError("复核重量必须大于零!");
            return result;
        }
        if(Objects.equals(spotCheckDto.getDimensionType(), SpotCheckDimensionEnum.SPOT_CHECK_WAYBILL.getCode())
                && (spotCheckDto.getVolume() == null || spotCheckDto.getVolume() <= Constants.DOUBLE_ZERO)){
            result.parameterError("运单维度抽检体积必须大于零!");
            return result;
        }
        if(Objects.equals(spotCheckDto.getDimensionType(), SpotCheckDimensionEnum.SPOT_CHECK_PACK.getCode())
                && (
                        spotCheckDto.getLength() == null || spotCheckDto.getLength() <= Constants.DOUBLE_ZERO
                                || spotCheckDto.getWidth() == null || spotCheckDto.getWidth() <= Constants.DOUBLE_ZERO
                                || spotCheckDto.getHeight() == null || spotCheckDto.getHeight() <= Constants.DOUBLE_ZERO
                    )
        ){
            result.parameterError("包裹维度抽检长宽高必须大于零!");
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
        spotCheckContext.setSpotCheckHandlerType(spotCheckDto.getSpotCheckHandlerType());
        spotCheckContext.setOperateTime(new Date());
        spotCheckContext.setWaybillCode(waybillCode);
        spotCheckContext.setPackageCode(spotCheckDto.getBarCode());
        BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getWaybillAndPackByWaybillCode(waybillCode);
        if(baseEntity == null || baseEntity.getData() == null || baseEntity.getData().getWaybill() == null){
            logger.error("根据运单号:{}未查询到运单信息!", waybillCode);
            throw new SpotCheckBusinessException(String.format("运单%s不存在!", waybillCode));
        }
        Waybill waybill = baseEntity.getData().getWaybill();
        spotCheckContext.setWaybill(waybill);
        Integer packNum = waybill.getGoodNumber();
        if(packNum == null || Objects.equals(packNum, Constants.NUMBER_ZERO)){
            packNum = WaybillUtil.getPackNumByPackCode(spotCheckContext.getPackageCode());
        }
        spotCheckContext.setPackNum(packNum);
        spotCheckContext.setIsMultiPack(packNum > Constants.CONSTANT_NUMBER_ONE);
        if(!spotCheckContext.getIsMultiPack()){
            spotCheckContext.setPackageCode(baseEntity.getData().getPackageList().get(0).getPackageBarcode());
        }
        String waybillSign = waybill.getWaybillSign();
        if(!BusinessUtil.isPurematch(waybillSign)){
            throw new SpotCheckBusinessException(SpotCheckConstants.SPOT_CHECK_ONLY_SUPPORT_PURE_MATCH);
        }
        if(BusinessUtil.isCInternet(waybillSign)){
            spotCheckContext.setSpotCheckBusinessType(SpotCheckBusinessTypeEnum.SPOT_CHECK_TYPE_C.getCode());
        }else if(BusinessUtil.isBInternet(waybillSign)){
            spotCheckContext.setSpotCheckBusinessType(SpotCheckBusinessTypeEnum.SPOT_CHECK_TYPE_B.getCode());
        }else {
            logger.warn("未知业务类型不支持!（非B非C）");
            throw new SpotCheckBusinessException("未知业务类型不支持!（非B非C）");
        }
        // 集齐
        if(!spotCheckContext.getIsMultiPack() || Objects.equals(spotCheckContext.getSpotCheckDimensionType(), SpotCheckDimensionEnum.SPOT_CHECK_WAYBILL.getCode())){
            spotCheckContext.setIsGatherTogether(true);
        }
        // 信任商家
        spotCheckContext.setIsTrustMerchant(BusinessUtil.isTrustBusi(waybillSign));
        spotCheckContext.setMerchantId(waybill.getBusiId());
        spotCheckContext.setMerchantCode(waybill.getBusiOrderCode());
        spotCheckContext.setMerchantName(waybill.getBusiName());

        // 站点
        spotCheckContext.setReviewSiteCode(spotCheckDto.getSiteCode());
        spotCheckContext.setReviewSite(baseMajorManager.getBaseSiteBySiteId(spotCheckDto.getSiteCode()));

        // 计泡比系数
        int volumeRate;
        if(Objects.equals(spotCheckContext.getSpotCheckBusinessType(), SpotCheckBusinessTypeEnum.SPOT_CHECK_TYPE_B.getCode())){
            volumeRate = BusinessUtil.isTKZH(waybillSign) ? SpotCheckConstants.B_VOLUME_RATIO_TKZH : SpotCheckConstants.B_VOLUME_RATIO_NOT_TKZH;
        }else if(Objects.equals(spotCheckContext.getSpotCheckBusinessType(), SpotCheckBusinessTypeEnum.SPOT_CHECK_TYPE_C.getCode())){
            volumeRate = BusinessUtil.isExpress(waybillSign) ? SpotCheckConstants.C_VOLUME_RATIO_KY : SpotCheckConstants.C_VOLUME_RATIO_DEFAULT;
        }else {
            throw new SpotCheckBusinessException("未知业务类型不支持!（非B非C）");
        }
        spotCheckContext.setVolumeRate(volumeRate);

        // 产品标识
        DmsBaseDict dmsBaseDict = spotCheckDealService.getProductType(waybillSign);
        spotCheckContext.setProductTypeCode(dmsBaseDict == null ? null : dmsBaseDict.getTypeCode());
        spotCheckContext.setProductTypeName(dmsBaseDict == null ? null : dmsBaseDict.getMemo());

        // 超标图片链接
        String picAddress = transferPicString(spotCheckDto);
        spotCheckContext.setIsHasPicture(StringUtils.isEmpty(picAddress) ? Constants.NUMBER_ZERO : Constants.CONSTANT_NUMBER_ONE);
        spotCheckContext.setPictureAddress(picAddress);


        // 复核明细
        SpotCheckReviewDetail spotCheckReviewDetail = new SpotCheckReviewDetail();
        spotCheckContext.setSpotCheckReviewDetail(spotCheckReviewDetail);
        double reviewWeight = spotCheckDto.getWeight();
        spotCheckReviewDetail.setReviewWeight(reviewWeight);
        spotCheckReviewDetail.setReviewTotalWeight(reviewWeight);
        spotCheckReviewDetail.setReviewLength(spotCheckDto.getLength());
        spotCheckReviewDetail.setReviewWidth(spotCheckDto.getWidth());
        spotCheckReviewDetail.setReviewHeight(spotCheckDto.getHeight());
        if(spotCheckDto.getLength() != null && spotCheckDto.getWidth() != null && spotCheckDto.getHeight() != null){
            spotCheckReviewDetail.setReviewLWH(spotCheckDto.getLength() + Constants.SEPARATOR_ASTERISK + spotCheckDto.getWidth()
                    + Constants.SEPARATOR_ASTERISK + spotCheckDto.getHeight());
        }
        double volume = spotCheckDto.getVolume() == null ? Constants.DOUBLE_ZERO : spotCheckDto.getVolume();
        if(Objects.equals(volume, Constants.DOUBLE_ZERO)){
            volume = spotCheckDto.getLength() * spotCheckDto.getWidth() * spotCheckDto.getHeight();
            volume = MathUtils.keepScale(volume, 2);
        }
        spotCheckReviewDetail.setReviewVolume(volume);
        spotCheckReviewDetail.setReviewTotalVolume(volume);
        double reviewVolumeWeight = MathUtils.keepScale(volume / volumeRate, 2);
        spotCheckReviewDetail.setReviewVolumeWeight(reviewVolumeWeight);
        spotCheckReviewDetail.setReviewLarge(Math.max(reviewWeight, reviewVolumeWeight));
        spotCheckReviewDetail.setReviewOrgId(spotCheckDto.getOrgId());
        spotCheckReviewDetail.setReviewOrgName(spotCheckDto.getOrgName());
        spotCheckReviewDetail.setReviewSiteCode(spotCheckDto.getSiteCode());
        spotCheckReviewDetail.setReviewSiteName(spotCheckDto.getSiteName());
        spotCheckReviewDetail.setReviewUserId(spotCheckDto.getOperateUserId());
        spotCheckReviewDetail.setReviewUserErp(spotCheckDto.getOperateUserErp());
        try {
            if(StringUtils.isNotEmpty(spotCheckDto.getOperateUserErp())){
                BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByErpNoCache(spotCheckDto.getOperateUserErp());
                spotCheckReviewDetail.setReviewUserName(baseStaff == null ? null : baseStaff.getStaffName());
            }
        }catch (Exception e){
            logger.error("根据erp:{}获取操作人信息异常!", spotCheckDto.getOperateUserErp());
        }
        spotCheckReviewDetail.setMachineCode(spotCheckDto.getMachineCode());

        return spotCheckContext;
    }

    private String transferPicString(SpotCheckDto spotCheckDto) {
        Map<String, String> picMap = spotCheckDto.getPictureUrls();
        if(picMap == null || Objects.equals(picMap.size(), Constants.NUMBER_ZERO)){
            return null;
        }
        Map<String, String> sortMap = new TreeMap<String, String>(
                new Comparator<String>() {
                    public int compare(String obj1, String obj2) {
                        // 升序排序
                        return obj1.compareTo(obj2);
                    }
                });
        for (String key : picMap.keySet()) {
            sortMap.put(key, picMap.get(key));
        }
        return StringUtils.join(sortMap.values(), Constants.SEPARATOR_SEMICOLON);
    }

    /**
     * 组装集齐前包裹抽检数据
     *
     * @param spotCheckContext
     */
    protected WeightVolumeCollectDto assembleBeforeGatherPackCollectDto(SpotCheckContext spotCheckContext) {
        WeightVolumeCollectDto packCollectDto = assembleCommonCollectDto(spotCheckContext);
        packCollectDto.setRecordType(SpotCheckRecordTypeEnum.DETAIL_RECORD.getCode());
        // notes：集齐前的包裹记录需记录'长宽高'字段
        packCollectDto.setReviewLWH(spotCheckContext.getSpotCheckReviewDetail().getReviewLWH());
        // 图片链接
        String pictureUrl = StringUtils.isEmpty(spotCheckContext.getPictureAddress())
                ? getPicUrlCache(spotCheckContext.getPackageCode(), spotCheckContext.getReviewSiteCode()) : spotCheckContext.getPictureAddress();
        packCollectDto.setIsHasPicture(StringUtils.isEmpty(pictureUrl) ? Constants.NUMBER_ZERO : Constants.CONSTANT_NUMBER_ONE);
        packCollectDto.setPictureAddress(pictureUrl);
        return packCollectDto;
    }

    /**
     * 组装集齐后抽检数据
     *
     * @param spotCheckContext
     * @return
     */
    protected WeightVolumeCollectDto assembleAfterGatherCollectDto(SpotCheckContext spotCheckContext) {
        WeightVolumeCollectDto waybillCollectDto = assembleCommonCollectDto(spotCheckContext);
        // notes：集齐后的总记录：
        // 1、只有包裹维度一单一件抽检需记录'长宽高'字段
        if(Objects.equals(spotCheckContext.getSpotCheckDimensionType(), SpotCheckDimensionEnum.SPOT_CHECK_PACK.getCode())
                && !spotCheckContext.getIsMultiPack()){
            waybillCollectDto.setReviewLWH(spotCheckContext.getSpotCheckReviewDetail().getReviewLWH());
        }
        // 2.1、人工抽检和web页面抽检图片实时存放在spotCheckContext.pictureAddress中
        // 2.2、dws抽检和客户端抽检此处不处理（因为图片异步上传在图片上传环节会更新'是否有图片'字段）
        String pictureUrl = StringUtils.isEmpty(spotCheckContext.getPictureAddress())
                ? getPicUrlCache(spotCheckContext.getPackageCode(), spotCheckContext.getReviewSiteCode()) : spotCheckContext.getPictureAddress();
        if(StringUtils.isNotEmpty(pictureUrl)){
            waybillCollectDto.setIsHasPicture(Constants.CONSTANT_NUMBER_ONE);
        }
        // 3、运单维度抽检 | 包裹维度一单一件抽检的需要记录'图片链接'字段
        if(Objects.equals(spotCheckContext.getSpotCheckDimensionType(), SpotCheckDimensionEnum.SPOT_CHECK_WAYBILL.getCode())
                || (Objects.equals(spotCheckContext.getSpotCheckDimensionType(), SpotCheckDimensionEnum.SPOT_CHECK_PACK.getCode()) && !spotCheckContext.getIsMultiPack())){
            waybillCollectDto.setPictureAddress(pictureUrl);
            waybillCollectDto.setIsHasPicture(StringUtils.isEmpty(pictureUrl) ? Constants.NUMBER_ZERO : Constants.CONSTANT_NUMBER_ONE);
        }

        waybillCollectDto.setRecordType(SpotCheckRecordTypeEnum.SUMMARY_RECORD.getCode());
        waybillCollectDto.setPackageCode(spotCheckContext.getWaybillCode());

        SpotCheckReviewDetail spotCheckReviewDetail = spotCheckContext.getSpotCheckReviewDetail();
        // 汇总数据
        double totalWeight = spotCheckReviewDetail.getReviewTotalWeight();
        double totalVolume = spotCheckReviewDetail.getReviewTotalVolume();
        double reviewVolumeWeight = MathUtils.keepScale(totalVolume / spotCheckContext.getVolumeRate(), 2);

        waybillCollectDto.setReviewWeight(totalWeight);
        waybillCollectDto.setReviewVolume(totalVolume);
        waybillCollectDto.setReviewVolumeWeight(reviewVolumeWeight);
        double reviewLarge = Math.max(totalWeight, reviewVolumeWeight);
        waybillCollectDto.setMoreBigWeight(reviewLarge);

        // 核对数据
        SpotCheckContrastDetail spotCheckContrastDetail = spotCheckContext.getSpotCheckContrastDetail();
        waybillCollectDto.setContrastSourceFrom(spotCheckContrastDetail.getContrastSourceFrom());
        waybillCollectDto.setBillingOrgCode(spotCheckContrastDetail.getContrastOrgId());
        waybillCollectDto.setBillingOrgName(spotCheckContrastDetail.getContrastOrgName());
        waybillCollectDto.setBillingDeptCodeStr(spotCheckContrastDetail.getContrastDeptCode());
        waybillCollectDto.setBillingDeptName(spotCheckContrastDetail.getContrastDeptName());
        waybillCollectDto.setBillingThreeLevelId(spotCheckContrastDetail.getDutyThirdId());
        waybillCollectDto.setBillingThreeLevelName(spotCheckContrastDetail.getDutyThirdName());
        waybillCollectDto.setBillingCompanyCode(spotCheckContrastDetail.getContrastSiteCode());
        waybillCollectDto.setBillingCompany(spotCheckContrastDetail.getContrastSiteName());
        waybillCollectDto.setBillingErp(spotCheckContrastDetail.getContrastOperateUserErp());
        waybillCollectDto.setBillingWeight(spotCheckContrastDetail.getContrastWeight());
        waybillCollectDto.setBillingVolume(spotCheckContrastDetail.getContrastVolume());
        waybillCollectDto.setBillingVolumeWeight(spotCheckContrastDetail.getContrastVolumeWeight());
        waybillCollectDto.setContrastLarge(spotCheckContrastDetail.getContrastLarge());
        waybillCollectDto.setBillingCalcWeight(spotCheckContrastDetail.getBillingCalcWeight());
        waybillCollectDto.setDutyType(spotCheckContrastDetail.getDutyType());

        // 比较差异数据
        double weightDiff = MathUtils.keepScale(Math.abs((spotCheckContrastDetail.getContrastWeight() == null
                ? Constants.DOUBLE_ZERO : spotCheckContrastDetail.getContrastWeight()) - totalWeight), 2);
        waybillCollectDto.setWeightDiff(String.valueOf(weightDiff));
        double volumeWeightDiff = MathUtils.keepScale(Math.abs((spotCheckContrastDetail.getContrastVolumeWeight() == null
                ? Constants.DOUBLE_ZERO : spotCheckContrastDetail.getContrastVolumeWeight()) - totalVolume), 2);
        waybillCollectDto.setVolumeWeightDiff(String.valueOf(volumeWeightDiff));
        waybillCollectDto.setDiffStandard(spotCheckContext.getDiffStandard());
        // ExcessStatus 为null 则表示一单多件还未判断出超标状态：'待集齐计算'
        waybillCollectDto.setIsExcess(spotCheckContext.getExcessStatus() == null ? ExcessStatusEnum.EXCESS_ENUM_COMPUTE.getCode() : spotCheckContext.getExcessStatus());
        waybillCollectDto.setExcessReason(spotCheckContext.getExcessReason());
        double largeDiff = MathUtils.keepScale(Math.abs((spotCheckContrastDetail.getContrastLarge() == null
                ? Constants.DOUBLE_ZERO : spotCheckContrastDetail.getContrastLarge()) - reviewLarge), 2);
        waybillCollectDto.setLargeDiff(largeDiff);
        // 集齐
        waybillCollectDto.setFullCollect(spotCheckContext.getIsGatherTogether() ? Constants.CONSTANT_NUMBER_ONE : Constants.NUMBER_ZERO);
        return waybillCollectDto;
    }

    /**
     * 组装公共数据
     *
     * @param spotCheckContext
     * @return
     */
    protected WeightVolumeCollectDto assembleCommonCollectDto(SpotCheckContext spotCheckContext) {
        WeightVolumeCollectDto commonCollectDto = new WeightVolumeCollectDto();
        commonCollectDto.setRecordType(SpotCheckRecordTypeEnum.SUMMARY_RECORD.getCode());
        commonCollectDto.setIsWaybillSpotCheck(spotCheckContext.getSpotCheckDimensionType());
        // 基础数据
        commonCollectDto.setFromSource(spotCheckContext.getSpotCheckSourceFrom());
        commonCollectDto.setSpotCheckType(spotCheckContext.getSpotCheckBusinessType());
        commonCollectDto.setMultiplePackage(spotCheckContext.getIsMultiPack() ? Constants.CONSTANT_NUMBER_ONE : Constants.NUMBER_ZERO);
        commonCollectDto.setReviewDate(spotCheckContext.getOperateTime());
        commonCollectDto.setWaybillCode(spotCheckContext.getWaybillCode());
        commonCollectDto.setPackageCode(spotCheckContext.getPackageCode());
        commonCollectDto.setIsTrustBusi(spotCheckContext.getIsTrustMerchant() ? Constants.CONSTANT_NUMBER_ONE : Constants.NUMBER_ZERO);
        commonCollectDto.setMerchantCode(spotCheckContext.getMerchantCode());
        commonCollectDto.setBusiCode(spotCheckContext.getMerchantId());
        commonCollectDto.setBusiName(spotCheckContext.getMerchantName());
        commonCollectDto.setProductTypeCode(spotCheckContext.getProductTypeCode());
        commonCollectDto.setProductTypeName(spotCheckContext.getProductTypeName());
        BaseStaffSiteOrgDto reviewSite = spotCheckContext.getReviewSite();
        commonCollectDto.setReviewSubType(reviewSite == null
                ? null : Objects.equals(reviewSite.getSiteType(), Constants.DMS_SITE_TYPE) ? Constants.CONSTANT_NUMBER_ONE : Constants.NUMBER_ZERO);
        commonCollectDto.setVolumeRate(spotCheckContext.getVolumeRate());
        // 复核数据
        SpotCheckReviewDetail spotCheckReviewDetail = spotCheckContext.getSpotCheckReviewDetail();
        commonCollectDto.setReviewOrgCode(spotCheckReviewDetail.getReviewOrgId());
        commonCollectDto.setReviewOrgName(spotCheckReviewDetail.getReviewOrgName());
        commonCollectDto.setReviewSiteCode(spotCheckReviewDetail.getReviewSiteCode());
        commonCollectDto.setReviewSiteName(spotCheckReviewDetail.getReviewSiteName());
        commonCollectDto.setReviewErp(spotCheckReviewDetail.getReviewUserErp());
        commonCollectDto.setReviewWeight(spotCheckReviewDetail.getReviewWeight());
        commonCollectDto.setReviewVolume(spotCheckReviewDetail.getReviewVolume());
        commonCollectDto.setReviewVolumeWeight(spotCheckReviewDetail.getReviewVolumeWeight());
        commonCollectDto.setMoreBigWeight(spotCheckReviewDetail.getReviewLarge());

        return commonCollectDto;
    }

    private String getPicUrlCache(String packageCode, Integer siteCode) {
        try {
            String key = String.format(CacheKeyConstants.CACHE_SPOT_CHECK_PICTURE, packageCode, siteCode);
            return jimdbCacheService.get(key);
        }catch (Exception e){
            logger.error("根据包裹号:{}站点:{}获取缓存异常!", packageCode, siteCode);
        }
        return null;
    }

    /**
     * 汇总复核重量体积
     *
     * @param spotCheckContext
     */
    protected void summaryReviewWeightVolume(SpotCheckContext spotCheckContext) {
        SpotCheckReviewDetail spotCheckReviewDetail = spotCheckContext.getSpotCheckReviewDetail();
        if(spotCheckReviewDetail.getReviewTotalWeight() > spotCheckReviewDetail.getReviewWeight()){
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
            condition.setRecordType(SpotCheckRecordTypeEnum.DETAIL_RECORD.getCode());
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
        spotCheckReviewDetail.setReviewTotalWeight(MathUtils.keepScale(totalWeight, 2));
        spotCheckReviewDetail.setReviewTotalVolume(MathUtils.keepScale(totalVolume, 2));
        // 集齐
        spotCheckContext.setIsGatherTogether(true);
    }

    /**
     * 多次数据处理
     *
     * @param spotCheckContext
     */
    protected void multiDataDeal(SpotCheckContext spotCheckContext) {
        if(StringUtils.isEmpty(spotCheckDealService.spotCheckPackSetStr(spotCheckContext.getWaybillCode(), spotCheckContext.getReviewSiteCode()))){
            // 初始化运单维度记录
            WeightVolumeCollectDto initialWaybillCollect = assembleCommonCollectDto(spotCheckContext);
            initialWaybillCollect.setPackageCode(spotCheckContext.getWaybillCode());
            initialWaybillCollect.setReviewWeight(null);
            initialWaybillCollect.setReviewVolume(null);
            initialWaybillCollect.setReviewLWH(null);
            initialWaybillCollect.setReviewVolumeWeight(null);
            initialWaybillCollect.setMoreBigWeight(null);
            initialWaybillCollect.setWeightDiff(null);
            initialWaybillCollect.setVolumeWeightDiff(null);
            initialWaybillCollect.setLargeDiff(null);
            // 初始化总记录时：需记录'是否有图片'字段
            String pictureUrl = StringUtils.isEmpty(spotCheckContext.getPictureAddress())
                    ? getPicUrlCache(spotCheckContext.getPackageCode(), spotCheckContext.getReviewSiteCode()) : spotCheckContext.getPictureAddress();
            initialWaybillCollect.setIsHasPicture(StringUtils.isEmpty(pictureUrl) ? Constants.NUMBER_ZERO : Constants.CONSTANT_NUMBER_ONE);
            initialWaybillCollect.setPictureAddress(null);
            initialWaybillCollect.setIsExcess(ExcessStatusEnum.EXCESS_ENUM_COMPUTE.getCode());
            spotCheckServiceProxy.insertOrUpdateProxyPrevious(initialWaybillCollect);
        }
        // 集齐
        if(spotCheckDealService.gatherTogether(spotCheckContext)){
            // 更新运单维度数据
            spotCheckServiceProxy.insertOrUpdateProxyPrevious(assembleAfterGatherCollectDto(spotCheckContext));
            // 设置超标缓存
            setSpotCheckCache(spotCheckContext.getWaybillCode(), spotCheckContext.getExcessStatus());
        }
        // 设置包裹维度缓存
        setSpotCheckPackCache(spotCheckContext.getPackageCode(), spotCheckContext.getReviewSiteCode());
        // 新增包裹维度记录
        spotCheckServiceProxy.insertOrUpdateProxyPrevious(assembleBeforeGatherPackCollectDto(spotCheckContext));
        // 抽检全程跟踪
        spotCheckDealService.sendWaybillTrace(spotCheckContext);
        // 记录抽检操作日志
        spotCheckDealService.recordSpotCheckLog(spotCheckContext);
    }

    /**
     * 一次数据处理
     *
     * @param spotCheckContext
     */
    protected void onceDataDeal(SpotCheckContext spotCheckContext) {
        // 设置超标缓存
        setSpotCheckCache(spotCheckContext.getWaybillCode(), spotCheckContext.getExcessStatus());
        // 数据落es
        spotCheckServiceProxy.insertOrUpdateProxyPrevious(assembleAfterGatherCollectDto(spotCheckContext));
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
            // 设置包裹已抽检缓存，key：packageCode， value：1
            String packSpotCheckKey = CacheKeyConstants.CACHE_KEY_PACKAGE_OR_WAYBILL_CHECK_FLAG.concat(packageCode);
            jimdbCacheService.setEx(packSpotCheckKey, Constants.CONSTANT_NUMBER_ONE, 7, TimeUnit.DAYS);
            // 设置运单下已抽检包裹缓存，key：waybillCode + siteCode ， value：packSet 缓存
            String packListKey = String.format(CacheKeyConstants.CACHE_SPOT_CHECK_PACK_LIST, siteCode, waybillCode);
            Set<String> packSet = new HashSet<>();
            String packSetStr = spotCheckDealService.spotCheckPackSetStr(waybillCode, siteCode);
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
