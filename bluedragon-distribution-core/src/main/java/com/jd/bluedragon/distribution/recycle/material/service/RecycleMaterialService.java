package com.jd.bluedragon.distribution.recycle.material.service;

import com.jd.bluedragon.distribution.api.request.material.collectionbag.CollectionBagRequest;
import com.jd.bluedragon.distribution.api.request.material.recyclingbox.RecyclingBoxInOutboundRequest;
import com.jd.bluedragon.distribution.api.request.material.warmbox.WarmBoxOutboundRequest;
import com.jd.bluedragon.distribution.api.response.material.recyclingbox.RecyclingBoxInOutResponse;
import com.jd.bluedragon.distribution.api.response.material.warmbox.WarmBoxInOutResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.recycle.material.domain.RecycleBasketEntity;
import com.jd.bluedragon.distribution.recycle.material.domain.RecycleBasketPrintInfo;
import com.jd.ql.dms.common.domain.JdResponse;

public interface RecycleMaterialService {
    JdResponse<RecycleBasketPrintInfo> getPrintInfo(RecycleBasketEntity recycleBasketEntity);

    /**
     * 集包袋发空袋
     * @param request
     * @return
     */
    JdResult<Boolean>  CollectionBagOperationSend(CollectionBagRequest request);

    /**
     * 保温箱出库
     * @param request
     * @return
     */
    JdResult<WarmBoxInOutResponse> warmBoxOutbound(WarmBoxOutboundRequest request);

    /**
     * 清流箱出库
     * @param request
     */
    JdResult<RecyclingBoxInOutResponse> recyclingBoxOutbound(RecyclingBoxInOutboundRequest request);
}
