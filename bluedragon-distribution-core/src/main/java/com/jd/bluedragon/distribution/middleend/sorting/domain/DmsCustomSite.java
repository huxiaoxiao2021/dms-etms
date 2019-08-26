package com.jd.bluedragon.distribution.middleend.sorting.domain;

public class DmsCustomSite {
    /**
     * 编码如910
     */
    private Integer siteId;
    /**
     * 7位编码
     */
    private String siteCode;
    /**
     * 站点名称
     */
    private String siteName;
    /**
     * 站点类型
     */
    private Integer siteType;
    /**
     * 子类型
     */
    private Integer subType;
    /**
     * 自定义类型 wms：0; site:1; 商家:2,
     */
    private Integer customSiteType;

    public final static Integer CUSTOM_SITE_TYPE_WMS = 0;
    public final static Integer CUSTOM_SITE_TYPE_SITE = 1;
    public final static Integer CUSTOM_SITE_TYPE_B_ENTERPRISE = 2;


    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getSiteType() {
        return siteType;
    }

    public void setSiteType(Integer siteType) {
        this.siteType = siteType;
    }

    public Integer getSubType() {
        return subType;
    }

    public void setSubType(Integer subType) {
        this.subType = subType;
    }

    public Integer getCustomSiteType() {
        return customSiteType;
    }

    public void setCustomSiteType(Integer customSiteType) {
        this.customSiteType = customSiteType;
    }
}
