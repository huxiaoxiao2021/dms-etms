package com.jd.bluedragon.distribution.abnormal.domain;

import com.jd.bluedragon.distribution.api.JdRequest;

/**
 * 滞留上报信息
 * @author jinjingcheng
 * @date 2020/3/10
 */
public class StrandReportRequest extends JdRequest {

    /**滞留原因code*/
    private Integer reasonCode;
    /**滞留原因描述*/
    private String reasonMessage;
    /**条码 可能为 包裹号 运单号 箱号 批次号*/
    private String barcode;
    /**上报条码类型*/
    private Integer reportType;

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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Integer getReportType() {
        return reportType;
    }

    public void setReportType(Integer reportType) {
        this.reportType = reportType;
    }
}
