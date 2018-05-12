package com.jd.bluedragon.distribution.abnormal.domain;

/**
 * @author tangchunqing
 * @Description: MQ 消费对象
 * @date 2018年05月12日 13时:36分
 */
public class AbnormalUnknownWaybillResponse {
    /**
     * 运单号
     */
    private String waybillCode;
    /**
     * 回复内容
     */
    private String content;
    /**
     * 回复次数
     */
    private String reportNumber;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReportNumber() {
        return reportNumber;
    }

    public void setReportNumber(String reportNumber) {
        this.reportNumber = reportNumber;
    }
}
