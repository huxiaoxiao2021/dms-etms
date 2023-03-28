package com.jd.bluedragon.distribution.jy.service.task.autoclose.dto;

import com.jd.bluedragon.distribution.task.domain.Task;

import java.io.Serializable;

/**
 * 作业工作台-自动关闭任务入参
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-01-31 16:46:33 周二
 */
public class AutoCloseTaskMq implements Serializable {

    private static final long serialVersionUID = -4576318467164943434L;

    private Integer taskBusinessType;

    /**
     * 任务bizId
     */
    private String bizId;

    private Integer changeStatus;

    /**
     * 操作时间
     */
    private Long operateTime;

    public Integer getTaskBusinessType() {
        return taskBusinessType;
    }

    public void setTaskBusinessType(Integer taskBusinessType) {
        this.taskBusinessType = taskBusinessType;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Integer getChangeStatus() {
        return changeStatus;
    }

    public void setChangeStatus(Integer changeStatus) {
        this.changeStatus = changeStatus;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }
}
