package com.jd.bluedragon.distribution.jy.send;

import java.io.Serializable;
import java.util.Date;

/**
 * 拣运发货波次待扫数据PO
 */
public class JySendPredictAggsPO implements Serializable {

    private Long id;


    private Long siteId;

    private Long planNextSiteId;

    private String planWaveCode;

    private Date planWaveWorkStartTime;

    private Date planWaveWorkEndTime;

    private String productType;

    private Integer unScanCount;

    private Date createTime;

    private Date updateTime;

    private Boolean yn;

    private Date ts;

    /**
     * 版本号
     */
    private Long version;

    /**
     * 并发锁id
     */
    private String uid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getPlanNextSiteId() {
        return planNextSiteId;
    }

    public void setPlanNextSiteId(Long planNextSiteId) {
        this.planNextSiteId = planNextSiteId;
    }

    public String getPlanWaveCode() {
        return planWaveCode;
    }

    public void setPlanWaveCode(String planWaveCode) {
        this.planWaveCode = planWaveCode;
    }

    public Date getPlanWaveWorkStartTime() {
        return planWaveWorkStartTime;
    }

    public void setPlanWaveWorkStartTime(Date planWaveWorkStartTime) {
        this.planWaveWorkStartTime = planWaveWorkStartTime;
    }

    public Date getPlanWaveWorkEndTime() {
        return planWaveWorkEndTime;
    }

    public void setPlanWaveWorkEndTime(Date planWaveWorkEndTime) {
        this.planWaveWorkEndTime = planWaveWorkEndTime;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Integer getUnScanCount() {
        return unScanCount;
    }

    public void setUnScanCount(Integer unScanCount) {
        this.unScanCount = unScanCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getYn() {
        return yn;
    }

    public void setYn(Boolean yn) {
        this.yn = yn;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}