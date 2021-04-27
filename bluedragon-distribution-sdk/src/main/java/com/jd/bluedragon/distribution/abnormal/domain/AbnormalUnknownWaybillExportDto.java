package com.jd.bluedragon.distribution.abnormal.domain;

import java.io.Serializable;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2021/4/2 14:38
 */
public class AbnormalUnknownWaybillExportDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 序号-标识该运单第几次提出申请
     */
    private Integer orderNumber;

    /**
     * 商家ID
     */
    private Integer traderId;

    /**
     * 商家名称
     */
    private String traderName;

    /**
     * 上报机构id
     */
    private Integer dmsSiteCode;

    /**
     * 上报机构名称
     */
    private String dmsSiteName;

    /**
     * 上报区域ID
     */
    private Integer areaId;

    /**
     * 上报区域名称
     */
    private String areaName;


    /**
     * 回复时间
     */
    private String receiptTime;

    /**
     * 是否回复
     */
    private String isReceipt;

    /**  */
    private String receiptContent;

    /**
     * 创建人code
     */
    private Integer createUserCode;

    /**
     * 创建人ERP
     */
    private String createUser;

    /**
     * 创建人名称
     */
    private String createUserName;
    /**
     * 回复系统 W运单, E ECLE,  B  B商家端
     */
    private String receiptFrom;

    /**
     * 是否上报（请求时 发过来）
     */
    private Integer isReport;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getTraderId() {
        return traderId;
    }

    public void setTraderId(Integer traderId) {
        this.traderId = traderId;
    }

    public String getTraderName() {
        return traderName;
    }

    public void setTraderName(String traderName) {
        this.traderName = traderName;
    }

    public Integer getDmsSiteCode() {
        return dmsSiteCode;
    }

    public void setDmsSiteCode(Integer dmsSiteCode) {
        this.dmsSiteCode = dmsSiteCode;
    }

    public String getDmsSiteName() {
        return dmsSiteName;
    }

    public void setDmsSiteName(String dmsSiteName) {
        this.dmsSiteName = dmsSiteName;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getReceiptTime() {
        return receiptTime;
    }

    public void setReceiptTime(String receiptTime) {
        this.receiptTime = receiptTime;
    }

    public String getIsReceipt() {
        return isReceipt;
    }

    public void setIsReceipt(String isReceipt) {
        this.isReceipt = isReceipt;
    }

    public String getReceiptContent() {
        return receiptContent;
    }

    public void setReceiptContent(String receiptContent) {
        this.receiptContent = receiptContent;
    }

    public Integer getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getReceiptFrom() {
        return receiptFrom;
    }

    public void setReceiptFrom(String receiptFrom) {
        this.receiptFrom = receiptFrom;
    }

    public Integer getIsReport() {
        return isReport;
    }

    public void setIsReport(Integer isReport) {
        this.isReport = isReport;
    }
}
    
