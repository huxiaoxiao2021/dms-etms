package com.jd.bluedragon.core.base;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCar;

import java.util.List;

public interface LoadScanPackageDetailServiceManager {
    JdCResponse getInspectNoSendWaybillInfo(LoadCar loadCar, List<String> waybillCodeList);
}
