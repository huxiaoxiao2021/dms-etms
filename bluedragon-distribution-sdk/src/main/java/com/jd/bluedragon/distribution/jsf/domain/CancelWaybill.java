package com.jd.bluedragon.distribution.jsf.domain;

public class CancelWaybill implements java.io.Serializable {

    public static final Integer FEATURE_TYPE_NORMAL = 1; // 正常解锁订单
    public static final Integer FEATURE_TYPE_CANCELED = -1; // 取消订单
    public static final Integer FEATURE_TYPE_DELETED = -2; // 删除订单
    public static final Integer FEATURE_TYPE_LOCKED = -3; // 锁定订单
    public static final Integer FEATURE_TYPE_REFUND100 = 3; // 退款100分订单
    public static final Integer FEATURE_TYPE_SICKCANCEL = 30; // 病单
    public static final Integer FEATURE_TYPE_INTERCEPT = 4; // 外单拦截订单
    public static final Integer FEATURE_TYPE_INTERCEPT_BUSINESS = 5; // B商家外单拦截订单

    public static final String FEATURE_MSG_UNNORMAL = "LOCKED_OR_CANCELED";
    public static final String FEATURE_MSG_CANCELED = "CANCELED";
    public static final String FEATURE_MSG_DELETED = "DELETED";
    public static final String FEATURE_MSG_LOCKED = "LOCKED";
    public static final String FEATURE_MSG_REFUND100 = "REFUND100";
    public static final String FEATURE_MSG_SICKCANCEL = "SICKCANCEL";
    public static final String FEATURE_MSG_INTERCEPT = "INTERCEPT";
    public static final String FEATURE_MSG_INTERCEPT_BUSINESS = "INTERCEPT_BUSINESS";
    private static final long serialVersionUID = 4711924279615099679L;


    private String waybillCode;
    /**
     * 预分拣站点
     **/
    private Integer siteCode;

    private Integer featureType;
    /**
     * 订单的省ID, 用于定位目的分拣中心的DB
     **/
    private Integer provinceId;

    private String sendPay;
    /**
     * 订单状态
     **/
    private Integer waybillFlag;

    private String businessType;//业务类型

    private Integer exceptionId;

    private String operatTime;

    private Long operateTimeOrder;

    private Long ts;

    private String messageType;

    public String getWaybillCode() {
        return this.waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getFeatureType() {
        return this.featureType;
    }

    public void setFeatureType(Integer featureType) {
        this.featureType = featureType;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public String getSendPay() {
        return sendPay;
    }

    public void setSendPay(String sendPay) {
        this.sendPay = sendPay;
    }

    public Integer getWaybillFlag() {
        return waybillFlag;
    }

    public void setWaybillFlag(Integer waybillFlag) {
        this.waybillFlag = waybillFlag;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Integer getExceptionId() {
        return exceptionId;
    }

    public void setExceptionId(Integer exceptionId) {
        this.exceptionId = exceptionId;
    }

    public String getOperatTime() {
        return operatTime;
    }

    public void setOperatTime(String operatTime) {
        this.operatTime = operatTime;
    }

    public Long getOperateTimeOrder() {
        return operateTimeOrder;
    }

    public void setOperateTimeOrder(Long operateTimeOrder) {
        this.operateTimeOrder = operateTimeOrder;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
