package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.PickingSendGoodsReq;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.PickingSendGoodsRes;

/**
 * 空铁提货岗网关
 * @Author zhengchengfa
 * @Date 2023/12/4 10:56
 * @Description
 */
public interface JyAviationRailwayPickingGoodsGatewayService {

    JdCResponse<PickingSendGoodsRes> pickingSendGoodsScan(PickingSendGoodsReq request);


}
