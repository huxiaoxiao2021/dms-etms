package com.jd.bluedragon.distribution.storage.domain;

public class WaybillOrderFlagMessage {

    private String waybillCode;

    private Integer orderFlag;

    private String fulfillmentOrderId;

    public boolean needDeal(){
        // 现阶段只消费 orderFlag = -1的时候
        // -1 时为剔除运单
        if(orderFlag!=null && orderFlag.equals(new Integer(-1))){
            return true;
        }
        return false;
    }


    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getOrderFlag() {
        return orderFlag;
    }

    public void setOrderFlag(Integer orderFlag) {
        this.orderFlag = orderFlag;
    }

    public String getFulfillmentOrderId() {
        return fulfillmentOrderId;
    }

    public void setFulfillmentOrderId(String fulfillmentOrderId) {
        this.fulfillmentOrderId = fulfillmentOrderId;
    }
}
