package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.jy.transport.dto.VehicleArriveDockBaseDataDto;
import com.jd.bluedragon.distribution.jy.transport.dto.VehicleArriveDockBaseDataQo;
import com.jd.bluedragon.distribution.jy.transport.dto.VehicleArriveDockDataDto;
import com.jd.bluedragon.distribution.jy.transport.dto.VehicleArriveDockDataQo;
import com.jd.dms.java.utils.sdk.base.Result;

/**
 * 拣运运输车辆服务
 * @author fanggang7
 * @time 2023-05-08 20:47:41 周一
 */
public interface JyTransportSendVehicleGatewayService {

    /**
     * 获取运输车辆基础数据
     * @param qo 查询入参
     * @return 查询结果
     * @author fanggang7
     * @time 2023-05-09 10:55:11 周二
     */
    JdCResponse<VehicleArriveDockBaseDataDto> getVehicleArriveDockBaseData(VehicleArriveDockBaseDataQo qo);

    /**
     * 获取运输车辆靠台数据
     * @param qo 查询入参
     * @return 查询结果
     * @author fanggang7
     * @time 2023-05-09 11:04:49 周二
     */
    JdCResponse<VehicleArriveDockDataDto> getVehicleArriveDockData(VehicleArriveDockDataQo qo);
}
