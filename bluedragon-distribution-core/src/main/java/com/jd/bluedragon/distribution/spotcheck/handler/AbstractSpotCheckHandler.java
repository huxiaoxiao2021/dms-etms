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

        // 抽检校验
        reformCheck(spotCheckContext, result);
        if(!result.codeSuccess()){
            checkResult.customMessage(result.getCode(), result.getMessage());
            return checkResult;
        }
        // 超标校验
        return checkIsExcessReform(spotCheckContext);
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
        // 业务类型校验
        if(!BusinessUtil.isBInternet(waybillSign) && !BusinessUtil.isCInternet(waybillSign)
                && !(BusinessUtil.isMedicalFreshProductType(waybillSign) || BusinessUtil.isMedicine(waybillSign))){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_FORBID);
            return;
        }
        // 泡重比校验
        if(Objects.equals(spotCheckContext.getSpotCheckBusinessType(), SpotCheckBusinessTypeEnum.SPOT_CHECK_TYPE_B.getCode())
                && weightVolumeRatioCheck(spotCheckContext, result)){
            return;
        }
        //有打木架服务不支持人工抽检
        if(!isSupportArtificialSpotCheck(spotCheckContext, result)){
            logger.info("人工抽检打木架服务校验不通过");
            return;
        }
        //重量体积误输入校验(超出理论值)
        if(checkWeightAndVolumeParam(spotCheckContext, result)){
            logger.info("防呆校验不通过");
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

    /**
     * 防呆逻辑校验(运单维度)
     * 体积（m³）除以重量（kg）不能大于0.2或者小于0.0005
     * 体积不能超过300m³或者重量不能超过62500kg
     * @param context
     * @param result
     */
    protected boolean checkWeightAndVolumeParam(SpotCheckContext context, InvokeResult<Boolean> result){
        double weight = context.getSpotCheckReviewDetail().getReviewTotalWeight();
        //单位cm³
        double volume = context.getSpotCheckReviewDetail().getReviewTotalVolume();

        if(volume > SpotCheckConstants.ARTIFICIAL_SPOT_CHECK_VOLUME_MAX * SpotCheckConstants.CM3_M3_MAGNIFICATION
            || weight > SpotCheckConstants.ARTIFICIAL_SPOT_CHECK_WEIGHT_MAX){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.ARTIFICIAL_SPOT_CHECK_EXCESS_LIMITATION);
            return true;
        }

        double ratio = volume / weight * SpotCheckConstants.CM3_M3_MAGNIFICATION;
        if(ratio > SpotCheckConstants.ARTIFICIAL_SPOT_CHECK_VOLUME_WEIGHT_RATIO_MAX
            || ratio < SpotCheckConstants.ARTIFICIAL_SPOT_CHECK_VOLUME_WEIGHT_RATIO_MIN){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.ARTIFICIAL_SPOT_CHECK_NOT_MEET_THEORETICAL_VALUE);
            return true;
        }

        return false;
    }

    protected boolean isSupportArtificialSpotCheck(SpotCheckContext context, InvokeResult<Boolean> result){return true;}

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
        InvokeResult<SpotCheckResult> checkExcessResultInvokeResult = checkIsExcessReform(spotCheckContext);
        checkResult.customMessage(checkExcessResultInvokeResult.getCode(), checkExcessResultInvokeResult.getMessage());
        checkResult.setData(checkExcessResultInvokeResult.getData().getExcessStatus());
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

        // 抽检校验
        reformCheck(spotCheckContext, result);
        if(!result.codeSuccess()){
            return result;
        }
        // 数据处理
        afterCheckDealReform(spotCheckDto, spotCheckContext, result);
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
        // 抽检全程跟踪
        spotCheckDealService.sendWaybillTrace(spotCheckContext);
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
        }else if(BusinessUtil.isMedicalFreshProductType(waybillSign) || BusinessUtil.isMedicine(waybillSign)){
            spotCheckContext.setSpotCheckBusinessType(SpotCheckBusinessTypeEnum.SPOT_CHECK_TYPE_MEDICAL.getCode());
        }else {
            logger.warn(SpotCheckConstants.SPOT_CHECK_FORBID);
            throw new SpotCheckBusinessException(SpotCheckConstants.SPOT_CHECK_FORBID);
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
            SpotCheckQueryCondition condition = new SpotCheckQueryCondition();
            condition.setWaybillCode(spotCheckContext.getWaybillCode());
            condition.setReviewSiteCode(spotCheckContext.getReviewSiteCode());
            condition.setRecordType(SpotCheckRecordTypeEnum.DETAIL_RECORD.getCode());
            List<WeightVolumeSpotCheckDto> spotCheckDtoList = spotCheckQueryManager.querySpotCheckByCondition(condition);
            if(CollectionUtils.isNotEmpty(spotCheckDtoList)){
                for (WeightVolumeSpotCheckDto spotCheckDto : spotCheckDtoList) {
                    totalWeight += spotCheckDto.getReviewWeight() == null ? Constants.DOUBLE_ZERO : spotCheckDto.getReviewWeight();
                    totalVolume += spotCheckDto.getReviewVolume() == null ? Constants.DOUBLE_ZERO : spotCheckDto.getReviewVolume();
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
