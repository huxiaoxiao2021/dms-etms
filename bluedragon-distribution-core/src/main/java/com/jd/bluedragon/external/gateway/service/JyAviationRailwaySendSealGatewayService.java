package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req.*;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res.*;
import com.jd.bluedragon.common.dto.seal.request.ShuttleTaskSealCarReq;
import com.jd.bluedragon.common.dto.select.SelectOption;

import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 14:13
 * @Description
 */


public interface JyAviationRailwaySendSealGatewayService {

    /**
     * 发货扫描方式枚举
     * @return
     */
    JdCResponse<List<SelectOption>> scanTypeOptions();

    /**
     * 航空发货列表数据查询： 待发货、已发货
     * @param request
     * @return
     */
    JdCResponse<AviationToSendAndSendingListRes> fetchAviationToSendAndSendingList(AviationSendTaskListReq request);
    /**
     * 查询流向下航空任务列表数据
     * @param request
     * @return
     */
    JdCResponse<AviationSendTaskQueryRes> pageFetchAviationTaskByNextSite(AviationSendTaskQueryReq request);
    /**
     * 航空发货列表数据查询： 待封车、已封车
     * @param request
     * @return
     */
    JdCResponse<AviationToSealAndSealedListRes> pageFetchAviationToSealAndSealedList(AviationSendTaskSealListReq request);
    /**
     * 航空已封列表查询
     * （仅列表查询，不含统计、条件查询， 使用场景：：用于摆渡绑定航空任务继续添加时查询待添加的已封航空任务列表）
     * todo 功能临时下线
     * @param request
     * @return
     */
    JdCResponse<AviationSealedListRes> pageFetchAviationSealedList(AviationSealedListReq request);
    /**
     * 列表查询筛选条件查询
     * @param request
     * @return
     */
    JdCResponse<FilterConditionQueryRes> pageFetchFilterCondition(FilterConditionQueryReq request);

    /**
     * 摆渡发车任务列表查询
     * todo 功能临时下线
     * @param request
     * @return
     */
    JdCResponse<ShuttleSendTaskRes> pageFetchShuttleSendTaskList(ShuttleSendTaskReq request);






    /**
     * 获取航空任务运力编码列表
     * @param request
     * @return
     */
    JdCResponse<TransportInfoQueryRes> fetchTransportCodeList(TransportCodeQueryReq request);
    /**
     * 自扫描运力编码时校验逻辑
     * @param request
     * @return
     */
    JdCResponse<TransportDataDto> scanAndCheckTransportInfo(ScanAndCheckTransportInfoReq request);




    /**
     * 摆渡发货任务上绑定航空发货任务
     * todo 功能临时下线
     * @param request
     * @return
     */
    JdCResponse<Void> sendTaskBinding(SendTaskBindReq request);

    /**
     * 已经绑定的任务进行删除
     * todo 功能临时下线
     * @param request
     * @return
     */
    JdCResponse<Void> sendTaskUnbinding(SendTaskUnbindReq request);

    /**
     * 查询摆渡发货任务上绑定的航空发货任务
     * todo 功能临时下线
     * @param request
     * @return
     */
    JdCResponse<SendTaskBindQueryRes> fetchSendTaskBindingData(SendTaskBindQueryReq request);

    /**
     * 查询摆渡发货任务封车详情
     * todo 功能临时下线
     * @param request
     * @return
     */
    JdCResponse<ShuttleTaskSealCarQueryRes> fetchShuttleTaskSealCarInfo(ShuttleTaskSealCarQueryReq request);

    /**
     * 摆渡任务封车
     * todo 功能临时下线
     * @param request
     * @return
     */
    JdCResponse<Void> shuttleTaskSealCar(ShuttleTaskSealCarReq request);

    /**
     * 航空任务封车
     * @param request
     * @return
     */
    JdCResponse<Void> aviationTaskSealCar(AviationTaskSealCarReq request);

    /**
     * 发货扫描接口
     * @param request
     * @return
     */
    JdVerifyResponse<AviationSendScanResp> scan(AviationSendScanReq request);

    /**
     * 发货任务详情查询
     * @param request
     * @return
     */
    JdCResponse<AviationSendVehicleProgressResp> getAviationSendVehicleProgress(AviationSendVehicleProgressReq request);

    /**
     * 异常包裹查询
     * @param request
     * @return
     */
    JdCResponse<AviationSendAbnormalPackResp> abnormalBarCodeDetail(AviationSendAbnormalPackReq request);

    /**
     * 发货明细查询
     * @param request
     * @return
     */
    JdCResponse<AviationBarCodeDetailResp> sendBarCodeDetail(AviationBarCodeDetailReq request);

    /**
     * 查询箱下的包裹明细数据
     * @param request
     * @return
     */
    JdCResponse<AviationBarCodeDetailResp> sendBoxDetail(AviationBoxDetailReq request);

    /**
     * 发货任务完成
     * @param request
     * @return
     */
    JdCResponse<Void> aviationSendComplete(AviationSendCompleteReq request);

    /**
     * 扫描批次号校验运力
     * @param request
     * @return
     */
    JdCResponse<ScanSendCodeValidRes> validateTranCodeAndSendCode(ScanSendCodeValidReq request);


    /**
     * 待封车摆渡明细查询
     * todo 功能临时下线
     * @param request
     * @return
     */
    JdCResponse<ShuttleTaskSealCarQueryRes> fetchToSealShuttleTaskDetail(ShuttleTaskSealCarQueryReq request);


    /**
     * （新）摆渡封车前数据准备
     * @param request
     * @return
     */
    JdCResponse<PrepareShuttleSealCarRes> prepareShuttleSealCarData(PrepareShuttleSealCarReq request);

    /**
     * 查询发货明细
     * @param request
     * @return
     */
    JdCResponse<SendTaskInfoRes> sendTaskDetail(SendTaskInfoReq request);

} 
