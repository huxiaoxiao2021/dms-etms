package com.jd.bluedragon.distribution.seal.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealTaskInfoRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealTaskInfo;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealVehicleTaskResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

/**
 * @ClassName IJySealVehicleService
 * @Description
 * @Author wyh
 * @Date 2022/3/11 14:30
 **/
public interface IJySealVehicleService {

    /**
     * 拉取封车任务
     * @param request
     * @return
     */
    InvokeResult<SealVehicleTaskResponse> fetchSealTask(SealVehicleTaskRequest request);

    /**
     * 封车任务明细
     * @param request
     * @return
     */
    InvokeResult<SealTaskInfo> taskInfo(SealTaskInfoRequest request);
}
