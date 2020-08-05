package com.jd.bluedragon.common.dto.air.response;

import java.io.Serializable;

/**
 * 运输方式变更-变更原因
 */
public class AirContrabandReason implements Serializable {
    private Integer reasonCode;

    private String reasonName;

    public Integer getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(Integer reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getReasonName() {
        return reasonName;
    }

    public void setReasonName(String reasonName) {
        this.reasonName = reasonName;
    }
}
