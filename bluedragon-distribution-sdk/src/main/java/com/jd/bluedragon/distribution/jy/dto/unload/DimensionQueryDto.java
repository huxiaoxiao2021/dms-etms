package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;

public class DimensionQueryDto extends UnloadBaseDto implements Serializable {
    private static final long serialVersionUID = 4185711426354946641L;

    /**
     * UnloadStatisticsQueryTypeEnum 查询类型
     */
    private Integer type;

    private String bizId;//按任务维度查询

    private String boardCode;//按板维度查询

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }
}
