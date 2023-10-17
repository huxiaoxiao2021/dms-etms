package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;

import java.io.Serializable;

/**
 **/
public class TaskStatusStatistics implements Serializable {

    private static final long serialVersionUID = 168767290763647636L;

    /**
     * 任务状态
     */
    private Integer taskStatus;

    /**
     * 任务状态描述
     */
    private String taskStatusName;

    /**
     * 总数
     */
    private Integer total;

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getTaskStatusName() {
        return taskStatusName;
    }

    public void setTaskStatusName(String taskStatusName) {
        this.taskStatusName = taskStatusName;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
