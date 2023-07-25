package com.jd.bluedragon.distribution.jy.exception;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 异常-破损 entity
 */
public class JyExceptionDamageEntity {


    /**
     *
     */
    private Long id;

    /**
     *
     */
    private String bizId;

    /**
     *
     */
    private Integer siteCode;

    /**
     *
     */
    private String siteName;


    /**
     *
     */
    private String packageCode;

    /**
     *
     */
    private Boolean saveType;

    /**
     *
     */
    private BigDecimal weightRepairBefore;

    /**
     *
     */
    private BigDecimal weightRepairAfter;

    /**
     *
     */
    private Boolean damageType;

    /**
     *
     */
    private Boolean repairType;

    /**
     *
     */
    private Boolean feedBackType;

    /**
     *
     */
    private String createErp;

    /**
     *
     */
    private Date createTime;

    /**
     *
     */
    private String updateErp;

    /**
     *
     */
    private Date updateTime;

    /**
     *
     */
    private Date ts;

    /**
     *
     */
    private Boolean yn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Boolean getSaveType() {
        return saveType;
    }

    public void setSaveType(Boolean saveType) {
        this.saveType = saveType;
    }

    public BigDecimal getWeightRepairBefore() {
        return weightRepairBefore;
    }

    public void setWeightRepairBefore(BigDecimal weightRepairBefore) {
        this.weightRepairBefore = weightRepairBefore;
    }

    public BigDecimal getWeightRepairAfter() {
        return weightRepairAfter;
    }

    public void setWeightRepairAfter(BigDecimal weightRepairAfter) {
        this.weightRepairAfter = weightRepairAfter;
    }


    public Boolean getDamageType() {
        return damageType;
    }

    public void setDamageType(Boolean damageType) {
        this.damageType = damageType;
    }

    public Boolean getRepairType() {
        return repairType;
    }

    public void setRepairType(Boolean repairType) {
        this.repairType = repairType;
    }

    public Boolean getFeedBackType() {
        return feedBackType;
    }

    public void setFeedBackType(Boolean feedBackType) {
        this.feedBackType = feedBackType;
    }

    public String getCreateErp() {
        return createErp;
    }

    public void setCreateErp(String createErp) {
        this.createErp = createErp;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateErp() {
        return updateErp;
    }

    public void setUpdateErp(String updateErp) {
        this.updateErp = updateErp;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    public Boolean getYn() {
        return yn;
    }

    public void setYn(Boolean yn) {
        this.yn = yn;
    }
}