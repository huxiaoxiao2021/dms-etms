package com.jd.bluedragon.distribution.jy.service.unseal;

import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealCodeRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealTaskInfoRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealCodeResponse;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealTaskInfo;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealVehicleTaskResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;

/**
 * @ClassName IJyUnSealVehicleService
 * @Description
 * @Author wyh
 * @Date 2022/3/11 14:30
 **/
public interface IJyUnSealVehicleService {

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


    /**
     * 封签号列表
     * @param request
     * @return
     */
    InvokeResult<SealCodeResponse> sealCodeList(SealCodeRequest request);


    /**
     * 创建卸车任务
     * @param entity
     * @return
     */
    boolean createUnSealTask(JyBizTaskUnloadVehicleEntity entity);
}
