package com.jd.bluedragon.distribution.recycle.material.service;

import com.jd.bluedragon.distribution.recycle.material.domain.RecycleBasketEntity;
import com.jd.bluedragon.distribution.recycle.material.domain.RecycleBasketPrintInfo;
import com.jd.ql.dms.common.domain.JdResponse;

public interface RecycleMaterialService {
    JdResponse<RecycleBasketPrintInfo> getPrintInfo(RecycleBasketEntity recycleBasketEntity);
}
