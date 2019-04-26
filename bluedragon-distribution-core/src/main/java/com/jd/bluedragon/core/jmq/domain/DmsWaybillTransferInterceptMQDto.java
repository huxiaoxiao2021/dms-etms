package com.jd.bluedragon.core.jmq.domain;

public class DmsWaybillTransferInterceptMQDto {
    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 拦截类型7：C网转B网；8：B网转C网
     */
    private Integer featureType;

    /**
     * 操作人Id
     */
    private Integer operatorCode;

    /**
     * 操作分拣中心code
     */
    private Integer siteCode;

    /**
     * 操作时间yyyy-MM-dd HH:mm:ss
     */
    private String  operateTime;

    /**
     * 系统来源 默认：DMS
     */
    private String sourceSystem;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getFeatureType() {
        return featureType;
    }

    public void setFeatureType(Integer featureType) {
        this.featureType = featureType;
    }

    public Integer getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(Integer operatorCode) {
        this.operatorCode = operatorCode;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }
}
