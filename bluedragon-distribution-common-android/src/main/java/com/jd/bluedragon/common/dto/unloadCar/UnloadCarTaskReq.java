package com.jd.bluedragon.common.dto.unloadCar;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * @ClassName: UnloadCarTaskReq
 * @Description: 获取卸车任务入参对象
 * @Author: biyubo
 * @CreateDate: 2020-6-22 10:08
 */

public class UnloadCarTaskReq implements Serializable {
    private static final long serialVersionUID = -1L;

    private String taskCode;

    private Integer taskStatus;

    private User user;

    private CurrentOperate currentOperate;

    private String operateTime;

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

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

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }
}