package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class FinishSendTaskReq extends BaseReq implements Serializable {

    private static final long serialVersionUID = -2293749310307428444L;
    /**
     * 流向场地id
     */
    private Integer nextSiteId;
    /**
     * 批次号
     */
    private String sendCode;

    /**
     * 提货任务类型
     * @see com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodTaskTypeEnum
     */
    private Integer taskType;

    public Integer getNextSiteId() {
        return nextSiteId;
    }

    public void setNextSiteId(Integer nextSiteId) {
        this.nextSiteId = nextSiteId;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }
}
