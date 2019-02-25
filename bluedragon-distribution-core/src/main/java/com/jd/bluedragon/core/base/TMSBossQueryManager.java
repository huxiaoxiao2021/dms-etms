package com.jd.bluedragon.core.base;

import com.jd.tms.boss.dto.RecyclingBoxDto;

import java.util.List;

public interface TMSBossQueryManager {
    /**
     * 调用运输系统获取青流箱信息
     * @param dto
     * @return
     * @throws Exception
     */
    RecyclingBoxDto getRecyclingBoxFaceInfo(RecyclingBoxDto dto) throws Exception;

        /**
         * 根据流水号获取青流箱号列表
         * @param batchCode
         * @return
         */
    List<String> getRecyclingBoxFaceInfoByBatchCode(String batchCode);
}
