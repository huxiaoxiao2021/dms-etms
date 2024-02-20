package com.jd.bluedragon.distribution.jy.dto.pickinggood;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class JyPickingTaskGroupQueryDto implements Serializable {
    private static final long serialVersionUID = 1092594866188253310L;
    private String bizId;
    /**
     * 提货场地id
     */
    private Long pickingSiteId;
    /**
     * 提货机场编码/提货车站编码
     */
    private String pickingNodeCode;
    /**
     * 任务状态
     * @see com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodStatusEnum
     */
    private Integer status;
    /**
     * 提货任务类型
     * @see com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodTaskTypeEnum
     */
    private Integer taskType;

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

    private List<String> bizIdList;

    private Date createTime;

    private Integer limit;

    private Integer offset;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Long getPickingSiteId() {
        return pickingSiteId;
    }

    public void setPickingSiteId(Long pickingSiteId) {
        this.pickingSiteId = pickingSiteId;
    }

    public String getPickingNodeCode() {
        return pickingNodeCode;
    }

    public void setPickingNodeCode(String pickingNodeCode) {
        this.pickingNodeCode = pickingNodeCode;
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

    public List<String> getBizIdList() {
        return bizIdList;
    }

    public void setBizIdList(List<String> bizIdList) {
        this.bizIdList = bizIdList;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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
