package com.jd.bluedragon.common.dto.unloadCar;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * @ClassName: TaskHelpersReq
 * @Description: 任务协助人修改入参对象
 * @Author: biyubo
 * @CreateDate: 2020-6-22 21:24
 */

public class TaskHelpersReq implements Serializable {
    private static final long serialVersionUID = -1L;

    private String taskCode;

    private Integer operateType;

    private String helperERP;

    private String helperName;

    private User user;

    private CurrentOperate currentOperate;

    private String operateTime;

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public String getHelperERP() {
        return helperERP;
    }

    public void setHelperERP(String helperERP) {
        this.helperERP = helperERP;
    }

    public String getHelperName() {
        return helperName;
    }

    public void setHelperName(String helperName) {
        this.helperName = helperName;
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