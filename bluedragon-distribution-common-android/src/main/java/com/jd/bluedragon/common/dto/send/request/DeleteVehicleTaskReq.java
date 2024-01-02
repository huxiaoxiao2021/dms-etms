package com.jd.bluedragon.common.dto.send.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class DeleteVehicleTaskReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = 2151425704342449497L;
    /**
     * 业务主键（主任务号）
     */
    private String bizId;

    /**
     * 是否取消发货
     */
    private Boolean cancelSendFlag;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Boolean getCancelSendFlag() {
        return cancelSendFlag;
    }

    public void setCancelSendFlag(Boolean cancelSendFlag) {
        this.cancelSendFlag = cancelSendFlag;
    }
}
