package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.sanwu.request.SanwuReq;
import com.jd.bluedragon.common.dto.jyexpection.sanwu.request.SwUploadScanReq;

/**
 * 作业app异常岗-三无
 */
public interface JyExceptionSanwuGatewayService {

    /**
     * 通用异常上报入口-扫描
     */
    JdCResponse<Object> uploadScan(SwUploadScanReq req);

    JdCResponse<Object> statisticsByStatus(SanwuReq req);
}
