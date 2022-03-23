package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.UnifiedExceptionProcess;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.ResponseCodeMapping;
import com.jd.bluedragon.common.dto.ministore.*;
import com.jd.bluedragon.distribution.ministore.domain.MiniStoreBindRelation;
import com.jd.bluedragon.distribution.ministore.dto.DeviceDto;
import com.jd.bluedragon.distribution.ministore.service.MiniStoreService;
import com.jd.bluedragon.distribution.saf.service.GetWaybillSafService;
import com.jd.bluedragon.enums.SwDeviceStatusEnum;
import com.jd.bluedragon.external.gateway.service.MiniStoreGatewayService;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.cmp.jsf.SwDeviceJsfService;
import com.jd.jddl.common.utils.Assert;
import org.springframework.beans.factory.annotation.Autowired;

@UnifiedExceptionProcess
public class MiniStoreGatewayServiceImpl implements MiniStoreGatewayService {

    @Autowired
    MiniStoreService miniStoreService;
    @Autowired
    SwDeviceJsfService swDeviceJsfService;

    @Override
    public JdCResponse validateDeviceStatus(DeviceStatusValidateReq request) {
        //调用保温箱jsf接口查询报文箱子可用状态
        Assert.assertNotNull(request);
        if (null != request.getStoreCode() && !"".equals(request.getStoreCode())) {
            Integer availableStatus = swDeviceJsfService.isDeviceUse(request.getStoreCode());
            if (SwDeviceStatusEnum.AVAILABLE.getCode() != availableStatus) {
                return JdCResponse.errorResponse(ResponseCodeMapping.MINI_STORE_IS_NOT_AVAILABLE);
            }
            Boolean hasBeenBind = miniStoreService.validateStoreBindStatus(request.getStoreCode());
            if (hasBeenBind) {
                return JdCResponse.errorResponse(ResponseCodeMapping.MINI_STORE_HASBEEN_BIND);
            }
        }
        if (null != request.getIceBoardCode() && !"".equals(request.getIceBoardCode())) {
            Integer availableStatus = swDeviceJsfService.isDeviceUse(request.getIceBoardCode());
            if (SwDeviceStatusEnum.AVAILABLE.getCode() != availableStatus) {
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
        DeviceDto deviceDto = BeanUtils.copy(request, DeviceDto.class);
        Boolean bindStatus = miniStoreService.validatDeviceBindStatus(deviceDto);
        if (null != bindStatus && bindStatus) {
            return JdCResponse.errorResponse(ResponseCodeMapping.DEVICE_HASBEEN_BIND);
        }
        Boolean bindSuccess = miniStoreService.bindMiniStoreDevice(deviceDto);
        if (bindSuccess) {
            return JdCResponse.successResponse();
        }
        return JdCResponse.errorResponse(ResponseCodeMapping.UNKNOW_ERROR);
    }

    @Override
    public JdCResponse sealBox(SealBoxReq sealBoxReq) {
        miniStoreService.updateProcessStatusAndSyncMsg(null);
        return null;
    }

    @Override
    public JdCResponse<Integer> querySortCount(String boxCode) {
        return null;
    }

    @Override
    public JdCResponse<UnBoxValidateResp> unBoxValidateBindRelation(UnBoxValidateReq unBoxValidateReq) {
        DeviceDto deviceDto = BeanUtils.copy(unBoxValidateReq, DeviceDto.class);
        MiniStoreBindRelation miniStoreBindRelation = miniStoreService.selectBindRelation(deviceDto);
        if (null != miniStoreBindRelation) {
            UnBoxValidateResp unBoxValidateResp = BeanUtils.copy(miniStoreBindRelation, UnBoxValidateResp.class);
            unBoxValidateResp.setMiniStoreBindRelationId(miniStoreBindRelation.getId());
            return JdCResponse.successResponse(unBoxValidateReq);
        }
        return JdCResponse.errorResponse(ResponseCodeMapping.NO_LEGAL_BIND_RELATIONSHIP);
    }

    @Override
    public JdCResponse validateSortRelation(ValidateSortRelationReq validateSortRelationReq) {
        return null;
    }

    @Override
    public JdCResponse unBox(UnBoxReq unBoxReq) {
        DeviceDto deviceDto =new DeviceDto();
        deviceDto.setBoxCode(unBoxReq.getBoxCode());
        deviceDto.setMiniStoreBindRelationId(unBoxReq.getMiniStoreBindRelationId());
        Boolean success =miniStoreService.updateProcessStatusAndInvaliSortRealtion(deviceDto);
        if (success){
            return JdCResponse.successResponse();
        }
        return JdCResponse.errorResponse(ResponseCodeMapping.UNKNOW_ERROR);
    }
}
