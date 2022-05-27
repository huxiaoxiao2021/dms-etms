package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.distribution.jy.dto.send.VehicleSendRelationDto;
import com.jd.bluedragon.distribution.jy.send.JySendCodeEntity;

import java.util.List;

public interface JyVehicleSendRelationService {
    List<String> querySendCodesByVehicleDetailBizId(String vehicleDetailBizId);

    /**
     * 更新运输-流向任务与 发货批次的关联关系
     * @param dto
     * @return
     */
    int updateVehicleSendRelation(VehicleSendRelationDto dto);
    /**
     * 删除运输-流向任务与 发货批次的关联关系
     * @param dto
     * @return
     */
    int deleteVehicleSendRelation(VehicleSendRelationDto dto);

    int add(JySendCodeEntity jySendCodeEntity);
}
