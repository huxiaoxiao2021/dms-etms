package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.material.batch.request.MaterialBatchSendReq;
import com.jd.bluedragon.common.dto.material.batch.response.MaterialTypeDto;
import com.jd.bluedragon.common.dto.sendcode.response.SendCodeCheckDto;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.external.gateway.service.MaterialBatchSendGatewayService;

import java.util.AbstractMap;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName MaterialBatchSendGatewayServiceImpl
 * @Description
 * @Author wyh
 * @Date 2020/3/24 11:13
 **/
public class MaterialBatchSendGatewayServiceImpl implements MaterialBatchSendGatewayService {


    @Override
    public JdCResponse<Void> materialBatchSend(MaterialBatchSendReq request) {
        return null;
    }

    @Override
    public JdCResponse<Void> cancelMaterialBatchSend(MaterialBatchSendReq request) {
        return null;
    }

    @Override
    public JdCResponse<List<MaterialTypeDto>> listMaterialType(MaterialBatchSendReq req) {
        return null;
    }

    @Override
    public JdCResponse<SendCodeCheckDto> checkSendCode(MaterialBatchSendReq req) {
//        InvokeResult<AbstractMap.Entry<Integer, String>> invokeResult = deliveryResource.checkSendCodeStatus(sendCode);
//        JdCResponse<SendCodeCheckDto> jdCResponse = new JdCResponse<>();
//        if(invokeResult == null){
//            jdCResponse.toError("接口返回错误！");
//            return jdCResponse;
//        }
//        if(!Objects.equals(invokeResult.getCode(), InvokeResult.RESULT_SUCCESS_CODE)){
//            jdCResponse.toError(invokeResult.getMessage());
//            return jdCResponse;
//        }
//        //成功也会返回数据
//        AbstractMap.Entry<Integer, String> entry = invokeResult.getData();
//        if(entry != null){
//            SendCodeCheckDto dto = new SendCodeCheckDto();
//            dto.setKey(entry.getKey());
//            dto.setValue(entry.getValue());
//            jdCResponse.setData(dto);
//        }
//        jdCResponse.toSucceed(invokeResult.getMessage());
//        return jdCResponse;
    }
}
