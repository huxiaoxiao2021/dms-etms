package com.jd.bluedragon.distribution.jy.dto.pickinggood;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class AirRailTaskCountQueryDto implements Serializable {
    private static final long serialVersionUID = 4869220727220937779L;

    private long pickingSiteId;

    private Integer status;

    private Integer taskType;

    private Date createTime;

    private List<String> bizIdList;

    /**
     * 预计降落时间/预计到达时间
     */
    private Date nodePlanArriveTime;
    /**
     * 实际降落时间/实际到达时间
     */
    private Date nodeRealArriveTime;
    /**
     * 提货完成时间
     */
    private Date pickingCompleteTime;

    public long getPickingSiteId() {
        return pickingSiteId;
    }

    public void setPickingSiteId(long pickingSiteId) {
        this.pickingSiteId = pickingSiteId;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<String> getBizIdList() {
        return bizIdList;
    }

    public void setBizIdList(List<String> bizIdList) {
        this.bizIdList = bizIdList;
    }

    public Date getNodePlanArriveTime() {
        return nodePlanArriveTime;
    }

    public void setNodePlanArriveTime(Date nodePlanArriveTime) {
        this.nodePlanArriveTime = nodePlanArriveTime;
    }

    public Date getNodeRealArriveTime() {
        return nodeRealArriveTime;
    }

    public void setNodeRealArriveTime(Date nodeRealArriveTime) {
        this.nodeRealArriveTime = nodeRealArriveTime;
    }

    public Date getPickingCompleteTime() {
        return pickingCompleteTime;
    }

    public void setPickingCompleteTime(Date pickingCompleteTime) {
        this.pickingCompleteTime = pickingCompleteTime;
    }
}
