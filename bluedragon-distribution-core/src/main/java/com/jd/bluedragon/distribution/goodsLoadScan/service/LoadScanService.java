package com.jd.bluedragon.distribution.goodsLoadScan.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingReq;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScan;

public interface LoadScanService {
    //装车发货
    JdCResponse goodsLoadingDeliver(GoodsLoadingReq req);

    //根据任务号查询任务状态
    Integer findTaskStatus(Long taskId);

    //根据任务id和运单号查询缓存中运单数据
    GoodsLoadScan queryByWaybillCodeAndTaskId(String waybillCode, Long taskId);

    //修改装车运单记录，计算已装车、未装车数量
    boolean updateGoodsLoadScanAmount(GoodsLoadScan param);

}
