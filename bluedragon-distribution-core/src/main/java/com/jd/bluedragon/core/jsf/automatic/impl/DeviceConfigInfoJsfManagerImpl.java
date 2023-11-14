package com.jd.bluedragon.core.jsf.automatic.impl;

import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;
import com.jd.bd.dms.automatic.sdk.modules.device.DeviceConfigInfoJsfService;
import com.jd.bd.dms.automatic.sdk.modules.device.dto.DeviceConfigDto;
import com.jd.bd.dms.automatic.sdk.modules.device.dto.DeviceGridDto;
import com.jd.bluedragon.core.jsf.automatic.DeviceConfigInfoJsfManager;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DeviceConfigInfoJsfManagerImpl implements DeviceConfigInfoJsfManager {
    @Autowired
    DeviceConfigInfoJsfService deviceConfigInfoJsfService;


    @Override
    @Cache(key = "DeviceConfigInfoJsfManagerImpl.findDeviceGridByBusinessKey-@args0@args1", memoryEnable = false, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    @JProfiler(jKey = "DMS.Manager.DeviceConfigInfoJsfManager.findDeviceGridByBusinessKey", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<DeviceGridDto> findDeviceGridByBusinessKey(String businessKey, List<String> businessKeys) {
        BaseDmsAutoJsfResponse<List<DeviceGridDto>> deviceGridByBusinessKey = deviceConfigInfoJsfService.findDeviceGridByBusinessKey(businessKey, businessKeys);
        if (!Objects.equals(deviceGridByBusinessKey.getStatusCode(), 200)) {
            return new ArrayList<>();
        }
        List<DeviceGridDto> data = deviceGridByBusinessKey.getData();
        return data;
    }

    @Override
    @Cache(key = "DeviceConfigInfoJsfManagerImpl.findOneDeviceConfigByMachineCode-@args0", memoryEnable = false, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    @JProfiler(jKey = "DMS.Manager.DeviceConfigInfoJsfManager.findOneDeviceConfigByMachineCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public DeviceConfigDto findOneDeviceConfigByMachineCode(String machineCode) {
        DeviceConfigDto oneDeviceConfigByMachineCode = deviceConfigInfoJsfService.findOneDeviceConfigByMachineCode(machineCode);
        return oneDeviceConfigByMachineCode;
    }
}
