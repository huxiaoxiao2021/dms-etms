package com.jd.bluedragon.distribution.transBillSchedule.domain;

/**
 * Created by wuzuxiang on 2017/4/26.
 */
public class TransBillScheduleRequest {

    /**
     * 箱号
     */
    private String boxCode;

    /**
     * 运单号
     */
    private String waybillCode;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    @Override
    public String toString() {
        return "TransBillScheduleRequest{" + "boxCode='" + boxCode + '\'' + ", waybillCode='" + waybillCode + '\'' + '}';
    }
}
