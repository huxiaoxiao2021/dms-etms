package com.jd.bluedragon.distribution.recycle.material.domain;

import java.io.Serializable;
import java.util.List;

public class RecycleBasketPrintInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 创建场地名称
     */
    private String createSiteName;
    /**
     * 机构名称
     */
    private String orgName;
    /**
     * 机构和场地名称
     */
    private String orgAndSiteName;
    /**
     * 循环周转筐编码
     */
    private List<String> recycleBasketCodes;

    /**
     * 周转筐类型
     */
    public String typeName;

    /**
     * 省区名称
     */
    private String provinceAgencyName;

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgAndSiteName() {
        return orgAndSiteName;
    }

    public void setOrgAndSiteName(String orgAndSiteName) {
        this.orgAndSiteName = orgAndSiteName;
    }

    public List<String> getRecycleBasketCodes() {
        return recycleBasketCodes;
    }

    public void setRecycleBasketCodes(List<String> recycleBasketCodes) {
        this.recycleBasketCodes = recycleBasketCodes;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getProvinceAgencyName() {
        return provinceAgencyName;
    }

    public void setProvinceAgencyName(String provinceAgencyName) {
        this.provinceAgencyName = provinceAgencyName;
    }
}
