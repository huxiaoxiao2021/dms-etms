package com.jd.bluedragon.distribution.send.domain;

import java.util.Date;

public class ArSendDetailMQBody implements java.io.Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 2292780849533744183L;

    /** 包裹号 */
    private String packageBarcode;

    /** 操作单位编码 */
    private Integer createSiteCode;

    /** 操作单位编码 */
    private Integer receiveSiteCode;

    /** 操作时间 */
    private Date operateTime;

    /** 发货交接单号 */
    private String sendCode;

    /** 操作人编码 */
    private Integer createUserCode;

    /** 操作人 */
    private String createUser;

    /** 箱号 */
    private String boxCode;

    /** 发货登记主键*/
    private String arSendRegisterId;

    public String getPackageBarcode() {
        return packageBarcode;
    }

    public void setPackageBarcode(String packageBarcode) {
        this.packageBarcode = packageBarcode;
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

    public Date getOperateTime() {
        return this.operateTime == null ? null : (Date) this.operateTime.clone();
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime == null ? null : (Date) operateTime.clone();
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
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

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getArSendRegisterId() {
        return arSendRegisterId;
    }

    public void setArSendRegisterId(String arSendRegisterId) {
        this.arSendRegisterId = arSendRegisterId;
    }
}
