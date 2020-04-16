package com.jd.bluedragon.distribution.abnormal.dto;

import java.io.Serializable;

/**
 * 滞留上报明细消息
 * @author jinjingcheng
 * @date 2020/3/15
 */
public class StrandDetailMessage implements Serializable{

    private static final long serialVersionUID = 2775635731606523615L;
    /**滞留原因code*/
    private Integer reasonCode;
    /**滞留原因描述*/
    private String reasonMessage;
    /**包裹号*/
    private String packageCode;
    /**运单号*/
    private String waybillCode;
    /**上报条码类型*/
    private Integer reportType;
    /**操作条码*/
    private String reportBarcode;
    /**操作时间*/
    private String operateTime;
    /**操作人code*/
    private Integer operatorCode;
    /**操作场地 id*/
    private Integer createSiteCode;
    /**异常原因*/
    private String abnormalDescription;

    public Integer getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(Integer reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getReasonMessage() {
        return reasonMessage;
    }

    public void setReasonMessage(String reasonMessage) {
        this.reasonMessage = reasonMessage;
    }

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

    public Integer getReportType() {
        return reportType;
    }

    public void setReportType(Integer reportType) {
        this.reportType = reportType;
    }

    public String getReportBarcode() {
        return reportBarcode;
    }

    public void setReportBarcode(String reportBarcode) {
        this.reportBarcode = reportBarcode;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(Integer operatorCode) {
        this.operatorCode = operatorCode;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getAbnormalDescription() {
        return abnormalDescription;
    }

    public void setAbnormalDescription(String abnormalDescription) {
        this.abnormalDescription = abnormalDescription;
    }
}
