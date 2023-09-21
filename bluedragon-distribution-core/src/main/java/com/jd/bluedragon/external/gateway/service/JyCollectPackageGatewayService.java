package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.collectpackage.request.CollectPackageReq;
import com.jd.bluedragon.common.dto.collectpackage.response.CollectPackageResp;

/**
 * 小件集包网关服务
 */
public interface JyCollectPackageGatewayService {

  /**
   * 集包扫描
   */
  JdCResponse<CollectPackageResp> collectScan(CollectPackageReq request);

}
