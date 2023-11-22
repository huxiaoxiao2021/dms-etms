package com.jd.bluedragon.core.jsf.collectpackage.dto;

public class StatisticsUnderTaskQueryDto {

    /**
     * 箱号
     */
    private String boxCode;
    /**
     * 任务bizId
     */
    private String bizId;
    /**
     * 类型
     */
    private Integer type;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
