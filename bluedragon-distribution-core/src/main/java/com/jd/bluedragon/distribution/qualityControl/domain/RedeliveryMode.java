package com.jd.bluedragon.distribution.qualityControl.domain;

/**
 * Created by biyubo on 2019/8/31
 * 协商再投状态校验出参对象
 */
public class RedeliveryMode {
    /**
     * 是否已完成
     */
    public boolean isCompleted;

    /**
     * 协商再投未完成的运单号
     */
    public String waybillCode;

    public boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }
}
