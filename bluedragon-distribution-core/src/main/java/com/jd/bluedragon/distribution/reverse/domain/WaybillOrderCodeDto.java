package com.jd.bluedragon.distribution.reverse.domain;

public class WaybillOrderCodeDto {
    /**
     * 新运单号
     */
    private String newWaybillCode;
    /**
     * 老运单号
     */
    private String oldWaybillCode;
    /**
     * 订单号
     */
    private String orderId;

    public String getNewWaybillCode() {
        return newWaybillCode;
    }

    public void setNewWaybillCode(String newWaybillCode) {
        this.newWaybillCode = newWaybillCode;
    }

    public String getOldWaybillCode() {
        return oldWaybillCode;
    }

    public void setOldWaybillCode(String oldWaybillCode) {
        this.oldWaybillCode = oldWaybillCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
