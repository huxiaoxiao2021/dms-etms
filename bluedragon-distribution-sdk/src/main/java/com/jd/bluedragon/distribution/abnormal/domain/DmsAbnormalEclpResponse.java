package com.jd.bluedragon.distribution.abnormal.domain;

import java.util.Date;

/**
 * MQ接收结果对象
 *
 * @author tangchunqing
 * @ClassName: DmsAbnormalEclpResponse
 * @Description: MQ接收结果对象
 * @date 2018年04月12日 15时:10分
 */
public class DmsAbnormalEclpResponse {

    /**
     * 运单
     */
    private String waybillCode;
    /**
     * 客服反馈时间
     */
    private String receiptTime;

    /**
     * 客服反馈结果
     */
    private String receiptValue;

    /**
     * 客服反馈备注
     */
    private String receiptMark;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getReceiptTime() {
        return receiptTime;
    }

    public void setReceiptTime(String receiptTime) {
        this.receiptTime = receiptTime;
    }

    public String getReceiptValue() {
        return receiptValue;
    }

    public void setReceiptValue(String receiptValue) {
        this.receiptValue = receiptValue;
    }

    public String getReceiptMark() {
        return receiptMark;
    }

    public void setReceiptMark(String receiptMark) {
        this.receiptMark = receiptMark;
    }
}
