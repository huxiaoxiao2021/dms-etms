package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.debon.ReturnScheduleRequest;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/5/22 16:14
 * @Description:
 */
public interface DebonReturnScheduleGateWayService {

    /**
     * 根据运单信息进行返调度
     * @param request
     * @return
     */
    JdCResponse<Boolean> returnScheduleSiteInfoByWaybill(ReturnScheduleRequest request);
}
