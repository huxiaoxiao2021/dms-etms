package com.jd.bluedragon.distribution.jy.api;

import java.util.List;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.InvokeWithMsgBoxResult;
import com.jd.bluedragon.distribution.jy.dto.*;
import com.jd.bluedragon.distribution.jy.dto.send.*;


/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/8/17
 * @Description: 拣运发货封车岗 转运专用服务
 *
 * 与卸车岗思想保持一致，暂不增加租户概念
 */
public interface JySendVehicleTysService {

    /**
     * 发货模式
     * @return
     */
    InvokeResult<List<JySelectOption>> sendModeOptions();

    /**
     * 车辆状态枚举
     * @return
     */
    InvokeResult<List<JySelectOption>> vehicleStatusOptions();

    /**
     * 线路类型枚举
     * @return
     */
    InvokeResult<List<JySelectOption>> lineTypeOptions();

    /**
     * 发货扫描方式枚举
     * @return
     */
    InvokeResult<List<JySelectOption>> scanTypeOptions();

    /**
     * 拉取发货任务列表
     * @param request
     * @return
     */
    InvokeResult<SendVehicleTaskResp> fetchSendVehicleTask(SendVehicleTaskReq request);

    /**
     * 车辆未到、已到候选
     * @return
     */
    InvokeResult<List<JySelectOption>> sendPhotoOptions();

    /**
     * 进入发货任务前拍照
     * @param request
     * @return
     */
    InvokeResult<Boolean> uploadPhoto(SendPhotoReq request);

    /**
     * 发货任务详情
     * @param request
     * @return
     */
    InvokeResult<SendVehicleInfo> sendVehicleInfo(SendVehicleInfoReq request);

    /**
     * 发货任务流向明细列表
     * @param request
     * @return
     */
    InvokeResult<List<SendDestDetail>> sendDestDetail(SendDetailReq request);

    /**
     * 发货进度
     * （补充运单维度数据）
     * @param request
     * @return
     */
    InvokeResult<SendVehicleProgress> loadProgress(SendVehicleProgressReq request);

    /**
     * 校验发货任务是否异常
     * <ul>
     *     <li>单流向任务直接校验，多流向只在最后一个流向封车时校验</li>
     *     <li>拦截&强扫或装载率不足，两者都满足时异常优先</li>
     * </ul>
     * @param request
     * @return
     */
    InvokeResult<SendAbnormalResp> checkSendVehicleNormalStatus(SendAbnormalReq request);



    /**
     * 查询扫描下钻运单明细
     *
     * @param querySendWaybillReq
     * @return
     */
    InvokeResult<SendWaybillStatisticsResp> listSendWaybillDetail(QuerySendWaybillReq querySendWaybillReq);

    /**
     * 查询扫描下钻包裹明细
     * @param querySendPackageReq
     * @return
     */

    InvokeResult<SendPackageStatisticsResp>  listSendPackageDetail(QuerySendPackageReq querySendPackageReq);


    /**
     * 选择封车流向
     * @param request
     * @return
     */
    InvokeResult<ToSealDestAgg> selectSealDest(SelectSealDestReq request);

    /**
     * 发货扫描
     * @param request
     * @return
     */
    InvokeWithMsgBoxResult<SendScanResp> sendScan(SendScanReq request);

    /**
     * 校验批次任务是否为干线发货任务
     * @param request
     * @return
     */
    InvokeResult<Void> checkMainLineSendTask(CheckSendCodeRequest request);

    /**
     * 查询发货任务详情
     * @param request
     * @return
     */
    InvokeResult<SendTaskInfo> sendTaskDetail(SendVehicleInfoReq request);

    /**
     * 查询发货批次信息
     * @param request
     * @return
     */
    InvokeResult<SendBatchResp> querySendBatch(SendBatchReq request);

    /**
     * 根据运输任务bizId查询车的封签号列表
     * @param sealCodeReq
     * @return
     */
    InvokeResult<SealCodeResp> listSealCodeByBizId(SealCodeReq sealCodeReq);

    /**
     * 查询流向任务封车数据详情
     * @param sealVehicleInfoReq
     * @return
     */
    InvokeResult<SealVehicleInfoResp> getSealVehicleInfo(SealVehicleInfoReq sealVehicleInfoReq);

    /**
     * 根据运力编码查询运输信息
     * @param transportReq
     * @return
     *
     */
    InvokeResult<TransportInfoDto>  getTransportResourceByTransCode(TransportReq transportReq);

    /**
     * 校验运力编码和任务简码是否匹配
     *
     */
    InvokeResult checkTransportCode(CheckTransportCodeReq checkTransportCodeReq);

    /**
     * 根据任务简码查询运输任务相关信息--原pda调用接口逻辑
     *
     */
    InvokeResult<TransportResp> getVehicleNumberByWorkItemCode(GetVehicleNumberReq getVehicleNumberReq);

    /**
     * 根据任务简码查询任务详情
     * @param getVehicleNumberReq
     * @return
     */
    InvokeResult<TransportResp> getTransWorkItemByWorkItemCode(GetVehicleNumberReq getVehicleNumberReq);

    /**
     * 提交封车
     *
     */
    InvokeResult sealVehicle(SealVehicleReq sealVehicleReq);

    /**
     * 保存封车数据
     *
     */
    InvokeResult<Boolean> saveSealVehicle(SealVehicleReq sealVehicleReq);

    /**
     * 校验运力编码和发货批次的目的地是否一致
     */
    InvokeResult<SealCarSendCodeResp> validateTranCodeAndSendCode(ValidSendCodeReq validSendCodeReq);


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
     * 查询运输车辆任务列表
     * @param vehicleTaskReq
     * @return
     */
    InvokeResult<VehicleTaskResp> listVehicleTask(VehicleTaskReq vehicleTaskReq);

    /**
     * 查询运输车辆任务列表：提供给任务迁移场景查询使用
     * 迁出时 扫包裹号定位包裹所在任务；迁入时 @1可扫包裹 @2也可录入站点id
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
     * 扫描包裹号、箱号 获取流向信息
     * @param getSendRouterInfoReq
     * @return
     */
    InvokeResult<GetSendRouterInfoResp> getSendRouterInfoByScanCode(GetSendRouterInfoReq getSendRouterInfoReq);

    /**
     * 不齐处理提交
     * （转运新增逻辑）
     * @param incompleteSendReq
     * @return
     */
    InvokeResult<IncompleteSendResp> incompleteSendSubmit(IncompleteSendReq incompleteSendReq);

    /**
     * 发货拦截包裹明细
     * @param request
     * @return
     */
    InvokeResult<SendAbnormalBarCode> interceptedBarCodeDetail(SendAbnormalPackReq request);

    /**
     * 强制发货包裹明细
     * @param request
     * @return
     */
    InvokeResult<SendAbnormalBarCode> forceSendBarCodeDetail(SendAbnormalPackReq request);

    /**
     * 发货异常包裹明细
     * @param request
     * @return
     */
    InvokeResult<SendAbnormalBarCode> abnormalSendBarCodeDetail(SendAbnormalPackReq request);

    /**
     * 串点运输任务提示
     * @param request
     * @return
     */
    InvokeWithMsgBoxResult<Void> transportTaskHint(TransportTaskReq request);


}
