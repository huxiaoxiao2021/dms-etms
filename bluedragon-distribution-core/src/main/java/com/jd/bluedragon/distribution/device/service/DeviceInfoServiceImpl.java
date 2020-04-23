package com.jd.bluedragon.distribution.device.service;

import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;
import com.jd.bd.dms.automatic.sdk.modules.device.DeviceConfigInfoJsfService;
import com.jd.bd.dms.automatic.sdk.modules.device.dto.DeviceConfigDto;
import com.jd.bluedragon.common.dto.device.response.DeviceInfoDto;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 *     查询自动化设备信息的实现类
 *
 * @author wuzuxiang
 * @since 2019/11/27
 **/
@Service
public class DeviceInfoServiceImpl implements DeviceInfoService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceInfoServiceImpl.class);

    @Autowired
    private DeviceConfigInfoJsfService deviceConfigInfoJsfService;

    @Override
    public List<DeviceInfoDto> queryDeviceConfigByTypeAndSiteCode(String siteCode, String deviceType) {
        BaseDmsAutoJsfResponse<List<DeviceConfigDto>> response = deviceConfigInfoJsfService.findDeviceConfigListByCondition(siteCode,deviceType);
        if (null == response || BaseDmsAutoJsfResponse.SUCCESS_CODE != response.getStatusCode()) {
            logger.warn("查询设备信息失败，创建站点：{},设备类型：{}，返回值：{}",siteCode,deviceType, JsonHelper.toJson(response));
            return Collections.emptyList();
        }
        List<DeviceInfoDto> results = new ArrayList<>(response.getData().size());
        for (DeviceConfigDto deviceConfigDto : response.getData()) {
            DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
            deviceInfoDto.setMachineCode(deviceConfigDto.getMachineCode());
            deviceInfoDto.setDeviceTypeCode(deviceConfigDto.getTypeCode());
            deviceInfoDto.setDeviceTypeName(deviceConfigDto.getTypeName());
            deviceInfoDto.setSiteCode(siteCode);
            deviceInfoDto.setIsEnable(deviceConfigDto.getIsEnable());

            results.add(deviceInfoDto);
        }
        return results;
    }
}
