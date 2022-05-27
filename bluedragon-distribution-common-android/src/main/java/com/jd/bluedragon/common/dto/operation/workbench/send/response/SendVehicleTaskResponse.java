package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.VehicleStatusStatis;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName SendVehicleTaskResponse
 * @Description
 * @Author wyh
 * @Date 2022/5/17 20:51
 **/
public class SendVehicleTaskResponse implements Serializable {

    private static final long serialVersionUID = -4406632213133779677L;

    /**
     * 车辆状态数量统计
     */
    private List<VehicleStatusStatis> statusAgg;

    /**
     * 待发货任务
     */
    private SendVehicleData<ToSendVehicle> toSendVehicleData;

    /**
     * 发货中任务
     */
    private SendVehicleData<SendingVehicle> sendingVehicleData;

    /**
     * 待封车任务
     */
    private SendVehicleData<ToSealVehicle> toSealVehicleData;

    /**
     * 封车完成任务
     */
    private SendVehicleData<SealedVehicle> sealedVehicleData;
}
