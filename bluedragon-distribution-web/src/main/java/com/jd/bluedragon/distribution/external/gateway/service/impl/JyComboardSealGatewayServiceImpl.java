package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.UnifiedExceptionProcess;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.comboard.request.BoardQueryReq;
import com.jd.bluedragon.common.dto.comboard.request.QueryBelongBoardReq;
import com.jd.bluedragon.common.dto.comboard.response.BoardQueryResp;
import com.jd.bluedragon.common.dto.comboard.response.QueryBelongBoardResp;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendDetailRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendDestDetail;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleTaskResponse;
import com.jd.bluedragon.common.dto.seal.request.CheckTransportReq;
import com.jd.bluedragon.common.dto.seal.request.SealCodeReq;
import com.jd.bluedragon.common.dto.seal.request.SealVehicleInfoReq;
import com.jd.bluedragon.common.dto.seal.request.SealVehicleReq;
import com.jd.bluedragon.common.dto.seal.response.SealCodeResp;
import com.jd.bluedragon.common.dto.seal.response.SealVehicleInfoResp;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.service.comboard.JyComboardService;
import com.jd.bluedragon.distribution.jy.service.seal.JySealVehicleService;
import com.jd.bluedragon.distribution.jy.service.send.IJySendVehicleService;
import com.jd.bluedragon.distribution.jy.service.send.JyComBoardSendService;
import com.jd.bluedragon.external.gateway.service.JyComboardSealGatewayService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Slf4j
@UnifiedExceptionProcess
public class JyComboardSealGatewayServiceImpl implements JyComboardSealGatewayService {

  @Autowired
  @Qualifier("jyComboardSendVehicleService")
  private IJySendVehicleService jySendVehicleService;
  @Autowired
  JySealVehicleService jySealVehicleService;
  @Autowired
  JyComBoardSendService jyComBoardSendService;


  private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
    return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(),
        invokeResult.getData());
  }

  @Override
  public JdCResponse<SendVehicleTaskResponse> fetchSendVehicleTask(SendVehicleTaskRequest request) {
    return retJdCResponse(jySendVehicleService.fetchSendVehicleTask(request));
  }

  @Override
  public JdCResponse<List<SendDestDetail>> sendDestDetail(SendDetailRequest request) {
    return retJdCResponse(jySendVehicleService.sendDestDetail(request));
  }

  @Override
  public JdCResponse<SealVehicleInfoResp> getSealVehicleInfo(
      SealVehicleInfoReq sealVehicleInfoReq) {
    return retJdCResponse(jySealVehicleService.getSealVehicleInfo(sealVehicleInfoReq));
  }

  @Override
  public JdCResponse<SealCodeResp> listSealCodeByBizId(SealCodeReq sealCodeReq) {
    return retJdCResponse(jySealVehicleService.listSealCodeByBizId(sealCodeReq));
  }

  @Override
  public JdCResponse checkTransCode(CheckTransportReq checkTransportReq) {
    return retJdCResponse(jySealVehicleService.checkTransCodeScan(checkTransportReq));
  }

  @Override
  public JdCResponse saveSealVehicle(SealVehicleReq sealVehicleReq) {
    return retJdCResponse(jySealVehicleService.saveSealVehicle(sealVehicleReq));
  }

  @Override
  public JdCResponse sealVehicle(SealVehicleReq sealVehicleReq) {
    return retJdCResponse(jySealVehicleService.czSealVehicle(sealVehicleReq));
  }
  
  @Override
  public JdCResponse<QueryBelongBoardResp> queryBelongBoardByBarCode(QueryBelongBoardReq request) {
    return retJdCResponse(jyComBoardSendService.queryBelongBoardByBarCode(request));
  }

  @Override
  public JdCResponse<BoardQueryResp> listComboardBySendFlow(BoardQueryReq request) {
    return retJdCResponse(jySealVehicleService.listComboardBySendFlow(request));
  }
}
