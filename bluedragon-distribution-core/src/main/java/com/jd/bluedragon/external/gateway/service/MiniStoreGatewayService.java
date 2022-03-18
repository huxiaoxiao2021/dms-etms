package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.ministore.BindMiniStoreDeviceReq;
import com.jd.bluedragon.common.dto.ministore.DeviceStatusValidateReq;

public interface MiniStoreGatewayService {
    /**
     * 检验设备可用状态
     * @param request
     * @return 成功 代表可用，反之 不可用
     */
    JdCResponse validateDeviceStatus(DeviceStatusValidateReq request);

    /**
     * 微仓相关设备（保温箱、冰板、箱子）进行绑定
     * @param request
     * @return
     */
    JdCResponse bindMiniStoreDevice(BindMiniStoreDeviceReq request);


}
