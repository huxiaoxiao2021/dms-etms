package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/8/4 16:52
 * @Description
 */
public class ScanAndCheckTransportInfoReq extends BaseReq implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;

    /**
     * 运力编码
     */
    private String transportCode;

    private Integer nextSiteId;
    /**
     * SendTaskTypeEnum
      */
    private Integer taskType;
    /**
     * 中转属性开关
     * true: 开启中转属性确认校验
     * false: 不校验中转属性逻辑
     */
    private Boolean temporaryTransferSwitch;

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public Integer getNextSiteId() {
        return nextSiteId;
    }

    public void setNextSiteId(Integer nextSiteId) {
        this.nextSiteId = nextSiteId;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Boolean getTemporaryTransferSwitch() {
        return temporaryTransferSwitch;
    }

    public void setTemporaryTransferSwitch(Boolean temporaryTransferSwitch) {
        this.temporaryTransferSwitch = temporaryTransferSwitch;
    }
}
