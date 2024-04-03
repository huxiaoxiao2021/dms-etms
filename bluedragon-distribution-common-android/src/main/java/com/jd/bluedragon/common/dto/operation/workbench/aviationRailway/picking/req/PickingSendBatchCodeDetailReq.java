package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2024/4/2 14:35
 * @Description
 */
public class PickingSendBatchCodeDetailReq extends BaseReq implements Serializable {

    private static final long serialVersionUID = -2083002886333203311L;
    /**
     * 任务类型
     * com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodTaskTypeEnum
     */
    private Integer taskType;
    /**
     * 下一流向场地Id
     */
    private Integer nextSiteId;
    /**
     * 查询批次状态（为空默认查询2-待封车批次）
     * com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.JyPickingSendBatchCodeStatusEnum
     */
    private Integer batchCodeStatus;
    //页码【1，++】
    private Integer pageNum;
    //页容量 [1,100]
    private Integer pageSize;


    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Integer getNextSiteId() {
        return nextSiteId;
    }

    public void setNextSiteId(Integer nextSiteId) {
        this.nextSiteId = nextSiteId;
    }

    public Integer getBatchCodeStatus() {
        return batchCodeStatus;
    }

    public void setBatchCodeStatus(Integer batchCodeStatus) {
        this.batchCodeStatus = batchCodeStatus;
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
