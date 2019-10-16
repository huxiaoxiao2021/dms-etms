package com.jd.bluedragon.distribution.external.pdd.domain;

/**
 * <p>
 *     jsf提供外部的拼多多的面单打印内容，只包含一部分
 *
 * @author wuzuxiang
 * @since 2019/10/15
 **/
public class PDDWaybillPrintInfoDto {

    /**
     * 电子面单号
     */
    private String waybillCode;

    /**
     * 寄件人姓名
     */
    private String senderName;

    /**
     * 寄件人电话: 021-31233121
     */
    private String senderPhone;

    /**
     * 寄件人手机: 13812345678
     */
    private String senderMobile;

    /**
     * 收件人姓名
     */
    private String consigneeName;

    /**
     * 收件人电话
     */
    private String consigneePhone;

    /**
     * 收件人手机
     */
    private String consigneeMobile;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getSenderMobile() {
        return senderMobile;
    }

    public void setSenderMobile(String senderMobile) {
        this.senderMobile = senderMobile;
    }

    public String getConsigneeName() {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    public String getConsigneePhone() {
        return consigneePhone;
    }

    public void setConsigneePhone(String consigneePhone) {
        this.consigneePhone = consigneePhone;
    }

    public String getConsigneeMobile() {
        return consigneeMobile;
    }

    public void setConsigneeMobile(String consigneeMobile) {
        this.consigneeMobile = consigneeMobile;
    }
}
