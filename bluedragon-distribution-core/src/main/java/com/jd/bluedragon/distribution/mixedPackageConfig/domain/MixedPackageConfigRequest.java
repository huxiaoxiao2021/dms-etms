package com.jd.bluedragon.distribution.mixedPackageConfig.domain;

import com.jd.bluedragon.distribution.api.JdRequest;

import java.util.List;

/**
 * 混集包请求对象
 */
public class MixedPackageConfigRequest extends JdRequest {

    private static final long serialVersionUID = -8848343776350014484L;

    /**
     * 建包/发货分拣中心编码
     */
    private Integer createSiteCode;
    /**
     * 建包/发货分拣中心区域
     */
    private Integer createSiteArea;
    /**
     * 建包/发货分拣中心区域
     */
    private String createSiteName;
    /**
     * 目的分拣中心编码
     */
    private Integer receiveSiteCode;
    /**
     * 目的分拣中心区域
     */
    private Integer receiveSiteArea;
    /**
     * 目的分拣中心区域
     */
    private String receiveSiteName;

    /**
     * 可混装目的地编码
     */
    private Integer mixedSiteCode;

    /**
     * 可混装目的地区域
     */
    private Integer mixedSiteArea;
    /**
     * 可混装目的地区域
     */
    private Integer mixedSiteName;

    /**
     * 可混装目的地类型
     */
    private Integer siteType;

    /**
     * 规则类型
     */
    private Integer ruleType;
    /**
     * 承运类型
     */
    private Integer transportType;
    /**
     * 开始索引
     */
    private Integer startIndex;

    /**
     * 每页最大显示条数
     */
    private Integer pageSize;

    private Integer userCode;
    private String userName;

    private Long ts;

    @Override
    public Integer getUserCode() {
        return userCode;
    }

    @Override
    public void setUserCode(Integer userCode) {
        this.userCode = userCode;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    private List<String> mixedSiteList;


    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public Integer getCreateSiteArea() {
        return createSiteArea;
    }

    public void setCreateSiteArea(Integer createSiteArea) {
        this.createSiteArea = createSiteArea;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public Integer getReceiveSiteArea() {
        return receiveSiteArea;
    }

    public void setReceiveSiteArea(Integer receiveSiteArea) {
        this.receiveSiteArea = receiveSiteArea;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public Integer getMixedSiteCode() {
        return mixedSiteCode;
    }

    public void setMixedSiteCode(Integer mixedSiteCode) {
        this.mixedSiteCode = mixedSiteCode;
    }

    public Integer getMixedSiteArea() {
        return mixedSiteArea;
    }

    public void setMixedSiteArea(Integer mixedSiteArea) {
        this.mixedSiteArea = mixedSiteArea;
    }

    public Integer getMixedSiteName() {
        return mixedSiteName;
    }

    public void setMixedSiteName(Integer mixedSiteName) {
        this.mixedSiteName = mixedSiteName;
    }

    public Integer getSiteType() {
        return siteType;
    }

    public void setSiteType(Integer siteType) {
        this.siteType = siteType;
    }

    public Integer getRuleType() {
        return ruleType;
    }

    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
    }

    public Integer getTransportType() {
        return transportType;
    }

    public void setTransportType(Integer transportType) {
        this.transportType = transportType;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<String> getMixedSiteList() {
        return mixedSiteList;
    }

    public void setMixedSiteList(List<String> mixedSiteList) {
        this.mixedSiteList = mixedSiteList;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    @Override
    public String toString() {
        return "MixedPackageConfigRequest{" +
                "createSiteCode=" + createSiteCode +
                ", createSiteArea=" + createSiteArea +
                ", createSiteName='" + createSiteName + '\'' +
                ", receiveSiteCode=" + receiveSiteCode +
                ", receiveSiteArea=" + receiveSiteArea +
                ", receiveSiteName='" + receiveSiteName + '\'' +
                ", mixedSiteCode=" + mixedSiteCode +
                ", mixedSiteArea=" + mixedSiteArea +
                ", mixedSiteName=" + mixedSiteName +
                ", siteType=" + siteType +
                ", ruleType=" + ruleType +
                ", transportType=" + transportType +
                ", startIndex=" + startIndex +
                ", pageSize=" + pageSize +
                ", userCode=" + userCode +
                ", userName='" + userName + '\'' +
                ", mixedSiteList=" + mixedSiteList +
                '}';
    }
}
