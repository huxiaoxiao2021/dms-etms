package com.jd.bluedragon.external.gateway.service;


import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.predict.request.WorkWaveInspectedNotSendDetailsReq;
import com.jd.bluedragon.common.dto.predict.request.WorkWaveInspectedNotSendPackageCountReq;
import com.jd.bluedragon.common.dto.predict.response.WorkWaveInspectedNotSendDetailsResponse;

/**
 * 货量预测
 */
public interface PkgPredictGateWayService {

    /**
     * 查询当前波次已验未发的包裹量
     * @return
     */
    JdCResponse<Integer> queryCurrentWorkWaveInspectedNotSendPackageCount(WorkWaveInspectedNotSendPackageCountReq req);

    /**
     * 查询当前波次已验未发的运单明细，分页
     */
    JdCResponse<WorkWaveInspectedNotSendDetailsResponse> queryCurrentWorkWaveInspectedNotSendWaybillsByPage(WorkWaveInspectedNotSendDetailsReq req);


    /**
     * 查询当前波次已验未发的包裹明细，分页
     */
    JdCResponse<WorkWaveInspectedNotSendDetailsResponse> queryCurrentWorkWaveInspectedNotSendPackagesByPage(WorkWaveInspectedNotSendDetailsReq req);


}
