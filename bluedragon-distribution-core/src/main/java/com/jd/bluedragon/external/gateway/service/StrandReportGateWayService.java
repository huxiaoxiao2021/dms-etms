package com.jd.bluedragon.external.gateway.service;

import java.util.List;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.strandreport.request.ConfigStrandReasonData;
import com.jd.bluedragon.common.dto.strandreport.request.StrandReportReq;

public interface StrandReportGateWayService {

    /**
     * 滞留上报
     * @param request
     * @return
     */
    JdCResponse<Boolean> report(StrandReportReq request);
    /**
     * 查询滞留上报原因
     * 全部
     * @return
     */
    JdCResponse<List<ConfigStrandReasonData>> queryReasonList();
    /**
     * 查询滞留上报原因
     * 非冷链
     * @return
     */
    JdCResponse<List<ConfigStrandReasonData>> queryNoColdReasonList();
}
