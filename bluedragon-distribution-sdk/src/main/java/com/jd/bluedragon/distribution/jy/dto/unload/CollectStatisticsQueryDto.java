package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;

public class CollectStatisticsQueryDto extends UnloadBaseDto implements Serializable {
    private static final long serialVersionUID = 4185711426354946641L;

    private String bizId;//任务bizId

    private Boolean manualCreateTaskFlag;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Boolean getManualCreateTaskFlag() {
        return manualCreateTaskFlag;
    }

    public void setManualCreateTaskFlag(Boolean manualCreateTaskFlag) {
        this.manualCreateTaskFlag = manualCreateTaskFlag;
    }
}
