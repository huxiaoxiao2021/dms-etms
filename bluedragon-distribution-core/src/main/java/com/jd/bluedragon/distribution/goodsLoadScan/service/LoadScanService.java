package com.jd.bluedragon.distribution.goodsLoadScan.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingReq;

public interface LoadScanService {
    //装车发货
    JdCResponse goodsLoadingDeliver(GoodsLoadingReq req);

    //根据任务号查询任务状态
    Integer findTaskStatus(Long taskId);
}
