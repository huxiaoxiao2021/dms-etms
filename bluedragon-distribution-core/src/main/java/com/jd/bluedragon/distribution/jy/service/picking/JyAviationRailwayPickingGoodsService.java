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
     * 根据待扫单据查询待扫任务服务
     * @param siteCode
     * @param barCode
     * @return
     */
    InvokeResult<JyBizTaskPickingGoodEntity> fetchPickingTaskByBarCode(Long siteCode, String barCode);
}
