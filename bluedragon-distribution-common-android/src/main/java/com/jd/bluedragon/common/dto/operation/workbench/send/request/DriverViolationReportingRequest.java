package com.jd.bluedragon.common.dto.operation.workbench.send.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * @author pengchong28
 * @description 司机违规举报请求参数
 * @date 2024/4/12
 */
public class DriverViolationReportingRequest implements Serializable {
    /**
     * 登录用户
     */
    private User user;

    /**
     * 操作场地
     */
    private CurrentOperate currentOperate;
    /**
     * 发车任务ID，业务主键
     */
    private String bizId;
    /**
     * 发车任务状态，2：待封车。3：已封车
     */
    private Integer taskSendStatus;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Integer getTaskSendStatus() {
        return taskSendStatus;
    }

    public void setTaskSendStatus(Integer taskSendStatus) {
        this.taskSendStatus = taskSendStatus;
    }
}
