package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleTaskResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

public interface JyWarehouseSendVehicleService {
    InvokeResult<SendVehicleTaskResponse> toSendVehicleTaskPage(SendVehicleTaskRequest request);
}
