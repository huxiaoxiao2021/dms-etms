package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.SendFlowDisplayEnum;

import java.io.Serializable;

public class SendFlowReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = -2186860688140786034L;

    /**
     * 展示类型
     * @see SendFlowDisplayEnum
     */
    private Integer displayType;

    private Integer taskType;

    public Integer getDisplayType() {
        return displayType;
    }

    public void setDisplayType(Integer displayType) {
        this.displayType = displayType;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }
}
