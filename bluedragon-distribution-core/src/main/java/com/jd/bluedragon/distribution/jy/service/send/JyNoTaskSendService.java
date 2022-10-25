package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleTaskResponse;
import com.jd.bluedragon.common.dto.send.request.*;
import com.jd.bluedragon.common.dto.send.response.CancelSendTaskResp;
import com.jd.bluedragon.common.dto.send.response.CreateVehicleTaskResp;
import com.jd.bluedragon.common.dto.send.response.DeleteVehicleTaskCheckResponse;
import com.jd.bluedragon.common.dto.send.response.VehicleSpecResp;
import com.jd.bluedragon.common.dto.send.response.VehicleTaskResp;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

import java.util.List;

public interface JyNoTaskSendService {
    /**
     * 获取车辆类型列表信息
     * @return
     */
    InvokeResult<List<VehicleSpecResp>> listVehicleType();

    /**
     * 创建自建类型的运输车辆任务（主任务）
     * @param createVehicleTaskReq
     * @return
     */
    InvokeResult<CreateVehicleTaskResp> createVehicleTask(CreateVehicleTaskReq createVehicleTaskReq);

    /**
     * 删除自建类型的运输车辆任务（主任务）
     * @param deleteVehicleTaskReq
     * @return
     */
    InvokeResult deleteVehicleTask(DeleteVehicleTaskReq deleteVehicleTaskReq);
    /**
     * 删除任务前-校验
     * @param deleteVehicleTaskReq
     * @return
     */
    InvokeResult<DeleteVehicleTaskCheckResponse> checkBeforeDeleteVehicleTask(DeleteVehicleTaskReq deleteVehicleTaskReq);    

    /**
     * 查询运输车辆任务列表：根据流向或者报告号筛选任务列表
     * @param vehicleTaskReq
     * @return
     */
    InvokeResult<VehicleTaskResp> listVehicleTask(VehicleTaskReq vehicleTaskReq);


    /**
     * 查询运输车辆任务列表：任务迁移场景时会根据迁入或者迁出做不同逻辑计算
     * 迁出时 扫包裹号定位包裹所在任务，迁入时 @1可扫包裹 @2也可录入站点id
     * @param transferVehicleTaskReq
     * @return
     */
    InvokeResult<VehicleTaskResp> listVehicleTaskSupportTransfer(TransferVehicleTaskReq transferVehicleTaskReq);

    /**
     * 自建任务绑定-运输真实任务
     * @param bindVehicleDetailTaskReq
     * @return
     */
    InvokeResult bindVehicleDetailTask(BindVehicleDetailTaskReq bindVehicleDetailTaskReq);

    /**
     * 迁移发货批次数据
     * @param transferSendTaskReq
     * @return
     */
    InvokeResult transferSendTask(TransferSendTaskReq transferSendTaskReq);

    /**
     * 取消发货
     * @param cancelSendTaskReq
     * @return
     */
    InvokeResult<CancelSendTaskResp> cancelSendTask(CancelSendTaskReq cancelSendTaskReq);
}
