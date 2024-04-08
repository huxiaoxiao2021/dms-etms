package com.jd.bluedragon.distribution.collect.service;

import com.jd.bluedragon.common.dto.operation.workbench.collect.CollectTerminatedReason;
import com.jd.bluedragon.common.dto.operation.workbench.collect.CollectTerminatedRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.collect.domain.DirectDeliverySortCollectRequest;

import java.util.List;

/**
 * 直送分拣揽收服务
 *
 * @author hujiping
 * @date 2024/2/29 10:29 AM
 */
public interface DirectDeliverySortCollectWaybillService {

    /**
     * 查询揽收终止原因
     *
     * @return
     */
    List<CollectTerminatedReason> queryTerminatedReasons();

    /**
     * 终止揽收
     *
     * @param request
     */
    InvokeResult<Void> terminateCollect(CollectTerminatedRequest request);

    /**
     * 直送分拣揽收校验
     * 
     * @param request
     * @return
     */
    InvokeResult<Void> directDeliverySortCollectCheck(DirectDeliverySortCollectRequest request);

    /**
     * 直送分拣揽收
     *
     * @param request
     * @return
     */
    InvokeResult<Void> directDeliverySortCollect(DirectDeliverySortCollectRequest request);
}
