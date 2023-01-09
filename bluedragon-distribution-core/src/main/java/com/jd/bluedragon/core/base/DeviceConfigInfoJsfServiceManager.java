package com.jd.bluedragon.core.base;

import com.jd.bd.dms.automatic.sdk.common.constant.WeightValidateSwitchEnum;
import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;
import com.jd.bd.dms.automatic.sdk.modules.device.dto.DeviceConfigDto;

public interface DeviceConfigInfoJsfServiceManager {

    BaseDmsAutoJsfResponse maintainWeightSwitch(WeightValidateSwitchEnum weightValidateSwitchEnum);


    BaseDmsAutoJsfResponse maintainSiteWeightSwitch(Integer[] siteCodesArray, WeightValidateSwitchEnum weightValidateSwitchEnum);

    /**
     * 根据设备编码查询再用设备详情
     *
     * @param machineCode
     * @return
     */
    DeviceConfigDto findOneDeviceConfigByMachineCode(String machineCode);

}
