package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.UnifiedExceptionProcess;
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
        return null;
    }

    @Override
    public JdCResponse<TableTrolleyResp> listTableTrolleyUnderCross(TableTrolleyReq request) {
        return null;
    }

    @Override
    public JdCResponse<CreateGroupCTTResp> createGroupCTTData(CreateGroupCTTReq request) {
        return null;
    }

    @Override
    public JdCResponse addCTT2Group(AddCTTReq request) {
        return null;
    }

    @Override
    public JdCResponse removeCTTFromGroup(RemoveCTTReq request) {
        return null;
    }

    @Override
    public JdCResponse<CTTGroupDataResp> listCTTGroupData(CTTGroupDataReq request) {
        return null;
    }

    @Override
    public JdCResponse<SendFlowDataResp> listSendFlowUnderCTTGroup(SendFlowDataReq request) {
        return null;
    }

    @Override
    public JdCResponse<SendFlowDetailResp> querySendFlowDetail(SendFlowDetailReq request) {
        return null;
    }

    @Override
    public JdCResponse<BoardResp> queryBoardDetail(BoardReq request) {
        return null;
    }

    @Override
    public JdCResponse finishBoard(BoardReq request) {
        return null;
    }

    @Override
    public JdCResponse finishBoardsUnderCTTGroup(CTTGroupReq request) {
        return null;
    }

    @Override
    public JdCResponse<ComboardScanResp> comboardScan(ComboardScanReq request) {
        return retJdCResponse(jyComBoardSendService.comboardScan(request));
    }

    @Override
    public JdCResponse<BoardStatisticsResp> queryBoardStatisticsUnderSendFlow(BoardStatisticsReq request) {
        return null;
    }

    @Override
    public JdCResponse<HaveScanStatisticsResp> queryHaveScanStatisticsUnderBoard(HaveScanStatisticsReq request) {
        return null;
    }

    @Override
    public JdCResponse<PackageDetailResp> listPackageDetailRespUnderBox(BoxQueryReq request) {
        return null;
    }

    @Override
    public JdCResponse<WaitScanStatisticsResp> queryWaitScanStatisticsUnderSendFlow(WaitScanStatisticsReq request) {
        return null;
    }

    @Override
    public JdCResponse<PackageDetailResp> listPackageDetailRespUnderSendFlow(SendFlowQueryReq request) {
        return null;
    }

    @Override
    public JdCResponse<BoardExcepStatisticsResp> queryExcepScanStatisticsUnderBoard(BoardExcepStatisticsReq request) {
        return null;
    }

    @Override
    public JdCResponse<SendFlowExcepStatisticsResp> queryExcepScanStatisticsUnderCTTGroup(SendFlowExcepStatisticsReq request) {
        return null;
    }
    private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
    }
}
