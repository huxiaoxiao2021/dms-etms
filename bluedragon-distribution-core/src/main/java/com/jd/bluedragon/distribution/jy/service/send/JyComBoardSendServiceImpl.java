package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.dto.comboard.request.*;
import com.jd.bluedragon.common.dto.comboard.response.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.utils.ObjectHelper;

public class JyComBoardSendServiceImpl implements JyComBoardSendService {
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
        checkComboardScanReq(request);
        if (ObjectHelper.isNotNull(request.getBoardCode())) {

        } else {

        }

        return null;
    }

    private void checkComboardScanReq(ComboardScanReq request) {
        if (ObjectHelper.isNotNull(request.getBoardCode())) {
            //validateStatus(request.getBoardCode());//是不是进行中状态
            //count是不是在限制一下



        }
        //扫货方式和扫描单号校验 包裹流向跟当前流向是否一致

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
