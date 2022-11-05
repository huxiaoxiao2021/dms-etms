package com.jd.bluedragon.distribution.jy.dto.exception;

import java.io.Serializable;

/**
 * 拣运异常岗-提报异常
 */
public class JyExpTaskMessage implements Serializable {
    // 任务类型
    private String taskType;
    // 任务状态
    private Integer taskStatus;
    // 业务主键
    private String bizId;
    // 操作人
    private String opeUser;

    private String opeUserName;
    // 操作时间
    private Long opeTime;

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getOpeUser() {
        return opeUser;
    }

    public void setOpeUser(String opeUser) {
        this.opeUser = opeUser;
    }

    public String getOpeUserName() {
        return opeUserName;
    }

    public void setOpeUserName(String opeUserName) {
        this.opeUserName = opeUserName;
    }

    public Long getOpeTime() {
        return opeTime;
    }

    public void setOpeTime(Long opeTime) {
        this.opeTime = opeTime;
    }
}
