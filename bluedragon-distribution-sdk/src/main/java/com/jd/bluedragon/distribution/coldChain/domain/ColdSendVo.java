package com.jd.bluedragon.distribution.coldChain.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : caozhixing3
 * @version V1.0
 * @Project: bluedragon-distribution
 * @Package com.jd.bluedragon.distribution.coldChain.domain
 * @Description:
 * @date Date : 2022年09月29日 9:20
 */
public class ColdSendVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 批次号
     */
    private String sendCode;
    /**
     * 运输计划编码
     */
    private String transPlanCode;
    /**
     * 包裹号|运单号
     */
    private String boxCode;
    /**
     * 业务
     */
    private Integer businessType;
    /**
     * 目的站点
     */
    private Integer receiveSiteCode;
    /**
     * 是否强制发货
     */
    private boolean forceSend = false;
    /**
     * 是否取消上次发货
     */
    private boolean cancelLastSend = false;
    /**
     * 业务来源
     */
    private Integer bizSource;
    /**
     *操作单位编号
     */
    private int siteCode;
    /**
     *操作单位名称
     */
    private String siteName;
    /**
     *操作时间
     */
    private String operateTime;
    private Integer userCode;
    private String userName;

    //==================================================

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getTransPlanCode() {
        return transPlanCode;
    }

    public void setTransPlanCode(String transPlanCode) {
        this.transPlanCode = transPlanCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public boolean isForceSend() {
        return forceSend;
    }

    public void setForceSend(boolean forceSend) {
        this.forceSend = forceSend;
    }

    public boolean isCancelLastSend() {
        return cancelLastSend;
    }

    public void setCancelLastSend(boolean cancelLastSend) {
        this.cancelLastSend = cancelLastSend;
    }

    public Integer getBizSource() {
        return bizSource;
    }

    public void setBizSource(Integer bizSource) {
        this.bizSource = bizSource;
    }

    public int getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(int siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getUserCode() {
        return userCode;
    }

    public void setUserCode(Integer userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
