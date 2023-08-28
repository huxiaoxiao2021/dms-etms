package com.jd.bluedragon.common.dto.operation.workbench.send.request;

import java.io.Serializable;

public class CallNumberRequest implements Serializable {
    private static final long serialVersionUID = -8022666524337551167L;

    /**
     * 提交场地
     */
    private Integer callSiteCode;

    /**
     * 提交人erp
     */
    private String callUserErp;

    /**
     * 发货明细流向bizId
     */
    private String bizId;

    public Integer getCallSiteCode() {
        return callSiteCode;
    }

    public void setCallSiteCode(Integer callSiteCode) {
        this.callSiteCode = callSiteCode;
    }

    public String getCallUserErp() {
        return callUserErp;
    }

    public void setCallUserErp(String callUserErp) {
        this.callUserErp = callUserErp;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }
}
