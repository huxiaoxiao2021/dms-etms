package com.jd.bluedragon.distribution.api.response;

/**
 * Created by wuzuxiang on 2017/4/26.
 */
public class TransBillScheduleResponse {

    /**
     * 箱号
     */
    private String boxCode;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 派车单号
     */
    private String scheduleCode;

    /**
     * 此运单数据的派车单号跟箱号的其他运输单号是不是保持一致
     */
    private Boolean sameScheduleBill;

    /**
     * 此运单号的路区编号
     */
    private String roadCode;

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

    public String getScheduleCode() {
        return scheduleCode;
    }

    public void setScheduleCode(String scheduleCode) {
        this.scheduleCode = scheduleCode;
    }

    public Boolean getSameScheduleBill() {
        return sameScheduleBill;
    }

    public void setSameScheduleBill(Boolean sameScheduleBill) {
        this.sameScheduleBill = sameScheduleBill;
    }

    public String getRoadCode() {
        return roadCode;
    }

    public void setRoadCode(String roadCode) {
        this.roadCode = roadCode;
    }
}
