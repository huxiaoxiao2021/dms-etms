package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.reverse.request.ReverseWarehouseReq;
import com.jd.bluedragon.distribution.api.request.ReverseWarehouseRequest;
import com.jd.bluedragon.distribution.rest.reverse.ReverseWarehouseResource;
import com.jd.bluedragon.external.gateway.service.ReverseWarehouseGateWayService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * @author : xumigen
 * @date : 2019/9/21
 */
public class ReverseWarehouseGateWayServiceImpl implements ReverseWarehouseGateWayService {
    private final Logger logger = LoggerFactory.getLogger(ReverseWarehouseGateWayServiceImpl.class);

    @Autowired
    private ReverseWarehouseResource reverseWarehouseResource;

    @Override
    @JProfiler(jKey = "DMSWEB.ReverseWarehouseGateWayServiceImpl.returnWarehouseCheck",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Void> returnWarehouseCheck(ReverseWarehouseReq reverseWarehouseReq){
        JdCResponse<Void> jdCResponse = new JdCResponse<>();
        jdCResponse.toSucceed();

        if(reverseWarehouseReq == null){
            jdCResponse.toFail("入参不能为空");
            return jdCResponse;
        }
        if(reverseWarehouseReq.getUser() == null){
            jdCResponse.toError("当前用户信息不能为空!");
            return jdCResponse;
        }
        if(reverseWarehouseReq.getCurrentOperate() == null){
            jdCResponse.toError("当前网点信息不能为空!");
            return jdCResponse;
        }

        if (reverseWarehouseReq.getUser().getUserCode()<=0 || StringUtils.isBlank(reverseWarehouseReq.getUser().getUserName()) || reverseWarehouseReq.getCurrentOperate().getSiteCode()<=0 || StringUtils.isBlank(reverseWarehouseReq.getCurrentOperate().getSiteName())){
            jdCResponse.toFail("操作人信息和场地信息都不能为空");
            return jdCResponse;
        }

        ReverseWarehouseRequest warehouseRequest = new ReverseWarehouseRequest();
        warehouseRequest.setPackageOrWaybillCode(reverseWarehouseReq.getPackageOrWaybillCode());
        warehouseRequest.setSiteCode(reverseWarehouseReq.getCurrentOperate().getSiteCode());
        JdResponse response = reverseWarehouseResource.returnWarehouseCheck(warehouseRequest);
        if(!Objects.equals(JdResponse.CODE_SUCCESS,response.getCode())){
            jdCResponse.toError(response.getMessage());
            return jdCResponse;
        }
        jdCResponse.toSucceed(response.getMessage());
        return jdCResponse;
    }
}
