package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.dto.comboard.request.*;
import com.jd.bluedragon.common.dto.comboard.response.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

public class JyComBoardSendServiceImpl implements JyComBoardSendService{
    @Override
    public InvokeResult<CrossDataResp> listCrossData(CrossDataReq request) {
        return null;
    }

    @Override
    public InvokeResult<TableTrolleyResp> listTableTrolleyUnderCross(TableTrolleyReq request) {
        return null;
    }

    @Override
    public InvokeResult<CreateGroupCTTResp> createGroupCTTData(CreateGroupCTTReq request) {
        return null;
    }

    @Override
    public InvokeResult addCTT2Group(AddCTTReq request) {
        return null;
    }

    @Override
    public InvokeResult removeCTTFromGroup(RemoveCTTReq request) {
        return null;
    }

    @Override
    public InvokeResult<CTTGroupDataResp> listCTTGroupData(CTTGroupDataReq request) {
        return null;
    }

    @Override
    public InvokeResult<SendFlowDataResp> listSendFlowUnderCTTGroup(SendFlowDataReq request) {
        return null;
    }

    @Override
    public InvokeResult<SendFlowDetailResp> querySendFlowDetail(SendFlowDetailReq request) {
        return null;
    }

    @Override
    public InvokeResult<BoardResp> queryBoardDetail(BoardReq request) {
        return null;
    }

    @Override
    public InvokeResult finishBoard(BoardReq request) {
        return null;
    }

    @Override
    public InvokeResult finishBoardsUnderCTTGroup(CTTGroupReq request) {
        return null;
    }

    @Override
    public InvokeResult<ComboardScanResp> comboardScan(ComboardScanReq request) {
        
        return null;
    }

    @Override
    public InvokeResult<BoardStatisticsResp> queryBoardStatisticsUnderSendFlow(BoardStatisticsReq request) {
        return null;
    }

    @Override
    public InvokeResult<HaveScanStatisticsResp> queryHaveScanStatisticsUnderBoard(HaveScanStatisticsReq request) {
        return null;
    }

    @Override
    public InvokeResult<PackageDetailResp> listPackageDetailRespUnderBox(BoxQueryReq request) {
        return null;
    }

    @Override
    public InvokeResult<WaitScanStatisticsResp> queryWaitScanStatisticsUnderSendFlow(WaitScanStatisticsReq request) {
        return null;
    }

    @Override
    public InvokeResult<PackageDetailResp> listPackageDetailRespUnderSendFlow(SendFlowQueryReq request) {
        return null;
    }

    @Override
    public InvokeResult<BoardExcepStatisticsResp> queryExcepScanStatisticsUnderBoard(BoardExcepStatisticsReq request) {
        return null;
    }

    @Override
    public InvokeResult<SendFlowExcepStatisticsResp> queryExcepScanStatisticsUnderCTTGroup(SendFlowExcepStatisticsReq request) {
        return null;
    }
}
