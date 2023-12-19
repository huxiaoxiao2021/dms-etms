package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.FinishSendTaskReq;

/**
 * @Author zhengchengfa
 * @Date 2023/12/6 20:20
 * @Description
 */
public interface JyPickingSendDestinationService {

    /**
     * 获取该流向发货中批次号
     * @param curSiteId
     * @param nextSiteId
     * @return
     */
    String fetchSendingBatchCode(Integer curSiteId, Integer nextSiteId);

    Boolean finishSendTask(FinishSendTaskReq req);
}
