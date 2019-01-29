package com.jd.bluedragon.core.base;

import java.util.List;

public interface BossQueryManager {
    /**
     * 根据流水号获取青流箱号列表
     * @param batchCode
     * @return
     */
    List<String> getRecyclingBoxFaceInfoByBatchCode(String batchCode);
}
