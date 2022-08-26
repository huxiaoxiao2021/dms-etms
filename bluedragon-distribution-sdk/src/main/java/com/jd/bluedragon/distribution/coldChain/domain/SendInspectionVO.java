package com.jd.bluedragon.distribution.coldChain.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @Auther: wangjianle
 * @Date: 2022/08/22
 * @Description:
 */
public class SendInspectionVO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5706252456345301775L;

    /**
     * 发货单位编码
     */
    private Integer createSiteCode;

    /**
     * 收货单位编码
     */
    private Integer receiveSiteCode;

    /**
     * 发货交接单号-发货批次号
     */
    private String sendCode;

    /**
     * 包裹号
     */
    private String boxCode;

    /**
     * 操作人
     */
    private String createUser;

    /**
     * 操作人编码
     */
    private Integer createUserCode;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 发货业务来源
     */
    private Integer bizSource;

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

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Integer getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getBizSource() {
        return bizSource;
    }

    public void setBizSource(Integer bizSource) {
        this.bizSource = bizSource;
    }
}
