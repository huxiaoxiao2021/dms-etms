package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.*;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.*;

import java.util.List;

/**
 * 空铁提货岗网关
 * @Author zhengchengfa
 * @Date 2023/12/4 10:56
 * @Description
 */
public interface JyAviationRailwayPickingGoodsGatewayService {

    JdCResponse<PickingGoodsRes> pickingGoodsScan(PickingGoodsReq request);
    /**
     * 提货完成
     * @param req
     * @return
     */
    JdCResponse<Void> finishPickGoods(FinishPickGoodsReq req);

    /**
     * 异常提报
     * @param req
     * @return
     */
    JdCResponse<Void> submitException(ExceptionSubmitReq req);

    /**
     * 查询已维护流向信息
     * @param req
     * @return
     */
    JdCResponse<SendFlowRes> listSendFlowInfo(SendFlowReq req);

    /**
     * 添加流向
     * @param req
     * @return
     */
    JdCResponse<Void> addSendFlow(SendFlowAddReq req);

    /**
     * 删除流向
     * @param req
     * @return
     */
    JdCResponse<Void> deleteSendFlow(SendFlowDeleteReq req);

    /**
     * 发货完成
     * @param req
     * @return
     */
    JdCResponse<Void> finishSendTask(FinishSendTaskReq req);

    /**
     * 所有机场任务列表
     * @param req
     * @return
     */
    JdCResponse<AirportTaskRes> listAirportTask(AirportTaskReq req);

    /**
     * 某机场下的任务列表
     * @param req
     * @return
     */
    JdCResponse<List<AirportTaskAggDto>> listAirportTaskAgg(AirportTaskAggReq req);
}
