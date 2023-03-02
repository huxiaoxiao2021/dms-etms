package com.jd.bluedragon.common.domain;

import java.io.Serializable;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/2/2
 * @Description: 运单异常实体 包裹维度
 */
public class WaybillErrorDomain implements Serializable {

    private static final long serialVersionUID = -1L;

    //包裹号  packageCode
    private String packageCode;
    //运单号  waybillCode
    private String waybillCode;
    //订单号  orderId
    private String orderId;
    //收件人姓名 receiverName
    private String receiverName;
    //收件人电话 receiverMobile
    private String receiverMobile;
    //收件人地址 receiverAddress
    private String receiverAddress;
    //商品信息（多个逗号分隔）  goodNames
    private String goodNames;
    //备注
    private String remark;

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getGoodNames() {
        return goodNames;
    }

    public void setGoodNames(String goodNames) {
        this.goodNames = goodNames;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
