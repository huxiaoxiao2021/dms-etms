package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.ResponseCodeMapping;
import com.jd.bluedragon.common.dto.ministore.BindMiniStoreDeviceReq;
import com.jd.bluedragon.common.dto.ministore.DeviceStatusValidateReq;
import com.jd.bluedragon.distribution.ministore.service.MiniStoreService;
import com.jd.bluedragon.distribution.saf.service.GetWaybillSafService;
import com.jd.bluedragon.enums.SwDeviceStatusEnum;
import com.jd.bluedragon.external.gateway.service.MiniStoreGatewayService;
import com.jd.cmp.jsf.SwDeviceJsfService;
import com.jd.jddl.common.utils.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public class MiniStoreGatewayServiceImpl implements MiniStoreGatewayService {

    @Autowired
    MiniStoreService miniStoreService;
    @Autowired
    SwDeviceJsfService swDeviceJsfService;

    @Override
    public JdCResponse validateDeviceStatus(DeviceStatusValidateReq request) {
        //调用保温箱jsf接口查询报文箱子可用状态
        Assert.assertNotNull(request);
        if (null != request.getMiniStoreCode() && !"".equals(request.getMiniStoreCode())) {
            Integer availableStatus = swDeviceJsfService.isDeviceUse(request.getMiniStoreCode());
            if (SwDeviceStatusEnum.AVAILABLE.getCode()!=availableStatus) {
                return JdCResponse.errorResponse(ResponseCodeMapping.MINI_STORE_IS_NOT_AVAILABLE);
            }
            Boolean hasBeenBind = miniStoreService.validateStoreBindStatus(request.getMiniStoreCode());
            if (hasBeenBind) {
                return JdCResponse.errorResponse(ResponseCodeMapping.MINI_STORE_HASBEEN_BIND);
            }
        }
        if (null != request.getIceBoardCode() && !"".equals(request.getIceBoardCode())) {
            Integer availableStatus = swDeviceJsfService.isDeviceUse(request.getIceBoardCode());
            if (SwDeviceStatusEnum.AVAILABLE.getCode()!=availableStatus) {
                return JdCResponse.errorResponse(ResponseCodeMapping.INCE_BOARD_IS_NOT_AVAILABLE);
            }
            Boolean hasBeenBind = miniStoreService.validateIceBoardBindStatus(request.getIceBoardCode());
            if (hasBeenBind) {
                return JdCResponse.errorResponse(ResponseCodeMapping.INCE_BOARD_HASBEEN_BIND);
            }
        }
        if (null != request.getBoxCode() && !"".equals(request.getBoxCode())) {
            Boolean hasBeenBind = miniStoreService.validateBoxBindStatus(request.getBoxCode());
            if (hasBeenBind) {
                return JdCResponse.errorResponse(ResponseCodeMapping.BOX_HASBEEN_BIND);
            }
        }
        return JdCResponse.successResponse();
    }

    @Override
    public JdCResponse bindMiniStoreDevice(BindMiniStoreDeviceReq request) {
        return null;
    }
}
