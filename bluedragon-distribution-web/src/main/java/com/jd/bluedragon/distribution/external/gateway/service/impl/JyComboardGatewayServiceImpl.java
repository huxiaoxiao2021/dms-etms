package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.UnifiedExceptionProcess;
import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.comboard.request.*;
import com.jd.bluedragon.common.dto.comboard.response.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.service.send.JyComBoardSendService;
import com.jd.bluedragon.external.gateway.service.JyComboardGatewayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@UnifiedExceptionProcess
public class JyComboardGatewayServiceImpl implements JyComboardGatewayService {

  @Autowired
  JyComBoardSendService jyComBoardSendService;

  @Override
  public JdCResponse<CrossDataResp> listCrossData(CrossDataReq request) {
    return retJdCResponse(jyComBoardSendService.listCrossData(request));
  }

  @Override
  public JdCResponse<TableTrolleyResp> listTableTrolleyUnderCross(TableTrolleyReq request) {
    return retJdCResponse(jyComBoardSendService.listTableTrolleyUnderCross(request));
  }

  @Override
  public JdCResponse<TableTrolleyResp> querySendFlowByBarCode(QuerySendFlowReq request) {
    return retJdCResponse(jyComBoardSendService.querySendFlowByBarCode(request));
  }

  @Override
  public JdCResponse<CreateGroupCTTResp> createGroupCTTData(CreateGroupCTTReq request) {
    return retJdCResponse(jyComBoardSendService.createGroupCTTData(request));
  }

  @Override
  public JdCResponse<CreateGroupCTTResp> getDefaultGroupCTTName(BaseReq request) {
    return retJdCResponse(jyComBoardSendService.getDefaultGroupCTTName(request));
  }

  @Override
  public JdCResponse addCTT2Group(AddCTTReq request) {
    return retJdCResponse(jyComBoardSendService.addCTT2Group(request));
  }

  @Override
  public JdCResponse removeCTTFromGroup(RemoveCTTReq request) {
    return retJdCResponse(jyComBoardSendService.removeCTTFromGroup(request));
  }

  @Override
  public JdCResponse<CTTGroupDataResp> listCTTGroupData(CTTGroupDataReq request) {
    return retJdCResponse(jyComBoardSendService.listCTTGroupData(request));
  }

  @Override
  public JdCResponse<CTTGroupDataResp> queryCTTGroupByBarCode(QueryCTTGroupReq request) {
    return retJdCResponse(jyComBoardSendService.queryCTTGroupByBarCode(request));
  }

  @Override
  public JdCResponse<SendFlowDataResp> listSendFlowUnderCTTGroup(SendFlowDataReq request) {
    return retJdCResponse(jyComBoardSendService.listSendFlowUnderCTTGroup(request));
  }

  @Override
  public JdCResponse<SendFlowDetailResp> querySendFlowDetail(SendFlowDetailReq request) {
    return retJdCResponse(jyComBoardSendService.querySendFlowDetail(request));
  }

  @Override
  public JdCResponse<BoardResp> queryBoardDetail(BoardReq request) {
    return retJdCResponse(jyComBoardSendService.queryBoardDetail(request));
  }

  @Override
  public JdCResponse finishBoard(BoardReq request) {
    return retJdCResponse(jyComBoardSendService.finishBoard(request));
  }

  @Override
  public JdCResponse finishBoardsUnderCTTGroup(CTTGroupReq request) {
    return retJdCResponse(jyComBoardSendService.finishBoardsUnderCTTGroup(request));
  }

  @Override
  public JdCResponse<ComboardScanResp> comboardScan(ComboardScanReq request) {
    return retJdCResponse(jyComBoardSendService.comboardScan(request));
  }

  @Override
  public JdCResponse<BoardStatisticsResp> queryBoardStatisticsUnderSendFlow(
      BoardStatisticsReq request) {
    return retJdCResponse(jyComBoardSendService.queryBoardStatisticsUnderSendFlow(request));
  }

  @Override
  public JdCResponse<QueryBelongBoardResp> queryBelongBoardByBarCode(QueryBelongBoardReq request) {
    return retJdCResponse(jyComBoardSendService.queryBelongBoardByBarCode(request));
  }

  @Override
  public JdCResponse<HaveScanStatisticsResp> queryHaveScanStatisticsUnderBoard(
      HaveScanStatisticsReq request) {
    return retJdCResponse(jyComBoardSendService.queryHaveScanStatisticsUnderBoard(request));
  }

  @Override
  public JdCResponse<PackageDetailResp> listPackageDetailUnderBox(BoxQueryReq request) {
    return retJdCResponse(jyComBoardSendService.listPackageDetailRespUnderBox(request));
  }

  @Override
  public JdCResponse<WaitScanStatisticsResp> queryWaitScanStatisticsUnderSendFlow(
      WaitScanStatisticsReq request) {
    return retJdCResponse(jyComBoardSendService.queryWaitScanStatisticsUnderSendFlow(request));
  }

  @Override
  public JdCResponse<PackageDetailResp> listPackageDetailUnderSendFlow(
      SendFlowQueryReq request) {
    return retJdCResponse(jyComBoardSendService.listPackageDetailUnderSendFlow(request));
  }

  @Override
  public JdCResponse<BoardExcepStatisticsResp> queryExcepScanStatisticsUnderBoard(
      BoardExcepStatisticsReq request) {
    return retJdCResponse(jyComBoardSendService.queryExcepScanStatisticsUnderBoard(request));
  }

  @Override
  public JdCResponse<SendFlowExcepStatisticsResp> queryExcepScanStatisticsUnderCTTGroup(
      SendFlowExcepStatisticsReq request) {
    return retJdCResponse(jyComBoardSendService.queryExcepScanStatisticsUnderCTTGroup(request));
  }

  @Override
  public JdCResponse<ComboardDetailResp> listPackageOrBoxUnderBoard(BoardReq request) {
    return retJdCResponse(jyComBoardSendService.listPackageOrBoxUnderBoard(request));
  }

  @Override
  public JdCResponse cancelComboard(CancelBoardReq request) {
    return retJdCResponse(jyComBoardSendService.cancelComboard(request));
  }

  private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
    return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(),
        invokeResult.getData());
  }
}
