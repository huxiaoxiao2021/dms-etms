package com.jd.bluedragon.distribution.jy.dto.pickinggood;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class JyPickingTaskBatchUpdateDto implements Serializable {
    private static final long serialVersionUID = 6564607030422596333L;

    private List<String> bizIdList;

    private Integer status;
    /**
     * @see com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodTaskTypeEnum
     */
    private Integer taskType;

    /**
     * 提货完成触发环节
     */
    private Integer completeNode;

    private Date updateTime;
    /**
     * 提货完成时间
     */
    private Date pickingCompleteTime;

    public List<String> getBizIdList() {
        return bizIdList;
    }

    public void setBizIdList(List<String> bizIdList) {
        this.bizIdList = bizIdList;
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

    public Integer getCompleteNode() {
        return completeNode;
    }

    public void setCompleteNode(Integer completeNode) {
        this.completeNode = completeNode;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getPickingCompleteTime() {
        return pickingCompleteTime;
    }

    public void setPickingCompleteTime(Date pickingCompleteTime) {
        this.pickingCompleteTime = pickingCompleteTime;
    }
}
