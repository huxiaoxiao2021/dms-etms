package com.jd.bluedragon.distribution.jy.dto.pickinggood;

import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2024/1/15 21:56
 * @Description
 */
public class PickingGoodBoxSplitDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String businessId;

    private String groupCode;
    private String packageCode;
    private String barCode;
    private String bizId;
    /**
     * 操作场地
     */
    private Long pickingSiteId;
    /**
     * true: 提货并发货
     */
    private Boolean sendGoodFlag;
    /**
     * 多扫标识
     */
    private Boolean moreScanFlag;

    private Long nextSiteId;

    private Long operateTime;

    private User user;

    private Long sysTime;


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

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
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

    public Long getPickingSiteId() {
        return pickingSiteId;
    }

    public void setPickingSiteId(Long pickingSiteId) {
        this.pickingSiteId = pickingSiteId;
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

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getSysTime() {
        return sysTime;
    }

    public void setSysTime(Long sysTime) {
        this.sysTime = sysTime;
    }
}
