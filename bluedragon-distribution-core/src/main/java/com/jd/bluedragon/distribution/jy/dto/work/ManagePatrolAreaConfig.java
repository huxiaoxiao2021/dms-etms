package com.jd.bluedragon.distribution.jy.dto.work;

import lombok.Data;

@Data
public class ManagePatrolAreaConfig {
    private String taskCode;
    private String areaCode;
    /**
     * 要生成网格数量
     */
    private Integer gridQuality;
}
