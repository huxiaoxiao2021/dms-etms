package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.comboard.request.BoardQueryReq;
import com.jd.bluedragon.common.dto.comboard.request.QueryBelongBoardReq;
import com.jd.bluedragon.common.dto.comboard.response.BoardQueryResp;
import com.jd.bluedragon.common.dto.comboard.response.QueryBelongBoardResp;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendDetailRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendDestDetail;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleTaskResponse;
import com.jd.bluedragon.common.dto.seal.request.CheckTransportCodeReq;
import com.jd.bluedragon.common.dto.seal.request.CheckTransportReq;
import com.jd.bluedragon.common.dto.seal.request.SealCodeReq;
import com.jd.bluedragon.common.dto.seal.request.SealVehicleInfoReq;
import com.jd.bluedragon.common.dto.seal.request.SealVehicleReq;
import com.jd.bluedragon.common.dto.seal.request.TransportReq;
import com.jd.bluedragon.common.dto.seal.response.SealCodeResp;
import com.jd.bluedragon.common.dto.seal.response.SealVehicleInfoResp;
import com.jd.bluedragon.external.gateway.service.JyComboardSealGatewayService;
import java.util.List;

public class JyComboardSealGatewayServiceImpl implements JyComboardSealGatewayService {

  @Override
  public JdCResponse<SendVehicleTaskResponse> fetchSendVehicleTask(SendVehicleTaskRequest request) {
    return null;
  }

  @Override
  public JdCResponse<List<SendDestDetail>> sendDestDetail(SendDetailRequest request) {
    return null;
  }

  @Override
  public JdCResponse<SealVehicleInfoResp> getSealVehicleInfo(
      SealVehicleInfoReq sealVehicleInfoReq) {
    return null;
  }

  @Override
  public JdCResponse<SealCodeResp> listSealCodeByBizId(SealCodeReq sealCodeReq) {
    return null;
  }

  @Override
  public JdCResponse checkTransCode(CheckTransportReq checkTransportReq) {
    return null;
  }

  @Override
  public JdCResponse saveSealVehicle(SealVehicleReq sealVehicleReq) {
    return null;
  }

  @Override
  public JdCResponse sealVehicle(SealVehicleReq sealVehicleReq) {
    return null;
  }

  @Override
  public JdCResponse<QueryBelongBoardResp> queryBelongBoardByBarCode(QueryBelongBoardReq request) {
    return null;
  }

  @Override
  public JdCResponse<BoardQueryResp> listComboardBySendFlow(BoardQueryReq request) {
    return null;
  }
}
