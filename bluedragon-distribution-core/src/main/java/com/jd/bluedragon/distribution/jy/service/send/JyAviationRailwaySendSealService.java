package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req.*;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

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

    InvokeResult<Void> shuttleTaskSealCar(ShuttleTaskSealCarReq request);

    InvokeResult<Void> aviationTaskSealCar(AviationTaskSealCarReq request);

    InvokeResult<AviationToSendAndSendingListRes> fetchAviationToSendAndSendingList(AviationSendTaskListReq request);

    InvokeResult<AviationSendTaskQueryRes> pageFetchAviationTaskByNextSite(AviationSendTaskQueryReq request);

    InvokeResult<AviationToSealAndSealedListRes> fetchAviationToSealAndSealedList(AviationSendTaskSealListReq request);
}
