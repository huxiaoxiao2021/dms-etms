package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req.*;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.send.BatchCodeShuttleSealDto;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 14:54
 * @Description
 */
public interface JyAviationRailwaySendSealService {
    
    InvokeResult<Void> sendTaskBinding(SendTaskBindReq request);

    InvokeResult<Void> sendTaskUnbinding(SendTaskUnbindReq request);

    InvokeResult<FilterConditionQueryRes> pageFetchCurrentSiteStartAirport(FilterConditionQueryReq request);

    InvokeResult<TransportInfoQueryRes> fetchTransportCodeList(TransportCodeQueryReq request);

    InvokeResult<TransportDataDto> scanAndCheckTransportInfo(ScanAndCheckTransportInfoReq request);

    InvokeResult<ShuttleTaskSealCarQueryRes> fetchShuttleTaskSealCarInfo(ShuttleTaskSealCarQueryReq request);

    InvokeResult<Void> aviationTaskSealCar(AviationTaskSealCarReq request);

    InvokeResult<AviationToSendAndSendingListRes> fetchAviationToSendAndSendingList(AviationSendTaskListReq request);

    InvokeResult<AviationSendTaskQueryRes> pageFetchAviationTaskByNextSite(AviationSendTaskQueryReq request);

    InvokeResult<AviationToSealAndSealedListRes> pageFetchAviationToSealAndSealedList(AviationSendTaskSealListReq request);

    JdVerifyResponse<AviationSendScanResp> scan(AviationSendScanReq request);

    InvokeResult<AviationSendVehicleProgressResp> getAviationSendVehicleProgress(AviationSendVehicleProgressReq request);

    InvokeResult<AviationSendAbnormalPackResp> abnormalBarCodeDetail(AviationSendAbnormalPackReq request);

    InvokeResult<AviationBarCodeDetailResp> sendBarCodeDetail(AviationBarCodeDetailReq request);

    InvokeResult<ShuttleSendTaskRes> pageFetchShuttleSendTaskList(ShuttleSendTaskReq request);

    InvokeResult<SendTaskBindQueryRes> queryBindTaskList(SendTaskBindQueryReq request);

    InvokeResult<AviationSealedListRes> pageFetchAviationSealedList(AviationSealedListReq request);

    InvokeResult<AviationBarCodeDetailResp> sendBoxDetail(AviationBoxDetailReq request);

    InvokeResult<Void> aviationSendComplete(AviationSendCompleteReq req);

    InvokeResult<ScanSendCodeValidRes> validateTranCodeAndSendCode(ScanSendCodeValidReq request);

    InvokeResult<ShuttleTaskSealCarQueryRes> fetchToSealShuttleTaskDetail(ShuttleTaskSealCarQueryReq request);

    InvokeResult<PrepareShuttleSealCarRes> prepareShuttleSealCarData(PrepareShuttleSealCarReq request);

    InvokeResult<AviationSendTaskDto> fetchLatestAviationTaskByNextSite(AviationSendTaskQueryReq request);

    boolean batchCodeShuttleSealMark(BatchCodeShuttleSealDto request);

}
