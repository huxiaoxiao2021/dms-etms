package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.distribution.jy.dto.send.JySendCodeDto;
import com.jd.bluedragon.distribution.jy.dto.send.VehicleSendRelationDto;
import com.jd.bluedragon.distribution.jy.send.JySendCodeEntity;

import java.util.List;

public interface JyVehicleSendRelationService {
    /**
     * 查询某一流向任务（子任务）下的发货批次信息
     * @param vehicleDetailBizId
     * @return
     */
    List<String> querySendCodesByVehicleDetailBizId(String vehicleDetailBizId);

    /**
     * 查询某一运输任务（主任务）下发货批次信息
     * @param vehicleBizId
     * @return
     */
    List<String> querySendCodesByVehicleBizId(String vehicleBizId);

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

    int deleteVehicleSendRelationByVehicleBizId(JySendCodeDto dto);

    int add(JySendCodeEntity jySendCodeEntity);

    /**
     * 保存封车批次号
     * @param jySendCodeEntityList
     * @return
     */
    int saveSealSendCode(List<JySendCodeEntity> jySendCodeEntityList);

    String findEarliestSendCode(String vehicleDetailBizId);

    String findEarliestNoSealCarSendCode(String detailBiz);

    List<JySendCodeEntity> querySendDetailBizIdBySendCode(List<String> sendCodes);

    /**
     * 根据任务ID查询批次数据
     * @param vehicleBizId 任务业务ID
     * @return 批次数据列表
     * @author fanggang7
     * @time 2022-09-26 17:36:53 周一
     */
    List<JySendCodeEntity> queryByVehicleBizId(String vehicleBizId);

    List<JySendCodeEntity> queryByVehicleDetailBizId(String vehicleDetailBizId);
}
