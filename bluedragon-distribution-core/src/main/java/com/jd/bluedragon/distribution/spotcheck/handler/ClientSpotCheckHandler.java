package com.jd.bluedragon.distribution.spotcheck.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.*;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckBusinessTypeEnum;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.etms.waybill.domain.Waybill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 客户端平台打印抽检
 *
 * @author hujiping
 * @date 2021/8/15 10:52 上午
 */
@Service("clientSpotCheckHandler")
public class ClientSpotCheckHandler extends AbstractSpotCheckHandler {

    @Autowired
    private SpotCheckDealService spotCheckDealService;

    @Autowired
    private AbstractExcessStandardHandler abstractExcessStandardHandler;

    @Override
    protected void spotCheck(SpotCheckContext spotCheckContext, InvokeResult<Boolean> result) {
        // 一单一件校验
        Waybill waybill = spotCheckContext.getWaybill();
        String waybillCode = spotCheckContext.getWaybillCode();
        String packageCode = spotCheckContext.getPackageCode();
        Integer siteCode = spotCheckContext.getReviewSiteCode();
        if(spotCheckContext.getIsMultiPack()){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_ONLY_SUPPORT_ONE_PACK);
            return;
        }
        // 纯配外单校验
        if(!BusinessUtil.isPurematch(waybill.getWaybillSign())){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_ONLY_SUPPORT_PURE_MATCH);
            return;
        }
        // 是否已发货校验
        if(spotCheckDealService.checkIsHasSend(packageCode, siteCode)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_MUST_BEFORE_SEND);
            return;
        }
        // 是否已抽检
        if(spotCheckDealService.checkIsHasSpotCheck(waybillCode)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_HAS_SPOT_CHECK);
        }
    }

    @Override
    protected InvokeResult<CheckExcessResult> checkIsExcessB(SpotCheckContext spotCheckContext) {
        obtainContrast(spotCheckContext);
        return abstractExcessStandardHandler.checkIsExcess(spotCheckContext);
    }

    @Override
    protected InvokeResult<CheckExcessResult> checkIsExcessC(SpotCheckContext spotCheckContext) {
        obtainContrast(spotCheckContext);
        return abstractExcessStandardHandler.checkIsExcess(spotCheckContext);
    }

    @Override
    protected void obtainContrast(SpotCheckContext spotCheckContext) {
        // B网 则从运单获取核对数据
        if(Objects.equals(spotCheckContext.getSpotCheckBusinessType(), SpotCheckBusinessTypeEnum.SPOT_CHECK_TYPE_B.getCode())){
            spotCheckDealService.assembleContrastDataFromWaybillFlow(spotCheckContext);
            return;
        }
        // C网 先从计费获取无则从运单获取核对数据
        spotCheckDealService.assembleContrastDataFromFinance(spotCheckContext);
        SpotCheckContrastDetail spotCheckContrastDetail = spotCheckContext.getSpotCheckContrastDetail();
        if(spotCheckContrastDetail.getContrastWeight() == null
                || Objects.equals(spotCheckContrastDetail.getContrastWeight(), Constants.DOUBLE_ZERO)){
            spotCheckDealService.assembleContrastDataFromWaybillFlow(spotCheckContext);
        }
    }

    @Override
    protected void dealAfterCheckSuc(SpotCheckContext spotCheckContext) {
        onePackDeal(spotCheckContext);
    }
}
