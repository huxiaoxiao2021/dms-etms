package com.jd.bluedragon.distribution.spotcheck.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.spotcheck.domain.*;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.etms.waybill.domain.Waybill;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
    private SendDetailService sendDetailService;

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
        SpotCheckReviewDetail spotCheckReviewDetail = spotCheckContext.getSpotCheckReviewDetail();
        Integer reviewSiteCode = spotCheckReviewDetail.getReviewSiteCode();
        List<SendDetail> sendDetailRecords = sendDetailService.findByWaybillCodeOrPackageCode(reviewSiteCode, waybillCode, packageCode);
        if(CollectionUtils.isNotEmpty(sendDetailRecords)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_MUST_BEFORE_SEND);
            return;
        }
        // 是否已抽检
        if(spotCheckDealService.checkIsHasSpotCheck(waybillCode)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_HAS_SPOT_CHECK);
        }
    }

    @Override
    protected InvokeResult<CheckExcessResult> checkIsExcess(SpotCheckContext spotCheckContext) {
        spotCheckDealService.assembleContrastDataFromFinance(spotCheckContext);
        SpotCheckContrastDetail spotCheckContrastDetail = spotCheckContext.getSpotCheckContrastDetail();
        // 计费重量为0或null则从运单称重流水获取
        if(spotCheckContrastDetail.getContrastWeight() == null
                || Objects.equals(spotCheckContrastDetail.getContrastWeight(), Constants.DOUBLE_ZERO)){
            spotCheckDealService.assembleContrastDataFromWaybillFlow(spotCheckContext);
        }
        return abstractExcessStandardHandler.checkIsExcess(spotCheckContext);
    }

    @Override
    protected void dealAfterCheckSuc(SpotCheckContext spotCheckContext) {
        onePackDeal(spotCheckContext);
    }
}
