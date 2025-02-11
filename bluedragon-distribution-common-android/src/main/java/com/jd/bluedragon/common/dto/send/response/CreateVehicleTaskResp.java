package com.jd.bluedragon.common.dto.send.response;

import java.io.Serializable;

public class CreateVehicleTaskResp implements Serializable {
    private static final long serialVersionUID = 9046473864156990011L;
    /**
     * 业务主键(主任务)
     */
    private String bizId;
    /**
     * 自建类型任务编号（页面展示自建 1、自建2...）
     */
    private String bizNo;

    private String taskName;

    private String createUserErp;

    /**
     * 是否有相同流向的发货任务
     */
    public Boolean hasSameDestinationTask;

    /**
     * 是否有相同流向的TMS运输任务
     */
    public Boolean hasSameDestinationTaskOfTms;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getBizNo() {
        return bizNo;
    }

    public void setBizNo(String bizNo) {
        this.bizNo = bizNo;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public Boolean getHasSameDestinationTask() {
        return hasSameDestinationTask;
    }

    public void setHasSameDestinationTask(Boolean hasSameDestinationTask) {
        this.hasSameDestinationTask = hasSameDestinationTask;
    }

    public Boolean getHasSameDestinationTaskOfTms() {
        return hasSameDestinationTaskOfTms;
    }

    public void setHasSameDestinationTaskOfTms(Boolean hasSameDestinationTaskOfTms) {
        this.hasSameDestinationTaskOfTms = hasSameDestinationTaskOfTms;
    }
}
