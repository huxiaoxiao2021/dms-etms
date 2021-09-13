package com.jd.bluedragon.distribution.spotcheck.handler;

import com.jd.bluedragon.core.base.ReportExternalManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.*;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckBusinessTypeEnum;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * 网页抽检
 *
 * @author hujiping
 * @date 2021/8/10 10:56 上午
 */
@Service("webSpotCheckHandler")
public class WebSpotCheckHandler extends AbstractSpotCheckHandler {

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
        // 是否已抽检
        if(spotCheckDealService.checkIsHasSpotCheck(waybillCode)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_HAS_SPOT_CHECK);
            return;
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
        obtainContrast(spotCheckContext);
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
        return checkIsExcessB(spotCheckContext);
    }

    @Override
    protected void obtainContrast(SpotCheckContext spotCheckContext) {
        // B网 从运单获取核对数据
        if(Objects.equals(spotCheckContext.getSpotCheckBusinessType(), SpotCheckBusinessTypeEnum.SPOT_CHECK_TYPE_B.getCode())){
            spotCheckDealService.assembleContrastDataFromWaybillFlow(spotCheckContext);
            return;
        }
        // C网 从运单获取核对数据
        spotCheckDealService.assembleContrastDataFromFinance(spotCheckContext);
    }

    @Override
    protected void dealAfterCheckSuc(SpotCheckContext spotCheckContext) {
        // 设置超标缓存
        setSpotCheckCache(spotCheckContext.getWaybillCode(), spotCheckContext.getExcessStatus());
        // 数据落库
        WeightVolumeCollectDto weightVolumeCollectDto = assembleWaybillCollectDto(spotCheckContext);
        reportExternalManager.insertOrUpdateForWeightVolume(weightVolumeCollectDto);
        // 非快运外单只记录es
        if(!BusinessUtil.isKyLdop(spotCheckContext.getWaybill().getWaybillSign())){
            return;
        }
        // 下发超标mq
        spotCheckDealService.issueSpotCheckDetail(weightVolumeCollectDto);
    }
}
