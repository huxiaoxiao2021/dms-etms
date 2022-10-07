package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.*;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.*;
import com.jd.bluedragon.common.dto.send.request.TransferVehicleTaskReq;
import com.jd.bluedragon.common.dto.send.request.VehicleTaskReq;
import com.jd.bluedragon.common.dto.send.response.VehicleTaskResp;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;

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
     * 校验任务明细是否已经封车：按照明细的原始批次进行判断
     * @param detail
     * @return
     */
    boolean checkIfSealed(JyBizTaskSendVehicleDetailEntity detail);

    /**
     * 校验任务明细是否已经封车：按照明细的原始批次进行判断
     * @param detail
     * @return
     */
    boolean checkIfSealedByAllSendCode(JyBizTaskSendVehicleDetailEntity detail);

    /**
     * 查询发货任务详情
     * @param request 请求参数
     * @return 返回结果
     * @author fanggang7
     * @time 2022-09-22 16:47:38 周四
     */
    InvokeResult<SendTaskInfo> sendTaskDetail(SendVehicleInfoRequest request);

    JyBizTaskSendVehicleDetailEntity pickUpOneUnSealedDetail(List<JyBizTaskSendVehicleDetailEntity> taskSendDetails, Long sendDestId);
    }
