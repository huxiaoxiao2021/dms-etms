package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.*;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.*;
import com.jd.bluedragon.common.dto.send.request.*;
import com.jd.bluedragon.common.dto.send.response.CancelSendTaskResp;
import com.jd.bluedragon.common.dto.send.response.CreateVehicleTaskResp;
import com.jd.bluedragon.common.dto.send.response.VehicleSpecResp;
import com.jd.bluedragon.common.dto.send.response.VehicleTaskResp;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

import java.util.List;

/**
 * @ClassName IJySendVehicleService
 * @Description 发货岗网关逻辑加工服务
 * @Author wyh
 * @Date 2022/5/29 14:31
 **/
public interface IJySendVehicleService {

    /**
     * 拉取发货任务
     * @param request
     * @return
     */
    InvokeResult<SendVehicleTaskResponse> fetchSendVehicleTask(SendVehicleTaskRequest request);

    /**
     * 无任务绑定发货任务列表
     * @param vehicleTaskReq
     * @return
     */
    InvokeResult<VehicleTaskResp> fetchSendTaskForBinding(VehicleTaskReq vehicleTaskReq);

    /**
     * 任务转移查询发货任务
     * @param vehicleTaskReq
     * @return
     */
    InvokeResult<VehicleTaskResp> fetchSendTaskForTransfer(TransferVehicleTaskReq vehicleTaskReq);

    /**
     * 发货扫描
     * @param request
     * @return
     */
    JdVerifyResponse<SendScanResponse> sendScan(SendScanRequest request);

    /**
     * 进入发货任务前拍照
     * @param request
     * @return
     */
    InvokeResult<Boolean> uploadPhoto(SendPhotoRequest request);

    /**
     * 发货任务详情
     * @param request
     * @return
     */
    InvokeResult<SendVehicleInfo> sendVehicleInfo(SendVehicleInfoRequest request);

    /**
     * 发货任务流向明细列表
     * @param request
     * @return
     */
    InvokeResult<List<SendDestDetail>> sendDestDetail(SendDetailRequest request);

    /**
     * 发货进度
     * @param request
     * @return
     */
    InvokeResult<SendVehicleProgress> loadProgress(SendVehicleProgressRequest request);

    /**
     * 校验发货任务是否异常
     * <ul>
     *     <li>单流向任务直接校验，多流向只在最后一个流向封车时校验</li>
     *     <li>拦截&强扫或装载率不足，两者都满足时异常优先</li>
     * </ul>
     * @param request
     * @return
     */
    InvokeResult<SendAbnormalResponse> checkSendVehicleNormalStatus(SendAbnormalRequest request);

    /**
     * 发货拦截包裹明细
     * @param request
     * @return
     */
    InvokeResult<SendAbnormalBarCode> interceptedBarCodeDetail(SendAbnormalPackRequest request);

    /**
     * 强制发货包裹明细
     * @param request
     * @return
     */
    InvokeResult<SendAbnormalBarCode> forceSendBarCodeDetail(SendAbnormalPackRequest request);

    /**
     * 发货异常包裹明细
     * @param request
     * @return
     */
    InvokeResult<SendAbnormalBarCode> abnormalSendBarCodeDetail(SendAbnormalPackRequest request);

    /**
     * 选择封车流向
     * @param request
     * @return
     */
    InvokeResult<ToSealDestAgg> selectSealDest(SelectSealDestRequest request);

    InvokeResult checkMainLineSendTask(CheckSendCodeRequest request);

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
