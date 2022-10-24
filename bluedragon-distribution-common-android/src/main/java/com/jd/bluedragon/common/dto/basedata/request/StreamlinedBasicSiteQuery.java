package com.jd.bluedragon.common.dto.basedata.request;

import com.jd.bluedragon.common.dto.base.request.BaseRequest;

import java.io.Serializable;
import java.util.List;

/**
 * 场地查询入参
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-10-11 17:36:04 周二
 */
public class StreamlinedBasicSiteQuery extends BaseRequest implements Serializable {
    private static final long serialVersionUID = -5343652369048693562L;

    /**
     * 站点编号
     */
    private List<Integer> siteCodes;

    /**
     * 站点名称
     *  支持模糊字段
     */
    private String siteName;

    /**
     * 站点/分拣7位编码
     */
    private String dmsCode;

    /**
     * 站点名称拼音码
     */
    private String siteNamePym;

    /**
     * 所属分拣中心ID
     */
    private List<Integer> dmsIds;

    /**
     * 站点类型
     */
    private List<Integer> siteTypes;

    /**
     * 子类型
     */
    private List<Integer> subTypes;

    /**
     * 地址
     *  支持模糊字段
     */
    private String address;

    /**
     * 机构ID
     */
    private List<Integer> orgIds;

    /**
     * 省份ID
     */
    private List<Integer> provinceIds;

    /**
     * 城市ID
     */
    private List<Integer> cityIds;

    /**
     * 县Id
     */
    private List<Integer> countryIds;

    /**
     * 乡Id
     */
    private List<Integer> countrySideIds;

    /**
     * 片区
     */
    private List<String> areaCodes;

    /**
     * 分区
     */
    private List<String> partitionCodes;

    /**
     * 搜索字符串
     */
    private String searchStr;

    public List<Integer> getSiteCodes() {
        return siteCodes;
    }

    public void setSiteCodes(List<Integer> siteCodes) {
        this.siteCodes = siteCodes;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getDmsCode() {
        return dmsCode;
    }

    public void setDmsCode(String dmsCode) {
        this.dmsCode = dmsCode;
    }

    public String getSiteNamePym() {
        return siteNamePym;
    }

    public void setSiteNamePym(String siteNamePym) {
        this.siteNamePym = siteNamePym;
    }

    public List<Integer> getDmsIds() {
        return dmsIds;
    }

    public void setDmsIds(List<Integer> dmsIds) {
        this.dmsIds = dmsIds;
    }

    public List<Integer> getSiteTypes() {
        return siteTypes;
    }

    public void setSiteTypes(List<Integer> siteTypes) {
        this.siteTypes = siteTypes;
    }

    public List<Integer> getSubTypes() {
        return subTypes;
    }

    public void setSubTypes(List<Integer> subTypes) {
        this.subTypes = subTypes;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Integer> getOrgIds() {
        return orgIds;
    }

    public void setOrgIds(List<Integer> orgIds) {
        this.orgIds = orgIds;
    }

    public List<Integer> getProvinceIds() {
        return provinceIds;
    }

    public void setProvinceIds(List<Integer> provinceIds) {
        this.provinceIds = provinceIds;
    }

    public List<Integer> getCityIds() {
        return cityIds;
    }

    public void setCityIds(List<Integer> cityIds) {
        this.cityIds = cityIds;
    }

    public List<Integer> getCountryIds() {
        return countryIds;
    }

    public void setCountryIds(List<Integer> countryIds) {
        this.countryIds = countryIds;
    }

    public List<Integer> getCountrySideIds() {
        return countrySideIds;
    }

    public void setCountrySideIds(List<Integer> countrySideIds) {
        this.countrySideIds = countrySideIds;
    }

    public List<String> getAreaCodes() {
        return areaCodes;
    }

    public void setAreaCodes(List<String> areaCodes) {
        this.areaCodes = areaCodes;
    }

    public List<String> getPartitionCodes() {
        return partitionCodes;
    }

    public void setPartitionCodes(List<String> partitionCodes) {
        this.partitionCodes = partitionCodes;
    }

    public String getSearchStr() {
        return searchStr;
    }

    public void setSearchStr(String searchStr) {
        this.searchStr = searchStr;
    }
}
