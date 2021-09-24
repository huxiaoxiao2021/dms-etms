package com.jd.bluedragon.distribution.spotcheck.handler;

import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.spotcheck.domain.*;
import com.jd.bluedragon.distribution.spotcheck.enums.ExcessStatusEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckBusinessTypeEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckDimensionEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckHandlerTypeEnum;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.service.DMSWeightVolumeService;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.Waybill;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 人工抽检
 *
 * @author hujiping
 * @date 2021/8/10 10:52 上午
 */
@Service("artificialSpotCheckHandler")
public class ArtificialSpotCheckHandler extends AbstractSpotCheckHandler {

    private static Logger logger = LoggerFactory.getLogger(ArtificialSpotCheckHandler.class);

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    private SendDetailService sendDetailService;

    @Autowired
    private SpotCheckDealService spotCheckDealService;

    @Autowired
    private AbstractExcessStandardHandler abstractExcessStandardHandler;

    @Autowired
    private DMSWeightVolumeService dmsWeightVolumeService;

    @Override
    protected void spotCheck(SpotCheckContext spotCheckContext, InvokeResult<Boolean> result) {
        Waybill waybill = spotCheckContext.getWaybill();
        String waybillCode = spotCheckContext.getWaybillCode();
        String packageCode = spotCheckContext.getPackageCode();
        Integer reviewSiteCode = spotCheckContext.getReviewSiteCode();
        if(!spotCheckDealService.isExecuteBCFuse()){
            if(!BusinessUtil.isCInternet(waybill.getWaybillSign())){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_ONLY_SUPPORT_C);
                return;
            }
        }
        // 纯配外单校验
        if(!BusinessUtil.isPurematch(waybill.getWaybillSign())){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_ONLY_SUPPORT_PURE_MATCH);
            return;
        }
        // 是否妥投
        if(waybillTraceManager.isWaybillFinished(waybillCode)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_FORBID_FINISHED_PACK);
            return;
        }
        // B网不支持包裹维度抽检
        if(Objects.equals(spotCheckContext.getSpotCheckBusinessType(), SpotCheckBusinessTypeEnum.SPOT_CHECK_TYPE_B.getCode())
                && Objects.equals(spotCheckContext.getSpotCheckDimensionType(), SpotCheckDimensionEnum.SPOT_CHECK_PACK.getCode())){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_ARTIFICIAL_PACK_FORBID_B);
            return;
        }
        // 是否发货
        // 1、运单维度抽检：只要其中某一包裹已经发货则提示按包裹维度操作抽检
        // 2、包裹维度抽检：只要操作了发货则提示禁止抽检
        if(Objects.equals(SpotCheckDimensionEnum.SPOT_CHECK_WAYBILL.getCode(), spotCheckContext.getSpotCheckDimensionType())){
            if(!WaybillUtil.isWaybillCode(spotCheckContext.getPackageCode())){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_ONLY_WAYBILL);
                return;
            }
            SendDetail sendDetail = sendDetailService.findOneByWaybillCode(reviewSiteCode, waybillCode);
            if(sendDetail != null){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, String.format(SpotCheckConstants.SPOT_CHECK_PACK_SEND_TRANSFER, sendDetail.getPackageBarcode()));
                return;
            }
        }else {
            if(!WaybillUtil.isPackageCode(spotCheckContext.getPackageCode())){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_ONLY_PACK);
                return;
            }
            List<SendDetail> sendList = sendDetailService.findByWaybillCodeOrPackageCode(reviewSiteCode, waybillCode, packageCode);
            if(CollectionUtils.isNotEmpty(sendList)){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, String.format(SpotCheckConstants.SPOT_CHECK_PACK_SEND, packageCode));
                return;
            }
        }
        // 是否已抽检
        if(spotCheckDealService.checkIsHasSpotCheck(waybillCode)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_HAS_SPOT_CHECK);
        }
    }

    /**
     * 超标校验
     *  1、获取复核数据、核对数据
     *      1）、一单多件
     *          a、未集齐返回'待集齐计算'
     *          b、集齐则汇总复核数据
     *          c、核对数据从计费获取
     *      2）、一单一件
     *          a、核对数据从计费获取
     *  2、超标判断
     * @param spotCheckContext
     * @return
     */
    @Override
    protected InvokeResult<CheckExcessResult> checkIsExcessC(SpotCheckContext spotCheckContext) {
        InvokeResult<CheckExcessResult> result = new InvokeResult<CheckExcessResult>();
        // 是否集齐
        if(Objects.equals(spotCheckContext.getSpotCheckDimensionType(), SpotCheckDimensionEnum.SPOT_CHECK_PACK.getCode())) {
            if(spotCheckContext.getIsMultiPack() && !spotCheckDealService.gatherTogether(spotCheckContext)){
                CheckExcessResult excessData = new CheckExcessResult();
                excessData.setExcessCode(ExcessStatusEnum.EXCESS_ENUM_COMPUTE.getCode());
                result.setData(excessData);
                return result;
            }
            summaryReviewWeightVolume(spotCheckContext);
        }
        // 获取核对数据
        obtainContrast(spotCheckContext);
        result = abstractExcessStandardHandler.checkIsExcess(spotCheckContext);
        // 对比复核校验时判定的超标结果  与  提交超标数据时判定的超标结果  是否一致
        if(Objects.equals(spotCheckContext.getSpotCheckHandlerType(), SpotCheckHandlerTypeEnum.CHECK_AND_DEAL.getCode())
                && StringUtils.isEmpty(spotCheckContext.getPictureAddress())
                && Objects.equals(result.getCode(), ExcessStatusEnum.EXCESS_ENUM_YES.getCode())){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_RESULT_CHANGE);
        }
        if(logger.isInfoEnabled()){
            logger.info("人工抽检入参:{},校验结果:{}", JsonHelper.toJson(spotCheckContext), JsonHelper.toJson(result));
        }
        return result;
    }

    @Override
    protected InvokeResult<CheckExcessResult> checkIsExcessB(SpotCheckContext spotCheckContext) {
        return checkIsExcessC(spotCheckContext);
    }

    @Override
    protected void dealAfterCheckSuc(SpotCheckContext spotCheckContext) {
        if(Objects.equals(spotCheckContext.getSpotCheckDimensionType(), SpotCheckDimensionEnum.SPOT_CHECK_WAYBILL.getCode())){
            // 运单维度
            onceDataDeal(spotCheckContext);
        }else {
            // 包裹维度
            if(spotCheckContext.getIsMultiPack()){
                multiDataDeal(spotCheckContext);
            }else {
                onceDataDeal(spotCheckContext);
            }
        }
        // 上传称重数据
        dmsWeightVolumeService.dealWeightAndVolume(transferWeightVolumeEntity(spotCheckContext), false);
    }

    private WeightVolumeEntity transferWeightVolumeEntity(SpotCheckContext spotCheckContext) {
        WeightVolumeEntity weightVolumeEntity = new WeightVolumeEntity();
        weightVolumeEntity.setBarCode(spotCheckContext.getWaybillCode());
        weightVolumeEntity.setWaybillCode(spotCheckContext.getWaybillCode());
        weightVolumeEntity.setPackageCode(spotCheckContext.getPackageCode());
        SpotCheckReviewDetail spotCheckReviewDetail = spotCheckContext.getSpotCheckReviewDetail();
        weightVolumeEntity.setWeight(spotCheckReviewDetail.getReviewWeight());
        weightVolumeEntity.setVolume(spotCheckReviewDetail.getReviewVolume());
        boolean isWaybillSpotCheck = Objects.equals(SpotCheckDimensionEnum.SPOT_CHECK_WAYBILL.getCode(), spotCheckContext.getSpotCheckDimensionType());
        if(!isWaybillSpotCheck){
            weightVolumeEntity.setBarCode(spotCheckContext.getPackageCode());
            weightVolumeEntity.setLength(spotCheckReviewDetail.getReviewLength());
            weightVolumeEntity.setWidth(spotCheckReviewDetail.getReviewWidth());
            weightVolumeEntity.setHeight(spotCheckReviewDetail.getReviewHeight());
        }
        weightVolumeEntity.setBusinessType(isWaybillSpotCheck ? WeightVolumeBusinessTypeEnum.BY_WAYBILL : WeightVolumeBusinessTypeEnum.BY_PACKAGE);
        weightVolumeEntity.setSourceCode(FromSourceEnum.ARTIFICIAL_SPOT_CHECK);
        weightVolumeEntity.setOperateSiteCode(spotCheckReviewDetail.getReviewSiteCode());
        weightVolumeEntity.setOperateSiteName(spotCheckReviewDetail.getReviewSiteName());
        weightVolumeEntity.setOperatorCode(spotCheckReviewDetail.getReviewUserErp());
        weightVolumeEntity.setOperatorId(spotCheckReviewDetail.getReviewUserId());
        weightVolumeEntity.setOperatorName(spotCheckReviewDetail.getReviewUserName());
        weightVolumeEntity.setOperateTime(new Date());
        return weightVolumeEntity;
    }
}
