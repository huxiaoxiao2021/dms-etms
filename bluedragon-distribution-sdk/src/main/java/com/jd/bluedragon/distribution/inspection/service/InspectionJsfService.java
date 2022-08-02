package com.jd.bluedragon.distribution.inspection.service;

import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.TaskResponse;
import com.jd.bluedragon.distribution.coldChain.domain.InspectionVO;
import com.jd.bluedragon.distribution.inspection.InspectionBizSourceEnum;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.ql.dms.common.domain.JdResponse;

import java.util.List;

public interface InspectionJsfService {
    InvokeResult<Boolean> inspection(InspectionVO vo, InspectionBizSourceEnum inspectionBizSourceEnum);

    InvokeResult<Void> autoAddInspectionTask(TaskRequest inspectionTaskRequest);
}
