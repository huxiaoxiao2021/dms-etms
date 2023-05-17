package com.jd.bluedragon.distribution.jy.transport.api;

import com.jd.bluedragon.distribution.jy.transport.dto.*;
import com.jd.dms.java.utils.sdk.base.Result;

/**
 * 拣运运输车辆服务
 * @author fanggang7
 * @time 2023-05-08 20:47:41 周一
 */
public interface JyTransportSendVehicleService {

    /**
     * 验证运输发货车辆到达月台合法性
     * @param requestDto 入参
     * @return 校验结果
     * @author fanggang7
     * @time 2023-05-08 21:05:43 周一
     */
    Result<VehicleArriveDockResponseDto> validateVehicleArriveDock(VehicleArriveDockRequestDto requestDto);

    /**
     * 获取运输车辆基础数据
     * @param qo 查询入参
     * @return 查询结果
     * @author fanggang7
     * @time 2023-05-09 10:55:11 周二
     */
    Result<VehicleArriveDockBaseDataDto> getVehicleArriveDockBaseData(VehicleArriveDockBaseDataQo qo);

    /**
     * 获取运输车辆靠台数据
     * @param qo 查询入参
     * @return 查询结果
     * @author fanggang7
     * @time 2023-05-09 11:04:49 周二
     */
    Result<VehicleArriveDockDataDto> getVehicleArriveDockData(VehicleArriveDockDataQo qo);
}
