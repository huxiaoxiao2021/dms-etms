package com.jd.bluedragon.distribution.jy.service.picking;

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
    InvokeResult<FinishSendTaskRes> completePickingSendTask(FinishSendTaskReq req);

    /**
     * 所有机场任务列表
     * @param req
     * @return
     */
    InvokeResult<AirRailTaskRes> listAirRailTaskSummary(AirRailTaskSummaryReq req);

    /**
     * 某机场下的任务列表
     * @param req
     * @return
     */
    InvokeResult<AirRailTaskAggRes> listAirRailTaskAgg(AirRailTaskAggReq req);

    /**
     * 是否首次扫描
     * @param
     * @param
     * @return
     */
//    boolean isFirstScanInTask(String bizId, Long siteId);


    void finishTaskWhenWaitScanEqZero();

    void finishTaskWhenTimeExceed();

    /**
     * 获取提发批次信息
     * @param req
     * @return
     */
    InvokeResult<PickingSendBatchCodeDetailRes> pageFetchSendBatchCodeDetailList(PickingSendBatchCodeDetailReq req);

    /**
     * 删除提发批次信息
     * @param req
     * @return
     */
    InvokeResult delBatchCodes(DelBatchCodesReq req);
}
