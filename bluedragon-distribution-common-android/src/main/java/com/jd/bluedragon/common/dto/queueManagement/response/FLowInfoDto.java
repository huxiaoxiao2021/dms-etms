package com.jd.bluedragon.common.dto.queueManagement.response;

import java.io.Serializable;

public class FLowInfoDto implements Serializable {
    private static final long serialVersionUID = -1L;

    private String flowCode;
    private String flowName;

    public String getFlowCode() {
        return flowCode;
    }

    public void setFlowCode(String flowCode) {
        this.flowCode = flowCode;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }
}
