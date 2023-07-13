package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.UnifiedExceptionProcess;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.comboard.request.FindGoodsReq;
import com.jd.bluedragon.common.dto.comboard.request.FindGoodsResp;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.service.send.JyComBoardSendService;
import com.jd.bluedragon.distribution.jy.service.send.JyFindGoodsService;
import com.jd.bluedragon.external.gateway.service.JyFindGoodsGatewayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@UnifiedExceptionProcess
public class JyFindGoodsGatewayServiceImpl implements JyFindGoodsGatewayService {
  @Autowired
  JyFindGoodsService jyFindGoodsService;

  @Override
  public JdCResponse<FindGoodsResp> findGoodsScan(FindGoodsReq request) {
    return retJdCResponse(jyFindGoodsService.findGoodsScan(request));
  }

  private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
    return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(),
        invokeResult.getData());
  }
}
