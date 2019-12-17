package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.device.response.DeviceInfoDto;

import java.util.List;

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
}
