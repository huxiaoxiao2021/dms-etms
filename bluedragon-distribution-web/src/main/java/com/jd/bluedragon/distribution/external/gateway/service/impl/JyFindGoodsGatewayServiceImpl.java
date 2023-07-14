package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.UnifiedExceptionProcess;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.inventory.FindGoodsReq;
import com.jd.bluedragon.common.dto.inventory.FindGoodsResp;
import com.jd.bluedragon.common.dto.inventory.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.service.send.JyFindGoodsService;
import com.jd.bluedragon.external.gateway.service.JyFindGoodsGatewayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@UnifiedExceptionProcess
public class JyFindGoodsGatewayServiceImpl implements JyFindGoodsGatewayService {
  @Autowired
  JyFindGoodsService jyFindGoodsService;

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyFindGoodsGatewayServiceImpl.findGoodsScan", mState = {JProEnum.TP})
  public JdCResponse<FindGoodsResp> findGoodsScan(FindGoodsReq request) {
    return retJdCResponse(jyFindGoodsService.findGoodsScan(request));
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyFindGoodsGatewayServiceImpl.findCurrentInventoryTask", mState = {JProEnum.TP})
  public JdCResponse<InventoryTaskDto> findCurrentInventoryTask(InventoryTaskQueryReq request) {
    return null;
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyFindGoodsGatewayServiceImpl.findInventoryTaskByBizId", mState = {JProEnum.TP})
  public JdCResponse<InventoryTaskDto> findInventoryTaskByBizId(InventoryTaskQueryReq request) {
    return null;
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyFindGoodsGatewayServiceImpl.findInventoryTaskListPage", mState = {JProEnum.TP})
  public JdCResponse<InventoryTaskListQueryRes> findInventoryTaskListPage(InventoryTaskListQueryReq request) {
    return null;
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyFindGoodsGatewayServiceImpl.inventoryTaskStatistics", mState = {JProEnum.TP})
  public JdCResponse<InventoryTaskStatisticsRes> inventoryTaskStatistics(InventoryTaskStatisticsReq request) {
    return null;
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyFindGoodsGatewayServiceImpl.inventoryTaskPhotograph", mState = {JProEnum.TP})
  public JdCResponse<Void> inventoryTaskPhotograph(InventoryTaskPhotographReq request) {
    return null;
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyFindGoodsGatewayServiceImpl.findInventoryDetailPage", mState = {JProEnum.TP})
  public JdCResponse<InventoryDetailQueryRes> findInventoryDetailPage(InventoryDetailQueryReq request) {
    return null;
  }

  private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
    return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(),
        invokeResult.getData());
  }
}
