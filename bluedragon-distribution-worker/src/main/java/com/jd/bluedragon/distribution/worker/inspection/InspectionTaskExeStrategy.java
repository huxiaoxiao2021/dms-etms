package com.jd.bluedragon.distribution.worker.inspection;

import com.jd.bluedragon.distribution.api.request.InspectionRequest;

/**
 * @ClassName InspectionTaskExeStrategy
 * @Description
 * @Author wyh
 * @Date 2020/9/24 20:59
 **/
public interface InspectionTaskExeStrategy {

    InspectionTaskExecutor decideExecutor(InspectionRequest request);
}
