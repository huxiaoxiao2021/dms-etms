package com.jd.bluedragon.distribution.spotcheck.service;

import com.jd.bluedragon.common.dto.spotcheck.PicAutoDistinguishRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckDto;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckResult;
import com.jd.etms.waybill.domain.Waybill;

/**
 * 抽检通用接口
 *
 * @author hujiping
 * @date 2021/8/10 11:12 上午
 */
public interface SpotCheckCurrencyService {

    /**
     * 获取运单信息
     *
     * @param barCode
     * @return
     */
    InvokeResult<Waybill> obtainBaseInfo(String barCode);

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
     * 校验超标
     *
     * @param spotCheckDto
     * @return
     */
    InvokeResult<SpotCheckResult> checkExcess(SpotCheckDto spotCheckDto);

    /**
     * 超标图片AI识别
     * @param picAutoDistinguishRequest
     * @return
     */
    InvokeResult<Boolean> picAutoDistinguish(PicAutoDistinguishRequest picAutoDistinguishRequest);

    /**
     * 抽检处理
     *
     * @param spotCheckDto
     * @return
     */
    InvokeResult<Boolean> spotCheckDeal(SpotCheckDto spotCheckDto);
}
