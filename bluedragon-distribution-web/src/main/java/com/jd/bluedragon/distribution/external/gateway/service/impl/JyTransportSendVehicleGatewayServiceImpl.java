package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.transport.api.JyTransportSendVehicleService;
import com.jd.bluedragon.distribution.jy.transport.dto.VehicleArriveDockBaseDataDto;
import com.jd.bluedragon.distribution.jy.transport.dto.VehicleArriveDockBaseDataQo;
import com.jd.bluedragon.distribution.jy.transport.dto.VehicleArriveDockDataDto;
import com.jd.bluedragon.distribution.jy.transport.dto.VehicleArriveDockDataQo;
import com.jd.bluedragon.external.gateway.service.JyTransportSendVehicleGatewayService;
import com.jd.bluedragon.utils.converter.ResultConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 拣运运输车辆服务
 * @author fanggang7
 * @time 2023-05-08 20:47:41 周一
 */
@Service("jyTransportSendVehicleGatewayServiceImpl")
public class JyTransportSendVehicleGatewayServiceImpl implements JyTransportSendVehicleGatewayService {

    @Autowired
    private JyTransportSendVehicleService jyTransportSendVehicleService;

    /**
     * 获取运输车辆基础数据
     *
     * @param qo 查询入参
     * @return 查询结果
     * @author fanggang7
     * @time 2023-05-09 10:55:11 周二
     */
    @Override
    public JdCResponse<VehicleArriveDockBaseDataDto> getVehicleArriveDockBaseData(VehicleArriveDockBaseDataQo qo) {
        return ResultConverter.convertResultToJdcResponse(jyTransportSendVehicleService.getVehicleArriveDockBaseData(qo));
    }

    /**
     * 获取运输车辆靠台数据
     *
     * @param qo 查询入参
     * @return 查询结果
     * @author fanggang7
     * @time 2023-05-09 11:04:49 周二
     */
    @Override
    public JdCResponse<VehicleArriveDockDataDto> getVehicleArriveDockData(VehicleArriveDockDataQo qo) {
        return ResultConverter.convertResultToJdcResponse(jyTransportSendVehicleService.getVehicleArriveDockData(qo));
    }

}
