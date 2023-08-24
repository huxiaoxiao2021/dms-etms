package com.jd.bluedragon.distribution.jsf.service;

import com.jd.bluedragon.distribution.jsf.domain.ReturnScheduleRequest;
import com.jd.bluedragon.distribution.jsf.domain.ReturnScheduleResult;
import com.jd.ql.dms.common.domain.JdResponse;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/5/22 16:14
 * @Description:
 */
public interface WaybillReturnScheduleGateWayService {

    /**
     * 根据运单信息进行返调度
     * @param request
     * @return
     */
    JdResponse<ReturnScheduleResult> returnScheduleSiteInfoByWaybill(ReturnScheduleRequest request);
}
