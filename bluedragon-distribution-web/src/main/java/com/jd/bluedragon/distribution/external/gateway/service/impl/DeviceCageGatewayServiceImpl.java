package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;
import com.jd.bd.dms.automatic.sdk.modules.device.DeviceConfigInfoJsfService;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.cage.request.AutoCageRequest;
import com.jd.bluedragon.distribution.sdk.common.domain.InvokeResult;
import com.jd.bluedragon.distribution.sdk.modules.cage.DeviceCageJsfService;
import com.jd.bluedragon.external.gateway.service.DeviceCageGatewayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceCageGatewayServiceImpl implements DeviceCageGatewayService {

    @Autowired
    private DeviceConfigInfoJsfService deviceConfigInfoJsfService;
    @Autowired
    private DeviceCageJsfService deviceCageJsfService;

    @Override
    @JProfiler(jKey = "DMSWEB.DeviceCageGatewayServiceImpl.getSortMachineBySiteCode",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<String>> getSortMachineBySiteCode(Integer siteCode) {
        JdCResponse<List<String>> jdCResponse = new JdCResponse<List<String>>();
        jdCResponse.toSucceed();
        if(siteCode == null){
            jdCResponse.toFail("场地编码为空，请退出重试!");
        }

        BaseDmsAutoJsfResponse<List<String>> response =  deviceConfigInfoJsfService.getAutoMachineAndCheckCage(siteCode);
        if(response.getStatusCode() != 200){
            jdCResponse.toFail("查询设备编码失败，请退出重试!");
            return jdCResponse;
        }

        if(CollectionUtils.isEmpty(response.getData())){
            return jdCResponse;
        }
        jdCResponse.setData(response.getData());
        return jdCResponse;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DeviceCageGatewayServiceImpl.querySortingInfo",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> querySortingInfo(AutoCageRequest request) {
        JdCResponse<Boolean> jdCResponse = new JdCResponse<Boolean>();
        jdCResponse.toSucceed();

        if(StringUtils.isEmpty(request.getBarcode())){
            jdCResponse.toFail("包裹号/箱号为空，请退出重试!");
        }

        if(StringUtils.isEmpty(request.getMachineCode())){
            jdCResponse.toFail("设备编号为空，请退出重试!");
        }
        com.jd.bluedragon.distribution.sdk.modules.cage.domain.AutoCageRequest autoCageRequest = new com.jd.bluedragon.distribution.sdk.modules.cage.domain.AutoCageRequest();
        BeanUtils.copyProperties(request, autoCageRequest);
        InvokeResult<Boolean> response =  deviceCageJsfService.querySortingInfo(autoCageRequest);
        if(response.getCode() != 200){
            jdCResponse.toFail("查询设备回传信息失败，请退出重试!");
            return jdCResponse;
        }

        jdCResponse.setData(response.getData());
        return jdCResponse;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DeviceCageGatewayServiceImpl.cage",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> cage(AutoCageRequest request) {
        JdCResponse<Boolean> jdCResponse = new JdCResponse<Boolean>();
        jdCResponse.toSucceed();
        if(StringUtils.isEmpty(request.getCageCode())){
            jdCResponse.toFail("笼车编码为空，请退出重试!");
            return jdCResponse;
        }

        if(StringUtils.isEmpty(request.getCageBoxCode())){
            jdCResponse.toFail("笼车箱号为空，请退出重试!");
            return jdCResponse;
        }
        JdCResponse<Boolean> booleanJdCResponse = this.querySortingInfo(request);
        if(booleanJdCResponse.isFail()){
            jdCResponse.toFail(booleanJdCResponse.getMessage());
            return jdCResponse;
        }
        com.jd.bluedragon.distribution.sdk.modules.cage.domain.AutoCageRequest autoCageRequest = new com.jd.bluedragon.distribution.sdk.modules.cage.domain.AutoCageRequest();
        BeanUtils.copyProperties(request, autoCageRequest);
        InvokeResult<Boolean> response =  deviceCageJsfService.cage(autoCageRequest);
        if(response.getCode() != 200){
            jdCResponse.toFail("装笼失败，请退出重试!");
            return jdCResponse;
        }

        jdCResponse.setData(response.getData());
        return jdCResponse;
    }

}
