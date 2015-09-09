package com.jd.bluedragon.distribution.quickProduce.domain;

/**
 * 清单实体类
 */
public class JoinDetail {

    /**
     * 库房号
     */
    private Integer distributeStoreId;

    /**
     * 特殊属性
     */
    private String SendPay;

    /**
     * 预分拣原始站点ID
     */
    private Integer OldSiteId;

    /**
     * 总金额
     */
    private Double Price;

    /**
     * 商品重量
     */
    private Double GoodWeight;

    /**
     * 商品数量
     */
    private Integer GoodNumber;

    /**
     * 支付方式
     */
    private Integer payment;

    /**
     * 收件人地址
     */
    private String receiverAddress;

    /**
     * 收件人手机
     */
    private String receiverMobile;


    /*收件人座机*/
    private String ReceiverTel;

    /**
     * 收件人姓名
     */
    private String receiverName;

    /**
     * 声明价值
     */
    private Double DeclaredValue;

    /**
     * 运单类型
     */
    private Integer WaybillType;

    public Integer getDistributeStoreId() {
        return distributeStoreId;
    }

    public void setDistributeStoreId(Integer distributeStoreId) {
        this.distributeStoreId = distributeStoreId;
    }

    public String getSendPay() {
        return SendPay;
    }

    public void setSendPay(String sendPay) {
        SendPay = sendPay;
    }

    public Integer getOldSiteId() {
        return OldSiteId;
    }

    public void setOldSiteId(Integer oldSiteId) {
        OldSiteId = oldSiteId;
    }

    public Double getPrice() {
        return Price;
    }

    public void setPrice(Double price) {
        Price = price;
    }

    public Double getGoodWeight() {
        return GoodWeight;
    }

    public void setGoodWeight(Double goodWeight) {
        GoodWeight = goodWeight;
    }

    public Integer getGoodNumber() {
        return GoodNumber;
    }

    public void setGoodNumber(Integer goodNumber) {
        GoodNumber = goodNumber;
    }

    public Integer getPayment() {
        return payment;
    }

    public void setPayment(Integer payment) {
        this.payment = payment;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    public String getReceiverTel() {
        return ReceiverTel;
    }

    public void setReceiverTel(String receiverTel) {
        ReceiverTel = receiverTel;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public Double getDeclaredValue() {
        return DeclaredValue;
    }

    public void setDeclaredValue(Double declaredValue) {
        DeclaredValue = declaredValue;
    }

    public Integer getWaybillType() {
        return WaybillType;
    }

    public void setWaybillType(Integer waybillType) {
        WaybillType = waybillType;
    }
}
