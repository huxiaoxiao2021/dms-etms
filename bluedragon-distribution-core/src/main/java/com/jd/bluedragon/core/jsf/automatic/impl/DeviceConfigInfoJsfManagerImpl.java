package com.jd.bluedragon.core.jsf.automatic.impl;

import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;
import com.jd.bd.dms.automatic.sdk.modules.device.DeviceConfigInfoJsfService;
import com.jd.bd.dms.automatic.sdk.modules.device.dto.DeviceConfigDto;
import com.jd.bd.dms.automatic.sdk.modules.device.dto.DeviceGridDto;
import com.jd.bluedragon.core.jsf.automatic.DeviceConfigInfoJsfManager;
import com.jd.common.annotation.CacheMethod;
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
    @CacheMethod(key = "DeviceConfigInfoJsfManagerImpl.findDeviceGridByBusinessKey-{0}-{1}", cacheBean = "redisCache", timeout = 1000 * 60 * 1)
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
    @CacheMethod(key = "DeviceConfigInfoJsfManagerImpl.findOneDeviceConfigByMachineCode-{0}", cacheBean = "redisCache", timeout = 1000 * 60 * 1)
    @JProfiler(jKey = "DMS.Manager.DeviceConfigInfoJsfManager.findOneDeviceConfigByMachineCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public DeviceConfigDto findOneDeviceConfigByMachineCode(String machineCode) {
        DeviceConfigDto oneDeviceConfigByMachineCode = deviceConfigInfoJsfService.findOneDeviceConfigByMachineCode(machineCode);
        return oneDeviceConfigByMachineCode;
    }
}
