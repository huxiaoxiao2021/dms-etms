package com.jd.bluedragon.common.dto.basedata.response;

import java.io.Serializable;

/**
 * 精简站点
 *
 * @author hujiping
 * @date 2021/2/24 10:51 上午
 */
public class StreamlinedBasicSite implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 站点ID
     */
    private Integer siteCode;
    /**
     * 站点7位编码
     */
    private String dmsSiteCode;
    /**
     * 站点名称
     */
    private String siteName;
    /**
     * 站点名称拼音码
     *  以,隔开，如：北辰自提点 bcztd,bczdd,bczsd
     */
    private String siteNamePym;
    /**
     * 站点所属分拣中心ID
     */
    private Integer dmsId;
    /**
     * 站点所属分拣中心名称
     */
    private String dmsName;
    /**
     * 速递ID
     */
    private Integer parentSiteCode;
    /**
     * 速递名称
     */
    private String parentSiteName;
    /**
     * 区域ID
     */
    private Integer orgId;
    /**
     * 区域名称
     */
    private String orgName;
    /**
     * 站点类型
     */
    private Integer siteType;
    /**
     * 站点子类型
     */
    private Integer subType;
    /**
     * 省ID
     */
    private Integer provinceId;
    /**
     * 省名称
     */
    private String provinceName;
    /**
     * 市ID
     */
    private Integer cityId;
    /**
     * 市名称
     */
    private String cityName;
    /**
     * 县ID
     */
    private Integer countryId;
    /**
     * 县名称
     */
    private String countryName;
    /**
     * 乡ID
     */
    private Integer countrySideId;
    /**
     * 乡名称
     */
    private String countrySideName;
    /**
     * 详细地址
     */
    private String address;
    /**
     * 站点联系人
     */
    private String siteContact;
    /**
     * 站点邮箱
     */
    private String siteMail;
    /**
     * 站点业务类型
     *  1-京配配送、自提服务
     *  2-京配配送服务
     *  3-自提服务
     *  4-非京配配送服务
     *  5-京配配送、自提服务、生鲜自提
     *  6-自提服务、生鲜自提
     *  7-非京配配送服务、自提服务
     */
    private Integer siteBusinessType;
    /**
     * 省公司
     */
    private String provinceCompanyCode;
    private String provinceCompanyName;
    /**
     * 片区
     */
    private String areaCode;
    private String areaName;
    /**
     * 分区
     */
    private String partitionCode;
    private String partitionName;
    /**
     * 是否有效
     */
    private Integer yn;
    /**
     * 运营状态：
     *  线上运营：1
     *  线下运营：2
     *  关闭：0
     *  不存在：-1
     */
    private Integer operateState;

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getDmsSiteCode() {
        return dmsSiteCode;
    }

    public void setDmsSiteCode(String dmsSiteCode) {
        this.dmsSiteCode = dmsSiteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteNamePym() {
        return siteNamePym;
    }

    public void setSiteNamePym(String siteNamePym) {
        this.siteNamePym = siteNamePym;
    }

    public Integer getDmsId() {
        return dmsId;
    }

    public void setDmsId(Integer dmsId) {
        this.dmsId = dmsId;
    }

    public String getDmsName() {
        return dmsName;
    }

    public void setDmsName(String dmsName) {
        this.dmsName = dmsName;
    }

    public Integer getParentSiteCode() {
        return parentSiteCode;
    }

    public void setParentSiteCode(Integer parentSiteCode) {
        this.parentSiteCode = parentSiteCode;
    }

    public String getParentSiteName() {
        return parentSiteName;
    }

    public void setParentSiteName(String parentSiteName) {
        this.parentSiteName = parentSiteName;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
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

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Integer getCountrySideId() {
        return countrySideId;
    }

    public void setCountrySideId(Integer countrySideId) {
        this.countrySideId = countrySideId;
    }

    public String getCountrySideName() {
        return countrySideName;
    }

    public void setCountrySideName(String countrySideName) {
        this.countrySideName = countrySideName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSiteContact() {
        return siteContact;
    }

    public void setSiteContact(String siteContact) {
        this.siteContact = siteContact;
    }

    public String getSiteMail() {
        return siteMail;
    }

    public void setSiteMail(String siteMail) {
        this.siteMail = siteMail;
    }

    public Integer getSiteBusinessType() {
        return siteBusinessType;
    }

    public void setSiteBusinessType(Integer siteBusinessType) {
        this.siteBusinessType = siteBusinessType;
    }

    public String getProvinceCompanyCode() {
        return provinceCompanyCode;
    }

    public void setProvinceCompanyCode(String provinceCompanyCode) {
        this.provinceCompanyCode = provinceCompanyCode;
    }

    public String getProvinceCompanyName() {
        return provinceCompanyName;
    }

    public void setProvinceCompanyName(String provinceCompanyName) {
        this.provinceCompanyName = provinceCompanyName;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getPartitionCode() {
        return partitionCode;
    }

    public void setPartitionCode(String partitionCode) {
        this.partitionCode = partitionCode;
    }

    public String getPartitionName() {
        return partitionName;
    }

    public void setPartitionName(String partitionName) {
        this.partitionName = partitionName;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public Integer getOperateState() {
        return operateState;
    }

    public void setOperateState(Integer operateState) {
        this.operateState = operateState;
    }
}
