package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

import java.util.List;

public class WaybillForPreSortOnSiteRequest extends JdRequest {
    //erp
    private String erp;

    //运单
    private String waybill;

    //分拣中心
    private Integer sortingSite;

    //现场调度站点
    private Integer siteOfSchedulingOnSite;


    /**
     * 省区编码
     */
    private String provinceAgencyCode;

    /**
     * 省区名称
     */
    private String provinceAgencyName;

    /**
     * 枢纽编码
     */
    private String areaHubCode;

    /**
     * 枢纽名称
     */
    private String areaHubName;

    /**
     * 返调度原因编码
     *  1：预分拣站点无法派送 2：特殊时期管制违禁品 3：邮政拒收 4：无预分拣站点
     */
    private Integer reasonType;


    /**
     * 操作员工是否为退货组标识
     */
    private Boolean returnGroupFlag;


    public String getErp() {
        return erp;
    }

    public void setErp(String erp) {
        this.erp = erp;
    }

    public String getWaybill() {
        return waybill;
    }

    public void setWaybill(String waybill) {
        this.waybill = waybill;
    }

    public Integer getSortingSite() {
        return sortingSite;
    }

    public void setSortingSite(Integer sortingSite) {
        this.sortingSite = sortingSite;
    }

    public Integer getSiteOfSchedulingOnSite() {
        return siteOfSchedulingOnSite;
    }

    public void setSiteOfSchedulingOnSite(Integer siteOfSchedulingOnSite) {
        this.siteOfSchedulingOnSite = siteOfSchedulingOnSite;
    }

    public String getProvinceAgencyCode() {
        return provinceAgencyCode;
    }

    public void setProvinceAgencyCode(String provinceAgencyCode) {
        this.provinceAgencyCode = provinceAgencyCode;
    }

    public String getProvinceAgencyName() {
        return provinceAgencyName;
    }

    public void setProvinceAgencyName(String provinceAgencyName) {
        this.provinceAgencyName = provinceAgencyName;
    }

    public String getAreaHubCode() {
        return areaHubCode;
    }

    public void setAreaHubCode(String areaHubCode) {
        this.areaHubCode = areaHubCode;
    }

    public String getAreaHubName() {
        return areaHubName;
    }

    public void setAreaHubName(String areaHubName) {
        this.areaHubName = areaHubName;
    }

    public Integer getReasonType() {
        return reasonType;
    }

    public void setReasonType(Integer reasonType) {
        this.reasonType = reasonType;
    }

    public Boolean getReturnGroupFlag() {
        return returnGroupFlag;
    }

    public void setReturnGroupFlag(Boolean returnGroupFlag) {
        this.returnGroupFlag = returnGroupFlag;
    }
}
