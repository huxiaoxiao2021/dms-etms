package com.jd.bluedragon.distribution.siteRetake.domain;

import java.util.Date;

/**
 * @author tangchunqing
 * @Description: 驻厂再取 查询类
 * @date 2018年08月02日 16时:29分
 */
public class SiteRetakeCondition {
    private Date requiredStartTime;
    private Date requiredEndTime;
    private Integer VendorId;
    private Integer siteCode;
    private Date  assignStartTime;
    private Date  assignEndTime;

    public Date getRequiredStartTime() {
        return requiredStartTime;
    }

    public void setRequiredStartTime(Date requiredStartTime) {
        this.requiredStartTime = requiredStartTime;
    }

    public Date getRequiredEndTime() {
        return requiredEndTime;
    }

    public void setRequiredEndTime(Date requiredEndTime) {
        this.requiredEndTime = requiredEndTime;
    }

    public Integer getVendorId() {
        return VendorId;
    }

    public void setVendorId(Integer vendorId) {
        VendorId = vendorId;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public Date getAssignStartTime() {
        return assignStartTime;
    }

    public void setAssignStartTime(Date assignStartTime) {
        this.assignStartTime = assignStartTime;
    }

    public Date getAssignEndTime() {
        return assignEndTime;
    }

    public void setAssignEndTime(Date assignEndTime) {
        this.assignEndTime = assignEndTime;
    }
}
