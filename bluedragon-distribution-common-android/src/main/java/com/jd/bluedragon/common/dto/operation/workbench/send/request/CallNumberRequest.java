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
     * 派车单明细
     */
    private String transWorkItemCode;

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

    public String getTransWorkItemCode() {
        return transWorkItemCode;
    }

    public void setTransWorkItemCode(String transWorkItemCode) {
        this.transWorkItemCode = transWorkItemCode;
    }
}
