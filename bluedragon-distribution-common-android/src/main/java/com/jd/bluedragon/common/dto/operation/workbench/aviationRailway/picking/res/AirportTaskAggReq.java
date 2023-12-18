package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class AirportTaskAggReq extends BaseReq implements Serializable {

    private static final long serialVersionUID = -2083002886333203311L;
    /**
     * 机场/车站编码
     */
    private String pickingNodeCode;
    /**
     * 是否无任务分组
     */
    private Boolean noTaskFlag;
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

    private Integer pageNum;

    private Integer pageSize;

    public String getPickingNodeCode() {
        return pickingNodeCode;
    }

    public void setPickingNodeCode(String pickingNodeCode) {
        this.pickingNodeCode = pickingNodeCode;
    }

    public Boolean getNoTaskFlag() {
        return noTaskFlag;
    }

    public void setNoTaskFlag(Boolean noTaskFlag) {
        this.noTaskFlag = noTaskFlag;
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

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
