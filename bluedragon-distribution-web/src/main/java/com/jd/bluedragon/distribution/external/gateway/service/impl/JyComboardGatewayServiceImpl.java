package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.UnifiedExceptionProcess;
import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.comboard.request.*;
import com.jd.bluedragon.common.dto.comboard.response.*;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JySendVehicleScanTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendVehicleScanTypeEnum;
import com.jd.bluedragon.common.dto.select.SelectOption;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.service.send.JyComBoardSendService;
import com.jd.bluedragon.external.gateway.service.JyComboardGatewayService;
import com.jdl.basic.api.enums.TenantEnum;
import com.jdl.sorting.tech.tenant.core.context.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
  public JdCResponse<String> finishBoard(BoardReq request) {
    return retJdCResponse(jyComBoardSendService.finishBoard(request));
  }

  @Override
  public JdCResponse<Void> finishBoardsUnderCTTGroup(CTTGroupReq request) {
    return retJdCResponse(jyComBoardSendService.finishBoardsUnderCTTGroup(request));
  }

  @Override
  public JdVerifyResponse<ComboardScanResp> comboardScan(ComboardScanReq request) {
    return jyComBoardSendService.comboardScan(request);
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
  public JdCResponse<Void> cancelComboard(CancelBoardReq request) {
    return retJdCResponse(jyComBoardSendService.cancelComboard(request));
  }

  @Override
  public JdCResponse erpPasswdCheck(UserInfoReq request) {
    return null;
  }

  @Override
  public JdCResponse<String> deleteCTTGroup(DeleteCTTGroupReq request) {
    return retJdCResponse(jyComBoardSendService.deleteCTTGroup(request));
  }

  @Override
  public JdCResponse<SendFlowDataResp> queryScanUser(SendFlowQueryReq req) {
    return retJdCResponse(jyComBoardSendService.queryScanUser(req));
  }

  @Override
  public JdCResponse<List<SelectOption>> scanTypeOptions() {
    List<SelectOption> optionList = new ArrayList<>();
    String tenantCode = TenantContext.getTenantCode();
    //冷链租户返回按件、按单、按板，非冷链返回按件、按单
    if(StringUtils.isNotBlank(tenantCode) && TenantEnum.TENANT_COLD_MEDICINE.getCode().equals(tenantCode)){
      for (SendVehicleScanTypeEnum _enum : SendVehicleScanTypeEnum.values()) {
        SelectOption option = new SelectOption(_enum.getCode(), _enum.getName(), _enum.getDesc(), _enum.getCode());
        optionList.add(option);
      }
    }else{
      for (SendVehicleScanTypeEnum _enum : Arrays.asList(SendVehicleScanTypeEnum.SCAN_ONE,SendVehicleScanTypeEnum.SCAN_WAYBILL)) {
        SelectOption option = new SelectOption(_enum.getCode(), _enum.getName(), _enum.getDesc(), _enum.getCode());
        optionList.add(option);
      }
    }

    Collections.sort(optionList, new SelectOption.OrderComparator());

    JdCResponse<List<SelectOption>> response = new JdCResponse<>();
    response.toSucceed();
    response.setData(optionList);
    return response;
  }

  private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
    return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(),
        invokeResult.getData());
  }
}
