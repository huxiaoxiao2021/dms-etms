package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req.AviationSendTaskListReq;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req.SendTaskBindReq;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req.SendTaskUnbindReq;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res.AviationSendTaskListRes;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 14:54
 * @Description
 */
public interface JyAviationRailwaySendSealService {
    
    InvokeResult<AviationSendTaskListRes> pageFetchAviationSendTaskList(AviationSendTaskListReq request);

    InvokeResult<Void> sendTaskBinding(SendTaskBindReq request);

    InvokeResult<Void> sendTaskUnbinding(SendTaskUnbindReq request);
}
