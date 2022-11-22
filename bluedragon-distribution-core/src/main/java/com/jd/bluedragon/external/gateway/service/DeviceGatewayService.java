package com.jd.bluedragon.external.gateway.service;

import java.util.List;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.device.response.DeviceInfoDto;
import com.jd.bluedragon.common.dto.device.response.DeviceTypeWithInfoDto;

/**
 * <p>
 *     设备列表查询
 *
 * @author wuzuxiang
 * @since 2019/12/3
 **/
public interface DeviceGatewayService {

    /**
     * 自动化设备列表查询接口
     * @return 返回设备列表
     */
    JdCResponse<List<DeviceInfoDto>> getDeviceInfoList(DeviceInfoDto deviceInfoDto);
    /**
     * 自动化设备类型列表查询接口
     * @return 返回设备列表
     */
    JdCResponse<List<DeviceTypeWithInfoDto>> queryDeviceTypeWithInfoList(DeviceInfoDto deviceInfoDto);
}
