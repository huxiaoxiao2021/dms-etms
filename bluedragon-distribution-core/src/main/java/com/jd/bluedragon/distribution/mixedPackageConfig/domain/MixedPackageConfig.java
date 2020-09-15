package com.jd.bluedragon.distribution.mixedPackageConfig.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 混装配置类
 * Created by zhangleqi on 2017/8/27
 */
public class MixedPackageConfig implements Serializable {

    private static final long serialVersionUID = 2251674044944393243L;
    /**
     * id
     */
    private Integer id;
    /**
     * 建包/发货分拣中心编码
     */
    private Integer createSiteCode;
    /**
     * 建包/发货分拣中心名称
     */
    private String createSiteName;
    /**
     * 建包/发货分拣中心区域
     */
    private Integer createSiteArea;

    /**
     * 目的分拣中心编码
     */
    private Integer receiveSiteCode;
    /**
     * 目的分拣中心名称
     */
    private String receiveSiteName;
    /**
     * 目的分拣中心区域
     */
    private Integer receiveSiteArea;

    /**
     * 可混装目的地编码
     */
    private Integer mixedSiteCode;
    /**
     * 可混装目的地名称
     */
    private String mixedSiteName;
    /**
     * 可混装目的地区域
     */
    private Integer mixedSiteArea;

    /**
     * 可混装目的地类型
     */
    private Integer siteType;
    /**
     * 可混装目的地类型名称
     */
    private String siteTypeName;

    /**
     * 规则类型
     */
    private Integer ruleType;
    /**
     * 规则类型名称
     */
    private String ruleTypeName;
    /**
     * 承运类型
     */
    private Integer transportType;
    /**
     * 承运类型
     */
    private String transportTypeName;

    /**
     * 创建人姓名
     */
    private String createUser;

    /**
     * 创建人编码
     */
    private Integer createUserCode;

    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新人姓名
     */
    private String updateUser;
    /**
     * 更新人编码
     */
    private Integer updateUserCode;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否有效
     */
    private Integer yn;

    private Long ts;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public Integer getCreateSiteArea() {
        return createSiteArea;
    }

    public void setCreateSiteArea(Integer createSiteArea) {
        this.createSiteArea = createSiteArea;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public Integer getReceiveSiteArea() {
        return receiveSiteArea;
    }

    public void setReceiveSiteArea(Integer receiveSiteArea) {
        this.receiveSiteArea = receiveSiteArea;
    }

    public Integer getMixedSiteCode() {
        return mixedSiteCode;
    }

    public void setMixedSiteCode(Integer mixedSiteCode) {
        this.mixedSiteCode = mixedSiteCode;
    }

    public String getMixedSiteName() {
        return mixedSiteName;
    }

    public void setMixedSiteName(String mixedSiteName) {
        this.mixedSiteName = mixedSiteName;
    }

    public Integer getMixedSiteArea() {
        return mixedSiteArea;
    }

    public void setMixedSiteArea(Integer mixedSiteArea) {
        this.mixedSiteArea = mixedSiteArea;
    }

    public Integer getSiteType() {
        return siteType;
    }

    public void setSiteType(Integer siteType) {
        this.siteType = siteType;
    }

    public String getSiteTypeName() {
        return siteTypeName;
    }

    public void setSiteTypeName(String siteTypeName) {
        this.siteTypeName = siteTypeName;
    }

    public Integer getRuleType() {
        return ruleType;
    }

    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
    }

    public String getRuleTypeName() {
        return ruleTypeName;
    }

    public void setRuleTypeName(String ruleTypeName) {
        this.ruleTypeName = ruleTypeName;
    }

    public Integer getTransportType() {
        return transportType;
    }

    public void setTransportType(Integer transportType) {
        this.transportType = transportType;
    }

    public String getTransportTypeName() {
        return transportTypeName;
    }

    public void setTransportTypeName(String transportTypeName) {
        this.transportTypeName = transportTypeName;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Integer getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Integer getUpdateUserCode() {
        return updateUserCode;
    }

    public void setUpdateUserCode(Integer updateUserCode) {
        this.updateUserCode = updateUserCode;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }
}
