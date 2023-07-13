package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.comboard.request.ComboardScanReq;
import com.jd.bluedragon.common.dto.comboard.request.FindGoodsReq;
import com.jd.bluedragon.common.dto.comboard.request.FindGoodsResp;
import com.jd.bluedragon.common.dto.comboard.response.ComboardScanResp;

/**
 * 拣运找货网关服务
 */
public interface JyFindGoodsGatewayService {

  /**
   * 找货扫描
   */
  JdCResponse<FindGoodsResp> findGoodsScan(FindGoodsReq request);

}
