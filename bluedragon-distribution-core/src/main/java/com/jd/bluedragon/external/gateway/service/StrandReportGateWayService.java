package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.strandreport.request.StrandReportReq;

public interface StrandReportGateWayService {

    /**
     * 滞留上报
     * @param request
     * @return
     */
    JdCResponse<Boolean> report(StrandReportReq request);
}
