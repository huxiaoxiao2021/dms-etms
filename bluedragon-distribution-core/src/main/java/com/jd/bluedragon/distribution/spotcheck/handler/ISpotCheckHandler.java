package com.jd.bluedragon.distribution.spotcheck.handler;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckDto;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckResult;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2021/8/10 9:47 上午
 */
public interface ISpotCheckHandler {

    /**
     * 校验是否超标
     * @param spotCheckDto
     * @return
     */
    InvokeResult<SpotCheckResult> checkExcess(SpotCheckDto spotCheckDto);

    /**
     * 抽检处理
     *
     * @param spotCheckDto
     * @return
     */
    InvokeResult<Boolean> dealSpotCheck(SpotCheckDto spotCheckDto);

    /**
     * 校验是否超标（无其他校验）
     *
     * @param spotCheckDto
     * @return
     */
    InvokeResult<Integer> checkIsExcessWithOutOtherCheck(SpotCheckDto spotCheckDto);
}
