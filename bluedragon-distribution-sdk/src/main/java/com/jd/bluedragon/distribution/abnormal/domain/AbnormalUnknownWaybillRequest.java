package com.jd.bluedragon.distribution.abnormal.domain;

/**
 * @author tangchunqing
 * @Description: MQ 请求对象
 * @date 2018年05月12日 13时:35分
 */
public class AbnormalUnknownWaybillRequest {
    /**
     * 运单号
     */
    private String waybillCode;
    /**
     * 上报次数
     */
    private String reportNumber;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getReportNumber() {
        return reportNumber;
    }

    public void setReportNumber(String reportNumber) {
        this.reportNumber = reportNumber;
    }
}
