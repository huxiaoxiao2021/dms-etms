package com.jd.bluedragon.common.dto.collectpackage.response;

import java.io.Serializable;

/**
 * @author liwenji
 * @description 
 * @date 2023-10-12 17:44
 */
public class CollectPackStatusCount implements Serializable {

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
    private Long total;

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

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
