package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.recyclematerial.request.ReflowPackageRequest;
import com.jd.bluedragon.common.dto.wastepackagestorage.request.WastePackageJSFRequest;

public interface ReflowPackageGatewayService {

    /**
     * 包裹回流扫描提交
     * @param request
     * @return
     */
    JdCResponse<Boolean> reflowPackageSubmit(ReflowPackageRequest request);

    /**
     * 弃件暂存
     * @param request
     * @return
     */
    JdCResponse<Boolean> wastepackagestorage(WastePackageJSFRequest request);
}
