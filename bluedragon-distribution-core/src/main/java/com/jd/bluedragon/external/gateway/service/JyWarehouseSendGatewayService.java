package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.operation.workbench.seal.SealCarSendCodeResp;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SelectSealDestRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleProgressRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleProgress;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleTaskResponse;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.ToSealDestAgg;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.send.*;
import com.jd.bluedragon.common.dto.seal.request.CheckTransportReq;
import com.jd.bluedragon.common.dto.seal.request.SealVehicleInfoReq;
import com.jd.bluedragon.common.dto.seal.request.SealVehicleReq;
import com.jd.bluedragon.common.dto.seal.request.ValidSendCodeReq;
import com.jd.bluedragon.common.dto.seal.response.SealVehicleInfoResp;
import com.jd.bluedragon.common.dto.seal.response.TransportResp;
import com.jd.bluedragon.common.dto.select.SelectOption;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

import java.util.List;

/**
 * 接货仓发货岗
 * zhengchengfa
 */
public interface JyWarehouseSendGatewayService {

    /**
     * 车辆状态枚举
     * @return
     */
    JdCResponse<List<SelectOption>> vehicleStatusOptions();
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
    JdCResponse<SendVehicleTaskResponse> fetchSendVehicleTaskPage(SendVehicleTaskRequest request);
    /**
     * 查询待发和发货中的任务数据
     *  fetchSendVehicleTaskPage方法是四个状态隔离，涉及其他功能，重逻辑， 本接口是待发货和发货中的轻量级接口
     * @param request
     * @return
     */
    JdCResponse<AppendSendVehicleTaskQueryRes> fetchToSendAndSendingTaskPage(AppendSendVehicleTaskQueryReq request);
    /**
     * 发货统计
     * todo loadProgress
     * @param request
     * @return
     */
    JdCResponse<SendVehicleProgress> loadStatistics(SendVehicleProgressRequest request);
    /**
     * 发货扫描
     * @param request
     * @return
     */
    JdVerifyResponse<SendScanRes> sendScan(SendScanReq request);
    /**
     * 取消发货
     * @param request 请求参数
     * @return 取消详情
     */
    JdCResponse<SendCancelScanRes> cancelSendScan(CancelSendScanReq request);

    /**
     * 不齐运单查询-分页
     * @param request
     * @return
     */
    JdCResponse<BuQiWaybillRes> findByQiWaybillPage(BuQiWaybillReq request);
    /**
     * 不齐包裹查询-分页
     * @param request
     * @return
     */
    JdCResponse<BuQiPackageRes> findByQiPackagePage(BuQiWaybillReq request);
    /**
     * 取消发货
     * @param request 请求参数
     * @return 取消详情
     */
    JdCResponse<Void> buQiCancelSendScan(BuQiCancelSendScanReq request);


    /**
     * 获取混扫任务默认名称
     * @return
     */
    JdCResponse<String> getMixScanTaskDefaultName(MixScanTaskDefaultNameQueryReq mixScanTaskDefaultNameQueryReq);
    /**
     * 新增混扫任务
     * todo createGroupCTTData
     * @return  新增的混扫任务信息返回
     */
    JdCResponse<CreateMixScanTaskRes> createMixScanTask(CreateMixScanTaskReq createMixScanTaskReq);
    /**
     * 混扫任务追加流向信息
     * todo addCTT2Group
     * @param appendMixScanTaskFlowReq
     * @return
     */
    JdCResponse<Void> appendMixScanTaskFlow(AppendMixScanTaskFlowReq appendMixScanTaskFlowReq);
    /**
     * 删除混扫任务
     * todo deleteCTTGroup
     * @param deleteMixScanTaskReq
     * @return
     */
    JdCResponse<Void> deleteMixScanTask(DeleteMixScanTaskReq deleteMixScanTaskReq);
    /**
     * 删除混扫任务流向
     * todo removeCTTFromGroup
     * @param removeMixScanTaskFlowReq
     * @return
     */
    JdCResponse<Void> removeMixScanTaskFlow(RemoveMixScanTaskFlowReq removeMixScanTaskFlowReq);
    /**
     * 混扫任务完成
     * @param mixScanTaskCompleteReq
     * @return
     */
    JdCResponse<Void> mixScanTaskComplete(MixScanTaskCompleteReq mixScanTaskCompleteReq);
    /**
     * 混扫任务关注/取消关注
     * @param mixScanTaskFocusReq
     * @return
     */
    JdCResponse<Void> mixScanTaskFocus(MixScanTaskFocusReq mixScanTaskFocusReq);
    /**
     * 混扫任务列表查询-分页
     * 适用场景：（1）发货中任务点击查询跳转；（2）混扫任务主页展示；（3）混扫任务主页按条件查询
     * todo listCTTGroupData
     * todo queryCTTGroupByBarCode
     */
    JdCResponse<MixScanTaskQueryRes> getMixScanTaskListPage(MixScanTaskListQueryReq mixScanTaskListQueryReq);
    /**
     * 获取混扫任务下流向信息
     * @param mixScanTaskFlowReq
     * @return
     */
    JdCResponse<MixScanTaskFlowDetailRes> getMixScanTaskFlowDetailList(MixScanTaskFlowDetailReq mixScanTaskFlowReq);
    /**
     * 获取混扫任务下流向信息(根据业务场景，无需支持分页)
     * @param mixScanTaskQueryReq
     * @return
     */
    JdCResponse<MixScanTaskDetailRes> getMixScanTaskDetailList(MixScanTaskQueryReq mixScanTaskQueryReq);


    /**
     * 选择封车流向
     * todo selectSealDest
     * @param request
     * @return
     */
    JdCResponse<MixScanTaskToSealDestAgg> selectMixScanTaskSealDest(SelectMixScanTaskSealDestReq request);
    /**
     * 查询流向任务封车数据详情
     * todo getSealVehicleInfo
     * @param sealVehicleInfoReq
     * @return
     */
    JdCResponse<SealVehicleInfoResp> getSealVehicleInfo(SealVehicleInfoReq sealVehicleInfoReq);

    /**
     * 校验运力编码和当前流向是否一致
     * @param checkTransportReq
     * @return
     *
     */
    JdCResponse<TransportResp>  checkTransCode(CheckTransportReq checkTransportReq);

    /**
     * 封车数据暂存
     *
     */
    JdCResponse<Boolean> saveSealVehicle(SealVehicleReq sealVehicleReq);
    
    /**
     * 校验运力编码和发货批次的目的地是否一致
     */
    JdCResponse<SealCarSendCodeResp> validateTranCodeAndSendCode(ValidSendCodeReq request);

    /**
     * 提交封车
     */
    JdCResponse<Void> sealVehicle(SealVehicleReq sealVehicleReq);
}
