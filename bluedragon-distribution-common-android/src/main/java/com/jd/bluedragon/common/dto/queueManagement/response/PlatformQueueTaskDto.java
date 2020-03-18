package com.jd.bluedragon.common.dto.queueManagement.response;

import java.io.Serializable;

public class PlatformQueueTaskDto implements Serializable {
    private static final long serialVersionUID = -1L;

    private String platformCode;
    private String carCode;
    private String loadType;
    private String queueTaskCode;
    private Integer queueTaskStatus;
    private String queueTaskStatusName;

    public String getPlatformCode() {
        return platformCode;
    }

    public void setPlatformCode(String platformCode) {
        this.platformCode = platformCode;
    }

    public String getCarCode() {
        return carCode;
    }

    public void setCarCode(String carCode) {
        this.carCode = carCode;
    }

    public String getLoadType() {
        return loadType;
    }

    public void setLoadType(String loadType) {
        this.loadType = loadType;
    }

    public String getQueueTaskCode() {
        return queueTaskCode;
    }

    public void setQueueTaskCode(String queueTaskCode) {
        this.queueTaskCode = queueTaskCode;
    }

    public Integer getQueueTaskStatus() {
        return queueTaskStatus;
    }

    public void setQueueTaskStatus(Integer queueTaskStatus) {
        this.queueTaskStatus = queueTaskStatus;
    }

    public String getQueueTaskStatusName() {
        return queueTaskStatusName;
    }

    public void setQueueTaskStatusName(String queueTaskStatusName) {
        this.queueTaskStatusName = queueTaskStatusName;
    }
}
