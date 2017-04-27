package com.jd.bluedragon.distribution.transBillSchedule.domain;

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
     * 此运单数据的派车单号跟箱号的其他运输单号是不是保持一致
     */
    private Boolean isSameScheduleBill;

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

    public Boolean getSameScheduleBill() {
        return isSameScheduleBill;
    }

    public void setSameScheduleBill(Boolean sameScheduleBill) {
        isSameScheduleBill = sameScheduleBill;
    }

    public String getRoadCode() {
        return roadCode;
    }

    public void setRoadCode(String roadCode) {
        this.roadCode = roadCode;
    }
}
