package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.api.request.client.DeviceLocationUploadPo;
import com.jd.bluedragon.distribution.device.service.DeviceLocationService;
import com.jd.bluedragon.external.gateway.service.DeviceLocationGatewayService;
import com.jd.bluedragon.utils.converter.ResultConverter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 设备位置网关服务
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-11-23 18:30:50 周三
 */
@Service
public class DeviceLocationGatewayServiceImpl implements DeviceLocationGatewayService {

    @Resource
    private DeviceLocationService deviceLocationService;

    /**
     * 上传设备位置信息
     *
     * @param deviceLocationUploadPO 位置信息
     * @return 上传结果
     * @author fanggang7
     * @time 2022-11-23 18:21:59 周三
     */
    @Override
    public JdCResponse<Boolean> uploadLocationInfo(DeviceLocationUploadPo deviceLocationUploadPO) {
        return ResultConverter.convertResultToJdcResponse(deviceLocationService.sendUploadLocationMsg(deviceLocationUploadPO));
    }
}
