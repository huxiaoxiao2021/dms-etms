package com.jd.bluedragon.distribution.recycle.material.service;

import com.jd.bluedragon.distribution.recycle.material.domain.RecycleBasketAbolishRequest;
import com.jd.bluedragon.distribution.recycle.material.domain.RecycleBasketEntity;
import com.jd.bluedragon.distribution.recycle.material.domain.RecycleBasketPrintInfo;
import com.jd.bluedragon.sdk.modules.recyclematerial.dto.MaterialAbolishReq;
import com.jd.ql.dms.common.domain.JdResponse;

public interface RecycleMaterialService {
    JdResponse<RecycleBasketPrintInfo> getPrintInfo(RecycleBasketEntity recycleBasketEntity);

    /**
     * 作废 周转筐条码
     */
    JdResponse<RecycleBasketPrintInfo> disableAkBox(RecycleBasketEntity recycleBasketEntity);

    /**
     * 批量作废
     * 
     * @param request
     * @return
     */
    JdResponse<Boolean> batchAbolishRecycleBasket(RecycleBasketAbolishRequest request);

    /**
     * 异步作废
     * 
     * @param materialAbolishReq
     */
    void syncAbolishRecycleBasket(MaterialAbolishReq materialAbolishReq);
}
