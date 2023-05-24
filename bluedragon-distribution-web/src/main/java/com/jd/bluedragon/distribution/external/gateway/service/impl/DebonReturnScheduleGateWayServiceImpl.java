package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.debon.DebonReturnScheduleService;
import com.jd.bluedragon.distribution.debon.ReturnScheduleRequest;
import com.jd.bluedragon.external.gateway.service.DebonReturnScheduleGateWayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/5/22 16:15
 * @Description:
 */
public class DebonReturnScheduleGateWayServiceImpl implements DebonReturnScheduleGateWayService {

    @Autowired
    private DebonReturnScheduleService debonReturnScheduleService;
    @Override
    public JdCResponse<Boolean> returnScheduleSiteInfoByWaybill(ReturnScheduleRequest request) {
        return debonReturnScheduleService.returnScheduleSiteInfoByWaybill(request);
    }
}
