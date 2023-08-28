package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.*;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.*;
import com.jd.bluedragon.common.dto.send.request.SendBatchReq;
import com.jd.bluedragon.common.dto.send.request.TransferVehicleTaskReq;
import com.jd.bluedragon.common.dto.send.request.VehicleTaskReq;
import com.jd.bluedragon.common.dto.send.response.*;
import com.jd.bluedragon.common.dto.send.request.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.send.DeleteVehicleTaskCheckResp;
import com.jd.bluedragon.distribution.jy.dto.send.JyTaskSendDetailFirstSendDto;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseTaskPo;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.dto.JyLineTypeDto;
import com.jd.dms.java.utils.sdk.base.Result;

import java.math.BigDecimal;
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
     * 查找各线路类型的任务数量
     * @param request
     * @return
     */
    List<JyLineTypeDto> findSendVehicleLineTypeAgg(SendVehicleTaskRequest request, InvokeResult<SendVehicleTaskResponse> invokeResult);

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

    InvokeResult<VehicleTaskResp> fetchSendTaskForTransferV2(TransferVehicleTaskReq vehicleTaskReq);

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

    /**
     * 查询子任务下的批次信息
     * @param request
     * @return
     */
    InvokeResult<SendBatchResp> listSendBatchByTaskDetail(SendBatchReq request);

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
     * 删除前校验-接口
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



    /**
     * 获取分拣发车岗待扫产品类型列表
     * @param request
     * @return
     */
    InvokeResult<List<SendVehicleProductTypeAgg>> sendVehicleToScanAggByProduct(SendVehicleCommonRequest request);

    /**
     * 按产品类型获取分拣发车岗待扫包裹列表
     * @param request
     * @return
     */
    InvokeResult<SendVehicleToScanPackageDetailResponse> sendVehicleToScanPackageDetail(SendVehicleToScanPackageDetailRequest request);

    /**
     * 计算操作进度
     * @param sendAggsEntity
     * @return
     */
    BigDecimal calculateOperateProgress(JySendAggsEntity sendAggsEntity,boolean needSendMsg);


    /**
     * 根据发货任务获取特安待扫数量
     * @param request
     * @return
     */
    InvokeResult<SendVehicleProductTypeAgg> getProductToScanInfo(SendAbnormalRequest request);

    /**
     * 根据发货任务获取特殊产品类型数量
     * @param request 请求参数
     * @return 待扫列表统计
     * @author fanggang7
     * @time 2023-07-26 10:00:32 周三
     */
    Result<SendVehicleToScanTipsDto> getSpecialProductTypeToScanList(SendVehicleToScanTipsRequest request);


    /**
     * 推送特安待扫包裹明细数据到场地负责人
     * @return
     */
    Result<Void> noticeToCanTEANPackage(AutoCloseTaskPo autoCloseTaskPo);

    /**
     * 首次发货任务扫描处理只装不卸属性
     * @param jyTaskSendDetailFirstSendDto 首次扫描数据
     * @return 处理结果
     * @author fanggang7
     * @time 2023-08-21 17:57:28 周一
     */
    Result<Boolean> handleOnlyLoadAttr(JyTaskSendDetailFirstSendDto jyTaskSendDetailFirstSendDto);

}
