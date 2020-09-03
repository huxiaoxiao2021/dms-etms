package com.jd.bluedragon.distribution.boxlimit.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 建箱包裹数 入库数据 dataObject
 */
public class BoxLimit implements Serializable {
    /**
     * id
     */
    private Integer id;
    /**
     * 机构名称
     */
    private String siteName;
    /**
     * 机构ID
     */
    private Integer siteId;
    /**
     * 建箱包裹上限
     */
    private Integer limitNum;
    /**
     * 操作人erp
     */
    private String operatorErp;
    /**
     * 操作人机构ID
     */
    private Integer operatorSiteId;
    /**
     * 操作机构
     */
    private String operatorSiteName;
    /**
     * 操作时间
     */
    private Date operatingTime;

    private Date createTime;

    private Date updateTime;

    private boolean yn;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(Integer limitNum) {
        this.limitNum = limitNum;
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public void setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
    }

    public Integer getOperatorSiteId() {
        return operatorSiteId;
    }

    public void setOperatorSiteId(Integer operatorSiteId) {
        this.operatorSiteId = operatorSiteId;
    }

    public String getOperatorSiteName() {
        return operatorSiteName;
    }

    public void setOperatorSiteName(String operatorSiteName) {
        this.operatorSiteName = operatorSiteName;
    }

    public Date getOperatingTime() {
        return operatingTime;
    }

    public void setOperatingTime(Date operatingTime) {
        this.operatingTime = operatingTime;
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

    public boolean isYn() {
        return yn;
    }

    public void setYn(boolean yn) {
        this.yn = yn;
    }
}
