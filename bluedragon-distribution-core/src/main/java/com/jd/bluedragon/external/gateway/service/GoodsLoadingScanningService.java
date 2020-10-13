package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsRemoveScanningReq;

/**
 * 货物装车扫描相关
 * 发布到物流网关 由安卓调用
 */
public interface GoodsLoadingScanningService {

    JdCResponse goodsRemoveScanning(GoodsRemoveScanningReq req);
}
