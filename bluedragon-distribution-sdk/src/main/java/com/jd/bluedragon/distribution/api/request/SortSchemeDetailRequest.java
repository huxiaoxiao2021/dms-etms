package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;

/**
 * Created by yangbo7 on 2016/6/22.
 */
public class SortSchemeDetailRequest implements Serializable {

    private static final long serialVersionUID = 5943099079170708882L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 分拣方案主键
     */
    private String schemeId;

    /**
     * 运单目的地代码
     */
    private String siteCode;

    /**
     * 箱号目的地名称
     */
    private String pkgLabelName;

    /**
     * 箱号目的地代码
     */
    private String boxSiteCode;

    /**
     * 箱号目的地代码
     */
    private String subType;


    /**
     * 滑槽号1
     */
    private String chuteCode1;

    /**
     * 当前使用滑槽，用于循环分拣模式
     */
    private String currChuteCode;

    /**
     * 接收标识 0：未接收 1：已接收
     */
    private Integer receFlag;

    private Integer pageNo;

    private Integer pageSize;

    /**
     * 分拣中心ID
     */
    private Integer siteNo;

    private String sortSchemeDetailJson;

    public SortSchemeDetailRequest() {
    }

    public SortSchemeDetailRequest(String schemeId) {
        this.schemeId = schemeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(String schemeId) {
        this.schemeId = schemeId;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getPkgLabelName() {
        return pkgLabelName;
    }

    public void setPkgLabelName(String pkgLabelName) {
        this.pkgLabelName = pkgLabelName;
    }

    public String getBoxSiteCode() {
        return boxSiteCode;
    }

    public void setBoxSiteCode(String boxSiteCode) {
        this.boxSiteCode = boxSiteCode;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getChuteCode1() {
        return chuteCode1;
    }

    public void setChuteCode1(String chuteCode1) {
        this.chuteCode1 = chuteCode1;
    }

    public String getCurrChuteCode() {
        return currChuteCode;
    }

    public void setCurrChuteCode(String currChuteCode) {
        this.currChuteCode = currChuteCode;
    }

    public Integer getReceFlag() {
        return receFlag;
    }

    public void setReceFlag(Integer receFlag) {
        this.receFlag = receFlag;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getSiteNo() {
        return siteNo;
    }

    public void setSiteNo(Integer siteNo) {
        this.siteNo = siteNo;
    }

    public String getSortSchemeDetailJson() {
        return sortSchemeDetailJson;
    }

    public void setSortSchemeDetailJson(String sortSchemeDetailJson) {
        this.sortSchemeDetailJson = sortSchemeDetailJson;
    }
}
