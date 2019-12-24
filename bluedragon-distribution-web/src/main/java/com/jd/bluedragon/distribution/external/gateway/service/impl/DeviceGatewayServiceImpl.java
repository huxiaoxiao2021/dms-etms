package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.device.response.DeviceInfoDto;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.rest.device.DeviceResource;
import com.jd.bluedragon.external.gateway.service.DeviceGatewayService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * <p>
 *     自动化设备列表查询jsf接口
 *
 * @author wuzuxiang
 * @since 2019/12/3
 **/
public class DeviceGatewayServiceImpl implements DeviceGatewayService {

    @Autowired
    private DeviceResource deviceResource;

    @Override
    public JdCResponse<List<DeviceInfoDto>> getDeviceInfoList(DeviceInfoDto deviceInfoDto) {
        InvokeResult<List<DeviceInfoDto>> result = deviceResource.getDeviceInfo(deviceInfoDto);
        if (result == null) {
            return new JdCResponse<>(JdCResponse.CODE_FAIL,JdCResponse.MESSAGE_FAIL);
        }
        JdCResponse<List<DeviceInfoDto>> response = new JdCResponse<>(result.getCode(),result.getMessage());
        response.setData(result.getData());
        return response;
    }
}
