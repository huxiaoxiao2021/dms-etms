package com.jd.bluedragon.distribution.transport.domain;

/**
 * @author lijie
 * @date 2020/3/26 15:44
 */
public class ArContrabandReason {

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

    @Override
    public String toString() {
        return "ArContrabandReason{" +
                "reasonCode=" + reasonCode +
                ", reasonName='" + reasonName + '\'' +
                '}';
    }
}
