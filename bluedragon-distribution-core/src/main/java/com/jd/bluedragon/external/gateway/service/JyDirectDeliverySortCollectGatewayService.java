package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.collect.CollectTerminatedRequest;
import com.jd.bluedragon.common.dto.operation.workbench.collect.CollectTerminatedReason;

import java.util.List;

/**
 * 揽收网关
 *
 * @author hujiping
 * @date 2024/3/4 3:07 PM
 */
public interface JyDirectDeliverySortCollectGatewayService {

    /**
     * 查询揽收终止原因
     * 
     * @return
     */
    JdCResponse<List<CollectTerminatedReason>> queryTerminatedReasons();
    
    /**
     * 终止揽收
     * 
     * @param request
     */
    JdCResponse<Void> terminateCollect(CollectTerminatedRequest request);
}
