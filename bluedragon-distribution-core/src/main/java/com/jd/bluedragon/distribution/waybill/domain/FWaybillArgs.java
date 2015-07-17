package com.jd.bluedragon.distribution.waybill.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * F返单参数
 * Created by wangtingwei on 2014/9/4.
 */
public class FWaybillArgs implements Serializable{

    /**
     * F返单号列表
     */
    private String[] fWaybills;

    /**
     * B商家Id
     */
    private Integer businessId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 站点ID
     */
    private int siteId;

    /**
     * 站点名称
     */
    private String siteName;

    /**
     * 操作人姓名
     */
    private String entryName;

    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String[] getFWaybills() {
        return fWaybills;
    }

    public void setFWaybills(String[] fWaybills) {
        this.fWaybills = fWaybills;
    }

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    @Override
    public String toString() {
        return "FWaybillArgs{" +
                "fWaybills=" + fWaybills +
                ", businessId=" + businessId +
                ", userId=" + userId +
                ", siteId=" + siteId +
                ", siteName='" + siteName + '\'' +
                '}';
    }
}
