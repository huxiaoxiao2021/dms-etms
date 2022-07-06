package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;

public class DimensionQueryDto extends UnloadBaseDto implements Serializable {
    private static final long serialVersionUID = 4185711426354946641L;

    /**
     * DimensionQueryTypeEnum 查询维度
     */
    private Integer type;

    private String bizId;
}
