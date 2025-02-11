package com.jd.bluedragon.distribution.spotcheck.handler;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.base.*;
import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.consumable.domain.ConsumableCodeEnums;
import com.jd.bluedragon.distribution.consumable.domain.PackingTypeEnum;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.send.service.SendMService;
import com.jd.bluedragon.distribution.spotcheck.domain.*;
import com.jd.bluedragon.distribution.spotcheck.enums.*;
import com.jd.bluedragon.distribution.spotcheck.exceptions.SpotCheckBusinessException;
import com.jd.bluedragon.distribution.spotcheck.exceptions.SpotCheckSysException;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.MathUtils;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.BoxChargeDto;
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
import java.util.stream.Collectors;

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
        // 泡重比校验
        if(weightVolumeRatioCheck(spotCheckContext, result)){
            return;
        }
        //有打木架服务不支持人工抽检
        if(!isSupportSpotCheck(spotCheckContext, result)){
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

    protected void fillContextSpotCheckRecords(SpotCheckContext context){
        SpotCheckQueryCondition queryCondition = new SpotCheckQueryCondition();
        queryCondition.setWaybillCode(context.getWaybillCode());
        List<WeightVolumeSpotCheckDto> spotCheckRecords = spotCheckQueryManager.querySpotCheckByCondition(queryCondition);
        if(CollectionUtils.isNotEmpty(spotCheckRecords)){
            context.setSpotCheckRecords(spotCheckRecords);
            return;
        }
        context.setSpotCheckRecords(Lists.newArrayList());
    }
    
    /**
     * 是否支持抽检，由子类实现，默认是true（支持抽检）
     * @param context
     * @param result
     * @return
     */
    protected boolean isSupportSpotCheck(SpotCheckContext context, InvokeResult<Boolean> result){return true;}

    /**
     * 检查是否存在打木架服务
     * @param context
     * @param result
     * @return
     */
    protected boolean checkWoodenFrameService(SpotCheckContext context, InvokeResult<Boolean> result){
        BaseEntity<List<BoxChargeDto>> baseEntity = waybillQueryManager.getBoxChargeByWaybillCode(context.getWaybillCode());
        if (baseEntity != null && CollectionUtils.isNotEmpty(baseEntity.getData())){
            for(BoxChargeDto boxChargeDto : baseEntity.getData()){
                if(ConsumableCodeEnums.isWoodenConsumable(boxChargeDto.getBarCode()) || PackingTypeEnum.isWoodenConsumable(boxChargeDto.getPackingType())){
                    result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.WOODEN_FRAME_NOT_SUPPORT_ARTIFICIAL_SPOT_CHECK);
                    return false;
                }
            }
        }
        return true;
    }

    private boolean reformSendCheck(SpotCheckContext spotCheckContext, InvokeResult<Boolean> result) {
        SendDetail param = new SendDetail();
        param.setCreateSiteCode(spotCheckContext.getReviewSiteCode());
        param.setPackageBarcode(spotCheckContext.getPackageCode());
        param.setOperateTime(spotCheckContext.getOperateTime());
        SendDetail sendDetail = sendDetailService.findOneByParams(param);
        if(sendDetail != null){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, String.format(SpotCheckConstants.SPOT_CHECK_PACK_SEND_REFORM, sendDetail.getPackageBarcode()));
            return true;
        }
        return false;
    }

    private void reformSpotCheck(SpotCheckContext spotCheckContext, InvokeResult<Boolean> result) {
        if(spotCheckDealService.checkIsHasSpotCheck(spotCheckContext)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_HAS_SPOT_CHECK);
        }
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
        
        // cache deal
        String key = String.format(CacheKeyConstants.CACHE_SPOT_CHECK_CHECK, spotCheckDto.getSiteCode(),
                WaybillUtil.getWaybillCode(spotCheckDto.getBarCode()));
        waitDeal(key);
        try {
            // 初始化抽检上下文
            SpotCheckContext spotCheckContext = initSpotCheckContext(spotCheckDto);
            // 抽检校验
            reformCheck(spotCheckContext, result);
            if(!result.codeSuccess()){
                return result;
            }
            // 数据处理
            afterCheckDealReform(spotCheckDto, spotCheckContext, result);
        }catch (Exception e){
            // hints: only here delete cache and do not add unLock method at finally
            unLock(key);
            throw e;
        }
        return result;
    }

    private void unLock(String key) {
        try {
            jimdbCacheService.del(key);
        }catch (Exception e){
            logger.error("删除抽检缓存:{}异常!", key, e);
        }
    }

    /**
     * hint: 将一个运单下的包裹处理顺序变成串行，为了解决es的1s刷盘问题
     * 
     * @param key
     */
    private void waitDeal(String key) {
        if(jimdbCacheService.exists(key)){
            try {
                // 此处sleep为了将运单下包裹执行顺序改成串行
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                logger.error("线程sleep异常", e);
            }
            throw new SpotCheckSysException("当前运单下有包裹正在处理,进行重试!");
        }
        jimdbCacheService.setNx(key, 1, 3, TimeUnit.SECONDS);
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
        // spotCheckDealService.sendWaybillTrace(spotCheckContext);
    }

    protected WeightVolumeSpotCheckDto assembleSummaryReform(SpotCheckContext spotCheckContext) {
        WeightVolumeSpotCheckDto dto = new WeightVolumeSpotCheckDto();
        // 复核数据
        SpotCheckReviewDetail spotCheckReviewDetail = spotCheckContext.getSpotCheckReviewDetail();
        dto.setReviewSource(SpotCheckSourceFromEnum.analysisCodeFromName(spotCheckContext.getSpotCheckSourceFrom()));
        dto.setReviewOrgCode(spotCheckReviewDetail.getReviewOrgId());
        dto.setReviewOrgName(spotCheckReviewDetail.getReviewOrgName());
        dto.setReviewProvinceAgencyCode(spotCheckReviewDetail.getReviewProvinceAgencyCode());
        dto.setReviewProvinceAgencyName(spotCheckReviewDetail.getReviewProvinceAgencyName());
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
        dto.setContrastProvinceAgencyCode(spotCheckContrastDetail.getContrastProvinceAgencyCode());
        dto.setContrastProvinceAgencyName(spotCheckContrastDetail.getContrastProvinceAgencyName());
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
        // 计费操作人ID
        dto.setBillOperatorId(spotCheckContrastDetail.getBillOperatorId());
        // 计费操作人erp或pin
        dto.setBillOperatorErp(spotCheckContrastDetail.getBillOperatorErp());
        // 通用数据
        dto.setReviewDate(System.currentTimeMillis());
        dto.setWaybillCode(spotCheckContext.getWaybillCode());
        dto.setPackageCode(spotCheckContext.getWaybillCode());
        dto.setMerchantCode(spotCheckContext.getMerchantCode());
        dto.setMerchantName(spotCheckContext.getMerchantName());
        dto.setIsTrustMerchant(spotCheckContext.getIsTrustMerchant() ? Constants.CONSTANT_NUMBER_ONE : Constants.NUMBER_ZERO);
        dto.setVolumeRateStr(spotCheckContext.getVolumeRate());
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

        // 视频链接
        if (Constants.NUMBER_ONE.equals(spotCheckContext.getIsHasVideo())) {
            dto.setIsHasVideo(spotCheckContext.getIsHasVideo());
            dto.setVideoPicture(spotCheckContext.getVideoAddress());
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
        // 扩展字段
        dto.setExtendMap(spotCheckContext.getExtendMap());
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
        int packNum = waybill.getGoodNumber();
        SpotCheckReviewDetail spotCheckReviewDetail = spotCheckContext.getSpotCheckReviewDetail();
        double weight = spotCheckReviewDetail.getReviewWeight();
        double volume = spotCheckReviewDetail.getReviewVolume();

        double standVolumeMax = weight * SpotCheckConstants.CM3_M3_MAGNIFICATION * SpotCheckConstants.VOLUME_WEIGHT_RATIO_MAX;
        double standVolumeMin = weight * SpotCheckConstants.CM3_M3_MAGNIFICATION * SpotCheckConstants.VOLUME_WEIGHT_RATIO_MIN;
        //体积（m³）与重量（kg）之比不能大于0.2，不能小于0.0005
        if(volume > standVolumeMax || volume < standVolumeMin){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_NOT_MEET_THEORETICAL_VALUE);
            return true;
        }
        // 包裹维度泡重比校验
        if(Objects.equals(spotCheckContext.getSpotCheckDimensionType(), SpotCheckDimensionEnum.SPOT_CHECK_PACK.getCode())){
            //运单估计重量和估计体积不超过最大值
            if(weight * packNum > SpotCheckConstants.WEIGHT_MAX_RATIO || volume * packNum > SpotCheckConstants.VOLUME_MAX_RATIO * SpotCheckConstants.CM3_M3_MAGNIFICATION){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_EXCESS_LIMITATION);
                return true;
            }
            return false;
        }
        // 运单维度泡重比校验
        //运单重量和体积不超过最大值
        if(weight > SpotCheckConstants.WEIGHT_MAX_RATIO || volume > SpotCheckConstants.VOLUME_MAX_RATIO * SpotCheckConstants.CM3_M3_MAGNIFICATION){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_EXCESS_LIMITATION);
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
        String waybillCode = WaybillUtil.getWaybillCode(spotCheckDto.getBarCode().trim());
        SpotCheckContext spotCheckContext = new SpotCheckContext();
        spotCheckContext.setSpotCheckSourceFrom(spotCheckDto.getSpotCheckSourceFrom());
        spotCheckContext.setSpotCheckDimensionType(spotCheckDto.getDimensionType());
        spotCheckContext.setSpotCheckHandlerType(spotCheckDto.getSpotCheckHandlerType());
        spotCheckContext.setOperateTime(Objects.isNull(spotCheckDto.getOperateTime()) ? new Date() : spotCheckDto.getOperateTime());
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
        }else if(BusinessUtil.isColdDelivery(waybillSign) || (BusinessUtil.isColdCityDistribute(waybillSign)
                && BusinessUtil.isColdKB(waybillSign) && BusinessUtil.isColdReceipt(waybillSign))){
            spotCheckContext.setSpotCheckBusinessType(SpotCheckBusinessTypeEnum.SPOT_CHECK_TYPE_COLD.getCode());
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

        // 视频链接
        spotCheckContext.setIsHasVideo(StringUtils.isEmpty(spotCheckDto.getVideoUrl()) ? Constants.NUMBER_ZERO : Constants.CONSTANT_NUMBER_ONE);
        spotCheckContext.setVideoAddress(spotCheckDto.getVideoUrl());

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
        spotCheckReviewDetail.setReviewOrgId(spotCheckContext.getReviewSite() == null ? null : spotCheckContext.getReviewSite().getOrgId());
        spotCheckReviewDetail.setReviewOrgName(spotCheckContext.getReviewSite() == null ? null : spotCheckContext.getReviewSite().getOrgName());
        spotCheckReviewDetail.setReviewProvinceAgencyCode(spotCheckContext.getReviewSite() == null ? null : spotCheckContext.getReviewSite().getProvinceAgencyCode());
        spotCheckReviewDetail.setReviewProvinceAgencyName(spotCheckContext.getReviewSite() == null ? null : spotCheckContext.getReviewSite().getProvinceAgencyName());
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

        // fill 已抽检记录
        fillContextSpotCheckRecords(spotCheckContext);
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
            String picUrl = picMap.get(key);
            if (StringUtils.isNotBlank(picUrl)) {
                sortMap.put(key, picUrl);
            }
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
            List<WeightVolumeSpotCheckDto> packList = spotCheckContext.getSpotCheckRecords().stream()
                    .filter(item -> Objects.equals(item.getReviewSiteCode(), spotCheckContext.getReviewSiteCode())
                            && Objects.equals(item.getRecordType(), SpotCheckRecordTypeEnum.DETAIL_RECORD.getCode()))
                    .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(packList)){
                for (WeightVolumeSpotCheckDto spotCheckDto : packList) {
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
