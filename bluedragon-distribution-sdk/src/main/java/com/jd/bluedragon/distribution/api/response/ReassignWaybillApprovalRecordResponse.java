package com.jd.bluedragon.distribution.api.response;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/10/31 15:24
 * @Description: 返调度查询结果
 */
public class ReassignWaybillApprovalRecordResponse implements Serializable {

    private static final long serialVersionUID = 7896822159938321276L;
    private String provinceAgencyCode;

    private String provinceAgencyName;

    private String areaHubCode;

    private String areaHubName;

    private Integer siteCode;

    private String siteName;

    private String applicationUserErp;

    private String barCode;

    private String submitTime;

    private Integer receiveSiteCode;

    private String receiveSiteName;

    private Integer changeSiteCode;

    private String changeSiteName;

    /**
     * 返调度原因类型 1：预分拣站点无法派送 2：特殊时期管制违禁品 3：邮政拒收 4：无预分拣站点
     */
    private Integer changeSiteReasonTypeCode;

    private String changeSiteReasonTypeName;



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

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getApplicationUserErp() {
        return applicationUserErp;
    }

    public void setApplicationUserErp(String applicationUserErp) {
        this.applicationUserErp = applicationUserErp;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(String submitTime) {
        this.submitTime = submitTime;
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

    public Integer getChangeSiteCode() {
        return changeSiteCode;
    }

    public void setChangeSiteCode(Integer changeSiteCode) {
        this.changeSiteCode = changeSiteCode;
    }

    public String getChangeSiteName() {
        return changeSiteName;
    }

    public void setChangeSiteName(String changeSiteName) {
        this.changeSiteName = changeSiteName;
    }

    public Integer getChangeSiteReasonTypeCode() {
        return changeSiteReasonTypeCode;
    }

    public void setChangeSiteReasonTypeCode(Integer changeSiteReasonTypeCode) {
        this.changeSiteReasonTypeCode = changeSiteReasonTypeCode;
    }

    public String getChangeSiteReasonTypeName() {
        return changeSiteReasonTypeName;
    }

    public void setChangeSiteReasonTypeName(String changeSiteReasonTypeName) {
        this.changeSiteReasonTypeName = changeSiteReasonTypeName;
    }
}
