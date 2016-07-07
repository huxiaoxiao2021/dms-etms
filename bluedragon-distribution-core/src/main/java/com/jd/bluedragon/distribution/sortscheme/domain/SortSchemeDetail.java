package com.jd.bluedragon.distribution.sortscheme.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yangbo7 on 2016/6/22.
 */
public class SortSchemeDetail implements Serializable {

    private static final long serialVersionUID = 7252192349095624111L;

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

    /**
     * 数据接收时间
     */
    private String receTime;

    /**
     * 数据库时间
     */
    private Date timesTamp;

    /**
     * 0不生效, 1生效
     */
    private Integer yn;

    public SortSchemeDetail() {
    }

    public SortSchemeDetail(Integer siteCode, String chuteCode) {
        this.siteCode = siteCode.toString();
        this.currChuteCode = chuteCode;
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

    public String getReceTime() {
        return receTime;
    }

    public void setReceTime(String receTime) {
        this.receTime = receTime;
    }

    public Date getTimesTamp() {
        return timesTamp;
    }

    public void setTimesTamp(Date timesTamp) {
        this.timesTamp = timesTamp;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public String toString() {
        return "SortSchemeDetail:" + "getBoxSiteCode" + this.getBoxSiteCode() + ", getSubType=\'" + this.getSubType() + '\'' + ", getPkgLabelName=\'" + this.getPkgLabelName() + '}';
    }
}
