package com.jd.bluedragon.distribution.waybill.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wangtingwei on 2014/9/17.
 */
public class FWaybillResult implements Serializable {
    /**
     * 返单F运单列表
     */
    private java.util.List<java.lang.String> fCodes;

    /**
     * 新运单号
     */
    private java.lang.String deliveryId;
    /**
     * 收件人
     */
    private java.lang.String receiveName;

    /**
     * 收件人电话
     */
    private java.lang.String receiveMobile;

    /**
     * 收件人地址
     */
    private java.lang.String receiveAddress;

    public List<String> getfCodes() {
        return fCodes;
    }

    public void setfCodes(List<String> fCodes) {
        this.fCodes = fCodes;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getReceiveName() {
        return receiveName;
    }

    public void setReceiveName(String receiveName) {
        this.receiveName = receiveName;
    }

    public String getReceiveMobile() {
        return receiveMobile;
    }

    public void setReceiveMobile(String receiveMobile) {
        this.receiveMobile = receiveMobile;
    }

    public String getReceiveAddress() {
        return receiveAddress;
    }

    public void setReceiveAddress(String receiveAddress) {
        this.receiveAddress = receiveAddress;
    }
}
