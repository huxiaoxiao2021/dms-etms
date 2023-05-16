package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.dto.operation.workbench.warehouse.send.MixScanTaskDetailRes;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.send.MixScanTaskQueryReq;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

public interface JyWarehouseSendVehicleService {
    InvokeResult<MixScanTaskDetailRes> getMixScanTaskDetailList(MixScanTaskQueryReq request);
}
