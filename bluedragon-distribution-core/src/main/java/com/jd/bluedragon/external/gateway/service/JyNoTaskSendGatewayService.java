package com.jd.bluedragon.external.gateway.service;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleInfoRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleTaskResponse;
import com.jd.bluedragon.common.dto.send.request.*;
import com.jd.bluedragon.common.dto.send.response.*;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
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
    JdCResponse<VehicleTaskResp> listVehicleTask(VehicleTaskReq vehicleTaskReq);

    /**
     * 查询运输车辆任务列表：提供给任务迁移场景查询使用
     * 迁出时 扫包裹号定位包裹所在任务；迁入时 @1可扫包裹 @2也可录入站点id
     * @param transferVehicleTaskReq
     * @return
     */
    JdCResponse<VehicleTaskResp> listVehicleTaskSupportTransfer(TransferVehicleTaskReq transferVehicleTaskReq);


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
    JdCResponse<CancelSendTaskResp> cancelSendTask(CancelSendTaskReq cancelSendTaskReq);


    /**
     * 扫描包裹号、箱号 获取流向信息
     * @param getSendRouterInfoReq
     * @return
     */
    JdCResponse<GetSendRouterInfoResq> getSendRouterInfoByScanCode(GetSendRouterInfoReq getSendRouterInfoReq);



}
