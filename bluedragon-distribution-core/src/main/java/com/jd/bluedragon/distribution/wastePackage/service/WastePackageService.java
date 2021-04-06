package com.jd.bluedragon.distribution.wastePackage.service;

import com.jd.bluedragon.distribution.api.request.WastePackageRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

/**
 * 弃件暂存
 * @author biyubo
 * @date 2021/3/23
 */
public interface WastePackageService {

    /**
     * 弃件暂存
     * @param request
     * @return
     */
    InvokeResult<Boolean> wastepackagestorage(WastePackageRequest request);

}
