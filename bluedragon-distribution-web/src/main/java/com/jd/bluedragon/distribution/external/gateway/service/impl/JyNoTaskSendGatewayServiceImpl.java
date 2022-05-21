package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.UnifiedExceptionProcess;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.MSCodeMapping;
import com.jd.bluedragon.common.dto.send.request.*;
import com.jd.bluedragon.common.dto.send.response.CreateVehicleTaskResp;
import com.jd.bluedragon.common.dto.send.response.VehicleSpecResp;
import com.jd.bluedragon.common.dto.send.response.VehicleTaskResp;
import com.jd.bluedragon.common.dto.send.response.VehicleTypeDto;
import com.jd.bluedragon.distribution.jy.manager.JyTransportManager;
import com.jd.bluedragon.distribution.jy.service.transfer.JySendTransferService;
import com.jd.bluedragon.external.gateway.service.JyNoTaskSendGatewayService;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.tms.basic.dto.BasicVehicleTypeDto;
import com.jd.tms.basic.dto.CommonDto;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@UnifiedExceptionProcess
public class JyNoTaskSendGatewayServiceImpl implements JyNoTaskSendGatewayService {

    @Autowired
    JyTransportManager jyTransportManager;
    @Autowired
    JySendTransferService jySendTransferService;

    @Override
    public JdCResponse<List<VehicleSpecResp>> listVehicleType() {
        CommonDto<List<BasicVehicleTypeDto>> rs = jyTransportManager.getVehicleTypeList();
        if (null != rs && rs.getCode() == 1) {
            Map<String, List<VehicleTypeDto>> groupByVehicleLength =
                    StreamSupport.stream(rs.getData())
                            .filter(basicVehicleTypeDto ->
                                    ObjectHelper.isNotNull(basicVehicleTypeDto.getVehicleLength()) ? true : false)
                            .map(basicVehicleTypeDto ->
                                    BeanUtils.copy(basicVehicleTypeDto, VehicleTypeDto.class))
                            .collect(Collectors.groupingBy(VehicleTypeDto::getVehicleLength));

            List<VehicleSpecResp> vehicleSpecRespList = new ArrayList<>();
            for (Map.Entry<String, List<VehicleTypeDto>> entry : groupByVehicleLength.entrySet()) {
                String key = entry.getKey();
                List<VehicleTypeDto> value = entry.getValue();
                VehicleSpecResp vehicleSpecResp = new VehicleSpecResp();
                vehicleSpecResp.setVehicleLength(key);
                vehicleSpecResp.setVehicleTypeDtoList(value);
                vehicleSpecRespList.add(vehicleSpecResp);
            }
            return new JdCResponse(MSCodeMapping.SUCCESS.getCode(), MSCodeMapping.SUCCESS.getMessage(), vehicleSpecRespList);
        }
        return new JdCResponse(MSCodeMapping.UNKNOW_ERROR.getCode(), MSCodeMapping.UNKNOW_ERROR.getMessage());
    }

    @Override
    public JdCResponse<CreateVehicleTaskResp> createVehicleTask(CreateVehicleTaskReq createVehicleTaskReq) {
        return null;
    }

    @Override
    public JdCResponse deleteVehicleTask(DeleteVehicleTaskReq deleteVehicleTaskReq) {
        return null;
    }

    @Override
    public JdCResponse<List<VehicleTaskResp>> listVehicleTask(VehicleTaskReq vehicleTaskReq) {
        return null;
    }

    @Override
    public JdCResponse bindVehicleDetailTask(BindVehicleDetailTaskReq bindVehicleDetailTaskReq) {
        return null;
    }

    @Override
    public JdCResponse transferSendTask(TransferSendTaskReq transferSendTaskReq) {
        return null;
    }

    @Override
    public JdCResponse cancelSendTask(CancelSendTaskReq cancelSendTaskReq) {
        return null;
    }
}
