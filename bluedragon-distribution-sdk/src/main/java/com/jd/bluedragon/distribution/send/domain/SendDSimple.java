package com.jd.bluedragon.distribution.send.domain;

import java.io.Serializable;

public class SendDSimple implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 批次号
     */
    private String sendCode;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 包裹号
     */
    private String packageBarCode;

    /**
     * 始发分拣中心编码
     */
    private Integer createSiteCode;

    /**
     * 目的分拣中心编码
     */
    private Integer receiveSiteCode;

    /**
     * 操作人编码
     */
    private Integer createUserCode;

    /**
     * 操作人姓名
     */
    private String createUserName;

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageBarCode() {
        return packageBarCode;
    }

    public void setPackageBarCode(String packageBarCode) {
        this.packageBarCode = packageBarCode;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public Integer getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }
}
