package com.jd.bluedragon.distribution.jy.service.unload;

import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadScanRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

/**
 * @ClassName IUnloadVehicleService
 * @Description
 * @Author wyh
 * @Date 2022/4/2 17:04
 **/
public interface IUnloadVehicleService {

    /**
     * 卸车扫描
     * @param request
     * @return
     */
    InvokeResult<Long> unloadScan(UnloadScanRequest request);
}
