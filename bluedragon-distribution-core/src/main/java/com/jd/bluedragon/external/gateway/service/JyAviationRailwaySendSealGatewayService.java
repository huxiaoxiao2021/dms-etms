package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req.*;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res.*;
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
     * todo zcf
     * @param request
     * @return
     */
    JdCResponse<AviationToSealAndSealedListRes> fetchAviationToSealAndSealedList(AviationSendTaskListReq request);
    /**
     * 列表查询筛选条件查询
     * @param request
     * @return
     */
    JdCResponse<FilterConditionQueryRes> pageFetchFilterCondition(FilterConditionQueryReq request);







    /**
     * 获取运力编码列表
     * todo zcf 确认接口是否分页, 如果分页，需要单独给个查最近运力的接口，
     * 入参业务字段待确认
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
     * @param request
     * @return
     */
    JdCResponse<Void> sendTaskBinding(SendTaskBindReq request);

    /**
     * 已经绑定的任务进行删除
     * @param request
     * @return
     */
    JdCResponse<Void> sendTaskUnbinding(SendTaskUnbindReq request);

    /**
     * todo zcf
     * 查询摆渡发货任务上绑定的航空发货任务
     * @param request
     * @return
     */
    JdCResponse<SendTaskBindQueryRes> fetchSendTaskBindingData(SendTaskBindQueryReq request);

    /**
     * 查询摆渡发货任务封车详情
     * @param request
     * @return
     */
    JdCResponse<ShuttleTaskSealCarQueryRes> fetchShuttleTaskSealCarInfo(ShuttleTaskSealCarQueryReq request);

    /**
     * 摆渡任务封车
     * todo zcf
     * @param request
     * @return
     */
    JdCResponse<Void> shuttleTaskSealCar(ShuttleTaskSealCarReq request);

    /**
     * 航空任务封车
     * todo zcf
     * @param request
     * @return
     */
    JdCResponse<Void> aviationTaskSealCar(AviationTaskSealCarReq request);

}
