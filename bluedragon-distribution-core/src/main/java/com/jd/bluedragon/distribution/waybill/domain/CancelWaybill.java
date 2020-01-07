package com.jd.bluedragon.distribution.waybill.domain;

import com.google.common.collect.ImmutableList;

public class CancelWaybill {

    public static final Integer FEATURE_TYPE_NORMAL = 1; // 正常解锁订单
    public static final Integer FEATURE_TYPE_CANCELED = -1; // 取消订单
    public static final Integer FEATURE_TYPE_DELETED = -2; // 删除订单
    public static final Integer FEATURE_TYPE_LOCKED = -3; // 锁定订单
    public static final Integer FEATURE_TYPE_REFUND100 = 3; // 退款100分订单
    public static final Integer FEATURE_TYPE_SICK = 30; // 病单
    public static final Integer FEATURE_TYPE_SICK_CANCEL = 31; // 取消病单
    public static final Integer FEATURE_TYPE_INTERCEPT = 4; // 外单拦截订单
    public static final Integer FEATURE_TYPE_INTERCEPT_BUSINESS = 5; // B商家外单拦截订单
    public static final Integer FEATURE_TYPE_INTERCEPT_LP = 10; // 理赔拦截订单
    public static final Integer FEATURE_TYPE_ORDER_MODIFY = 6; // 运单修改配送方式拦截订单
    public static final Integer FEATURE_TYPE_C_TRANSPORT_B= 7; // 运单C网转B网
    public static final Integer FEATURE_TYPE_B_TRANSPORT_C = 8; //运单B网转C网

    public static final String BUSINESS_TYPE_LOCK = "1"; //业务类型 1 锁定
    public static final String BUSINESS_TYPE_UNLOCK = "2";//业务类型 2 解锁

    public static final String FEATURE_MSG_UNNORMAL = "LOCKED_OR_CANCELED";
    public static final String FEATURE_MSG_CANCELED = "CANCELED";
    public static final String FEATURE_MSG_DELETED = "DELETED";
    public static final String FEATURE_MSG_LOCKED = "LOCKED";
    public static final String FEATURE_MSG_REFUND100 = "REFUND100";
    public static final String FEATURE_MSG_SICKCANCEL = "SICKCANCEL";
    public static final String FEATURE_MSG_INTERCEPT = "INTERCEPT";
    public static final String FEATURE_MSG_INTERCEPT_BUSINESS = "INTERCEPT_BUSINESS";
    public static final String FEATURE_MSG_INTERCEPT_LP = "INTERCEPT_LP"; //理赔完成拦截

    /**根据运单拦截 包裹也需要处理的类型*/
    public static ImmutableList<Integer> FEATURE_TYPES_NEED_PACKAGE_DEAL =
            ImmutableList.of(FEATURE_TYPE_ORDER_MODIFY, FEATURE_TYPE_C_TRANSPORT_B, FEATURE_TYPE_B_TRANSPORT_C);
    /**待处理的包裹最多查询数量*/
    public static final Integer BLOCK_PACKAGE_QUERY_NUMBER = 10;
    /*拦截完成后需要做后续操作的（比如发jmq给别的应用）*/
    public static final Integer[] NEED_AFTER_DEAL_FEATURE_TYPES = {FEATURE_TYPE_ORDER_MODIFY};

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

    /**
     * 业务类型
     */
    private String businessType;

    private Integer exceptionId;

    /**
     * 拦截类型  1:取消订单拦截,2:拒收订单拦截,3:恶意订单拦截,4:分拣中心拦截,5:仓储异常拦截,6:白条强制拦截
     */
    private Integer interceptType;

    /**
     * 拦截方式 1:提示 2：强制
     */
    private Integer interceptMode;

    private String operateTime;

    private Long operateTimeOrder;

    private Long ts;

    private String messageType;

    private String createTime;

    private String updateTime;

    private Integer yn;

    private String packageCode;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public Integer getFeatureType() {
        return featureType;
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

    public Integer getInterceptType() {
        return interceptType;
    }

    public void setInterceptType(Integer interceptType) {
        this.interceptType = interceptType;
    }

    public Integer getInterceptMode() {
        return interceptMode;
    }

    public void setInterceptMode(Integer interceptMode) {
        this.interceptMode = interceptMode;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }
}
