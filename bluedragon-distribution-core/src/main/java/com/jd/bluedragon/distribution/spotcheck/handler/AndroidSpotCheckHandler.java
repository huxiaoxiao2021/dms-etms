package com.jd.bluedragon.distribution.spotcheck.handler;

import com.jd.bluedragon.core.base.ReportExternalManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.*;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 安卓抽检
 *
 * @author hujiping
 * @date 2021/8/10 10:52 上午
 */
@Service("androidSpotCheckHandler")
public class AndroidSpotCheckHandler extends AbstractSpotCheckHandler {

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    private SpotCheckDealService spotCheckDealService;

    @Autowired
    private AbstractExcessStandardHandler abstractExcessStandardHandler;

    @Autowired
    private ReportExternalManager reportExternalManager;

    @Override
    protected void spotCheck(SpotCheckContext spotCheckContext, InvokeResult<Boolean> result) {
        Waybill waybill = spotCheckContext.getWaybill();
        String waybillCode = spotCheckContext.getWaybillCode();
        // 是否B网
        if(!BusinessUtil.isB2b(waybill.getWaybillSign())){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_ONLY_SUPPORT_B);
            return;
        }
        // 是否妥投
        if(waybillTraceManager.isWaybillFinished(waybillCode)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_FORBID_FINISHED_PACK);
            return;
        }
        // 是否已抽检
        if(spotCheckDealService.checkIsHasSpotCheck(waybillCode)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_HAS_SPOT_CHECK);
        }
        // 重泡比校验
        SpotCheckReviewDetail spotCheckReviewDetail = spotCheckContext.getSpotCheckReviewDetail();
        double weight = spotCheckReviewDetail.getReviewWeight();
        double volume = spotCheckReviewDetail.getReviewVolume();
        if(weight / volume > SpotCheckConstants.WEIGHT_VOLUME_RATIO){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, String.format(SpotCheckConstants.SPOT_CHECK_VOLUME_RATE_LIMIT_B, waybillCode, SpotCheckConstants.WEIGHT_VOLUME_RATIO));
            return;
        }
        int packNum = waybill.getGoodNumber();
        if(weight / packNum > SpotCheckConstants.WEIGHT_MAX_RATIO){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, String.format(SpotCheckConstants.SPOT_CHECK_WEIGHT_LIMIT_B, SpotCheckConstants.WEIGHT_MAX_RATIO));
            return;
        }
        if(volume / packNum > SpotCheckConstants.VOLUME_MAX_RATIO * SpotCheckConstants.CM3_M3_MAGNIFICATION){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, String.format(SpotCheckConstants.SPOT_CHECK_VOLUME_LIMIT_B, SpotCheckConstants.VOLUME_MAX_RATIO));
        }
    }

    @Override
    protected InvokeResult<CheckExcessResult> checkIsExcessB(SpotCheckContext spotCheckContext) {
        spotCheckDealService.assembleContrastDataFromWaybillFlow(spotCheckContext);
        SpotCheckReviewDetail spotCheckReviewDetail = spotCheckContext.getSpotCheckReviewDetail();
        SpotCheckContrastDetail spotCheckContrastDetail = spotCheckContext.getSpotCheckContrastDetail();
        if(Objects.equals(spotCheckReviewDetail.getReviewSiteCode(), spotCheckContrastDetail.getContrastSiteCode())){
            InvokeResult<CheckExcessResult> result = new InvokeResult<CheckExcessResult>();
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_SAME_SITE);
            return result;
        }
        return abstractExcessStandardHandler.checkIsExcess(spotCheckContext);
    }

    @Override
    protected InvokeResult<CheckExcessResult> checkIsExcessC(SpotCheckContext spotCheckContext) {
        InvokeResult<CheckExcessResult> result = new InvokeResult<CheckExcessResult>();
        result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "待扩展!");
        return result;
    }

    @Override
    protected void dealAfterCheckSuc(SpotCheckContext spotCheckContext) {
        // 设置超标缓存
        setSpotCheckCache(spotCheckContext.getWaybillCode(), spotCheckContext.getExcessStatus());
        // 数据落库
        WeightVolumeCollectDto weightVolumeCollectDto = assembleWeightVolumeCollectDto(spotCheckContext);
        reportExternalManager.insertOrUpdateForWeightVolume(assembleWeightVolumeCollectDto(spotCheckContext));
        // 非快运外单只记录es
        if(!BusinessUtil.isKyLdop(spotCheckContext.getWaybill().getWaybillSign())){
            return;
        }
        // 下发超标mq
        spotCheckDealService.issueSpotCheckDetail(weightVolumeCollectDto);
    }
}
