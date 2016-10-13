package com.jd.bluedragon.distribution.base.domain;

import java.io.Serializable;

/**
 * 站点、库房、商家
 * Created by wangtingwei on 2016/9/21.
 */
public class SiteWareHouseMerchant implements Serializable {

    private static final long serialVersionUID = 42L;

    /**
     * 机构编号
     */
    private Integer orgId;

    /**
     * 机构名称
     */
    private String orgName;

    /**
     * ID
     */
    private Integer id;

    /**
     * 名称
     */
    private String name ;

    /**
     * 速递中心ID
     */
    private Integer parentId;

    /**
     * 速递中心名称
     */
    private String parentName;

    /**
     * 七位编号
     */
    private String dmsCode;

    /**
     * 站点类型
     */
    private Integer type;

    /**
     * 子类型
     */
    private Integer subType;

    /**
     * 拼音码
     */
    private String pinyinCode;

    /**
     * 对应分拣中心id
     */
    private Integer sortingCenterId;

    /**
     * 对应分拣中心名称
     */
    private String sortingCenterName;


    /**
     * 省
     */
    private Integer provinceId;	//省id
    /**
     * 市
     */
    private Integer cityId;	//市id

    /**
     *
     */
    private Integer countryId;	//县id



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

    public String getPinyinCode() {
        return pinyinCode;
    }

    public void setPinyinCode(String pinyinCode) {
        this.pinyinCode = pinyinCode;
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
