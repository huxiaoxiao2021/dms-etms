package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.search.request.PackBoxRequest;
import com.jd.bluedragon.common.dto.search.response.PackBoxResponse;

import java.util.List;

/**
 * 安卓查询接口服务类
 */
public interface SearchGateWayService {

    /**
     * 根据运单号或包裹号查询箱号包裹信息
     * @param request
     * @return
     */
    JdCResponse<List<PackBoxResponse>> getBoxPackList(PackBoxRequest request);

}
