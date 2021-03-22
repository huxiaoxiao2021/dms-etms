package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;

/**
 * 站点实体
 *
 * @author hujiping
 * @date 2021/2/24 8:21 下午
 */
public class BasicSiteDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 站点ID
     */
    private Integer id;
    /**
     * 站点名称
     */
    private String name;
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
    private Integer type;
    /**
     * 站点子类型
     */
    private Integer subType;
    /**
     * 所属分拣中心ID
     */
    private Integer sortingCenterId;
    /**
     * 所属分拣中心名称
    */
    private String sortingCenterName;
    /**
     * 速递ID
     */
    private Integer parentId;
    /**
     * 速递名称
     */
    private String parentName;
    /**
     * 站点编码
     */
    private String dmsCode;
    /**
     * 站点名称拼音码
     */
    private String pinyinCode;
    /**
     * 省份ID
     */
    private Integer provinceId;
    /**
     * 市ID
     */
    private Integer cityId;
    /**
     * 县ID
     */
    private Integer countryId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSubType() {
        return subType;
    }

    public void setSubType(Integer subType) {
        this.subType = subType;
    }

    public Integer getSortingCenterId() {
        return sortingCenterId;
    }

    public void setSortingCenterId(Integer sortingCenterId) {
        this.sortingCenterId = sortingCenterId;
    }

    public String getSortingCenterName() {
        return sortingCenterName;
    }

    public void setSortingCenterName(String sortingCenterName) {
        this.sortingCenterName = sortingCenterName;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getDmsCode() {
        return dmsCode;
    }

    public void setDmsCode(String dmsCode) {
        this.dmsCode = dmsCode;
    }

    public String getPinyinCode() {
        return pinyinCode;
    }

    public void setPinyinCode(String pinyinCode) {
        this.pinyinCode = pinyinCode;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }
}
