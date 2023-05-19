package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.send.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

public interface JyWarehouseSendVehicleService {
    InvokeResult<MixScanTaskDetailRes> getMixScanTaskDetailList(MixScanTaskQueryReq request);

    /**
     * 查询待发和发货中的任务数据
     * @param request
     * @return
     */
    InvokeResult<AppendSendVehicleTaskQueryRes> fetchToSendAndSendingTaskPage(AppendSendVehicleTaskQueryReq request);

    /**
     * 获取场地混扫流向限制
     * @param siteCode
     * @return
     */
    Integer getFlowMaxBySiteCode(Integer siteCode);

    JdVerifyResponse<SendScanRes> scan(SendScanReq request, JdVerifyResponse<SendScanRes> response);
}
