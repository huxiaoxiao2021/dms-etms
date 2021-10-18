package com.jd.bluedragon.distribution.jsf.domain;

import java.io.Serializable;

public class SiteResponse implements Serializable {
    private static final long serialVersionUID = 5172344896938895744L;
    /**
     * 场地名称
     */
    private String siteName;
    /**
     *站点类型
     */
    private Integer siteType;
    /**
     * 分拣中心id
     */
    private Integer siteCode;
    /**
     *站点子类型
     */
    private Integer subType;
    /**
     *机构编号
     */
    private Integer orgId;

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

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public Integer getSubType() {
        return subType;
    }

    public void setSubType(Integer subType) {
        this.subType = subType;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }
}
