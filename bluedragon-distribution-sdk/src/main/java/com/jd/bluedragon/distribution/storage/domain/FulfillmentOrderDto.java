package com.jd.bluedragon.distribution.storage.domain;

/**
 * 履约单关系 实体
 */
public class FulfillmentOrderDto {

    //履约单号
    private String fulfillmentOrderId;
    //生产单号
    private String manufactureOrderId;
    //运单号
    private String deliveryOrderId;

    public String getFulfillmentOrderId() {
        return fulfillmentOrderId;
    }

    public void setFulfillmentOrderId(String fulfillmentOrderId) {
        this.fulfillmentOrderId = fulfillmentOrderId;
    }

    public String getManufactureOrderId() {
        return manufactureOrderId;
    }

    public void setManufactureOrderId(String manufactureOrderId) {
        this.manufactureOrderId = manufactureOrderId;
    }

    public String getDeliveryOrderId() {
        return deliveryOrderId;
    }

    public void setDeliveryOrderId(String deliveryOrderId) {
        this.deliveryOrderId = deliveryOrderId;
    }
}
