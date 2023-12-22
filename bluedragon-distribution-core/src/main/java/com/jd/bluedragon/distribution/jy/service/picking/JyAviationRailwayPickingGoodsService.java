package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.*;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntity;

/**
 * @Author zhengchengfa
 * @Date 2023/12/4 13:27
 * @Description
 */
public interface JyAviationRailwayPickingGoodsService {
    /**
     * 提货发货扫描
     * @param request
     * @return
     */
    InvokeResult<PickingGoodsRes> pickingGoodsScan(PickingGoodsReq request);

    /**
     * 根据待扫单据查询待提任务服务
     *   [ ** 这里查待提， 非已提任务， 已提可能因为多提存在多个实际提货的任务]
     * @param siteCode
     * @param barCode
     * @return
     */
    JyBizTaskPickingGoodEntity fetchWaitPickingBizIdByBarCode(Long siteCode, String barCode);

    /**
     * 提货完成
     * @param req
     * @return
     */
    InvokeResult<Void> finishPickGoods(FinishPickGoodsReq req);

    /**
     * 异常提报
     * @param req
     * @return
     */
    InvokeResult<Void> submitException(ExceptionSubmitReq req);

    /**
     * 查询已维护流向信息
     * @param req
     * @return
     */
    InvokeResult<SendFlowRes> listSendFlowInfo(SendFlowReq req);

    /**
     * 添加流向
     * @param req
     * @return
     */
    InvokeResult<Void> addSendFlow(SendFlowAddReq req);

    /**
     * 删除流向
     * @param req
     * @return
     */
    InvokeResult<Void> deleteSendFlow(SendFlowDeleteReq req);

    /**
     * 发货完成
     * @param req
     * @return
     */
    InvokeResult<Void> finishSendTask(FinishSendTaskReq req);

    /**
     * 所有机场任务列表
     * @param req
     * @return
     */
    InvokeResult<AirRailTaskRes> listAirportTask(AirRailTaskSummaryReq req);

    /**
     * 某机场下的任务列表
     * @param req
     * @return
     */
    InvokeResult<AirRailTaskAggRes> listAirportTaskAgg(AirRailTaskAggReq req);

    /**
     * 是否首次扫描
     * @param bizId
     * @param siteId
     * @return
     */
    boolean isFirstScanInTask(String bizId, Long siteId);

    /**
     * 首次提货
     * @param siteId
     * @param bizId
     * @param time
     * @param user
     */
    void startPickingGoodTask(Long siteId, String bizId, Long time, User user);
}
