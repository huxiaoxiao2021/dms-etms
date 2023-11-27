package com.jd.bluedragon.distribution.jy.dto.work;

import com.jd.bluedragon.common.dto.work.BusinessQuotaInfoData;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TaskScanSiteInfo {
    /**
     * 分拣中心id
     */
    private List<Integer> siteCodes;
    /**
     * 场地对应的任务扩展信息
     */
    private Map<Integer, BusinessQuotaInfoData> businessQuotaInfoDataMap;
}
