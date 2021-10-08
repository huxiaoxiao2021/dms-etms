package com.jd.bluedragon.distribution.spotcheck.handler;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.*;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
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
    private AbstractExcessStandardHandler abstractExcessStandardHandler;

    @Override
    protected void spotCheck(SpotCheckContext spotCheckContext, InvokeResult<Boolean> result) {
        super.spotCheck(spotCheckContext, result);
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
    protected void dealAfterCheckSuc(SpotCheckContext spotCheckContext) {
        onceDataDeal(spotCheckContext);
    }
}
