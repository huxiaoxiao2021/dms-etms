package com.jd.bluedragon.distribution.spotcheck.handler;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.*;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
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
    private SpotCheckDealService spotCheckDealService;

    @Autowired
    private AbstractExcessStandardHandler abstractExcessStandardHandler;

    @Override
    protected void spotCheck(SpotCheckContext spotCheckContext, InvokeResult<Boolean> result) {
        if(!spotCheckDealService.isExecuteBCFuse()){
            if(!BusinessUtil.isB2b(spotCheckContext.getWaybill().getWaybillSign())){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_ONLY_SUPPORT_B);
                return;
            }
        }
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
