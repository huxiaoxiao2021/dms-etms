package com.jd.bluedragon.distribution.external.gateway.service.impl;



import com.jd.bluedragon.distribution.debon.WaybillReturnScheduleService;
import com.jd.bluedragon.distribution.jsf.domain.ReturnScheduleRequest;
import com.jd.bluedragon.distribution.jsf.service.WaybillReturnScheduleGateWayService;
import com.jd.ql.dms.common.domain.JdResponse;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/5/22 16:15
 * @Description:
 */
public class WaybillReturnScheduleGateWayServiceImpl implements WaybillReturnScheduleGateWayService {

    @Autowired
    private WaybillReturnScheduleService debonReturnScheduleService;
    @Override
    public JdResponse<Boolean> returnScheduleSiteInfoByWaybill(ReturnScheduleRequest request) {
        return debonReturnScheduleService.returnScheduleSiteInfoByWaybill(request);
    }
}
