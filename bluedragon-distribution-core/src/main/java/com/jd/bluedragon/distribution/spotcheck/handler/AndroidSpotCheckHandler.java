package com.jd.bluedragon.distribution.spotcheck.handler;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 安卓抽检
 *
 * @author hujiping
 * @date 2021/8/10 10:52 上午
 */
@Service("androidSpotCheckHandler")
public class AndroidSpotCheckHandler extends AbstractSpotCheckHandler {

    @Autowired
    private AbstractExcessStandardHandler abstractExcessStandardHandler;

    @Override
    protected void spotCheck(SpotCheckContext spotCheckContext, InvokeResult<Boolean> result) {
        super.spotCheck(spotCheckContext, result);
    }

    @Override
    protected InvokeResult<CheckExcessResult> checkIsExcessB(SpotCheckContext spotCheckContext) {
        obtainContrast(spotCheckContext);
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
        onceDataDeal(spotCheckContext);
    }
}
