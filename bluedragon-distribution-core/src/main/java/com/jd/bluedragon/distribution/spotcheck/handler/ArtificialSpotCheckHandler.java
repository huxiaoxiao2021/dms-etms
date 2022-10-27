package com.jd.bluedragon.distribution.spotcheck.handler;

import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckConstants;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckContext;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 人工抽检
 *
 * @author hujiping
 * @date 2021/8/10 10:52 上午
 */
@Service("artificialSpotCheckHandler")
public class ArtificialSpotCheckHandler extends AbstractSpotCheckHandler {

    @Autowired
    WaybillQueryManager waybillQueryManager;

    @Override
    protected void uniformityCheck(SpotCheckDto spotCheckDto, SpotCheckContext spotCheckContext, InvokeResult<Boolean> result) {
        if(!Objects.equals(spotCheckDto.getExcessStatus(), spotCheckContext.getExcessStatus())
                || !Objects.equals(spotCheckDto.getExcessType(), spotCheckContext.getExcessType())){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, SpotCheckConstants.SPOT_CHECK_RESULT_CHANGE);
        }
    }

    @Override
    protected boolean artificialSpotCheck(SpotCheckContext context, InvokeResult<Boolean> result) {
        return super.checkWoodenFrameService(context, result);
    }
}
