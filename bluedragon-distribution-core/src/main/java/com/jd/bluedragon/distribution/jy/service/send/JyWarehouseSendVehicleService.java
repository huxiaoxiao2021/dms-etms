package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.dto.operation.workbench.warehouse.send.AppendSendVehicleTaskQueryReq;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.send.AppendSendVehicleTaskQueryRes;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.send.MixScanTaskDetailRes;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.send.MixScanTaskQueryReq;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

public interface JyWarehouseSendVehicleService {
    InvokeResult<MixScanTaskDetailRes> getMixScanTaskDetailList(MixScanTaskQueryReq request);

    /**
     * 查询待发和发货中的任务数据
     * @param request
     * @return
     */
    InvokeResult<AppendSendVehicleTaskQueryRes> fetchToSendAndSendingTaskPage(AppendSendVehicleTaskQueryReq request);
}
