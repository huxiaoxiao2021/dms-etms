package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.PickingGoodsReq;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.PickingGoodsRes;
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
}
