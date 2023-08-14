package com.jd.bluedragon.distribution.jy.task;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/8/14 17:48
 * @Description
 */
public class JyBizTaskAviationStatusStatistics implements Serializable {

    private static final long serialVersionUID = 168767290763647636L;

    /**
     * 任务状态
     */
    private Integer taskStatus;

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

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
