package com.jd.bluedragon.distribution.spotcheck.handler;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.*;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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
        if(spotCheckContext.getIsMultiPack()){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_ONLY_SUPPORT_ONE_PACK);
            return;
        }
        super.spotCheck(spotCheckContext, result);
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
    protected void reformCheck(SpotCheckContext spotCheckContext, InvokeResult<Boolean> result) {
        // 一单一件校验
        if(spotCheckContext.getIsMultiPack()){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_ONLY_SUPPORT_ONE_PACK);
            return;
        }
        super.reformCheck(spotCheckContext, result);
    }

    @Override
    protected void dealAfterCheckSuc(SpotCheckContext spotCheckContext) {
        onceDataDeal(spotCheckContext);
    }
}
