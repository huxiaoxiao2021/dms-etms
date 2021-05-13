package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;
import java.util.List;

/**
 * 站点查询条件
 *
 * @author hujiping
 * @date 2021/2/24 8:09 下午
 */
public class SiteQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 站点ID
     */
    private Integer siteId;
    /**
     * 站点编码
     */
    private String dmsCode;
    /**
     * 站点名称
     */
    private String siteName;
    /**
     * 站点名称拼音码
     */
    private String siteNamePym;
    /**
     * 区域ID
     */
    private Integer orgId;
    /**
     * 站点类型
     */
    private List<Integer> siteTypeList;
    /**
     * 站点子类型
     */
    private List<Integer>  subTypeList;
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
    /**
     * 查询数量
     */
    private Integer fetchNum;

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getDmsCode() {
        return dmsCode;
    }

    public void setDmsCode(String dmsCode) {
        this.dmsCode = dmsCode;
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

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public List<Integer> getSiteTypeList() {
        return siteTypeList;
    }

    public void setSiteTypeList(List<Integer> siteTypeList) {
        this.siteTypeList = siteTypeList;
    }

    public List<Integer> getSubTypeList() {
        return subTypeList;
    }

    public void setSubTypeList(List<Integer> subTypeList) {
        this.subTypeList = subTypeList;
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

    public Integer getFetchNum() {
        return fetchNum;
    }

    public void setFetchNum(Integer fetchNum) {
        this.fetchNum = fetchNum;
    }
}
