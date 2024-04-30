package com.jd.bluedragon.distribution.external.gateway.service.impl;

import static com.jd.bluedragon.common.dto.base.response.JdCResponse.CODE_ERROR;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.UnifiedExceptionProcess;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.comboard.request.BoardQueryReq;
import com.jd.bluedragon.common.dto.comboard.request.BoardReq;
import com.jd.bluedragon.common.dto.comboard.request.QueryBelongBoardReq;
import com.jd.bluedragon.common.dto.comboard.response.BoardQueryResp;
import com.jd.bluedragon.common.dto.comboard.response.GoodsCategoryDto;
import com.jd.bluedragon.common.dto.comboard.response.QueryBelongBoardResp;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.*;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.*;
import com.jd.bluedragon.common.dto.seal.request.CancelSealRequest;
import com.jd.bluedragon.common.dto.seal.request.CheckTransportReq;
import com.jd.bluedragon.common.dto.seal.request.JyCancelSealRequest;
import com.jd.bluedragon.common.dto.seal.request.SealCodeReq;
import com.jd.bluedragon.common.dto.seal.request.SealVehicleInfoReq;
import com.jd.bluedragon.common.dto.seal.request.SealVehicleReq;
import com.jd.bluedragon.common.dto.seal.response.JyCancelSealInfoResp;
import com.jd.bluedragon.common.dto.seal.response.SealCodeResp;
import com.jd.bluedragon.common.dto.seal.response.SealVehicleInfoResp;
import com.jd.bluedragon.common.dto.seal.response.TransportResp;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.seal.JySealVehicleService;
import com.jd.bluedragon.distribution.jy.service.seal.impl.JyComboardSealVehicleServiceImpl;
import com.jd.bluedragon.distribution.jy.service.send.IJyDriverViolationReportingService;
import com.jd.bluedragon.distribution.jy.service.send.IJySendVehicleService;
import com.jd.bluedragon.distribution.jy.service.send.JyComBoardSendService;
import com.jd.bluedragon.external.gateway.service.JyComboardSealGatewayService;
import com.jd.bluedragon.utils.ObjectHelper;
import java.util.List;

import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Slf4j
@UnifiedExceptionProcess
public class JyComboardSealGatewayServiceImpl implements JyComboardSealGatewayService {

  @Autowired
  @Qualifier("jyComboardSendVehicleService")
  private IJySendVehicleService jyComboardSendVehicleService;
  @Autowired
  @Qualifier("JyComboardSealVehicleService")
  JySealVehicleService jySealVehicleService;
  @Autowired
  JyComBoardSendService jyComBoardSendService;

  @Autowired
  DmsConfigManager dmsConfigManager;

  @Autowired
  IJyDriverViolationReportingService driverViolationReportingService;

  private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
    return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(),
        invokeResult.getData());
  }

  @Override
  public JdCResponse<SendVehicleTaskResponse> fetchSendVehicleTask(SendVehicleTaskRequest request) {
    return retJdCResponse(jyComboardSendVehicleService.fetchSendVehicleTask(request));
  }

  @Override
  public JdCResponse<List<SendDestDetail>> sendDestDetail(SendDetailRequest request) {
    return retJdCResponse(jyComboardSendVehicleService.sendDestDetail(request));
  }

  @Override
  public JdCResponse<ToSealDestAgg> selectSealDest(SelectSealDestRequest request) {
    return retJdCResponse(jyComboardSendVehicleService.selectSealDest(request));
  }

  @Override
  public JdCResponse<SendVehicleInfo> sendVehicleInfo(SendVehicleInfoRequest request) {
    return retJdCResponse(jyComboardSendVehicleService.sendVehicleInfo(request));
  }

  @Override
  public JdCResponse<SendVehicleProgress> loadProgress(SendVehicleProgressRequest request) {
    return retJdCResponse(jyComboardSendVehicleService.loadProgress(request));
  }

  @Override
  public JdCResponse<SendAbnormalBarCode> interceptedBarCodeDetail(
      SendAbnormalPackRequest request) {
    return retJdCResponse(jyComboardSendVehicleService.interceptedBarCodeDetail(request));
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
  public JdCResponse<TransportResp> checkTransCode(CheckTransportReq checkTransportReq) {
    return retJdCResponse(jySealVehicleService.checkTransCode(checkTransportReq));
  }

  @Override
  public JdCResponse saveSealVehicle(SealVehicleReq sealVehicleReq) {
    return retJdCResponse(jySealVehicleService.saveSealVehicle(sealVehicleReq));
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComboardSealGatewayServiceImpl.sealVehicle", mState = {JProEnum.TP, JProEnum.FunctionError})
  public JdCResponse sealVehicle(SealVehicleReq sealVehicleReq) {
    try {
      return retJdCResponse(jySealVehicleService.czSealVehicle(sealVehicleReq));
    } catch (JyBizException exception) {
      if (ObjectHelper.isNotNull(exception.getCode())){
        return new JdCResponse(exception.getCode(), exception.getMessage());
      }
      return new JdCResponse(CODE_ERROR, exception.getMessage());
    }
  }

  @Override
  public JdCResponse<QueryBelongBoardResp> queryBelongBoardByBarCode(QueryBelongBoardReq request) {
    return retJdCResponse(jySealVehicleService.queryBelongBoardByBarCode(request));
  }

  @Override
  public JdCResponse<BoardQueryResp> listComboardBySendFlow(BoardQueryReq request) {
    return retJdCResponse(jyComBoardSendService.listComboardBySendFlow(request));
  }

  @Override
  public JdCResponse<List<GoodsCategoryDto>> queryGoodsCategoryByBoardCode(BoardReq boardReq) {
    return retJdCResponse(jyComBoardSendService.queryGoodsCategoryByBoardCode(boardReq));
  }

  @Override
  public JdCResponse<SendTaskInfo> sendTaskDetail(SendVehicleInfoRequest request) {
    return retJdCResponse(jyComboardSendVehicleService.sendTaskDetail(request));
  }

  @Override
  public JdCResponse cancelSeal(JyCancelSealRequest request) {
    return retJdCResponse(jySealVehicleService.cancelSeal(request));
  }

  @Override
  public JdCResponse<JyCancelSealInfoResp> getCancelSealInfo(JyCancelSealRequest request) {
    return retJdCResponse(jySealVehicleService.getCancelSealInfo(request));
  }

  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComboardSealGatewayServiceImpl.checkViolationReporting", mState = {JProEnum.TP, JProEnum.FunctionError})
  @Override
  public JdCResponse<DriverViolationReportingDto> checkAndQueryViolationReporting(DriverViolationReportingRequest request) {
    return retJdCResponse(driverViolationReportingService.checkViolationReporting(request));
  }

  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComboardSealGatewayServiceImpl.submitViolationReporting", mState = {JProEnum.TP, JProEnum.FunctionError})
  @Override
  public JdCResponse<Void> submitViolationReporting(DriverViolationReportingAddRequest request) {
    return retJdCResponse(driverViolationReportingService.submitViolationReporting(request));
  }

}
