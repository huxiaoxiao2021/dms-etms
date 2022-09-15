package com.jd.bluedragon.distribution.spotcheck.handler;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.*;
import org.springframework.stereotype.Service;


/**
 * 客户端平台打印抽检
 *
 * @author hujiping
 * @date 2021/8/15 10:52 上午
 */
@Service("clientSpotCheckHandler")
public class ClientSpotCheckHandler extends AbstractSpotCheckHandler {

    @Override
    protected void reformCheck(SpotCheckContext spotCheckContext, InvokeResult<Boolean> result) {
        // 一单一件校验
        if(spotCheckContext.getIsMultiPack()){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_ONLY_SUPPORT_ONE_PACK);
            return;
        }
        super.reformCheck(spotCheckContext, result);
    }
}
