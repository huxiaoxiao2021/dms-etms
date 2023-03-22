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
public class AutoCloseTaskPo implements Serializable {
    private static final long serialVersionUID = -4489590854270263804L;

    /**
     * 任务类型，解封车、卸车、发货、封车任务等
     */
    private Integer taskBusinessType;

    /**
     * 任务bizId
     */
    private String bizId;

    private Task task;

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

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public String toString() {
        return "AutoCloseTaskPo{" +
                "taskBusinessType=" + taskBusinessType +
                ", bizId='" + bizId + '\'' +
                ", task=" + task +
                '}';
    }
}
