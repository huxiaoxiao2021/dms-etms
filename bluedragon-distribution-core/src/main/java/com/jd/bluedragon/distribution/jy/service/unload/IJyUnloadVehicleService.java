package com.jd.bluedragon.distribution.jy.service.unload;

import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadScanRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadVehicleRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.UnloadScanDetail;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.UnloadVehicleTaskResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;

/**
 * @ClassName IUnloadVehicleService
 * @Description
 * @Author wyh
 * @Date 2022/4/2 17:04
 **/
public interface IJyUnloadVehicleService {

    /**
     * 拉取卸车任务
     * @param request
     * @return
     */
    InvokeResult<UnloadVehicleTaskResponse> fetchUnloadTask(UnloadVehicleTaskRequest request);

    /**
     * 卸车扫描
     * @param request
     * @return
     */
    InvokeResult<Integer> unloadScan(UnloadScanRequest request);

    /**
     * 查询卸车进度
     * @param request
     * @return
     */
    InvokeResult<UnloadScanDetail> unloadDetail(UnloadVehicleRequest request);

    /**
     * 创建卸车任务
     * @param entity
     * @return
     */
    boolean createUnloadTask(JyBizTaskUnloadVehicleEntity entity);
}
