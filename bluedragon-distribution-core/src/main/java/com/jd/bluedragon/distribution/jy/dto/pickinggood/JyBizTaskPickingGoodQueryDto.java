package com.jd.bluedragon.distribution.jy.dto.pickinggood;

import java.io.Serializable;
import java.util.Date;

public class JyBizTaskPickingGoodQueryDto implements Serializable {
    private static final long serialVersionUID = 267084569474367674L;
    /**
     * 查询开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 提货任务开始时间
     */
    private Date pickingStartTime;
    /**
     * 任务状态
     */
    private Integer status;

    /**
     * 任务类型
     */
    private Integer taskType;

    private Integer limit = 200;

    private Integer offset = 0;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getPickingStartTime() {
        return pickingStartTime;
    }

    public void setPickingStartTime(Date pickingStartTime) {
        this.pickingStartTime = pickingStartTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}
