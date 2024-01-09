package com.jd.bluedragon.distribution.jy.dto.pickinggood;

import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 *
 * 提货扫描异步处理
 *
 * @Author zhengchengfa
 * @Date 2023/12/15 10:37
 * @Description
 */
public class JyPickingGoodScanDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String businessId;
    private String groupCode;
    private String barCode;
    private String bizId;
    /**
     * 操作场地
     */
    private Long siteId;
    /**
     * true: 提货并发货
     */
    private Boolean sendGoodFlag;
    /**
     * 多扫标识
     */
    private Boolean moreScanFlag;

    private Long nextSiteId;

    private Long operatorTime;

    private User user;

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Boolean getSendGoodFlag() {
        return sendGoodFlag;
    }

    public void setSendGoodFlag(Boolean sendGoodFlag) {
        this.sendGoodFlag = sendGoodFlag;
    }

    public Boolean getMoreScanFlag() {
        return moreScanFlag;
    }

    public void setMoreScanFlag(Boolean moreScanFlag) {
        this.moreScanFlag = moreScanFlag;
    }

    public Long getNextSiteId() {
        return nextSiteId;
    }

    public void setNextSiteId(Long nextSiteId) {
        this.nextSiteId = nextSiteId;
    }

    public Long getOperatorTime() {
        return operatorTime;
    }

    public void setOperatorTime(Long operatorTime) {
        this.operatorTime = operatorTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
