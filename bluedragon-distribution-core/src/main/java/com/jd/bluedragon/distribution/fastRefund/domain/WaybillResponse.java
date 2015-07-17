package com.jd.bluedragon.distribution.fastRefund.domain;

import com.jd.bluedragon.distribution.api.JdResponse;


public class WaybillResponse extends JdResponse {

    private static final long serialVersionUID = 6909288367509971554L;
    
    /**
     * 运单编号
     */
    private String waybillCode;

    /**
     * POP商家ID
     */
    private Integer popSupId;

    /**
     * POP商家名称
     */
    private String popSupName;

    /**
     * 站点编号
     */
    private Integer siteCode = 0;

    /**
     * 站点名称
     */
    private String siteName;

    /**
     * 支付类型
     */
    private Integer paymentType = 0;

    /**
     * 特殊属性
     */
    private String sendPay;

    /**
     * 重量
     */
    private Double weight;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 地址
     */
    private String address;

    /** 机构ID */
    private Integer orgId;

    /** 运单类型(JYN) */
    private Integer type;
    
    /**
     * 中转站编号
     */
    private Integer transferStationId;
    
    public static final Integer CODE_NOT_EXISTS = 10001;
    public static final String MESSAGE_NOT_EXISTS = "无此订单信息";
    
    public static final Integer CODE_NONVIABLE = 10002;
    public static final String MESSAGE_NONVIABLE = "无有效数据";

    public WaybillResponse() {
        super();
    }

    public WaybillResponse(Integer code, String message) {
        super(code, message);
    }

    public String getWaybillCode() {
        return this.waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getPopSupId() {
        return this.popSupId;
    }

    public void setPopSupId(Integer popSupId) {
        this.popSupId = popSupId;
    }

    public String getPopSupName() {
        return this.popSupName;
    }

    public void setPopSupName(String popSupName) {
        this.popSupName = popSupName;
    }

    public Integer getSiteCode() {
        return this.siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return this.siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getPaymentType() {
        return this.paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    public String getSendPay() {
        return this.sendPay;
    }

    public void setSendPay(String sendPay) {
        this.sendPay = sendPay;
    }

    public Double getWeight() {
        return this.weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getOrgId() {
        return this.orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
    
    public Integer getTransferStationId() {
        return this.transferStationId;
    }
    
    public void setTransferStationId(Integer transferStationId) {
        this.transferStationId = transferStationId;
    }
}
