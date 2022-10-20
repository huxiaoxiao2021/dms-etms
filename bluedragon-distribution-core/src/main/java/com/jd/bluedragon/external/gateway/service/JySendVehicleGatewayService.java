package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.*;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.*;
import com.jd.bluedragon.common.dto.select.SelectOption;
import com.jd.bluedragon.common.dto.send.request.SendBatchReq;
import com.jd.bluedragon.common.dto.send.response.SendBatchResp;

import java.util.List;

/**
 * @ClassName JySendVehicleGatewayService
 * @Description 拣运发货封车岗网关服务
 * @Author wyh
 * @Date 2022/5/17 19:57
 **/
public interface JySendVehicleGatewayService {

    /**
     * 发货模式
     * @return
     */
    JdCResponse<List<SelectOption>> sendModeOptions();

    /**
     * 车辆状态枚举
     * @return
     */
    JdCResponse<List<SelectOption>> vehicleStatusOptions();

    /**
     * 线路类型枚举
     * @return
     */
    JdCResponse<List<SelectOption>> lineTypeOptions();

    /**
     * 发货扫描方式枚举
     * @return
     */
    JdCResponse<List<SelectOption>> scanTypeOptions();

    /**
     * 拉取发货任务列表
     * @param request
     * @return
     */
    JdCResponse<SendVehicleTaskResponse> fetchSendVehicleTask(SendVehicleTaskRequest request);

    /**
     * 车辆未到、已到候选
     * @return
     */
    JdCResponse<List<SelectOption>> sendPhotoOptions();

    /**
     * 进入发货任务前拍照
     * @param request
     * @return
     */
    JdCResponse<Boolean> uploadPhoto(SendPhotoRequest request);

    /**
     * 发货任务详情
     * @param request
     * @return
     */
    JdCResponse<SendVehicleInfo> sendVehicleInfo(SendVehicleInfoRequest request);

    /**
     * 发货任务流向明细列表
     * @param request
     * @return
     */
    JdCResponse<List<SendDestDetail>> sendDestDetail(SendDetailRequest request);

    /**
     * 发货进度
     * @param request
     * @return
     */
    JdCResponse<SendVehicleProgress> loadProgress(SendVehicleProgressRequest request);

    /**
     * 校验发货任务是否异常
     * <ul>
     *     <li>单流向任务直接校验，多流向只在最后一个流向封车时校验</li>
     *     <li>拦截&强扫或装载率不足，两者都满足时异常优先</li>
     * </ul>
     * @param request
     * @return
     */
    JdCResponse<SendAbnormalResponse> checkSendVehicleNormalStatus(SendAbnormalRequest request);

    /**
     * 发货拦截包裹明细
     * @param request
     * @return
     */
    JdCResponse<SendAbnormalBarCode> interceptedBarCodeDetail(SendAbnormalPackRequest request);

    /**
     * 强制发货包裹明细
     * @param request
     * @return
     */
    JdCResponse<SendAbnormalBarCode> forceSendBarCodeDetail(SendAbnormalPackRequest request);

    /**
     * 发货异常包裹明细
     * @param request
     * @return
     */
    JdCResponse<SendAbnormalBarCode> abnormalSendBarCodeDetail(SendAbnormalPackRequest request);

    /**
     * 选择封车流向
     * @param request
     * @return
     */
    JdCResponse<ToSealDestAgg> selectSealDest(SelectSealDestRequest request);

    /**
     * 发货扫描
     * @param request
     * @return
     */
    JdVerifyResponse<SendScanResponse> sendScan(SendScanRequest request);

    /**
     * 校验批次任务是否为干线发货任务
     * @param request
     * @return
     */
    JdCResponse checkMainLineSendTask(CheckSendCodeRequest request);

    /**
     * 查询发货任务详情
     * @param request 请求参数
     * @return 返回结果
     * @author fanggang7
     * @time 2022-09-22 16:47:38 周四
     */
    JdCResponse<SendTaskInfo> sendTaskDetail(SendVehicleInfoRequest request);


    /**
     * 查询发货批次信息
     * @param request
     * @return
     */
    JdCResponse<SendBatchResp> querySendBatch(SendBatchReq request);

}
