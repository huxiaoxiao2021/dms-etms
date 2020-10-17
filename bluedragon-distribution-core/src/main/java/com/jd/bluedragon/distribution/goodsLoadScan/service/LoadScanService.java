package com.jd.bluedragon.distribution.goodsLoadScan.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingReq;

public interface LoadScanService {
    JdCResponse goodsLoadingDeliver(GoodsLoadingReq req);
}
