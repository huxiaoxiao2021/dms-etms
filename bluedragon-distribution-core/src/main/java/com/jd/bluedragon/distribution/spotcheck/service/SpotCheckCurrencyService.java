package com.jd.bluedragon.distribution.spotcheck.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckDto;

/**
 * 抽检通用接口
 *
 * @author hujiping
 * @date 2021/8/10 11:12 上午
 */
public interface SpotCheckCurrencyService {

    /**
     * 校验是否超标（只校验超标，不校验其他信息）
     * @param spotCheckDto
     * @return
     */
    InvokeResult<Integer> checkIsExcessWithOutOtherCheck(SpotCheckDto spotCheckDto);

    /**
     * 校验是否超标
     *
     * @param spotCheckDto
     * @return
     */
    InvokeResult<Integer> checkIsExcess(SpotCheckDto spotCheckDto);

    /**
     * 抽检处理
     *
     * @param spotCheckDto
     * @return
     */
    InvokeResult<Boolean> spotCheckDeal(SpotCheckDto spotCheckDto);
}
