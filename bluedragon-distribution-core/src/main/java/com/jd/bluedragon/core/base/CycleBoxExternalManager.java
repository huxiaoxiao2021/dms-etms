package com.jd.bluedragon.core.base;

import java.util.List;

public interface CycleBoxExternalManager {
    /**
     * 根据运单号获取青流箱明细
     * @param waybillCode
     * @return
     */
    List<String> getCbUniqueNoByWaybillCode(String waybillCode);
}
