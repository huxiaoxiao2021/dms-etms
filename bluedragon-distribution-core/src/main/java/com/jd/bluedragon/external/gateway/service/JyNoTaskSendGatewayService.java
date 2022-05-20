package com.jd.bluedragon.external.gateway.service;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.send.request.*;
import com.jd.bluedragon.common.dto.send.response.CreateVehicleTaskResp;
import com.jd.bluedragon.common.dto.send.response.VehicleSpecResp;
import com.jd.bluedragon.common.dto.send.response.VehicleTaskResp;

import java.util.List;

public interface JyNoTaskSendGatewayService {
    /**
     * 获取车辆类型列表信息
     * @return
     */
    JdCResponse<List<VehicleSpecResp>> listVehicleType();

    /**
     * 创建自建类型的运输车辆任务（主任务）
     * @param createVehicleTaskReq
     * @return
     */
    JdCResponse<CreateVehicleTaskResp> createVehicleTask(CreateVehicleTaskReq createVehicleTaskReq);

    /**
     * 删除自建类型的运输车辆任务（主任务）
     * @param deleteVehicleTaskReq
     * @return
     */
    JdCResponse deleteVehicleTask(DeleteVehicleTaskReq deleteVehicleTaskReq);

    /**
     * 查询运输车辆任务列表
     * @param vehicleTaskReq
     * @return
     */
    JdCResponse<List<VehicleTaskResp>> listVehicleTask(VehicleTaskReq vehicleTaskReq);

    /**
     * 自建任务绑定-运输真实任务
     * @param bindVehicleDetailTaskReq
     * @return
     */
    JdCResponse bindVehicleDetailTask(BindVehicleDetailTaskReq bindVehicleDetailTaskReq);

    /**
     * 迁移发货批次数据
     * @param transferSendTaskReq
     * @return
     */
    JdCResponse transferSendTask(TransferSendTaskReq transferSendTaskReq);

    /**
     * 取消发货
     * @param cancelSendTaskReq
     * @return
     */
    JdCResponse cancelSendTask(CancelSendTaskReq cancelSendTaskReq);




}
