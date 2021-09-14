package com.jd.bluedragon.distribution.jsf.domain;

import java.io.Serializable;
import java.util.List;

public class SiteQuery implements Serializable {
    private static final long serialVersionUID = 829493402638778875L;

    private Integer orgId;

    private Integer siteCode;

    private String siteName;

    private Integer siteType;

    private Integer subType;

    private List<Integer> subTypes;


    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
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

    public List<Integer> getSubTypes() {
        return subTypes;
    }

    public void setSubTypes(List<Integer> subTypes) {
        this.subTypes = subTypes;
    }
}
