package com.jd.bluedragon.distribution.jy.service.unload;

import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadScanRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadVehicleRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.UnloadScanDetail;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

/**
 * @ClassName IUnloadVehicleService
 * @Description
 * @Author wyh
 * @Date 2022/4/2 17:04
 **/
public interface IJyUnloadVehicleService {

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
}
