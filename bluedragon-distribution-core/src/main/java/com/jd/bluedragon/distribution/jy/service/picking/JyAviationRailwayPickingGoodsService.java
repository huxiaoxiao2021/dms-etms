package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.PickingSendGoodsReq;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.PickingSendGoodsRes;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

/**
 * @Author zhengchengfa
 * @Date 2023/12/4 13:27
 * @Description
 */
public interface JyAviationRailwayPickingGoodsService {
    InvokeResult<PickingSendGoodsRes> pickingSendGoods(PickingSendGoodsReq request);
}
