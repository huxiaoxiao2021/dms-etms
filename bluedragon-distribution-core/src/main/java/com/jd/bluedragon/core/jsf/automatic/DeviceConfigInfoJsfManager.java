package com.jd.bluedragon.core.jsf.automatic;

import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;
import com.jd.bd.dms.automatic.sdk.modules.device.dto.DeviceConfigDto;
import com.jd.bd.dms.automatic.sdk.modules.device.dto.DeviceGridDto;

import java.util.List;

public interface DeviceConfigInfoJsfManager {
    List<DeviceGridDto> findDeviceGridByBusinessKey(String businessKey, List<String> businessKeys);

    DeviceConfigDto findOneDeviceConfigByMachineCode(String machineCode);


}
